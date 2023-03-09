package com.example.myday;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import android.os.AsyncTask;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.O)

// ★★★★★★메인 캘린더랑 플로팅버튼(메뉴이동부분)★★★★★★
public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnDateSelectedListener {
    public static Activity mainActivity;
    // 플로팅버튼1, 설정버튼, 일기작성버튼/플로팅버튼 열리고 닫히는거, 플로팅버튼 상태
    FloatingActionButton fab, fab2, fab3;
    Animation fab_open, fab_close;
    Boolean openFlag = false;

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;
    @BindView(R.id.textView)
    TextView textView;

    int i=0;
    int size=0;

    //날짜 형식
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy MM dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = MainActivity.this;
        selectEmoticonActivity SelectEmoticonActivity = (selectEmoticonActivity)selectEmoticonActivity.SelectEmoticonActivity;
        WorkActivity workActivity =(WorkActivity)WorkActivity.workActivity;
        ShowList showList =(ShowList) ShowList.showActivity;

        Intent intent = getIntent(); /*데이터 수신*/
        if (intent.hasExtra("info")) {
            String info = intent.getExtras().getString("info");
            if(info=="true")
            workActivity.finish();
            if(info=="show") {
                ActivityCompat.finishAffinity(this);
            }
        }
        ButterKnife.bind(this);
        //선택된 날짜 확인용
        widget.setOnDateChangedListener(this);

        //fab = 메뉴버튼, fab2 = 설정,메뉴버튼, fab3 = 작성버튼
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab = findViewById(R.id.fab);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);

        fab2.startAnimation(fab_close);
        fab3.startAnimation(fab_close);
        fab2.setClickable(false);
        fab3.setClickable(false);

        fab.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        widget.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator()
        );

        String tokenID = FirebaseInstanceId.getInstance().getToken();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference user_Diaries_Reference = firebaseDatabase.getReference("userdiaries").child(tokenID);

        fetchData(user_Diaries_Reference);
    }


    //날짜 클릭시 해당하는데 데이터 뿌려주기!
    @Override
    public void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected) {
        if (selected){
            Intent showList = new Intent(getApplicationContext(), ShowList.class);
            showList.putExtra("date",FORMATTER.format(LocalDateTime.ofInstant(date.getDate().toInstant(), ZoneId.systemDefault())));
            startActivity(showList);
            // firebase 연결
            String tokenID = FirebaseInstanceId.getInstance().getToken();
            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference user_Diaries_Reference = firebaseDatabase.getReference("userdiaries").child(tokenID);
            final Query query = user_Diaries_Reference.orderByChild("day").equalTo(FORMATTER.format(LocalDateTime.ofInstant(date.getDate().toInstant(), ZoneId.systemDefault())));
                }
    }
    public void fetchData(DatabaseReference user_Diaries_Reference) {
        //tokenID 하위값 불러오기

        String[] emoticon = {"angry","happy","tired","sad","confused","scared"};

        ArrayList<String> angrydays = new ArrayList<>();
        ArrayList<String> happydays = new ArrayList<>();
        ArrayList<String> tireddays = new ArrayList<>();
        ArrayList<String> saddays = new ArrayList<>();
        ArrayList<String> confuseddays = new ArrayList<>();
        ArrayList<String> scareddays = new ArrayList<>();

        Query user_Diaries_Query = user_Diaries_Reference.orderByChild("emoticon").equalTo(emoticon[0]);
        user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String temp = singleSnapshot.child("day").getValue(String.class);
                    angrydays.add(temp);
                }
                String[] angryResult = new String[angrydays.size()];
                size = 0;
                for (String temp2 : angrydays) {
                    angryResult[size++] = temp2;
                }
                new AngryApiSimulator(angryResult).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        user_Diaries_Query = user_Diaries_Reference.orderByChild("emoticon").equalTo(emoticon[1]);
        user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String temp = singleSnapshot.child("day").getValue(String.class);
                    happydays.add(temp);
                }
                String[] happyResult = new String[happydays.size()];
                size = 0;
                for (String temp2 : happydays) {
                    happyResult[size++] = temp2;
                }
                new HappyApiSimulator(happyResult).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        user_Diaries_Query = user_Diaries_Reference.orderByChild("emoticon").equalTo(emoticon[2]);
        user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String temp = singleSnapshot.child("day").getValue(String.class);
                    tireddays.add(temp);

                }
                String[] tiredResult = new String[tireddays.size()];
                size = 0;
                for (String temp2 : tireddays) {
                    tiredResult[size++] = temp2;
                }
                new TiredApiSimulator(tiredResult).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        user_Diaries_Query = user_Diaries_Reference.orderByChild("emoticon").equalTo(emoticon[3]);
        user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String temp = singleSnapshot.child("day").getValue(String.class);
                    saddays.add(temp);
                }
                String[] sadResult = new String[saddays.size()];
                size = 0;
                for (String temp2 : saddays) {
                    sadResult[size++] = temp2;
                }
                new SadApiSimulator(sadResult).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        user_Diaries_Query = user_Diaries_Reference.orderByChild("emoticon").equalTo(emoticon[4]);
        user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String temp = singleSnapshot.child("day").getValue(String.class);
                    confuseddays.add(temp);
                }
                String[] confusedResult = new String[confuseddays.size()];
                size = 0;
                for (String temp2 : confuseddays) {
                    confusedResult[size++] = temp2;
                }
                new ConfusedApiSimulator(confusedResult).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        user_Diaries_Query = user_Diaries_Reference.orderByChild("emoticon").equalTo(emoticon[5]);
        user_Diaries_Query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String temp = singleSnapshot.child("day").getValue(String.class);
                    scareddays.add(temp);
                }
                String[] scaredResult = new String[scareddays.size()];
                size = 0;
                for (String temp2 : scareddays) {
                    scaredResult[size++] = temp2;
                }
                new ScaredApiSimulator(scaredResult).executeOnExecutor(Executors.newSingleThreadExecutor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //플로팅버튼 닫혔다 열렸다하는 애니메이션
    public void anim() {
        if (openFlag) {
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab2.setClickable(false);
            fab3.setClickable(false);
            openFlag = false;
        } else {
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab2.setClickable(true);
            fab3.setClickable(true);
            openFlag = true;
        }
    }

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab2:
                anim();
                Intent settings = new Intent(getApplicationContext(), MainSetting.class);
                startActivity(settings);
                break;
            case R.id.fab3:
                anim();
            //날짜 받아오기

            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

            Intent select_emoticon = new Intent(getApplicationContext(), selectEmoticonActivity.class);
            select_emoticon.putExtra("date",yearFormat.format(currentTime)+" "+monthFormat.format(currentTime)+" "+dayFormat.format(currentTime));
            startActivity(select_emoticon);
            break;
        }
    }

    //뒤로가기 버튼을 두번 연속으로 눌러야 종료되게끔 하는 메소드
    private long time=0;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    ////////////////////////////////////////////////배열 잘라서 날짜마다 이벤트 넣어주는 부분////////////////////////////////////////////////////
    ///////////////////화난 이모티콘
    private class AngryApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;
        AngryApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){

                String[] time = Time_Result[i].split(" ");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            widget.addDecorator(new AngryEventDecorator(calendarDays,MainActivity.this));
        }
    }


    ///////////////////웃는 이모티콘
    public class HappyApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;

        HappyApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){

                String[] time = Time_Result[i].split(" ");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            widget.addDecorator(new HappyEventDecorator(calendarDays,MainActivity.this));
        }
    }

    ///////////////////슬픈 이모티콘
    public class SadApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;

        SadApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){

                String[] time = Time_Result[i].split(" ");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            widget.addDecorator(new SadEventDecorator(calendarDays,MainActivity.this));
        }
    }

    ///////////////////졸린 이모티콘
    public class TiredApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;
        TiredApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){

                String[] time = Time_Result[i].split(" ");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            if (isFinishing()) {
                return;
            }
            widget.addDecorator(new TiredEventDecorator(calendarDays,MainActivity.this));
        }
    }

    ///////////////////혼란스러운 이모티콘
    public class ConfusedApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;
        ConfusedApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                String[] time = Time_Result[i].split(" ");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            if (isFinishing()) {
                return;
            }
            widget.addDecorator(new ConfusedEventDecorator(calendarDays,MainActivity.this));
        }
    }
    ///////////////////무서운 이모티콘
    public class ScaredApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;
        ScaredApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){

                String[] time = Time_Result[i].split(" ");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                calendar.set(year,month-1,dayy);
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            if (isFinishing()) {
                return;
            }
            widget.addDecorator(new ScaredEventDecorator(calendarDays,MainActivity.this));
        }
    }
}
