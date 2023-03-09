package com.example.myday;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static java.lang.System.out;

public class OnDraw extends AppCompatActivity {


    private Button cancel_btn;
    private ImageButton back_draw_btn;
    private ImageButton front_draw_btn;
    private Button complete_btn;
    private Button black_pen, red_pen, blue_pen;
    private ImageButton eraser_btn;
    private SeekBar seekBar;
    private TextView text;
    private MyView m;

    class Point {
        float x;
        float y;
        int pValue;
        int color;
        int size;

        public Point(float x, float y, int value, int color ,int size) {
            this.x = x;
            this.y = y;
            pValue = value;
            this.color = color;
            this.size = size;
        }
    }

    class MyView extends View {
        Paint p;
        ArrayList<Point> arrP;
        final int START = 0;
        final int MOVE = 1;
        int value, color, size;

        Bitmap bit;

        public MyView(Context context) {
            super(context);
            p = new Paint(Paint.ANTI_ALIAS_FLAG);
            arrP = new ArrayList<Point>();
            color = Color.BLACK;
            bit = null;
            size = 3;
        }

        @Override
        protected void onDraw(Canvas canvas) {

            canvas.drawColor(Color.WHITE);

            for (int i = 0; i < arrP.size(); i++) {
                p.setColor(arrP.get(i).color);
                p.setStrokeWidth(arrP.get(i).size);
                if (arrP.get(i).pValue == MOVE)
                    canvas.drawLine(arrP.get(i - 1).x, arrP.get(i - 1).y, arrP.get(i).x, arrP.get(i).y, p);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if(event.getAction() ==  MotionEvent.ACTION_DOWN) {
                value = START;
                arrP.add(new Point(event.getX(), event.getY(), value, color, size));
                return true;
            }
            if(event.getAction() == MotionEvent.ACTION_MOVE) {
                value = MOVE;
                arrP.add(new Point(event.getX(), event.getY(), value, color, size));
                invalidate();
                return true;
            }
            return false;
        }
    }

    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_draw);

        m = new MyView(this);
        ll = findViewById(R.id.drawview);
        ll.addView(m);

        text = (TextView) findViewById(R.id.pensize_text);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(mSeekBar);
        seekBar.setMax(10);
        seekBar.setProgress(3);


        black_pen = findViewById(R.id.black_pen);
        black_pen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                m.color = Color.BLACK;
            }
        });

        blue_pen = findViewById(R.id.blue_pen);
        blue_pen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                m.color = Color.BLUE;
            }
        });


        red_pen = findViewById(R.id.red_pen);
        red_pen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                m.color = Color.RED;
            }
        });

        //지우기 버튼 눌렸을때
        eraser_btn = findViewById(R.id.eraser_btn);
        eraser_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m.color = Color.WHITE;
            }
        });


        //취소 버튼 동작
        cancel_btn = findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WorkActivity.class);
                startActivity(intent);
            }
        });


        //뒤 버튼 동작
        back_draw_btn = findViewById(R.id.back_draw_btn);
        back_draw_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(m.arrP.size()>0)
                    m.arrP.remove(m.arrP.size()-1);
                m.invalidate();
            }
        });

        //앞 버튼 동작
//        front_draw_btn = findViewById(R.id.front_draw_btn);
//        front_draw_btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });

        //완료 버튼 동작(그림그린거 일기작성화면에 띄우기)
        complete_btn = findViewById(R.id.complete_btn);
        complete_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                m.setDrawingCacheEnabled(true);
                Bitmap bit = Bitmap.createBitmap(m.getDrawingCache());
                m.setDrawingCacheEnabled(false);

                File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(new File(file, "my.png"));
                    bit.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();

                } catch (Exception e) {

                    Toast.makeText(OnDraw.this, "file error", Toast.LENGTH_SHORT).show();}

                Intent intent = new Intent(OnDraw.this, WorkActivity.class);
                setResult(RESULT_OK, intent);
                finish();

            }

        });

    }

    SeekBar.OnSeekBarChangeListener mSeekBar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            m.size = progress;
            text.setText("PenSize" + progress);
            m.invalidate();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }


    };

}

