package com.example.myday;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    public static Activity SettingTime;
    @Override
    public void onReceive(Context context, Intent intent) { //알람 시간이 되었을때 onReceive를 호출함


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        Intent notificationIntent = new Intent(context, selectEmoticonActivity.class);
        notificationIntent.putExtra("date",yearFormat.format(currentTime)+" "+monthFormat.format(currentTime)+" "+dayFormat.format(currentTime));

        //그메세지를 클릭했을때 불러올엑티비티를 설정함

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남


        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())

                .setSmallIcon(R.drawable.ic_note_add_black_24dp)
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle("아직 일기를 쓰지 않으셨네요.")
                .setContentText("지금 쓰러 가시겠어요?")
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        /*
        - setSmallIcon : 푸시 알림 왼쪽 그림
        - setTicker : 알람 발생시 잠깐 나오는 텍스트
        - setWhen : 푸시 알림 시간 miliSecond 단위 설정
        - setNumber : 확인하지 않은 알림 개수 표시 설정
        - setContetnTitle : 푸시 알림 상단 텍스트(제목)
        - setContentText : 푸시 알림 내용
        - setDefaults : 푸시 알림 발생시 진동, 사운드 등 설정
        - setContentIntent : 푸시 알림 터치시 실행할 작업 인텐트 설정
        - setAutoCancel : 푸시 알림 터치시 자동 삭제 설정
        - setOngoing : 푸시 알림을 지속적으로 띄울 것인지 설정
         */

        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());

            Calendar nextNotifyTime = Calendar.getInstance();


            //  Preference에 설정한 값 저장
            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            editor.apply();

            Toast.makeText(context.getApplicationContext(),"알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}