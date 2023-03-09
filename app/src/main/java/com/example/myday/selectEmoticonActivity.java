package com.example.myday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class selectEmoticonActivity extends MainActivity {
    private String date;
    public static Activity SelectEmoticonActivity;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_emoticon);
        SelectEmoticonActivity = selectEmoticonActivity.this;
    }

    public void onClick(View view) {

        Intent write_diary = new Intent(getApplicationContext(), WorkActivity.class);
        Intent intent = getIntent(); /*데이터 수신*/
        date = intent.getExtras().getString("date");
        Log.d("date",date);
        Log.d("왔다가 감","왔다가 감");

        switch (view.getId()){
            case R.id.button_angry:
                // smile버튼 눌렀을때
                write_diary.putExtra("emoticon","angry");
                write_diary.putExtra("date",date);
                startActivity(write_diary);
                break;

            case R.id.button_happy:
                // laughing버튼 눌렀을때
                write_diary.putExtra("emoticon","happy");
                write_diary.putExtra("date",date);
                startActivity(write_diary);
                break;

            case R.id.button_tired:
                // disappointment버튼 눌렀을때
                write_diary.putExtra("emoticon","tired");
                write_diary.putExtra("date",date);
                startActivity(write_diary);
                break;

            case R.id.button_sad:
                // cold버튼 눌렀을때
                write_diary.putExtra("emoticon","sad");
                write_diary.putExtra("date",date);
                startActivity(write_diary);
                break;

            case R.id.button_confused:
                // crying버튼 눌렀을때
                write_diary.putExtra("emoticon","confused");
                write_diary.putExtra("date",date);
                startActivity(write_diary);
                break;

            case R.id.button_scared:
                // angry버튼 눌렀을때
                write_diary.putExtra("emoticon","scared");
                write_diary.putExtra("date",date);
                startActivity(write_diary);
                break;
        }
    }

    // 뒤로가기 버튼 클릭했을 때 홈으로 이동하기
    @Override
    public void onBackPressed() {
        finish();
    }

}