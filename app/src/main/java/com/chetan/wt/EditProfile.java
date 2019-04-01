package com.chetan.wt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;


import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

public class EditProfile extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    EditText nameed,cityed,qfed;
    TextView emailed;
    String names,citys,qfs,emails;
    String validEmail="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
    String validName="[a-zA-Z ]+";
    public String downloadUrl=new String("https://i.imgur.com/tGbaZCY.jpg");
    //String validPass="^(?=.[0-9])(?=.[a-zA-Z])(?=.[@#$%^&+=!%(),.':;<>/?{}|+-_])(?=\\S+$).{6,}$";
    Button b,b2;
    Bitmap bitmap;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    int flag=0;
    ImageButton profiledp;
    private FirebaseAuth fa;
    user us;
    private DatabaseReference dbr;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== RESULT_LOAD_IMAGE && resultCode== RESULT_OK && null!=data){
            bitmap=(Bitmap)data.getExtras().get("data");
            profiledp.setImageBitmap(bitmap);
            flag=1;

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        setTitle("Edit Profile");
        nameed=(EditText)findViewById(R.id.name);
        cityed=(EditText)findViewById(R.id.city);
        emailed=(TextView) findViewById(R.id.email);
        qfed=(EditText)findViewById(R.id.qualification);
        b=(Button)findViewById(R.id.save);
        //b2=(Button)findViewById(R.id.backbut);
        profiledp=(ImageButton)findViewById(R.id.dp);
        fa=FirebaseAuth.getInstance();

        FirebaseUser cuser = fa.getCurrentUser();
        dbr= FirebaseDatabase.getInstance().getReference("users");
        String id=cuser.getUid();

        Intent int3=getIntent();
        dbr.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameed.setText(dataSnapshot.getValue(user.class).getName());
                emailed.setText(dataSnapshot.getValue(user.class).getEmail());
                qfed.setText(dataSnapshot.getValue(user.class).getDegree());
                cityed.setText(dataSnapshot.getValue(user.class).getCity());
                downloadUrl=dataSnapshot.getValue(user.class).getDurl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();

        final StorageReference mountainsRef = storageRef.child(id+".jpg");
        /*nameed.setText((String)int3.getSerializableExtra("Username"));
        cityed.setText((String)int3.getSerializableExtra("city"));
        qfed.setText((String)int3.getSerializableExtra("qf"));
        emailed.setText((String)int3.getSerializableExtra("em"));*/
        /*b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                names=nameed.getText().toString().trim();
                emails=emailed.getText().toString().trim();
                //pass=password.getText().toString();
                //cpass=cpassword.getText().toString();
                citys=cityed.getText().toString().trim();
                qfs=qfed.getText().toString().trim();
                if(names.equalsIgnoreCase(""))
                {
                    nameed.setError("This is a required field");
                }
               /* if(emails.equalsIgnoreCase(""))
                {
                    emailed.setError("This is a required field");
                }*/
                if(qfs.equalsIgnoreCase(""))
                {
                    qfed.setError("This is a required field");
                }
                if(citys.equalsIgnoreCase(""))
                {
                    cityed.setError("This is a required field");
                }
                if(names.matches(validName)&&names.length()!=0&&emails.length()!=0&&qfs.length()!=0&&citys.length()!=0)
                {

                    FirebaseUser cuser = fa.getCurrentUser();

                    final String id = cuser.getUid();
                    if(flag==1){
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = mountainsRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Uri durl=taskSnapshot.getMetadata().getDo; //contains file metadata such as size, content-type, etc.
                                // ...
                                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUrl = uri.toString();
                                        us=new user(id,names,emails,qfs,citys,downloadUrl);
                                        dbr.child(id).setValue(us);
                                        Toast.makeText(getApplicationContext(),"Profile Photo Updated Successfully",Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        });}


                    if(flag==0){
                        us=new user(id,names,emails,qfs,citys,downloadUrl);}
                    else{
                        us=new user(id,names,emails,qfs,citys,downloadUrl);
                    }
                    dbr.child(id).setValue(us);
                    Intent intobj=new Intent(EditProfile.this,ListOfCourseTutor.class);
                    startActivity(intobj);

                }
                else{
                    /*if(!pass.equals(cpass)){
                            cpassword.setError("");
                            Toast.makeText(getApplicationContext(),"Re-entered password does not match",Toast.LENGTH_LONG).show();}
                    */if(names.length()==0||emails.length()==0||qfs.length()==0||citys.length()==0){
                        Toast.makeText(getApplicationContext(),"Please fill all the fields",Toast.LENGTH_LONG).show();}
                    else if(!emails.matches(validEmail)){
                        emailed.setError("");
                        Toast.makeText(getApplicationContext(),"Invalid Email Address",Toast.LENGTH_LONG).show();}
                    else if(!names.matches(validName)){
                        nameed.setError("");
                        Toast.makeText(getApplicationContext(),"Name should not contain digits/special characters)",Toast.LENGTH_LONG).show();}
                    /*else if(!pass.matches(validPass)){
                        password.setError("");
                        Toast.makeText(getApplicationContext(),"Password should be atleast 6 characters long containing special characters,digits and alphabets without spaces",Toast.LENGTH_LONG).show();
                    }*/
                }
            }
        });
        profiledp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intobj2=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intobj2, RESULT_LOAD_IMAGE);

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