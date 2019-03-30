package com.chetan.wt;

import android.content.Intent;
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
    public static int loginState = 0;
    private String id;
    private FirebaseAuth fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        Intent intent = getIntent();

        FirebaseUser cuser = fa.getCurrentUser();
        id = cuser.getUid();

        if(loginState == 1 && !id.equals(null))
        {
            Intent stu = new Intent(Welcome.this, CourseList.class);
            startActivity(stu);
        }
        else if(loginState == 2 && id != null)
        {
            Intent tut = new Intent(Welcome.this, ListOfCourseTutor.class);
            startActivity(tut);
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

