package com.chetan.wt;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListOfCourseTutor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> key;
    Course mCourse;
    String TId;
    String Tid;
    String TutorID;
   // private DatabaseReference dbr;
    int flag = 0;
    ArrayList<Course> course_list;
    CardView cd;
    DatabaseReference refff;
    private FirebaseAuth fa;
    private DatabaseReference dbr;
    ProgressBar pg;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_course_tutor);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        //setTitle("Courses");
        setTitle("Courses");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.startblue1));

        // Intent intent = getIntent();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        
        sp = getSharedPreferences("login",MODE_PRIVATE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        mCourse = new Course();
        Button newcourse = (Button)findViewById(R.id.newcourse);
        final ListView courselist = (ListView)findViewById(R.id.courselist);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tutor Courses");
        pg = (ProgressBar)findViewById(R.id.progressBar);
        cd = findViewById(R.id.card_view);
        //dbr= FirebaseDatabase.getInstance().getReference("users");
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);

/////////////////
        final TextView navEmail = (TextView) header.findViewById(R.id.emname);
        final ImageView navImage = (ImageView) header.findViewById(R.id.nav_image);
        final TextView navName = (TextView) header.findViewById(R.id.usname);
        navName.setText("Name");
        fa=FirebaseAuth.getInstance();
        FirebaseUser cuser = fa.getCurrentUser();
        dbr= FirebaseDatabase.getInstance().getReference("users");
        final String id=cuser.getUid();
        dbr.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                navName.setText(dataSnapshot.getValue(user.class).getName());
                navEmail.setText(dataSnapshot.getValue(user.class).getEmail());
                String imageUri = dataSnapshot.getValue(user.class).getDurl();
                //Toast.makeText(getApplicationContext(),dataSnapshot.getValue(user.class).getName(),Toast.LENGTH_LONG).show();
                //ImageView ivBasicImage = (ImageView) findViewById(R.id.ivBasicImage);
                if(imageUri!="")
                    Picasso.get().load(imageUri).fit().centerCrop().into(navImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
////////////////////
        key = new ArrayList<>();
        course_list = new ArrayList<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                list.clear();
                course_list.clear();
                key.clear();

                pg.setVisibility(View.VISIBLE);
                for(DataSnapshot ds:dataSnapshot.getChildren()) {



                    if(ds.getValue(Course.class).getTId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        key.add(ds.getKey());
                        mCourse = ds.getValue(Course.class);
                        // list.add("\nCourse Name:    " + mCourse.getName() + "\nVenue:             " + mCourse.getVenue() + "\nDate:              " + mCourse.getDate() + "\nTime:              " + mCourse.getStart() + "\nTotal no. of Students enrolled:" + mCourse.getNo_of_students());
                        course_list.add(mCourse);
                    }


                }
                pg.setVisibility(View.GONE);

                CustomAdapter customAdapter = new CustomAdapter();
                courselist.setAdapter(customAdapter);

                courselist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(ListOfCourseTutor.this, CourseViewOnly.class);
                        mCourse = course_list.get(i);
                        intent.putExtra("CourseID",key.get(i));
                        // Toast.makeText(MainActivity.this,Integer.toString(course_list.size()),Toast.LENGTH_SHORT).show();
                        intent.putExtra("Course_name", mCourse.getName());
                        intent.putExtra("Tutor_name", mCourse.getTname());
                        intent.putExtra("Venue", mCourse.getVenue());
                        intent.putExtra("Time", mCourse.getStart());
                        intent.putExtra("Duration", mCourse.getDuration().toString());
                        intent.putExtra("agenda", mCourse.getAgenda());
                        intent.putExtra("date", mCourse.getDate());
                        intent.putExtra("TId",mCourse.getTId());
                        intent.putExtra("price",mCourse.getPrice());
                        //intent.putExtra("id", mCourse.getI());
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TutorID = user.getUid();

        //Toast.makeText(getApplicationContext(),TutorID,Toast.LENGTH_LONG).show();
        //FirebaseAuth.getInstance().getCurrentUser().getUid()
       /* final TextView navName = (TextView) header.findViewById(R.id.nav_name);
        final TextView navEmail = (TextView) header.findViewById(R.id.nav_email);
        final ImageView navImage = (ImageView) header.findViewById(R.id.nav_image);
        navEmail.setText(user.getEmail());
        newcourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListOfCourseTutor.this, CourseDetailsTutor.class);
                startActivity(i);
            }
        });
        dbr.child(TutorID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").getValue()!=null) {
                    navName.setText(dataSnapshot.getValue(user.class).getName());
                    navEmail.setText(dataSnapshot.getValue(user.class).getEmail());
                    String imageUri = dataSnapshot.getValue(user.class).getDurl();
                    //Toast.makeText(getApplicationContext(),imageUri,Toast.LENGTH_LONG).show();
                    //ImageView ivBasicImage = (ImageView) findViewById(R.id.ivBasicImage);
                    if(imageUri!="")
                        Picasso.get().load(imageUri).fit().centerCrop().into(navImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
*/

        newcourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ListOfCourseTutor.this,CourseDetailsTutor.class);
                startActivity(i);
            }
        });
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return key.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customview,null);
            TextView cours_name = convertView.findViewById(R.id.cou_name);
            TextView course_date = convertView.findViewById(R.id.cou_date);
            TextView no_enrolled = convertView.findViewById(R.id.no_of_en);

            cours_name.setText(course_list.get(position).getName());
            course_date.setText(course_list.get(position).getDate());
            no_enrolled.setText(String.valueOf(course_list.get(position).getNo_of_students()));


            return convertView;
        }
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
        getMenuInflater().inflate(R.menu.list_of_course_tutor, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nmy_profile) {
            // Handle the camera action

            Intent it=new Intent(ListOfCourseTutor.this,Profile.class);
            startActivity(it);

        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(ListOfCourseTutor.this,TutorNotifications.class);
            startActivity(i);

        } else if (id == R.id.nav_slideshow) {
            Intent it=new Intent(ListOfCourseTutor.this,TutorWallet.class);
            startActivity(it);

        } else if (id == R.id.Logout) {
            FirebaseAuth fbu=FirebaseAuth.getInstance();
            fbu.signOut();
            sp.edit().putBoolean("loginStatus", false).apply();
            sp.edit().putString("userClass", "").apply();
            
            Toast.makeText(getApplicationContext(),"logout successful",Toast.LENGTH_SHORT).show();
            Intent it=new Intent(getApplicationContext(),Welcome.class);
            startActivity(it);

        }  else if (id == R.id.nav_developers) {
            Intent it=new Intent(ListOfCourseTutor.this,developers.class);
            startActivity(it);

        } else if (id == R.id.nav_feedback)
        {
            Intent it=new Intent(ListOfCourseTutor.this,feedback_activity.class);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
