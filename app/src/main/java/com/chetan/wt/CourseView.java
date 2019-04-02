package com.chetan.wt;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/*
 * class for update courses.
 *
 * @auther chetan
 *
 * */

public class CourseView extends AppCompatActivity {
    EditText coursename,tutorName,Venue,Time,Duration,courseAgenda,course_date,price;
    String course_name,tutor_name,venue,time,duration,courseagenda,coursedate,pr;
    Button Submit,delete;
    String id,TId,c,d,SID;
    DatabaseReference notify;
    DatabaseReference databaseReference2;
    FirebaseDatabase databaseReference3;
    TutorNotification notification = new TutorNotification();
    Button b1;
    private FusedLocationProviderClient mFusedLocationClient;
    Double latittude,longitude;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SID=user.getUid();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        setTitle("Edit Details");
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coursename = (EditText)findViewById(R.id.courseName);
        c=getIntent().getStringExtra("Course_name");
        coursename.setText(c);


        id = getIntent().getStringExtra("CourseID");
//        tutorName = (EditText)findViewById(R.id.tutorName);
//        tutorName.setText(getIntent().getStringExtra("Tutor_name"));

        Venue = (EditText)findViewById(R.id.Venue);
        Venue.setText(getIntent().getStringExtra("Venue"));

        Time = (EditText)findViewById(R.id.Time);
        Time.setText(getIntent().getStringExtra("Time"));

        Duration = (EditText)findViewById(R.id.Duration);
        Duration.setText(getIntent().getStringExtra("Duration"));

        courseAgenda = (EditText)this.findViewById(R.id.courseAgenda);
        courseAgenda.setText(getIntent().getStringExtra("agenda"));

        course_date = (EditText)findViewById(R.id.course_date);
        d=getIntent().getStringExtra("date");
        coursename.setText(d);

        price = findViewById(R.id.price);
        price.setText(getIntent().getStringExtra("price"));

        TId = getIntent().getStringExtra("TId");


        // final long id = getIntent().getLongExtra("id",0);

        Submit = (Button)findViewById(R.id.Submit);
        delete = (Button)findViewById(R.id.delete);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                course_name = coursename.getText().toString();
                tutor_name = getIntent().getStringExtra("Tutor_name");
                venue = Venue.getText().toString();
                time = Time.getText().toString();
                duration = Duration.getText().toString().trim();
                courseagenda = courseAgenda.getText().toString();
                coursedate = course_date.getText().toString();
                pr = price.getText().toString();

                try {
                    updateCourse(id,course_name,courseagenda,coursedate,time,venue,duration,tutor_name,latittude,longitude);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCourse(id);
            }
        });
    }

    public void updateCourse(String id,String course_name,String courseagenda,String coursedate,String time,String venue,String duration,String tutor_name,Double latittude,Double longitude) throws ParseException {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tutor Courses").child(id);

        int flag=1;
        if(course_name.equalsIgnoreCase("")){
            coursename.setError("Course name is required field");
            flag=0;
        }

//        if(tutor_name.equalsIgnoreCase("")){
//            tutorName.setError("Enter tutor name");
//            flag=0;
//        }


        if (venue.equalsIgnoreCase("")){
            Venue.setError("This field can't be empty");
            flag=0;
        }

        if (Integer.parseInt(pr)<0){
            price.setError("Price cannot be negative");
        }

        String[] time_format = time.split(":");

        if(time_format.length!=2){
            Time.setError("Please enter time in correct format");
            flag=0;
        }

        if (time_format.length==2) {
            if ((Integer.parseInt(time_format[0]) < 0) || Integer.parseInt(time_format[0]) > 23 || Integer.parseInt(time_format[1])<0 || Integer.parseInt(time_format[1])>59) {
                Time.setError("Enter correct time");
                flag=0;
            }
        }

        String[] duration_format = duration.split(":");

        if (duration_format.length!=2){
            Log.d("Length",duration_format[0]);
            Duration.setError("Please enter duartion in correct format");
            flag=0;
        }
        if (duration_format.length==2){
            if (Integer.parseInt(duration_format[0])<0 || Integer.parseInt(duration_format[0])>23 || Integer.parseInt(duration_format[1])<0 || Integer.parseInt(duration_format[1])>59){
                Duration.setError("Enter correct duration");
                flag=0;
            }
        }


        if (courseagenda.equalsIgnoreCase("")){
            courseAgenda.setError("This field can't be empty");
            flag=0;
        }

        String[] date_format = coursedate.split("/");

        if (date_format.length!=3){
            course_date.setError("Enter correct date");
            flag=0;
        }

        if (date_format.length==3){

            SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
            sdfrmt.setLenient(false);
            /* Create Date object
             * parse the string into date
             */
            try
            {
                Date javaDate = sdfrmt.parse(coursedate);
                //System.out.println(strDate+" is valid date format");

            }
            /* Date format is invalid */
            catch (ParseException e)
            {
                //System.out.println(strDate+" is Invalid Date format");
                //return false;
                course_date.setError("Enter valid date");
                flag=0;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date d1 = new Date();
            Date d2 = formatter.parse(coursedate);
            if(d1.compareTo(d2)>=0){
                course_date.setError("Can't enter past/today's date");
                flag=0;
            }


        }
        if(flag==1) {

            Course course = new Course(course_name,courseagenda,coursedate,time,venue,duration,tutor_name,TId);
            course.setPrice(Integer.parseInt(pr));
            databaseReference.setValue(course);
            finish();
        }
    }

    public void deleteCourse(final String id){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tutor Courses").child(id);
        databaseReference.removeValue();

        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Student Courses");
        databaseReference2=FirebaseDatabase.getInstance().getReference("Students");
        databaseReference3=FirebaseDatabase.getInstance();
        notification.setCourse_name("COURSE NAME:"+c);
        notification.setDate("DATE:"+d);
        ArrayList<String> deleteKey = new ArrayList<>();

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    for (DataSnapshot ds1:ds.getChildren()){
                        if(ds1.getKey().equals(id)){
                            Log.i("GOES","INSIDE");
                            String x= (String) ds1.child("name").getValue();
                            notification.setStudent_name(x);
                            databaseReference1.child(ds.getKey()).child(ds1.getKey()).removeValue();
                            notify = databaseReference3.getReference("Student Notifications").child(ds.getKey());
                            notify.push().setValue(notification);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        finish();

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

