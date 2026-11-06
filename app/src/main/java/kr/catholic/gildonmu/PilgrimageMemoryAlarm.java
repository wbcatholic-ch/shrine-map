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
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** A single pending reminder for the next valid pilgrimage anniversary; no visit data modifications. */
public final class PilgrimageMemoryAlarm extends BroadcastReceiver {
    static final String OPEN_ACTION="kr.catholic.gildonmu.MEMORY_OPEN";
    static final String DATE_EXTRA="memory_visit_date";
    private static final String FIRE_ACTION="kr.catholic.gildonmu.MEMORY_FIRE";
    private static final String STORE="pilgrimage_memory_v1";
    private static final String CHANNEL="pilgrimage_memory_v1";
    private static final int REQUEST=9851;
    private static final String FIRST_LAUNCH="2026-11-07";

    private static Calendar parse(String iso){
        if(iso==null||!iso.matches("\\d{4}-\\d{2}-\\d{2}"))return null;
        try{
            SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd",Locale.US);fmt.setLenient(false);
            java.util.Date value=fmt.parse(iso);
            if(value==null||!iso.equals(fmt.format(value)))return null;
            Calendar c=Calendar.getInstance();c.setTime(value);return c;
        }catch(Exception ex){return null;}
    }
    private static String iso(Calendar c){return new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(c.getTime());}
    private static Calendar shift(Calendar original,int months){
        Calendar c=(Calendar)original.clone();int originalDay=c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH,1);c.add(Calendar.MONTH,months);
        c.set(Calendar.DAY_OF_MONTH,Math.min(originalDay,c.getActualMaximum(Calendar.DAY_OF_MONTH)));
        return c;
    }
    private static Calendar at1230(Calendar original){Calendar c=(Calendar)original.clone();c.set(Calendar.HOUR_OF_DAY,12);c.set(Calendar.MINUTE,30);c.set(Calendar.SECOND,0);c.set(Calendar.MILLISECOND,0);return c;}
    private static boolean eligibleForMonth(Calendar first,Calendar visit){
        if(first==null)return false;
        Calendar deadline=shift(first,12);
        // A GPS visit can be registered automatically on the following day.
        Calendar earliest=(Calendar)first.clone();earliest.add(Calendar.DAY_OF_YEAR,-1);
        return !visit.before(earliest)&&!visit.after(deadline);
    }
    private static class Next {
        final String visitDate,kind;
        final long at;
        Next(String visitDate,String kind,long at){this.visitDate=visitDate;this.kind=kind;this.at=at;}
    }
    private static Next nextFromSnapshot(String firstDate,String dates,Set<String> delivered,long now){
        Calendar first=parse(firstDate);
        if(first==null)return null;
        Calendar launch=at1230(parse(FIRST_LAUNCH));
        long minimum=Math.max(now,launch.getTimeInMillis());
        Next best=null;
        try{
            JSONArray arr=new JSONArray(dates);
            Set<String> unique=new HashSet<>();
            for(int i=0;i<arr.length()&&i<5000;i++){
                String date=arr.optString(i,"");
                if(!unique.add(date))continue;
                Calendar visit=parse(date);if(visit==null||visit.after(Calendar.getInstance()))continue;
                for(int mode=0;mode<2;mode++){
                    if(mode==0&&!eligibleForMonth(first,visit))continue;
                    Calendar due=at1230(shift(visit,mode==0?1:12));
                    long when=due.getTimeInMillis();
                    String key=(mode==0?"month:":"year:")+date;
                    if(when<minimum||delivered.contains(key))continue;
                    if(best==null||when<best.at)best=new Next(date,mode==0?"month":"year",when);
                }
            }
        }catch(Exception ignored){}
        return best;
    }
    static synchronized void update(Context context,String firstDate,String datesJson){
        if(parse(firstDate)==null||datesJson==null)return;
        try{if(!(new JSONArray(datesJson).length()<=5000))return;}catch(Exception e){return;}
        context.getSharedPreferences(STORE,Context.MODE_PRIVATE).edit()
            .putString("first",firstDate).putString("dates",datesJson).apply();
        restore(context);
    }
    static synchronized void restore(Context context){
        SharedPreferences p=context.getSharedPreferences(STORE,Context.MODE_PRIVATE);
        Set<String> sent=p.getStringSet("sent",new HashSet<>());
        Next next=nextFromSnapshot(p.getString("first",""),p.getString("dates","[]"),sent,System.currentTimeMillis());
        AlarmManager alarms=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(alarms==null)return;
        Intent intent=new Intent(context,PilgrimageMemoryAlarm.class).setAction(FIRE_ACTION);
        PendingIntent pending=PendingIntent.getBroadcast(context,REQUEST,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        alarms.cancel(pending);
        if(next==null)return;
        p.edit().putString("next_date",next.visitDate).putString("next_kind",next.kind).apply();
        if(Build.VERSION.SDK_INT>=31&&!alarms.canScheduleExactAlarms())alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next.at,pending);
        else alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,next.at,pending);
    }
    @Override public void onReceive(Context context,Intent intent){
        if(intent==null)return;
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){restore(context);return;}
        if(!FIRE_ACTION.equals(intent.getAction()))return;
        SharedPreferences p=context.getSharedPreferences(STORE,Context.MODE_PRIVATE);
        String date=p.getString("next_date",""),kind=p.getString("next_kind","");
        Calendar visit=parse(date);
        if(visit==null||!("month".equals(kind)||"year".equals(kind))){restore(context);return;}
        Calendar due=at1230(shift(visit,"month".equals(kind)?1:12));
        long now=System.currentTimeMillis();
        if(now<due.getTimeInMillis()){restore(context);return;}
        String key=kind+":"+date;
        Set<String> sent=new HashSet<>(p.getStringSet("sent",new HashSet<>()));
        if(sent.contains(key)){restore(context);return;}
        // Avoid surprise notifications long after their intended day.
        Calendar today=Calendar.getInstance();
        if(iso(today).equals(iso(due))&&!(Build.VERSION.SDK_INT>=33&&context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)){
            NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            if(manager!=null){
                if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(new NotificationChannel(CHANNEL,"순례 추억 알림",NotificationManager.IMPORTANCE_DEFAULT));
                Intent open=new Intent(context,MainActivity.class).setAction(OPEN_ACTION)
                    .putExtra(DATE_EXTRA,date).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                int requestCode=REQUEST+Integer.parseInt(date.replace("-","").substring(2));
                PendingIntent tap=PendingIntent.getActivity(context,requestCode,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
                Notification.Builder builder=Build.VERSION.SDK_INT>=26?new Notification.Builder(context,CHANNEL):new Notification.Builder(context);
                String title="month".equals(kind)?"한 달 전 그날의 순례":"1년 전 그날의 순례";
                builder.setSmallIcon(R.drawable.ic_anniversary_notification).setContentTitle(title)
                    .setContentText(date.substring(5,7)+"월 "+date.substring(8,10)+"일의 사진과 메모를 다시 만나보세요.")
                    .setAutoCancel(true).setContentIntent(tap);
                manager.notify(requestCode,builder.build());
            }
        }
        sent.add(key);p.edit().putStringSet("sent",sent).apply();
        restore(context);
    }
}
