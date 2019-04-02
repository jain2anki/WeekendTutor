package com.chetan.wt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.chetan.wt.R.id.no_courses_found;
import static com.chetan.wt.R.id.toolbar;
import static java.io.File.createTempFile;

public class CourseList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    DatabaseReference reff,ref;
    Course course;
    ArrayList<String> key;
    Intent intent;
    int flag=0;
    MyAdapter myAdapter;
    ArrayAdapter adapter;
    String StudentID="1";
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        setTitle("Available Courses");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.startblue1));

        sp = getSharedPreferences("login",MODE_PRIVATE);
        
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        final ProgressBar pgsBar = (ProgressBar)findViewById(R.id.pBar);

        key = new ArrayList<>();
        final ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> tutorList = new ArrayList<>();
        final ArrayList<String> courseList = new ArrayList<>();
        final ArrayList<String> dateList = new ArrayList<>();
        final ArrayList<String> timeList = new ArrayList<>();
        final ArrayList<String> durationList = new ArrayList<>();
        final ArrayList<String> tidList = new ArrayList<>();

        final ArrayList<String> tutorList1 = new ArrayList<>();
        final ArrayList<String> courseList1 = new ArrayList<>();
        final ArrayList<String> dateList1 = new ArrayList<>();
        final ArrayList<String> timeList1 = new ArrayList<>();
        final ArrayList<String> durationList1 = new ArrayList<>();



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Students");
        StudentID = user.getUid();
        final TextView navName = (TextView) header.findViewById(R.id.nav_name);
        final TextView navEmail = (TextView) header.findViewById(R.id.nav_email);
        final ImageView navImage = (ImageView) header.findViewById(R.id.nav_image);






        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef=mStorageRef.child(StudentID+".jpg");
        File localFile = null;
        try {
            localFile = File.createTempFile("images","jpg");
            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    navImage.setImageResource(R.drawable.course);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    navImage.setImageResource(R.drawable.noimage);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        ref.child(StudentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                navName.setText(dataSnapshot.child("name").getValue().toString());
                navEmail.setText(dataSnapshot.child("mail").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







        final TextView noCourse = (TextView) findViewById(R.id.no_courses_found);

        reff = FirebaseDatabase.getInstance().getReference("Tutor Courses");

        adapter = new ArrayAdapter(this,R.layout.list_item,courseList);
        //final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myArrayList);

        final SearchView search = (SearchView) findViewById(R.id.search);
        //FirebaseAuth SID = FirebaseAuth.getInstance();
        //String sid = SID.getCurrentUser().getUid();
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tutorList.clear();
                courseList.clear();
                durationList.clear();
                dateList.clear();
                timeList.clear();
                tidList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    course = ds.getValue(Course.class);
                    key.add(ds.getKey());
                    //myArrayList.add("\nTutor:                     " + course.getTname() + "\nCourse Name:           " + course.getName() + "\nDate:                      " + course.getDate() + "\nTime-Duration(hrs):  " + course.getStart() + "-" + course.getDuration() + "\nVenue:                    " + course.getVenue()+"\n");
                    tutorList.add(course.getTname());
                    courseList.add(course.getName());
                    durationList.add(course.getDuration());
                    dateList.add(course.getDate());
                    timeList.add(course.getStart());
                    tidList.add(course.getTId());
                }
                if (tutorList.size()==0);
                else {
                    pgsBar.setVisibility(View.VISIBLE);
                    myAdapter = new MyAdapter(CourseList.this,tutorList,courseList,dateList,durationList,timeList,tidList);
                    listView.setAdapter(myAdapter);
                    pgsBar.setVisibility(View.GONE);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent in = new Intent(view.getContext(),SelectCourse.class);
                        in.putExtra("CourseID",key.get(position));
                        startActivity(in);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(view.getContext(),SelectCourse.class);
                in.putExtra("CourseID",key.get(position));
                startActivity(in);
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*query=query.toLowerCase();
                tutorList1.clear();
                courseList1.clear();
                durationList1.clear();
                dateList1.clear();
                timeList1.clear();
                /*
                for(int i=0;i<courseList.size();i++) {
                    if (query.compareToIgnoreCase(courseList.get(i).toLowerCase())==0) {
                        courseList1.add(courseList.get(i));
                        tutorList1.add(tutorList.get(i));
                        dateList1.add(dateList.get(i));
                        durationList1.add(durationList.get(i));
                        timeList1.add(timeList.get(i));
                    }
                }
                if(courseList1.size()==0)
                {
                    myAdapter = new MyAdapter(CourseList.this, tutorList, courseList, dateList, durationList, timeList, tidList);
                    listView.setAdapter(myAdapter);
                    Toast.makeText(getApplicationContext(),"No Courses Found",Toast.LENGTH_SHORT).show();
                }
                else {
                    myAdapter = new MyAdapter(CourseList.this, tutorList1, courseList1, dateList1, durationList1, timeList1, tidList);
                    listView.setAdapter(myAdapter);
                }
                if(query.length()==0)
                {
                    myAdapter = new MyAdapter(CourseList.this, tutorList, courseList, dateList, durationList, timeList, tidList);
                    listView.setAdapter(myAdapter);
                }
                else
                {
                    for(int i=0;i<courseList.size();i++)
                    {
                        if (query.toLowerCase(Locale.getDefault()).contains(courseList.get(i).toLowerCase()))
                        {
                            courseList1.add(courseList.get(i));
                            tutorList1.add(tutorList.get(i));
                            dateList1.add(dateList.get(i));
                            durationList1.add(durationList.get(i));
                            timeList1.add(timeList.get(i));
                        }
                    }
                    myAdapter = new MyAdapter(CourseList.this, tutorList1, courseList1, dateList1, durationList1, timeList1, tidList);
                    listView.setAdapter(myAdapter);
                }*/
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                /*newText=newText.toLowerCase();
                tutorList1.clear();
                courseList1.clear();
                durationList1.clear();
                dateList1.clear();
                timeList1.clear();
                for(int i=0;i<courseList.size();i++) {
                    if (newText.compareToIgnoreCase(courseList.get(i).toLowerCase())==0) {
                        courseList1.add(courseList.get(i));
                        tutorList1.add(tutorList.get(i));
                        dateList1.add(dateList.get(i));
                        durationList1.add(durationList.get(i));
                        timeList1.add(timeList.get(i));
                    }
                }
                if(courseList1.size()==0)
                {
                    myAdapter = new MyAdapter(CourseList.this, tutorList, courseList, dateList, durationList, timeList, tidList);
                    listView.setAdapter(myAdapter);
                    Toast.makeText(getApplicationContext(),"No Courses Found",Toast.LENGTH_SHORT).show();
                }
                else {
                    myAdapter = new MyAdapter(CourseList.this, tutorList1, courseList1, dateList1, durationList1, timeList1, tidList);
                    listView.setAdapter(myAdapter);
                }*/
                noCourse.setVisibility(View.INVISIBLE);
                newText=newText.toLowerCase();
                tutorList1.clear();
                courseList1.clear();
                durationList1.clear();
                dateList1.clear();
                timeList1.clear();
                /*
                for(int i=0;i<courseList.size();i++) {
                    if (query.compareToIgnoreCase(courseList.get(i).toLowerCase())==0) {
                        courseList1.add(courseList.get(i));
                        tutorList1.add(tutorList.get(i));
                        dateList1.add(dateList.get(i));
                        durationList1.add(durationList.get(i));
                        timeList1.add(timeList.get(i));
                    }
                }
                if(courseList1.size()==0)
                {
                    myAdapter = new MyAdapter(CourseList.this, tutorList, courseList, dateList, durationList, timeList, tidList);
                    listView.setAdapter(myAdapter);
                    Toast.makeText(getApplicationContext(),"No Courses Found",Toast.LENGTH_SHORT).show();
                }
                else {
                    myAdapter = new MyAdapter(CourseList.this, tutorList1, courseList1, dateList1, durationList1, timeList1, tidList);
                    listView.setAdapter(myAdapter);
                }*/
                if(newText.length()==0)
                {
                    myAdapter = new MyAdapter(CourseList.this, tutorList, courseList, dateList, durationList, timeList, tidList);
                    listView.setAdapter(myAdapter);
                }
                else
                {
                    for(int i=0;i<courseList.size();i++)
                    {
                        if (courseList.get(i).toLowerCase(Locale.getDefault()).contains(newText))
                        {
                            courseList1.add(courseList.get(i));
                            tutorList1.add(tutorList.get(i));
                            dateList1.add(dateList.get(i));
                            durationList1.add(durationList.get(i));
                            timeList1.add(timeList.get(i));
                        }
                    }
                    if(courseList1.size()==0)
                    {
                        noCourse.setVisibility(View.VISIBLE);
                    }
                    myAdapter = new MyAdapter(CourseList.this, tutorList1, courseList1, dateList1, durationList1, timeList1, tidList);
                    listView.setAdapter(myAdapter);
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(flag==1){
                finishAffinity();
            }
            else {
                Toast.makeText(this,"Press once again to exit",Toast.LENGTH_SHORT).show();
                flag++;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_courses) {
            intent = new Intent(getApplicationContext(),StudentCourses.class);
            startActivity(intent);

        } else if (id == R.id.my_profile) {
            intent = new Intent(getApplicationContext(),MyProfileStudent.class);
            startActivity(intent);
        }


        else if (id == R.id.logout) {
            FirebaseAuth fbu=FirebaseAuth.getInstance();
            fbu.signOut();
            //sp.edit().putBoolean("loginStatus", false).apply();
            //sp.edit().putString("userClass", "").apply();
            
            Toast.makeText(getApplicationContext(),"logout successful",Toast.LENGTH_SHORT).show();
            Intent it=new Intent(getApplicationContext(),Welcome.class);
            startActivity(it);

        } else if (id == R.id.developer_nav) {
            intent = new Intent(getApplicationContext(),developers.class);
            startActivity(intent);

        } else if (id == R.id.wallet) {
            intent = new Intent(getApplicationContext(),StudentWallet.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {

        } else if(id == R.id.feedback_nav)
        {
            intent = new Intent(getApplicationContext(),feedback_activity.class);
            startActivity(intent);
        }
        else if(id==R.id.stu_notifications)
        {
            intent=new Intent(getApplicationContext(),StudentNotifications.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
