package com.example.myday;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myday.applock.core.BaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Button;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class ExecutionActivity extends BaseActivity {

    //firebase 연결
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //유저 등록
//        String tokenID = FirebaseInstanceId.getInstance().getToken();
//        Users users = new Users(tokenID);
//        databaseReference.child("users").child(tokenID).push().setValue(tokenID);
//        databaseReference.child("Users").child(tokenID).setValue(users);

        Intent main = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(main);
    }

    public void User_Register(){
    }

}
