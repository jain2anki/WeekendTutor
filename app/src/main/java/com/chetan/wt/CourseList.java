package com.chetan.wt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
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

import com.chootdev.recycleclick.RecycleClick;
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
    Intent intent;
    int flag=0;
    MyAdapter myAdapter;
    ArrayAdapter adapter;
    String StudentID="1";
    ArrayList<String> key = new ArrayList<>();
    ArrayList<String> key1 = new ArrayList<>();

    final ArrayList<Course> courselist = new ArrayList<>();
    ArrayList<Course> courselist1 = new ArrayList<>();
    String cid;
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


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle);
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


        




//        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
//        mStorageRef=mStorageRef.child(StudentID+".jpg");
//        File localFile = null;
//        try {
//            localFile = File.createTempFile("images","jpg");
//            mStorageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    navImage.setImageResource(R.drawable.course);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    navImage.setImageResource(R.drawable.noimage);
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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


        final SearchView search = (SearchView) findViewById(R.id.search);

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    courselist.add(ds.getValue(Course.class));
                    courselist1.add(ds.getValue(Course.class));
                    key.add(ds.getKey());
                }
                myAdapter = new MyAdapter(courselist);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                recyclerView.setAdapter(myAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        DatabaseReference refff;
                        refff = FirebaseDatabase.getInstance().getReference("Tutor Courses");
                        refff.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot ds:dataSnapshot.getChildren())
                                {
                                    Course C = ds.getValue(Course.class);
                                    if(courselist1.get(position).getTId().equals(C.getTId()))
                                    {
                                        cid= ds.getKey();
                                        Intent in = new Intent(getApplicationContext(),SelectCourse.class);
                                        in.putExtra("CourseID",cid);
                                        startActivity(in);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



//        RecycleClick.addTo(recyclerView).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
//            @Override
//            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
//                DatabaseReference refff;
//                refff = FirebaseDatabase.getInstance().getReference("Tutor Courses");
//                refff.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for(DataSnapshot ds:dataSnapshot.getChildren())
//                        {
//                            Course C = ds.getValue(Course.class);
//                            if(courselist1.get(position).getTId().equals(C.getTId()))
//                            {
//                                cid= ds.getKey();
//                                Intent in = new Intent(getApplicationContext(),SelectCourse.class);
//                                in.putExtra("CourseID",cid);
//                                startActivity(in);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                noCourse.setVisibility(View.INVISIBLE);
                newText=newText.toLowerCase();
                courselist1.clear();
                if(newText.length()==0)
                {
                    myAdapter = new MyAdapter(courselist);
                    recyclerView.setAdapter(myAdapter);
                }
                else
                {
                    for(int i=0;i<courselist.size();i++)
                    {
                        if (courselist.get(i).getName().toLowerCase(Locale.getDefault()).contains(newText))
                        {
                            courselist1.add(courselist.get(i));
                        }
                    }
                    if(courselist1.size()==0)
                    {
                        noCourse.setVisibility(View.VISIBLE);
                    }
                    myAdapter = new MyAdapter(courselist1);
                    recyclerView.setAdapter(myAdapter);
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
            //Welcome.loginState = 0;
            sp.edit().putBoolean("loginStatus", false).apply();
            sp.edit().putString("userClass", "").apply();
            Toast.makeText(getApplicationContext(),"logout successful",Toast.LENGTH_SHORT).show();
            Intent it=new Intent(getApplicationContext(),Welcome.class);
            startActivity(it);

        } else if (id == R.id.developer_nav) {
            intent = new Intent(getApplicationContext(),developers.class);
            startActivity(intent);

        } else if (id == R.id.wallet) {
            intent = new Intent(getApplicationContext(),StudentWallet.class);
            startActivity(intent);

        }  else if(id == R.id.feedback_nav)
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

class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
}
