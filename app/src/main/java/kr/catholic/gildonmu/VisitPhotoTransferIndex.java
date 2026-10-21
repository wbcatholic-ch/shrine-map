package kr.catholic.gildonmu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Auxiliary, rebuildable transfer index; never the source of truth for user photos.
 * No file deletion or cloud operation may be triggered solely by an index state.
 */
final class VisitPhotoTransferIndex extends SQLiteOpenHelper {
    private static final String DB_NAME = "visit_photo_transfer_index_v1.db";
    private static final int SCHEMA_VERSION = 4;
    VisitPhotoTransferIndex(Context context) { super(context, DB_NAME, null, SCHEMA_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE photo_transfer (photo_id TEXT PRIMARY KEY NOT NULL, "
                + "drive_file_id TEXT NOT NULL DEFAULT '', transfer_state TEXT NOT NULL DEFAULT 'pending', "
                + "updated_at INTEGER NOT NULL, place_code TEXT NOT NULL DEFAULT '', "
                + "visit_date TEXT NOT NULL DEFAULT '', retry_count INTEGER NOT NULL DEFAULT 0, "
                + "next_retry_at INTEGER NOT NULL DEFAULT 0, last_error TEXT NOT NULL DEFAULT '', "
                + "thumb_state TEXT NOT NULL DEFAULT 'pending', thumb_retry_at INTEGER NOT NULL DEFAULT 0)");
        createIndexes(db);
        createRestoreTable(db);
        createThumbnailIndexes(db);
    }

    private static void createRestoreTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS photo_restore (photo_id TEXT PRIMARY KEY NOT NULL, "
            + "drive_file_id TEXT NOT NULL DEFAULT '', place_code TEXT NOT NULL DEFAULT '', "
            + "visit_date TEXT NOT NULL DEFAULT '', transfer_state TEXT NOT NULL DEFAULT 'pending', "
            + "next_retry_at INTEGER NOT NULL DEFAULT 0, retry_count INTEGER NOT NULL DEFAULT 0, "
            + "thumb_state TEXT NOT NULL DEFAULT 'pending')");
        db.execSQL("CREATE INDEX IF NOT EXISTS photo_restore_queue_idx "
            + "ON photo_restore(transfer_state, next_retry_at, visit_date DESC, photo_id)");
    }

    private static void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS transfer_state_idx ON photo_transfer(transfer_state, updated_at)");
        db.execSQL("CREATE INDEX IF NOT EXISTS transfer_retry_idx ON photo_transfer(transfer_state, next_retry_at)");
        db.execSQL("CREATE INDEX IF NOT EXISTS transfer_place_idx ON photo_transfer(place_code, visit_date)");
    }

    private static void createThumbnailIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS thumb_transfer_queue_idx ON photo_transfer(transfer_state,thumb_state,thumb_retry_at,updated_at)");
        db.execSQL("CREATE INDEX IF NOT EXISTS thumb_restore_queue_idx ON photo_restore(transfer_state,thumb_state,next_retry_at,visit_date)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // In-place migration: preserve every historical photo_id, Drive ID and state.
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE photo_transfer ADD COLUMN place_code TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE photo_transfer ADD COLUMN visit_date TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE photo_transfer ADD COLUMN retry_count INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE photo_transfer ADD COLUMN next_retry_at INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE photo_transfer ADD COLUMN last_error TEXT NOT NULL DEFAULT ''");
            createIndexes(db);
        }
        if(oldVersion<3)createRestoreTable(db);
        if(oldVersion<4){
            db.execSQL("ALTER TABLE photo_transfer ADD COLUMN thumb_state TEXT NOT NULL DEFAULT 'pending'");
            db.execSQL("ALTER TABLE photo_transfer ADD COLUMN thumb_retry_at INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE photo_restore ADD COLUMN thumb_state TEXT NOT NULL DEFAULT 'pending'");
        }
        createThumbnailIndexes(db);
    }

    synchronized void queued(String photoId) {
        if (!valid(photoId)) return;
        ContentValues v = new ContentValues();
        v.put("photo_id", photoId);
        v.put("drive_file_id", "");
        v.put("transfer_state", "pending");
        v.put("updated_at", System.currentTimeMillis());
        // IGNORE is intentional: a repeated enqueue must not reset uploaded/deleted states.
        getWritableDatabase().insertWithOnConflict("photo_transfer", null, v, SQLiteDatabase.CONFLICT_IGNORE);
    }

    synchronized void setPhotoMetadata(String photoId, String placeCode, String visitDate) {
        if (!valid(photoId)) return;
        queued(photoId);
        ContentValues v = new ContentValues();
        if (placeCode != null && placeCode.length() < 256) v.put("place_code", placeCode);
        if (visitDate != null && visitDate.length() < 64) v.put("visit_date", visitDate);
        if (v.size() > 0) getWritableDatabase().update("photo_transfer", v, "photo_id=?", new String[]{photoId});
    }

    synchronized void uploaded(String photoId, String driveId) {
        if (!valid(photoId) || !valid(driveId)) return;
        queued(photoId);
        // Do not revive a locally deleted photo when an old upload callback arrives late.
        ContentValues v = new ContentValues();
        v.put("drive_file_id", driveId);
        v.put("transfer_state", "uploaded");
        v.put("retry_count", 0);
        v.put("next_retry_at", 0);
        v.put("last_error", "");
        v.put("updated_at", System.currentTimeMillis());
        getWritableDatabase().update("photo_transfer", v, "photo_id=? AND transfer_state!='deleted'", new String[]{photoId});
    }

    // Separate thumbnail backup journal; never changes the confirmed original Drive ID.
    synchronized String nextThumbnailUploadCandidate(long now) {
        try(Cursor c=getReadableDatabase().rawQuery(
            "SELECT photo_id FROM photo_transfer WHERE transfer_state='uploaded' "
             +"AND thumb_state IN ('pending','retry') AND thumb_retry_at<=? "
             +"ORDER BY updated_at DESC LIMIT 1",new String[]{String.valueOf(now)})){
            return c.moveToFirst()?c.getString(0):null;
        }
    }
    synchronized void thumbUploaded(String photoId) {
        ContentValues v=new ContentValues();v.put("thumb_state","complete");v.put("thumb_retry_at",0);
        getWritableDatabase().update("photo_transfer",v,"photo_id=? AND transfer_state='uploaded'",new String[]{photoId});
    }
    synchronized void thumbDeferred(String photoId,long when) {
        ContentValues v=new ContentValues();v.put("thumb_state","retry");v.put("thumb_retry_at",when);
        getWritableDatabase().update("photo_transfer",v,"photo_id=? AND transfer_state='uploaded'",new String[]{photoId});
    }
    synchronized RestoreCandidate nextThumbnailRestore(long now) {
        try(Cursor c=getReadableDatabase().rawQuery(
            "SELECT photo_id,drive_file_id,place_code,visit_date FROM photo_restore "
             +"WHERE transfer_state IN ('pending','retry') AND thumb_state='pending' "
             +"AND next_retry_at<=? ORDER BY visit_date DESC,photo_id ASC LIMIT 1",
             new String[]{String.valueOf(now)})){
            return c.moveToFirst()?new RestoreCandidate(c.getString(0),c.getString(1),c.getString(2),c.getString(3)):null;
        }
    }
    // Skip in-flight photo IDs so parallel workers never fetch the same photo.
    synchronized RestoreCandidate nextThumbnailRestoreExcluding(long now, java.util.Set<String> excluded){
        try(Cursor c=getReadableDatabase().rawQuery(
            "SELECT photo_id,drive_file_id,place_code,visit_date FROM photo_restore "
             +"WHERE transfer_state IN ('pending','retry') AND thumb_state='pending' "
             +"AND next_retry_at<=? ORDER BY visit_date DESC,photo_id ASC LIMIT 8",
             new String[]{String.valueOf(now)})){
            while(c.moveToNext())if(!excluded.contains(c.getString(0)))
                return new RestoreCandidate(c.getString(0),c.getString(1),c.getString(2),c.getString(3));
            return null;
        }
    }
    synchronized void thumbnailRestoreChecked(String photoId) {
        ContentValues v=new ContentValues();v.put("thumb_state","checked");
        getWritableDatabase().update("photo_restore",v,"photo_id=? AND thumb_state='pending'",new String[]{photoId});
    }
    synchronized void deleted(String photoId) {
        if (!valid(photoId)) return;
        queued(photoId);
        ContentValues v = new ContentValues();
        v.put("transfer_state", "deleted");
        v.put("thumb_state", "complete");
        v.put("updated_at", System.currentTimeMillis());
        getWritableDatabase().update("photo_transfer", v, "photo_id=?", new String[]{photoId});
    }

    /** Fast lookup for confirmed remote mappings; the legacy preferences remain a fallback. */
    synchronized String uploadedDriveId(String photoId) {
        if (!valid(photoId)) return "";
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT drive_file_id FROM photo_transfer WHERE photo_id=? "
                        + "AND transfer_state='uploaded' AND drive_file_id!='' LIMIT 1",
                new String[]{photoId})) {
            return c.moveToFirst() ? c.getString(0) : "";
        }
    }

    synchronized boolean isUploaded(String photoId) {
        if (!valid(photoId)) return false;
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT transfer_state FROM photo_transfer WHERE photo_id=?", new String[]{photoId})) {
            return c.moveToFirst() && "uploaded".equals(c.getString(0));
        }
    }

    // Call only after the corresponding real local photo is verified and cloud upload was attempted.
    synchronized void failedUpload(String photoId, String error, long retryAfterMillis) {
        if (!valid(photoId)) return;
        queued(photoId);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE photo_transfer SET transfer_state='retry', retry_count=retry_count+1, "
                        + "next_retry_at=?, last_error=?, updated_at=? "
                        + "WHERE photo_id=? AND transfer_state IN ('pending','retry')",
                new Object[]{Math.max(0L, retryAfterMillis), error == null ? "" : error.substring(0, Math.min(200, error.length())),
                        System.currentTimeMillis(), photoId});
    }

    // Returns at most one candidate. Callers must still verify file existence and authorization.
    synchronized String nextUploadCandidate(long nowMillis) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT photo_id FROM photo_transfer WHERE (transfer_state='pending' OR transfer_state='retry') "
                        + "AND next_retry_at<=? AND place_code!='' AND visit_date!='' ORDER BY updated_at ASC, photo_id ASC LIMIT 1",
                new String[]{String.valueOf(nowMillis)})) {
            return c.moveToFirst() ? c.getString(0) : null;
        }
    }

    // Secondary restore journal. Never replaces the authoritative Web visit records.
    // Existing completed/deleted photo records are never changed by an imported manifest.
    synchronized void enqueueRestore(String photoId, String driveId, String placeCode, String visitDate) {
        if(!valid(photoId) || !photoId.matches("[A-Za-z0-9_-]{8,200}")
           || !valid(placeCode) || !visitDate.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))return;
        ContentValues v=new ContentValues();
        v.put("photo_id",photoId);v.put("place_code",placeCode);v.put("visit_date",visitDate);
        v.put("drive_file_id",valid(driveId)?driveId:"");
        v.put("transfer_state","pending");
        getWritableDatabase().insertWithOnConflict("photo_restore",null,v,SQLiteDatabase.CONFLICT_IGNORE);
        if(valid(driveId)) {
            ContentValues update=new ContentValues();update.put("drive_file_id",driveId);
            getWritableDatabase().update("photo_restore",update,
                "photo_id=? AND transfer_state!='complete' AND drive_file_id=''",new String[]{photoId});
        }
    }

    static final class RestoreCandidate {
        final String photoId, driveId, placeCode, visitDate;
        RestoreCandidate(String p,String d,String c,String v){photoId=p;driveId=d;placeCode=c;visitDate=v;}
    }

    synchronized RestoreCandidate nextRestore(long now){
        try(Cursor c=getReadableDatabase().rawQuery(
            "SELECT photo_id,drive_file_id,place_code,visit_date FROM photo_restore "
              +"WHERE transfer_state IN ('pending','retry') AND next_retry_at<=? "
              +"ORDER BY visit_date DESC,photo_id ASC LIMIT 1",
            new String[]{String.valueOf(now)})){
            return c.moveToFirst()?new RestoreCandidate(c.getString(0),c.getString(1),c.getString(2),c.getString(3)):null;
        }
    }
    synchronized RestoreCandidate nextRestoreExcluding(long now, java.util.Set<String> excluded){
        try(Cursor c=getReadableDatabase().rawQuery(
            "SELECT photo_id,drive_file_id,place_code,visit_date FROM photo_restore "
              +"WHERE transfer_state IN ('pending','retry') AND next_retry_at<=? "
              +"ORDER BY visit_date DESC,photo_id ASC LIMIT 8",
            new String[]{String.valueOf(now)})){
            while(c.moveToNext())if(!excluded.contains(c.getString(0)))
                return new RestoreCandidate(c.getString(0),c.getString(1),c.getString(2),c.getString(3));
            return null;
        }
    }
    synchronized void requireRestoreIfMissing(String photoId){
        if(!valid(photoId))return;
        ContentValues v=new ContentValues();v.put("transfer_state","pending");
        v.put("next_retry_at",0);v.put("retry_count",0);
        getWritableDatabase().update("photo_restore",v,
            "photo_id=? AND transfer_state='complete'",new String[]{photoId});
    }
    synchronized void markRestored(String photoId){
        ContentValues v=new ContentValues();v.put("transfer_state","complete");
        v.put("next_retry_at",0);v.put("retry_count",0);
        getWritableDatabase().update("photo_restore",v,"photo_id=?",new String[]{photoId});
    }
    synchronized void deferRestore(String photoId,long retryAt){
        getWritableDatabase().execSQL("UPDATE photo_restore SET transfer_state='retry', "
              +"retry_count=retry_count+1,next_retry_at=? WHERE photo_id=? AND transfer_state!='complete'",
            new Object[]{Math.max(0L,retryAt),photoId});
    }

    // Failure isolation for both thumbnails and full photos. Do not change cloud IDs,
    // completed rows or any durable photo file. Retry grows up to one hour.
    synchronized long deferRestoreWithBackoff(String photoId,long now) {
        if(!valid(photoId))return 60000L;
        int attempts=0;
        try(Cursor c=getReadableDatabase().rawQuery(
                "SELECT retry_count FROM photo_restore WHERE photo_id=? AND transfer_state!='complete' LIMIT 1",
                new String[]{photoId})){
            if(!c.moveToFirst())return 60000L;
            attempts=Math.max(0,c.getInt(0));
        }
        long delay=Math.min(3600000L,60000L*(1L<<Math.min(6,attempts)));
        deferRestore(photoId,now+delay);
        return delay;
    }

    private static boolean valid(String s) { return s != null && !s.isEmpty() && s.length() < 256; }
}
