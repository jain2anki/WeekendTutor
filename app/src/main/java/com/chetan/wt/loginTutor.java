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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginTutor extends Activity {

    private String Password;
    private String Mail;
    private Matcher mail_matcher;
    FirebaseAuth mAuth, fa;
    private DatabaseReference refStud, refTut;
    private String UserID = "", UserClass;
    Button bu;
    private ProgressDialog pb;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_tutor);

        Intent intent = getIntent();

        final TextView mail = (TextView) findViewById(R.id.email);
        final TextView pass = (TextView) findViewById(R.id.pass);
        final Button signup =(Button)findViewById(R.id.signIn);
        bu=(Button)findViewById(R.id.button4);
        pb=new ProgressDialog(this);
        pb.setMessage("Logging In...");

        mAuth = FirebaseAuth.getInstance();
        fa = FirebaseAuth.getInstance();
        refTut = FirebaseDatabase.getInstance().getReference("users");
        sp = getSharedPreferences("login",MODE_PRIVATE);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;

                pb.setMessage("Logging In...");
                pb.show();

                Mail = mail.getText().toString();
                Password = pass.getText().toString();

                if(TextUtils.isEmpty(Mail)){
                    mail.setError("Please Enter Email ID");
                    Toast.makeText(loginTutor.this, "Please Fill all the details!!", Toast.LENGTH_LONG).show();
                    flag = 1;
                    pb.dismiss();
                }

                if(TextUtils.isEmpty(Password)){
                    pass.setError("Please Enter Password");
                    Toast.makeText(loginTutor.this, "Please Fill all the details!!", Toast.LENGTH_LONG).show();
                    flag = 1;
                    pb.dismiss();

                }

                String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                mail_matcher = pattern.matcher(Mail);

                if (mail_matcher.matches() && pass.length() >= 8 && flag == 0) {
                    pb.show();
                    //if(mAuth.getCurrentUser().)
                    mAuth.signInWithEmailAndPassword(Mail, Password)
                            .addOnCompleteListener(loginTutor.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                UserID = fa.getCurrentUser().getUid();
                                
                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                refTut.child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        pb.dismiss();
                                        if(dataSnapshot.exists()){

                                            refTut.removeEventListener(this);
                                            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                                            sp.edit().putString("userClass", "Tutor").apply();
                                            sp.edit().putBoolean("loginStatus", true).apply();

                                            Intent intent = new Intent(loginTutor.this, ListOfCourseTutor.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();

                                        }
                                        else{
                                            refTut.removeEventListener(this);
                                            Toast.makeText(getApplicationContext(), "Account does not exist\nPlease re-check your credentials!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        pb.dismiss();
                                        refTut.removeEventListener(this);
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
                        Toast.makeText(loginTutor.this, "Invalid E-Mail ID!!", Toast.LENGTH_SHORT).show();
                    } else if (Password.length() <= 8) {
                        pass.setError("password not long enough");
                        Toast.makeText(loginTutor.this, "Invalid Password!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(loginTutor.this, "Invalid Email/Password!!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    public void forgot_pass(View view){
        Intent i = new Intent(loginTutor.this, forgotPassword.class);
        startActivity(i);
    }

    public void register_student(View view) {
        Intent intent = new Intent(loginTutor.this, TutorRegistration.class);
        startActivity(intent);
    }
}

