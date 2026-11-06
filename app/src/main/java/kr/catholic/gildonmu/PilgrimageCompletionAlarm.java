package kr.catholic.gildonmu;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** Native, date-scoped notice; does not read or modify WebView visit data. */
public final class PilgrimageCompletionAlarm extends BroadcastReceiver {
    static final String OPEN_ACTION="kr.catholic.gildonmu.COMPLETION_OPEN";
    static final String DATE_EXTRA="completion_date";
    private static final String FIRE_ACTION="kr.catholic.gildonmu.COMPLETION_FIRE";
    private static final String PREFS="pilgrimage_completion_notice_v1";
    private static final String CHANNEL="pilgrimage_completion_daily_v1";
    private static final int NOTICE_ID=9842;
    private static String today(){
        return new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Calendar.getInstance().getTime());
    }
    private static Calendar day(String date){
        if(date==null||!date.matches("\\d{4}-\\d{2}-\\d{2}"))return null;
        try {
            SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd",Locale.US);
            fmt.setLenient(false);
            java.util.Date parsed=fmt.parse(date);
            if(parsed==null||!date.equals(fmt.format(parsed)))return null;
            Calendar calendar=Calendar.getInstance(); calendar.setTime(parsed);return calendar;
        }catch(Exception e){return null;}
    }
    static boolean isYesterday(String date){
        Calendar yesterday=Calendar.getInstance();yesterday.add(Calendar.DAY_OF_YEAR,-1);
        return date!=null&&date.equals(new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(yesterday.getTime()));
    }
    static void schedule(Context context,String visitDate){
        if(!today().equals(visitDate))return; // only verified same-day shrine GPS
        SharedPreferences prefs=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        if(!visitDate.equals(prefs.getString("visit_date","")))
            prefs.edit().putString("visit_date",visitDate).apply();
        setAlarm(context,visitDate);
    }
    static void restore(Context context){
        String date=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE).getString("visit_date","");
        if(today().equals(date)||isYesterday(date))setAlarm(context,date);
    }
    private static void setAlarm(Context context,String visitDate){
        Calendar target=day(visitDate);if(target==null)return;
        target.add(Calendar.DAY_OF_YEAR,1);target.set(Calendar.HOUR_OF_DAY,10);
        target.set(Calendar.MINUTE,0);target.set(Calendar.SECOND,0);target.set(Calendar.MILLISECOND,0);
        if(target.getTimeInMillis()<=System.currentTimeMillis())return;
        Intent intent=new Intent(context,PilgrimageCompletionAlarm.class).setAction(FIRE_ACTION).putExtra(DATE_EXTRA,visitDate);
        PendingIntent pending=PendingIntent.getBroadcast(context,NOTICE_ID,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarms=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(alarms==null)return;
        if(Build.VERSION.SDK_INT>=31&&alarms.canScheduleExactAlarms())
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,target.getTimeInMillis(),pending);
        else if(Build.VERSION.SDK_INT<31)
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,target.getTimeInMillis(),pending);
        else alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,target.getTimeInMillis(),pending);
    }
    @Override public void onReceive(Context context,Intent intent){
        if(intent==null)return;
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){restore(context);return;}
        if(!FIRE_ACTION.equals(intent.getAction()))return;
        String date=intent.getStringExtra(DATE_EXTRA);
        if(!isYesterday(date))return;
        SharedPreferences prefs=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE);
        if(!date.equals(prefs.getString("visit_date",""))||date.equals(prefs.getString("notified_date","")))return;
        if(Build.VERSION.SDK_INT>=33&&context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)return;
        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager==null)return;
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel channel=new NotificationChannel(CHANNEL,"순례완료 알림",NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Intent open=new Intent(context,MainActivity.class).setAction(OPEN_ACTION).putExtra(DATE_EXTRA,date)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent tap=PendingIntent.getActivity(context,NOTICE_ID,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder=Build.VERSION.SDK_INT>=26?new Notification.Builder(context,CHANNEL):new Notification.Builder(context);
        String display=date.substring(5,7)+"월 "+date.substring(8,10)+"일";
        builder.setSmallIcon(R.drawable.ic_anniversary_notification)
            .setContentTitle("어제의 순례기록을 확인해 보세요")
            .setContentText(display+"에 방문한 성지의 순례코스를 확인할 수 있습니다.")
            .setAutoCancel(true).setContentIntent(tap);
        manager.notify(NOTICE_ID,builder.build());
        prefs.edit().putString("notified_date",date).apply();
    }
}
