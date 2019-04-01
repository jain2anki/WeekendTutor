package com.chetan.wt;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class delete_multiple_courses extends AppCompatActivity {

    DatabaseReference reff, notify, temp, users, trans_student, trans_tutor;
    int stu_wallet,w;
    TutorNotification notification = new TutorNotification();
    Transaction trans_stu = new Transaction();
    Transaction trans_tut = new Transaction();
    Course course;
    ArrayList<String> key, tutorId, course_name, course_date;
    String StudentID,cid, tid, temp2;
    ArrayList<Integer> price;
    ArrayList<Integer> tutor_wallet;
    ArrayList<String> tutor_name;
    String required_id;
    private FirebaseAuth SID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_multiple_courses);
        setTitle("Select Courses To Delete");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        key = new ArrayList<String>();
        tutorId = new ArrayList<String>();
        course_name = new ArrayList<String>();
        course_date = new ArrayList<String>();
        tutor_name = new ArrayList<String>();
        price = new ArrayList<>();
        tutor_wallet = new ArrayList<>();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StudentID = user.getUid();

        temp = FirebaseDatabase.getInstance().getReference("Students");
        temp.child(StudentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").getValue()!=null)
                    notification.setStudent_name(dataSnapshot.child("name").getValue().toString()+" unregistered for ");
                trans_tut.setName("Paid to "+dataSnapshot.child("name").getValue().toString());
                stu_wallet = Integer.parseInt(dataSnapshot.child("wallet").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        String dt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        trans_stu.setDate(dt);
        trans_tut.setDate(dt);
        trans_student = FirebaseDatabase.getInstance().getReference("Student Transactions").child(StudentID);
        final ListView listView = (ListView) findViewById(R.id.ListView);
        final ArrayList<String> myArrayList = new ArrayList<>();
        reff = FirebaseDatabase.getInstance().getReference("Student Courses").child(StudentID);

        final ArrayList<Integer> total_stu = new ArrayList<>();
        final ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, myArrayList)
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
                key.clear();
                price.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    course = ds.getValue(Course.class);
                    key.add(ds.getKey());
                    course_name.add(course.getName());
                    course_date.add(course.getDate());
                    //Toast.makeText(getApplicationContext(),course.getTname(),Toast.LENGTH_SHORT).show();
                    tutor_name.add(course.getTname());
                    tutorId.add(course.getTId());
                    price.add(course.getPrice());
                    myArrayList.add("\n" + course.getName() + "\n");
                    //   total_stu.add(course.getNo_of_students());

                }
                listView.setAdapter(myArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        final DatabaseReference tu_ref = FirebaseDatabase.getInstance().getReference("Tutor Courses");

        tu_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(int i=0;i<key.size();i++){
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        if(ds.getKey().equals(key.get(i))){
                            total_stu.add(Integer.parseInt(ds.child("no_of_students").getValue().toString()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        users = FirebaseDatabase.getInstance().getReference("users");

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    for (String id : tutorId) {
                        if (id.equals(ds.getKey())) {
                            tutor_wallet.add(Integer.parseInt(dataSnapshot.child(id).child("wallet").getValue().toString()));
                        }
                    }
                }}


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        View.OnClickListener listenerDel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(delete_multiple_courses.this);
                builder.setMessage("Are you sure you want to Delete this Course?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int x) {
                        /** Getting the checked items from the listview */
                        SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                        int itemCount = listView.getCount();

                        for(int i=itemCount-1; i >= 0; i--){
                            if(checkedItemPositions.get(i)){
                                myArrayAdapter.remove(myArrayList.get(i));
                                cid = key.get(i);
                                tid = tutorId.get(i);



//                                final int p = price.get(i);
//                                users = FirebaseDatabase.getInstance().getReference("users");
//
//                                users.child(tid).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        tutor_wallet = Integer.parseInt(dataSnapshot.child("wallet").getValue().toString());
//                                        String temp = dataSnapshot.child("wallet").getValue().toString();
//                                        users.child(tid).child("wallet").setValue(tutor_wallet-p+10);
//                                        Toast.makeText(getApplicationContext(),temp,Toast.LENGTH_LONG).show();
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                                users.child(tid).addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        tutor_wallet = Integer.parseInt(dataSnapshot.child("wallet").getValue().toString());
//                                        temp2 = dataSnapshot.child("wallet").getValue().toString();
//                                        final int w = tutor_wallet;
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
                                //Toast.makeText(getApplicationContext(),temp2,Toast.LENGTH_SHORT).show();
//                                users.child(tid).child("wallet").setValue(w+10);
                                //Toast.makeText(getApplicationContext(),temp,Toast.LENGTH_LONG).show();
                                notification.setCourse_name(course_name.get(i) + " which is on ");
                                trans_tut.setCourse_name(" for "+course_name.get(i));
                                trans_stu.setCourse_name(" for "+course_name.get(i));
                                trans_tut.setAmount("-"+(price.get(i)-10)+"\nRefunded to ");
                                trans_stu.setAmount("+"+(price.get(i)-10)+"\n");
                                trans_stu.setName("Refunded by "+tutor_name.get(i));
                                notification.setDate(course_date.get(i));
                                final DatabaseReference tut = FirebaseDatabase.getInstance().getReference("Tutor Courses").child(cid);
                                notify = FirebaseDatabase.getInstance().getReference("Tutor Notifications").child(tid);
                                trans_tutor = FirebaseDatabase.getInstance().getReference("Tutor Transactions").child(tid);
                                trans_student = FirebaseDatabase.getInstance().getReference("Student Transactions").child(StudentID);
                                notify.push().setValue(notification);
                                trans_tutor.push().setValue(trans_tut);
                                trans_student.push().setValue(trans_stu);
                                DatabaseReference del = FirebaseDatabase.getInstance().getReference("Student Courses").child(StudentID).child(cid);
                                del.setValue(null);
                                int n = total_stu.get(i)-1;
                                tut.child("no_of_students").setValue(n);

                                stu_wallet+=price.get(i)-10;

                                for(int j=itemCount-1;j>i;j--){
                                    if(tutorId.get(i).equals(tutorId.get(j))){
                                        tutor_wallet.set(i,tutor_wallet.get(j));
                                    }
                                }
                                System.out.println("Trail: Before" + tutor_wallet.get(i));
                                tutor_wallet.set(i,tutor_wallet.get(i)-price.get(i)+10);
                                System.out.println("Trail: After" + tutor_wallet.get(i));

                            }
                        }

                        for (int j=tutorId.size()-1;j>=0;j--){
                            users.child(tutorId.get(j)).child("wallet").setValue(tutor_wallet.get(j));
                        }

                        temp.child(StudentID).child("wallet").setValue(stu_wallet);
                        Toast.makeText(getApplicationContext(),"Deleted Succesfully",Toast.LENGTH_SHORT).show();
                        checkedItemPositions.clear();
                        myArrayAdapter.notifyDataSetChanged();
                        finish();
                    }
                }).setNegativeButton("Cancel",null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        };

        Button delmultiple = findViewById(R.id.delete_multiple);
        delmultiple.setOnClickListener(listenerDel);



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