package com.chetan.wt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginStudent extends Activity {

    private String Password;
    private String Mail;
    private Matcher mail_matcher;
    FirebaseAuth mAuth;
    private FirebaseAuth fa;
    private FirebaseUser User;
    private DatabaseReference refStud;
    private ProgressDialog pb;
    private String UserID = null;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_student);

        Intent intent = getIntent();


        final TextView mail = (TextView) findViewById(R.id.emailStu);
        final TextView pass = (TextView) findViewById(R.id.passStu);
        final Button signup =(Button)findViewById(R.id.signInStu);
        pb=new ProgressDialog(this);
        pb.setMessage("Logging In...");

        mAuth = FirebaseAuth.getInstance();
        fa = FirebaseAuth.getInstance();
        refStud = FirebaseDatabase.getInstance().getReference("Students");
        sp = getSharedPreferences("login",MODE_PRIVATE);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                pb.setMessage("Logging In...");
                pb.show();

                int flag = 0;

                Mail = mail.getText().toString();
                Password = pass.getText().toString();

                if(TextUtils.isEmpty(Mail)){
                    mail.setError("Please Enter Email ID");
                    Toast.makeText(loginStudent.this, "Please Fill all the details!!", Toast.LENGTH_LONG).show();
                    flag = 1;
                    pb.dismiss();
                }
                if(TextUtils.isEmpty(Password)){
                    pass.setError("Please Enter Password");
                    Toast.makeText(loginStudent.this, "Please Fill all the details!!", Toast.LENGTH_LONG).show();
                    flag = 1;
                    pb.dismiss();
                }

                String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                mail_matcher = pattern.matcher(Mail);

                if (mail_matcher.matches() && pass.length() >= 8 && flag == 0)
                {
                    pb.show();
//                    if(mAuth.getCurrentUser().)
                    mAuth.signInWithEmailAndPassword(Mail, Password).addOnCompleteListener(loginStudent.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                UserID = fa.getCurrentUser().getUid();

                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                refStud.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        pb.dismiss();
                                        if(dataSnapshot.exists()) {

                                            refStud.removeEventListener(this);
                                            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                                            sp.edit().putString("userClass", "Student").apply();
                                            sp.edit().putBoolean("loginStatus", true).apply();

                                            Intent intent = new Intent(loginStudent.this, CourseList.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            refStud.removeEventListener(this);
                                            //Log.d("TAG", UserClass);
                                            Toast.makeText(getApplicationContext(), "Account does not exist\nPlease re-check your credentials!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        pb.dismiss();
                                        refStud.removeEventListener(this);
                                        Toast.makeText(getApplicationContext(), "Error in Login!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else
                            {
                                pb.dismiss();

                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(getApplicationContext(), "Error in Login!", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(), "Please check your Internet Connectivity!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    pb.dismiss();
                    if (!mail_matcher.matches()) {
                        mail.setError("invalid e-mail");
                        Toast.makeText(loginStudent.this, "Invalid E-Mail ID!!", Toast.LENGTH_SHORT).show();
                    } else if (Password.length() <= 8) {
                        pass.setError("invalid password");
                        Toast.makeText(loginStudent.this, "Invalid Password!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(loginStudent.this, "Invalid Email/Password!!", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    public void forgot_pass(View view){
        Intent intent = new Intent(loginStudent.this, forgotPassword.class);
        startActivity(intent);
    }

    public void register_student(View view){
        Intent intent = new Intent(loginStudent.this, registerStudent.class);
        startActivity(intent);
    }

}




