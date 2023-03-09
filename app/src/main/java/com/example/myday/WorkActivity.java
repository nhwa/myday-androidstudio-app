package com.example.myday;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androdocs.httprequest.HttpRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;


public class WorkActivity extends AppCompatActivity {

    String CITY = "Jeju, KR"; // CITY 값을 제주로 설정
    String API = "09901ae9a39b1722a5b68a350f590903"; // API 키 발급받은

    private TextView text_year,text_month,text_day;
    private EditText text_content;
    private TextView text_weather;
    private ImageButton back_btn;
    private ImageButton file_btn;
    private Button save_btn;
    private ImageView file_img,img_weather;
    private VideoView file_video;

    public String content;
    private String day,day2,month,year,emoticon,weather,weatherimg,date,fileuri;
    private String encrypt_day,encrypt_content,encrypt_weather,encrypt_emoticon,encrypt_filesort,encrypt_fileuri;
    private int num;

    public static Activity workActivity;
    private Uri filePath,cameraUri;
    private String filesort;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        workActivity = WorkActivity.this;
        //weather
        new weatherTask().execute();

        MainActivity mainActivity = (MainActivity)MainActivity.mainActivity;
        mainActivity.finish();

        selectEmoticonActivity SelectEmoticonActivity = (selectEmoticonActivity)selectEmoticonActivity.SelectEmoticonActivity;
        SelectEmoticonActivity.finish();

        //file_img 임시로 가려놓기
        file_img = findViewById(R.id.file_img);
        file_img.setVisibility(View.GONE);

        //videoview 임시로 가려놓기
        file_video = findViewById(R.id.file_video);
        file_video.setVisibility(View.GONE);

        //emoticon 및 날짜 받아오기
        Intent intent = getIntent(); /*데이터 수신*/
        date = intent.getExtras().getString("date");
        emoticon = intent.getExtras().getString("emoticon");

        //뒤로가기 버튼 동작
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.putExtra("info","true");
                startActivity(main);
            }
        });

        day2 = date;
        String[] array = date.split(" ");
        year = array[0];
        month = array[1];
        day = array[2];

        text_year = findViewById(R.id.year);
        text_month = findViewById(R.id.month);
        text_day = findViewById(R.id.day);

        text_year.setText(year);
        text_month.setText(month+"월");
        text_day.setText(day);

        text_weather = findViewById(R.id.weather);
        weather="23°C";
        text_weather.setText(weather);

        //파일버튼 동작
        file_btn = findViewById(R.id.fileBtn);
        file_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(WorkActivity.this, file_btn);
                popup.getMenuInflater().inflate(R.menu.file_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.gallery:
                                checkSelfPermission();
                                Intent intent = new Intent(); //기기 기본 갤러리 접근
                                intent.setType(MediaStore.Images.Media.CONTENT_TYPE); //구글 갤러리 접근
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, 101);
                                break;

                            case R.id.camera:
                                Intent takepictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takepictureintent, 0);
                                break;

                            case R.id.movie:
                                checkSelfPermission();
                                Intent movieintent = new Intent(); //기기 기본 갤러리 접근
                                movieintent.setType(MediaStore.Images.Media.CONTENT_TYPE); //구글 갤러리 접근
                                movieintent.setType("video/*");
                                movieintent.setAction(Intent.ACTION_GET_CONTENT);
                                movieintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(movieintent, 102);
                                break;

                            case R.id.draw:
                                Toast.makeText(getApplication(), "그리기모드", Toast.LENGTH_SHORT).show();
                                Intent drawintent = new Intent(WorkActivity.this, OnDraw.class);
                                startActivityForResult(drawintent, 1234);
                                break;
                        }
                        return false;

                    }
                });
                popup.show();
            }
        });

//        저장버튼 구현
        save_btn =  findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            //firebase 연결
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference();
            @Override
            public void onClick(View view) {
                text_content = (EditText)findViewById(R.id.work_view);
                content = text_content.getText().toString();
                if(filePath!=null){
                    fileuri= filePath.toString();
                }
                //암호화
                try {
                    AES256Util AES256Util  = new AES256Util();
                    encrypt_day = AES256Util.encrypt(day2);
                    encrypt_content = AES256Util.encrypt(content);
                    encrypt_weather = AES256Util.encrypt(weather);
                    encrypt_emoticon = AES256Util.encrypt(emoticon);
                    encrypt_filesort = AES256Util.encrypt(filesort);
                    encrypt_fileuri = AES256Util.encrypt(fileuri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //db에 저장
                String tokenID = FirebaseInstanceId.getInstance().getToken();
                // Firebase Database 에 등록된 Key 값

                if (!TextUtils.isEmpty(tokenID)) {
//                    UserDiaries userDiaries = new UserDiaries(day2,content,weather,emoticon,fileuri,filesort);
                    UserDiaries userDiaries = new UserDiaries(encrypt_day,encrypt_content,encrypt_weather,encrypt_emoticon,encrypt_fileuri,encrypt_filesort);
                    databaseReference.child("userdiaries").child(tokenID).push().setValue(userDiaries);
                }
                //storage에 저장
                if(filePath!=null) {
                    uploadFile();
                }
                else{
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    main.putExtra("info","true");
                    startActivity(main);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //권한을 허용 했을 경우
        if(requestCode == 1){
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("WorkActivity","권한 허용 : " + permissions[i]); }
            }
        }
    }

    public void checkSelfPermission() {
        String temp = "";

        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }
        else {
            // 모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }
    //이미지 첨부 기능 함수 ************다시 수정 요망
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 101 && resultCode == RESULT_OK){
            try{
                filePath = data.getData();
                filesort="image";
                InputStream is = getContentResolver().openInputStream(filePath);
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                file_img.setVisibility(View.VISIBLE);
                file_img.setImageBitmap(bm);
            }
            catch (Exception e){ e.printStackTrace();
            }
        }
        //동영상첨부 수정
        else if(requestCode == 102 && resultCode == RESULT_OK ){
            Uri fileUri = data.getData();
            filesort="video";
            filePath = fileUri;
            file_video.setVideoURI(fileUri);
            file_video.setVisibility(View.VISIBLE); //비디오뷰 출력하기
            file_video.start();
        }

        else if(requestCode == 101 && resultCode == RESULT_CANCELED){
            Toast.makeText(this,"취소", Toast.LENGTH_SHORT).show();
        }

        //사진촬영 이미지 띄우기
        else if(requestCode == 0  && data.hasExtra("data")){
//            filePath =(Uri) data.getExtras().get("data");
            filesort="image";
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500,500, true);
            String imageSaveUri = MediaStore.Images.Media.insertImage(getContentResolver(),resized,"사진저장","저장되었습니다.");
            Uri uri = Uri.parse(imageSaveUri);
            filePath = uri;
            file_img.setVisibility(View.VISIBLE);
            file_img.setImageURI(uri);
        }

        //그림판 띄우기
        else if(requestCode == 1234 && resultCode == RESULT_OK){
            try{
                filesort="image";
                File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                Uri uri = Uri.parse("file:///" + file + "/my.png");
                filePath = uri;
                file_img.setVisibility(View.VISIBLE);
                file_img.setImageURI(uri);
                Toast.makeText(getApplicationContext(), "load ok", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e){
                Toast.makeText(getApplicationContext(), "load error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            String filename;
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("등록중.....");
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            //사용자별 token값 받아오기
            String tokenID = FirebaseInstanceId.getInstance().getToken();

            if(filesort=="video"){
                filename = day2.replaceAll(" ", "") + ".mp4";
            }
            else{
                filename = day2.replaceAll(" ", "") + ".jpg";
            }

            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://myday-b6bcc.appspot.com/").child(tokenID).child(filename);
            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "일기 등록!", Toast.LENGTH_SHORT).show();

                            Intent main = new Intent(getApplicationContext(), MainActivity.class);
                            main.putExtra("info","true");
                            startActivity(main);
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressBar.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        } else {
//            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
    }
    public void setProgressDialog() {
        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        main.putExtra("info","true");
        startActivity(main);
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        // jeju의 json data 받아오기
        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                weather = main.getString("temp") + "°C";
            } catch (JSONException e) { // 오류 시 에러메세지
            }

        }
    }

}






