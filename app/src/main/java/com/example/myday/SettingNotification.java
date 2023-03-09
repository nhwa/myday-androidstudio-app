package com.example.myday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SettingNotification extends AppCompatActivity{
    private Button btSetTime;
    private Button btSetInterval;
    private ImageButton back_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);
        SettingTime settingTime = (SettingTime) SettingTime.SettingTime;

        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.hasExtra("check")) {
            String check = intent.getExtras().getString("check");
            if(check=="true")
                Log.d("check",check);
                settingTime.finish();
        }

        btSetTime = findViewById(R.id.bt_set_time);
        btSetInterval = findViewById(R.id.bt_set_interval);
        //btSetInterval.setText(R.string.);

        //뒤로가기 버튼 동작
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainSetting.class);
                startActivity(intent);
            }
        });

        btSetTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingTime.class);
                startActivity(intent);
            }
        });

        btSetInterval.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingInterval.class);
                startActivity(intent);
                finish();
            }
        });

    }

    //뒤로가기
    @Override
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), MainSetting.class);
        startActivity(main);
        finish();
    }

}
