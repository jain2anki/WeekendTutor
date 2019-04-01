package com.chetan.wt;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentNotifications extends AppCompatActivity {
    TutorNotification notification = new TutorNotification();
    DatabaseReference reff;
    String Tid;
    //private FirebaseAuth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_notifications);
        setTitle("Notifications");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Tid = user.getUid();
        final ListView listView = (ListView) findViewById(R.id.ListView);
        final ArrayList<String> myArrayList = new ArrayList<>();
        reff = FirebaseDatabase.getInstance().getReference("Student Notifications").child(Tid);

        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myArrayList)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView text = (TextView) view.findViewById(android.R.id.text1);
            text.setTextColor(Color.WHITE);
            return view;
        }
        };
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    notification = ds.getValue(TutorNotification.class);
                    myArrayList.add("\nName:" + notification.getStudent_name() +"\n"+ notification.getCourse_name() + "\n"+ notification.getDate() + "\n");
                }
                listView.setAdapter(myArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button clear_notification = findViewById(R.id.clear_notifications);
        clear_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentNotifications.this);

                builder.setMessage("Are you sure you want to clear the notifictaions?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference del = FirebaseDatabase.getInstance().getReference("Tutor Notifications").child(Tid);
                        del.setValue(null);
                        Toast.makeText(getApplicationContext(),"Cleared Succesfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("Cancel",null);
                AlertDialog alert = builder.create();
                alert.show();
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
}
