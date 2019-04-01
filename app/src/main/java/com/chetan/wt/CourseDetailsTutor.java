package com.chetan.wt;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CourseDetailsTutor extends AppCompatActivity {

    EditText coursename,tutorName,Venue,Time,Duration,courseAgenda,course_date,price;
    Button Submit;
    String name_of_tutor;
    long maxId=0;
    Date dateObject;
    String coursedate;
    DatabaseReference databaseCourse,tutor;
    int flag=1;
    private ProgressDialog pb;
    private FirebaseAuth fa;
    private DatabaseReference dbr;
    String TId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details_tutor);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        setTitle("Add a Course");
        assert  getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fa=FirebaseAuth.getInstance();
        TId = fa.getCurrentUser().getUid();
        // TId = "abc";
        databaseCourse = FirebaseDatabase.getInstance().getReference("Tutor Courses");
        databaseCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for(DataSnapshot ds:dataSnapshot.getChildren()){

                        Course mCourse = ds.getValue(Course.class);
                        //maxId = mCourse.getI()+1;

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tutor = FirebaseDatabase.getInstance().getReference("users").child(TId);

        tutor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(user.class).getName()!=null){
                    // tutorName.setText(dataSnapshot.child("name").getValue().toString());
                    name_of_tutor = dataSnapshot.getValue(user.class).getName();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        coursename = (EditText) findViewById(R.id.courseName);
//        tutorName = (EditText) findViewById(R.id.tutorName);
        Venue = (EditText) findViewById(R.id.Venue);
        Time = (EditText) findViewById(R.id.Time);
        Duration = (EditText) findViewById(R.id.Duration);
        courseAgenda = (EditText) findViewById(R.id.courseAgenda);
        course_date = (EditText) findViewById(R.id.course_date);
        Submit = (Button) findViewById(R.id.Submit);
        price = findViewById(R.id.price);


        //String dob_var = (course_date.getText().toString());
        // coursedate = course_date.getText().toString().split("/", 3);


     /*   if (Integer.parseInt(coursedate[2]) < 2019){
            Toast.makeText(this,"Past date is not allowed",Toast.LENGTH_SHORT).show();
    }
        if(Integer.parseInt(coursedate[0])>2020){
            Toast.makeText(this,"Date is outside allowed range",Toast.LENGTH_SHORT).show();
        }*/

        /*try {
            dateObject = formatter.parse(dob_var);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        //coursedate = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=1;
                try {
                    addCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addCourse() throws ParseException {
        String course_name = coursename.getText().toString().trim();
        String tutor_name = name_of_tutor;
        String venue = Venue.getText().toString().trim();
        String time = Time.getText().toString().trim();
        String duration = Duration.getText().toString().trim();
        String agenda = courseAgenda.getText().toString().trim();
        coursedate = course_date.getText().toString();
        int pr;
        if (price.getText().toString().trim().equals(""))
            pr = 0;
        else
            pr = Integer.parseInt(price.getText().toString().trim());

        if(course_name.equalsIgnoreCase("")){
            coursename.setError("Course name is required field");
            flag=0;
        }

        if (pr<0){
            price.setError("Price cannot be negative");
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
            Duration.setError("Please enter duartion in correct format");
            flag=0;
        }
        if (duration_format.length==2){
            if (Integer.parseInt(duration_format[0])<0 || Integer.parseInt(duration_format[0])>23 || Integer.parseInt(duration_format[1])<0 || Integer.parseInt(duration_format[1])>59){
                Duration.setError("Enter correct duration");
                flag=0;
            }
        }

        if (agenda.equalsIgnoreCase("")){
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
            Course course = new Course(course_name,agenda,coursedate,time,venue,duration,tutor_name,TId);
            course.setPrice(pr);
            //databaseCourse.child(String.valueOf(maxId)).setValue(course);
            // Toast.makeText(this,TId,Toast.LENGTH_LONG).show();
            databaseCourse.push().setValue(course);


            finish();
        }


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
