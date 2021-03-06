package com.chetan.wt;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class StudentWallet extends AppCompatActivity {

    DatabaseReference reff, reff_trans;
    String StudentID;
    Button addMoney;
    EditText money;
    int walletBalance, temp;
    Transaction trans = new Transaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_wallet);
        setTitle("My Wallet");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView wallet = findViewById(R.id.wallet);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StudentID = user.getUid();
        reff = FirebaseDatabase.getInstance().getReference("Students").child(StudentID);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wallet.setText(dataSnapshot.child("wallet").getValue().toString());
                walletBalance = Integer.parseInt(dataSnapshot.child("wallet").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addMoney=findViewById(R.id.addMoney);
        money=findViewById(R.id.money);

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(money.getVisibility()==View.VISIBLE) {
                    if (money.getText().toString().length() == 0)
                        money.setVisibility(View.GONE);
                    else {
                        temp = Integer.parseInt(money.getText().toString());
                        if (walletBalance + temp > 100000)
                            Toast.makeText(getApplicationContext(), "Maximum Wallet Limit is 100000", Toast.LENGTH_SHORT).show();
                        else {
                            reff.child("wallet").setValue(walletBalance + temp);
                            money.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Money Successfully Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                    money.setVisibility(View.VISIBLE);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.Transactions);
        final ArrayList<String> myArrayList = new ArrayList<>();
        reff_trans = FirebaseDatabase.getInstance().getReference("Student Transactions").child(StudentID);

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
        reff_trans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myArrayList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    trans = ds.getValue(Transaction.class);
                    myArrayList.add("\n" + trans.getAmount()+ trans.getName()+ trans.getCourse_name()+"\n"+trans.getDate()+"\n");
                }
                listView.setAdapter(myArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
