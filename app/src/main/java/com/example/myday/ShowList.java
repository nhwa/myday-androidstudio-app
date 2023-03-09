package com.example.myday;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowList extends AppCompatActivity {
    String CITY = "Jeju, KR"; // CITY 값을 제주로 설정
    String API = "09901ae9a39b1722a5b68a350f590903"; // API 키 발급받은

    ProgressBar progressBar = null;
    private TextView text_year,text_month,text_day;
    private TextView text_content;
    private TextView text_weather;
    private ImageButton back_btn;
    private ImageButton delete;
    private ImageButton download;
    private ImageView file_img,img_weather;
    private VideoView file_video;
    public String content,day,day3,date,temp,weather,filesort,filepath;
    public String filesort2,tokenID;
    private String encrypt_content,encrypt_weather;
    public static Activity showActivity;
    private LinearLayout download_vi;
    private ImageButton download_btn;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        showActivity = ShowList.this;

        delete = findViewById(R.id.delete);
        text_weather = findViewById(R.id.weather);
        text_content = findViewById(R.id.work_view);
        file_img = findViewById(R.id.file_img);

        //file_img 임시로 가려놓기
        file_img = findViewById(R.id.file_img);
        file_img.setVisibility(View.GONE);

        //videoview 임시로 가려놓기
        file_video = findViewById(R.id.file_video);
        file_video.setVisibility(View.GONE);

        text_year = findViewById(R.id.year);
        text_month = findViewById(R.id.month);
        text_day = findViewById(R.id.day);
        text_weather = findViewById(R.id.weather);
        img_weather = findViewById(R.id.weatherimg);

        //emoticon 받아오기
        Intent intent = getIntent(); /*데이터 수신*/
        date = intent.getExtras().getString("date");

        String[] array = date.split(" ");

        Log.d("weather",array[2]);

        if(array[2]=="18") {
        }
        text_year.setText(array[0]+"년");
        text_month.setText(array[1]+"월");
        text_day.setText(array[2]+"일의 일기");

        //db에서 가져오기
        tokenID = FirebaseInstanceId.getInstance().getToken();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference user_Diaries_Reference = firebaseDatabase.getReference("userdiaries").child(tokenID);

        Query user_Diaries_Query = user_Diaries_Reference.orderByChild("day").equalTo(date);
        user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    content = singleSnapshot.child("content").getValue(String.class);
                    weather = singleSnapshot.child("weather").getValue(String.class);
                    filesort = singleSnapshot.child("filesort").getValue(String.class);
                    getData();
                }
                try {
                    AES256Util AES256Util  = new AES256Util();

                    content = AES256Util.decrypt(content);
                    text_content.setText(content);
                    weather = AES256Util.decrypt(weather);
                    text_weather.setText(weather);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //삭제 버튼
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final FirebaseStorage storageref = storage.getInstance("gs://myday-b6bcc.appspot.com/");
                String filename;
                if(filesort.equals("video")){
                    filename = date.replaceAll(" ", "") + ".mp4";
                }
                else {
                    filename = date.replaceAll(" ", "") + ".jpg";
                }
                StorageReference pathRef = storageref.getReference().child(tokenID).child(filename);
                pathRef.delete();

                final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference user_Diaries_Reference = firebaseDatabase.getReference("userdiaries").child(tokenID);
                Query user_Diaries_Query = user_Diaries_Reference.orderByChild("day").equalTo(date);
                user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            singleSnapshot.getRef().removeValue();
                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                            main.putExtra("info","show");
                            MainActivity mainActivity =(MainActivity)MainActivity.mainActivity;
                            mainActivity.finish();
                            startActivity(main);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        //뒤로가기 버튼 동작
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        download_btn = findViewById(R.id.download);
        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout capture = (LinearLayout) findViewById(R.id.download_vi);//캡쳐할영역(프레임레이아웃)
                Bitmap bit = Bitmap.createBitmap(capture.getWidth(), capture.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bit);
                canvas.drawColor(Color.WHITE);
                capture.draw(canvas);

                //갤러리에 저장
                MediaStore.Images.Media.insertImage(getContentResolver(), bit, "일기", Environment.DIRECTORY_PICTURES);
                Toast.makeText(getApplicationContext(), "다운완료", Toast.LENGTH_LONG).show();
            }

        });
    }
    public void getData(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final FirebaseStorage storageref = storage.getInstance("gs://myday-b6bcc.appspot.com/");

        String filename;
        if(filesort.equals("video")) {
            filename = date.replaceAll(" ", "") + ".mp4";
            StorageReference pathRef = storageref.getReference().child(tokenID).child(filename);

            pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Success Case
                    progressBar = (ProgressBar) findViewById(R.id.progressbar);
                    file_video.setVideoURI(uri);
                    file_video.setVisibility(View.VISIBLE); //비디오뷰 출력하기
                    file_video.start();
                    progressBar.setVisibility(View.VISIBLE);
                    file_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.start();
                            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                @Override
                                public void onVideoSizeChanged(MediaPlayer mp,int arg1,int arg2) {
                                    // TODO Auto-generated method stub
                                    progressBar.setVisibility(View.GONE);
                                    mp.start();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail Case
                    e.printStackTrace();
                }
            });
        }
        else if(filesort.equals("image")){
            try {
                filename = date.replaceAll(" ", "") + ".jpg";
                StorageReference pathRef = storageref.getReference().child(tokenID).child(filename);
                final File Filename;
                Filename = File.createTempFile("images", "jpg");
                pathRef.getFile(Filename).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Success Case
                        Bitmap bitmapImage = BitmapFactory.decodeFile(Filename.getPath());
                        file_img.setVisibility(View.VISIBLE);
                        file_img.setImageBitmap(bitmapImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Fail Case
                        e.printStackTrace();
                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
