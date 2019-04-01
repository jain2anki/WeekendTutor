package com.chetan.wt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Welcome extends AppCompatActivity {
    int flag=0;
    SharedPreferences sp;
    private boolean login;
    private String userClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        Intent intent = getIntent();

        sp = getSharedPreferences("login",MODE_PRIVATE);
        login = sp.getBoolean("loginStatus",false);
        userClass = sp.getString("userClass", "");

        if(login == true){
            if(userClass.equals("Student"))
            {
                Intent intentStu = new Intent(Welcome.this, CourseList.class);
                intentStu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentStu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentStu);
                finish();
            }
            else if(userClass.equals("Tutor"))
            {
                Intent intentTut = new Intent(Welcome.this, ListOfCourseTutor.class);
                intentTut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentTut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentTut);
                finish();
            }
        }
    
    }

    public void login_tutor(View view){
        Intent intent = new Intent(this, loginTutor.class);
        startActivity(intent);
    }

    public void login_student(View view){
        Intent intent = new Intent(this, loginStudent.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(flag==1){
            finishAffinity();
        }
        else {
            Toast.makeText(this,"Press once again to exit",Toast.LENGTH_SHORT).show();
            flag++;
        }
    }

}

