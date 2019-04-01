package com.chetan.wt;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class feedback_activity extends AppCompatActivity {
    float value;
    RatingBar rb;
    DatabaseReference database_feedback;
    String message1;
    EditText editText;
    EditText feedback;
    Spinner spinnerusers;

    private boolean isValid(String message1) {
        if (message1.isEmpty()) {
            editText.setError("field cant be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(message1).matches()) {
            editText.setError("enter a valid email adress");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        setTitle("Feedback");
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.user_email);
        rb = (RatingBar) findViewById(R.id.ratingBar);
        feedback = findViewById(R.id.user_feedback);
        spinnerusers = (Spinner) findViewById(R.id.spinner_1);
        Button button = (Button) findViewById(R.id.buttonsave);
        database_feedback = FirebaseDatabase.getInstance().getReference();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    message1 = editText.getText().toString();
                    boolean x = isValid(message1);
                    if(x) {
                        float value = rb.getRating();
                        String message2 = feedback.getText().toString();
                        String user = spinnerusers.getSelectedItem().toString();
                        feedback_user feedback_user_message = new feedback_user(message1, user, value, message2);
                        String id = database_feedback.push().getKey();
                        database_feedback.child("feedback").child(id).setValue(feedback_user_message);
                        database_feedback.child("feedback").child(id).setValue(feedback_user_message);
                        Intent i = new Intent(getApplicationContext(), pop_feedback.class);
                        startActivity(i);
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
