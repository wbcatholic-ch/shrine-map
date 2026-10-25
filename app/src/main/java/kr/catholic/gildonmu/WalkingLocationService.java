package kr.catholic.gildonmu;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

/** Native location-service foundation. Not activated until the web/native
 * stamp handoff and course-state persistence are implemented and tested. */
public final class WalkingLocationService extends Service implements LocationListener {
    private static final String CHANNEL_ID = "walking_pilgrimage_location_v1";
    private static final int NOTIFICATION_ID = 6402;
    public static final String ACTION_OPEN = "kr.catholic.gildonmu.walk.OPEN";
    public static final String ACTION_STOP = "kr.catholic.gildonmu.walk.STOP";
    private final android.os.Handler notificationHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable minuteTick = new Runnable() {
        @Override public void run() {
            if (startedAt <= 0L) return;
            if (!sameLocalDay(startedAt, System.currentTimeMillis())) {
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().clear().apply();
                stopSelf();
                return;
            }
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION_ID, notification());
            notificationHandler.postDelayed(this, 60000L);
        }
    };
    private static boolean sameLocalDay(long a, long b) {
        java.util.Calendar first = java.util.Calendar.getInstance();
        first.setTimeInMillis(a);
        java.util.Calendar second = java.util.Calendar.getInstance();
        second.setTimeInMillis(b);
        return first.get(java.util.Calendar.ERA) == second.get(java.util.Calendar.ERA)
                && first.get(java.util.Calendar.YEAR) == second.get(java.util.Calendar.YEAR)
                && first.get(java.util.Calendar.DAY_OF_YEAR) == second.get(java.util.Calendar.DAY_OF_YEAR);
    }
    private void startMinuteTick() {
        notificationHandler.removeCallbacks(minuteTick);
        notificationHandler.postDelayed(minuteTick, 60000L);
    }
    private LocationManager manager;
    private boolean listening;
    private long startedAt;
    private int visited;
    private int total;
    private String nextName = "";
    private String distanceLabel = "도보 경로 확인 중";
    private String waypointsJson = "[]";
    private long samplingMs = 30000L;
    private long lastFixNotificationAt = 0L;
    private String lastFixNotificationContent = "";
    public static final String ACTION_UPDATE = "kr.catholic.gildonmu.walk.UPDATE";
    public static final String EXTRA_STARTED_AT = "startedAt";
    public static final String EXTRA_MODE = "travelMode";
    private String travelMode = "walk";
    public static final String EXTRA_VISITED = "visited";
    public static final String EXTRA_TOTAL = "total";
    public static final String EXTRA_NEXT_NAME = "nextName";
    public static final String EXTRA_DISTANCE_LABEL = "distanceLabel";
    public static final String EXTRA_WAYPOINTS_JSON = "waypointsJson";
    private static final String PREFS = "walking_service_status";
    static final Object FIX_QUEUE_LOCK = new Object();


    @Override public void onCreate() {
        super.onCreate();
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        NotificationManager notifications = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifications.createNotificationChannel(new NotificationChannel(CHANNEL_ID,
                "순례 위치 추적", NotificationManager.IMPORTANCE_LOW));
    }

    private Notification notification() {
        Intent open = new Intent(this, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        open.setAction(ACTION_OPEN);
        PendingIntent pending = PendingIntent.getActivity(this, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long minutes = Math.max(0L, (System.currentTimeMillis() - startedAt) / 60000L);
        String elapsed = String.format(java.util.Locale.KOREA, "%02d:%02d", minutes / 60, minutes % 60);
        boolean car = "car".equals(travelMode);
        String next = nextName.isEmpty() ? "다음 순례지 확인 중" : "다음: " + nextName;
        String progress = "순례시간 " + elapsed + " · 방문 " + visited + "/" + total + "곳";
        String tracking = "GPS 위치 추적 중";
        String route = car ? next + "\n" + tracking : next + " · " + distanceLabel;
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(car ? R.drawable.ic_car_pilgrimage_notification : R.drawable.ic_pilgrimage_notification)
                .setLargeIcon(PilgrimageNotificationIcon.largeIcon(this, car))
                .setContentTitle("순례 중")
                .setContentText(progress)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(progress + "\n" + route))
                .setOngoing(true).setOnlyAlertOnce(true)
                .setContentIntent(pending)
                .addAction(android.R.drawable.ic_menu_revert, "앱으로 돌아가기", pending)
                .build();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().clear().commit();
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
            return START_NOT_STICKY;
        }
        if (intent == null) {
            android.content.SharedPreferences saved = getSharedPreferences(PREFS, MODE_PRIVATE);
            long previousStart = saved.getLong(EXTRA_STARTED_AT, 0L);
            if (previousStart <= 0L || previousStart > System.currentTimeMillis()
                    || !sameLocalDay(previousStart, System.currentTimeMillis())) {
                stopSelf();
                return START_NOT_STICKY;
            }
            intent = new Intent(ACTION_UPDATE);
            intent.putExtra(EXTRA_STARTED_AT, previousStart);
            intent.putExtra(EXTRA_MODE, saved.getString(EXTRA_MODE, "walk"));
            intent.putExtra(EXTRA_VISITED, saved.getInt(EXTRA_VISITED, 0));
            intent.putExtra(EXTRA_TOTAL, saved.getInt(EXTRA_TOTAL, 0));
            intent.putExtra(EXTRA_NEXT_NAME, saved.getString(EXTRA_NEXT_NAME, ""));
            intent.putExtra(EXTRA_DISTANCE_LABEL, saved.getString(EXTRA_DISTANCE_LABEL, "도보 경로 확인 중"));
            intent.putExtra(EXTRA_WAYPOINTS_JSON, saved.getString(EXTRA_WAYPOINTS_JSON, "[]"));
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (ACTION_UPDATE.equals(intent.getAction()) && !listening
                && getSharedPreferences(PREFS, MODE_PRIVATE).getLong(EXTRA_STARTED_AT, 0L) == 0L)
            return START_NOT_STICKY;
        if (intent.hasExtra(EXTRA_STARTED_AT)) {
            long requested = intent.getLongExtra(EXTRA_STARTED_AT, 0L);
            long prior = getSharedPreferences(PREFS, MODE_PRIVATE).getLong(EXTRA_STARTED_AT, 0L);
            if (requested > 0L && requested != prior && !ACTION_UPDATE.equals(intent.getAction())) {
                getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                        .remove("pending_fixes").remove("last_fix").commit();
            }
            if (requested > 0 && requested <= System.currentTimeMillis()) startedAt = requested;
        }
        if (startedAt == 0L) startedAt = System.currentTimeMillis();
        // A previous day's course must never keep collecting location indefinitely.
        if (!sameLocalDay(startedAt, System.currentTimeMillis())) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().clear().apply();
            stopSelf();
            return START_NOT_STICKY;
        }
        if (intent.hasExtra(EXTRA_MODE)) travelMode = "car".equals(intent.getStringExtra(EXTRA_MODE)) ? "car" : "walk";
        if (intent.hasExtra(EXTRA_VISITED)) visited = Math.max(0, intent.getIntExtra(EXTRA_VISITED, 0));
        if (intent.hasExtra(EXTRA_TOTAL)) total = Math.max(visited, intent.getIntExtra(EXTRA_TOTAL, 0));
        if (intent.hasExtra(EXTRA_NEXT_NAME)) nextName = intent.getStringExtra(EXTRA_NEXT_NAME);
        if (intent.hasExtra(EXTRA_DISTANCE_LABEL)) distanceLabel = intent.getStringExtra(EXTRA_DISTANCE_LABEL);
        if (intent.hasExtra(EXTRA_WAYPOINTS_JSON)) waypointsJson = intent.getStringExtra(EXTRA_WAYPOINTS_JSON);
        if (waypointsJson == null) waypointsJson = "[]";
        if (nextName == null) nextName = "";
        if (distanceLabel == null || distanceLabel.isEmpty()) distanceLabel = "도보 경로 확인 중";
        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                .putLong(EXTRA_STARTED_AT, startedAt)
                .putString(EXTRA_MODE, travelMode)
                .putInt(EXTRA_VISITED, visited).putInt(EXTRA_TOTAL, total)
                .putString(EXTRA_NEXT_NAME, nextName)
                .putString(EXTRA_DISTANCE_LABEL, distanceLabel)
                .putString(EXTRA_WAYPOINTS_JSON, waypointsJson).apply();
        if (listening) {
            startMinuteTick();
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification());
            return START_STICKY;
        }
        startForeground(NOTIFICATION_ID, notification());
        startMinuteTick();
        try {
            String provider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    ? LocationManager.GPS_PROVIDER
                    : (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        ? LocationManager.NETWORK_PROVIDER : null);
            if (provider == null) {
                stopSelf();
                return START_NOT_STICKY;
            }
            manager.requestLocationUpdates(provider, 30000L, 10f, this);
            listening = true;
        } catch (SecurityException | IllegalArgumentException ex) {
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    private void refreshWalkingDistanceAndSampling(Location fix) {
        if (fix == null || waypointsJson == null) return;
        double nearestM = Double.MAX_VALUE;
        String closestName = "";
        try {
            org.json.JSONArray items = new org.json.JSONArray(waypointsJson);
            for (int i = 0; i < items.length(); i++) {
                org.json.JSONObject item = items.optJSONObject(i);
                if (item == null || item.optBoolean("done", false)) continue;
                double lat = item.optDouble("lat", Double.NaN), lon = item.optDouble("lng", Double.NaN);
                if (!Double.isFinite(lat) || !Double.isFinite(lon)
                        || Math.abs(lat) > 90 || Math.abs(lon) > 180) continue;
                float[] results = new float[1];
                Location.distanceBetween(fix.getLatitude(), fix.getLongitude(), lat, lon, results);
                if (results[0] < nearestM) {
                    nearestM = results[0];
                    closestName = item.optString("name", "");
                }
            }
        } catch (org.json.JSONException ex) {
            android.util.Log.w("WalkingPilgrimage", "Invalid route points", ex);
        }
        if (nearestM == Double.MAX_VALUE) return;
        // GPS proximity selects the sampling cadence only. The notification's
        // distance and next stop must follow the planned route, not the nearest
        // crow-flies waypoint. Only API-verified walking lengths may be displayed.
        long desiredMs = nearestM <= 500.0 ? 5000L : 30000L;
        if (desiredMs != samplingMs && listening && manager != null) {
            try {
                String provider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
                // Register the replacement request before removing anything; a provider
                // failure must not accidentally disable an otherwise valid tracking session.
                manager.requestLocationUpdates(provider, desiredMs, desiredMs <= 5000L ? 0f : 10f, this);
                samplingMs = desiredMs;
            } catch (SecurityException | IllegalArgumentException ex) {
                android.util.Log.w("WalkingPilgrimage", "Location cadence unchanged", ex);
            }
        }
    }

    @Override public void onLocationChanged(Location location) {
        // Keep a bounded last-known fix for web reconciliation after returning.
        // Never mint a stamp here: the existing web visit pipeline owns deduplication.
        if (location != null && startedAt > 0L && location.hasAccuracy()
                && location.getAccuracy() >= 0f && location.getAccuracy() <= 100f
                && location.getTime() >= startedAt
                && location.getTime() <= System.currentTimeMillis() + 5000L) {
            android.content.SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            String fix = location.getLatitude() + "," + location.getLongitude()
                    + "," + location.getAccuracy() + "," + location.getTime();
            // Preserve a bounded sequence while the WebView is backgrounded.
            // Only the existing web GPS visit pipeline may register stamps.
            synchronized (FIX_QUEUE_LOCK) {
                String history = prefs.getString("pending_fixes", "");
                // Append without splitting and rebuilding the entire GPS history
                // on every fix. Trim only after the bounded 2048-point cap.
                StringBuilder queue = new StringBuilder(history.length() + fix.length() + 1);
                queue.append(history).append(fix).append('\n');
                int count = 0;
                for (int i = 0; i < queue.length(); i++) {
                    if (queue.charAt(i) == '\n') count++;
                }
                int excess = count - 2048;
                if (excess > 0) {
                    int start = 0;
                    while (excess-- > 0) {
                        int end = queue.indexOf("\n", start);
                        if (end < 0) break;
                        start = end + 1;
                    }
                    if (start > 0) queue.delete(0, start);
                }
                prefs.edit().putString("last_fix", fix)
                        .putString("pending_fixes", queue.toString()).commit();
            }
        }
        if (location != null && location.hasAccuracy() && location.getAccuracy() <= 100f) {
            refreshWalkingDistanceAndSampling(location);
        }
        // Avoid recreating the notification for every GPS fix. Update once per
        // minute, or immediately if the next shrine/distance label changed.
        if (startedAt > 0L) {
            long now = System.currentTimeMillis();
            String content = nextName + "|" + distanceLabel;
            if (!content.equals(lastFixNotificationContent)
                    || now - lastFixNotificationAt >= 60000L) {
                lastFixNotificationContent = content;
                lastFixNotificationAt = now;
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .notify(NOTIFICATION_ID, notification());
            }
        }
    }
    @Override public void onProviderEnabled(String provider) { }
    @Override public void onProviderDisabled(String provider) { }
    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override public void onDestroy() {
        notificationHandler.removeCallbacks(minuteTick);
        if (listening && manager != null) {
            manager.removeUpdates(this);
            listening = false;
        }
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
