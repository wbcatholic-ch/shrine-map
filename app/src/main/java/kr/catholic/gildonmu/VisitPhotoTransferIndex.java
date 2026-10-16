package kr.catholic.gildonmu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

/**
 * Lightweight on-device transfer index. Never holds image bytes, visit records,
 * credentials or user-facing data. Original photo files and their IDs remain authoritative.
 * Cloud transfer stays disabled until the separate transfer-manager stage is tested.
 */
final class VisitPhotoTransferIndex extends SQLiteOpenHelper {
    private static final String DB_NAME = "visit_photo_transfer_index_v1.db";
    VisitPhotoTransferIndex(Context ctx) { super(ctx, DB_NAME, null, 1); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE photo_transfer (photo_id TEXT PRIMARY KEY NOT NULL, drive_file_id TEXT NOT NULL DEFAULT '', transfer_state TEXT NOT NULL DEFAULT 'pending', updated_at INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX transfer_state_idx ON photo_transfer(transfer_state, updated_at)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Never drop historical photo references or the local transfer index.
    }
    synchronized void queued(String photoId) {
        if (!valid(photoId)) return;
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("INSERT OR IGNORE INTO photo_transfer(photo_id, drive_file_id, transfer_state, updated_at) VALUES(?, '', 'pending', ?)",
                new Object[]{photoId, System.currentTimeMillis()});
    }
    synchronized void uploaded(String photoId, String driveId) {
        if (!valid(photoId) || !valid(driveId)) return;
        ContentValues v=new ContentValues();
        v.put("drive_file_id", driveId); v.put("transfer_state", "uploaded");v.put("updated_at", System.currentTimeMillis());
        getWritableDatabase().insertWithOnConflict("photo_transfer", null, valuesWithId(v,photoId), SQLiteDatabase.CONFLICT_REPLACE);
    }
    synchronized void deleted(String photoId) {
        if (!valid(photoId)) return;
        ContentValues v=new ContentValues();v.put("transfer_state","deleted");v.put("updated_at",System.currentTimeMillis());
        getWritableDatabase().update("photo_transfer",v,"photo_id=?",new String[]{photoId});
    }
    synchronized boolean isUploaded(String photoId) {
        if (!valid(photoId)) return false;
        try(Cursor c=getReadableDatabase().rawQuery("SELECT transfer_state FROM photo_transfer WHERE photo_id=?",new String[]{photoId})) {
            return c.moveToFirst() && "uploaded".equals(c.getString(0));
        }
    }
    private static boolean valid(String s){return s!=null && s.length()>0 && s.length()<256;}
    private static ContentValues valuesWithId(ContentValues values,String photoId){values.put("photo_id",photoId);return values;}
}
