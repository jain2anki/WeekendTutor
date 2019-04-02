package com.chetan.wt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class forgotPassword extends AppCompatActivity {

    Intent Newpage;
    private int flag=0;
    FirebaseAuth mAuth;
//    private pro;
    private ProgressDialog pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        setTitle("Password Reset");
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        pb=new ProgressDialog(this);

        final Button signup = (Button) findViewById(R.id.reset);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;

                //Intent Newpage = new Intent(forgotPassword.this, );

                TextView mail = (TextView) findViewById(R.id.emailRe);
                String M = mail.getText().toString();

                if (TextUtils.isEmpty(M)) {
                    mail.setError("Enter E-Mail ID");
                    Toast.makeText(forgotPassword.this, "Please Fill all the details!!", Toast.LENGTH_LONG).show();
                    flag=1;
                }

                String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(M);

                if (matcher.matches()) {
                    mAuth.sendPasswordResetEmail(M).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override

                        public void onComplete(@NonNull Task<Void> task) {
                            TextView print = findViewById(R.id.print);
                            if(task.isSuccessful()) {
                                pb.setMessage("Sending...");
                                pb.show();
                                print.setText("Mail sent to your E-Mail");
                                Toast.makeText(forgotPassword.this, "Mail Sent!!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                print.setText("Error!!\nPlease check the email address");
                        }
                    });
                    //startActivity(Newpage);
                }
                else {
                    mail.setError("Enter a valid email id!!");
                    flag = 1;
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

