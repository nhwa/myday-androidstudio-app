package com.example.myday;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingInterval extends AppCompatActivity {

    TextView textView;
    String[] items = {
            "      선택",
            "      10분",
            "      20분",
            "      30분",
            "      40분",
            "      50분"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_noti_interval);

        textView = findViewById(R.id.textView);
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(

                this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        textView.setText(items[position]);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        textView.setText("");
                    }
                });

        Button button2 = (Button) findViewById(R.id.button_cancel);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(SettingInterval.this,SettingNotification.class);
                startActivity(intent);
                finish();
            }

        });

        Button button = (Button) findViewById(R.id.button_OK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent=new Intent(SettingInterval.this,SettingNotification.class);
                startActivity(intent);
                finish();

                Toast.makeText(getApplicationContext(), "알람 간격을 30분으로 설정했습니다.", Toast.LENGTH_LONG).show();

            }

        });


        }
}


