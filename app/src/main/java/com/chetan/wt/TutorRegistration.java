package com.chetan.wt;

import android.app.ProgressDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class TutorRegistration extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    EditText nameview,emailview,password,cpassword,cityview,qfview;
    Button b,b2;
    int logic=1;
    user us;
    ImageButton profiledp;
    String Name=new String();
    String email=new String();
    String s2=new String();
    String pass=new String();
    String cpass=new String();
    String qf=new String();
    String city=new String();
    String val=new String();
    Bitmap bitmap;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int flag=0;
    SharedPreferences sp;
    private StorageReference mStorageRef;
    private ProgressDialog pb;
    private FirebaseAuth fa;
    private DatabaseReference dbr;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String downloadUrl=new String("https://i.imgur.com/tGbaZCY.jpg");
    String validEmail="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
    String validName="[a-zA-Z ]+";
    String validPass="^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=*!%(),.':;<>/?{}|+-_])(?=\\S+$).{6,}$";
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
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_registration);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue1)));
        setTitle("Tutor Registration");
        sp = getSharedPreferences("login",MODE_PRIVATE);
        nameview=(EditText)findViewById(R.id.name);
        emailview=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        cpassword=(EditText)findViewById(R.id.confirmpassword);
        qfview=(EditText)findViewById(R.id.qualification);
        cityview=(EditText)findViewById(R.id.city);
        b=(Button)findViewById(R.id.registerbutton);
        //b2=(Button)findViewById(R.id.backbut);
        profiledp=(ImageButton)findViewById(R.id.dp);
        pb=new ProgressDialog(this);
        fa=FirebaseAuth.getInstance();
        dbr= FirebaseDatabase.getInstance().getReference("users");
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Name=nameview.getText().toString().trim();
                email=emailview.getText().toString().trim();
                pass=password.getText().toString();
                cpass=cpassword.getText().toString();
                city=cityview.getText().toString().trim();
                qf=qfview.getText().toString().trim();
                if(pass.equalsIgnoreCase(""))
                {
                    password.setError("This is a required field");
                }
                if(cpass.equalsIgnoreCase(""))
                {
                    cpassword.setError("This is a required field");
                }
                if(Name.equalsIgnoreCase(""))
                {
                    nameview.setError("This is a required field");
                }
                if(email.equalsIgnoreCase(""))
                {
                    emailview.setError("This is a required field");
                }
                if(qf.equalsIgnoreCase(""))
                {
                    qfview.setError("This is a required field");
                }
                if(city.equalsIgnoreCase(""))
                {
                    cityview.setError("This is a required field");
                }
                if(pass.matches(validPass)&&Name.matches(validName)&&email.matches(validEmail)&&pass.equals(cpass)&&Name.length()!=0&&email.length()!=0&&pass.length()!=0&&cpass.length()!=0&&qf.length()!=0&&city.length()!=0)
                {

                    pb.setMessage("Registering...");
                    pb.show();

                    fa.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(TutorRegistration.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                pb.dismiss();
                                Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_LONG).show();
                                try {
                                    TimeUnit.SECONDS.sleep(0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                logic=0;
                                Intent intobj=new Intent(TutorRegistration.this,ListOfCourseTutor.class);
                                /*intobj.putExtra("Username",Name);
                                intobj.putExtra("Email",email);
                                intobj.putExtra("city",city);
                                intobj.putExtra("degree",qf);
                                intobj.putExtra("BitmapImage", bitmap);*/
                                FirebaseUser cuser = fa.getCurrentUser();
                                final String id = cuser.getUid();
                                if(flag==1){
                                    final StorageReference mountainsRef = storageRef.child(id+".jpg");
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
                                                    us=new user(id,Name,email,qf,city,downloadUrl);
                                                    us.setWallet(0);
                                                    dbr.child(id).setValue(us);
                                                }
                                            });
                                        }
                                    });}

                                if(flag==0){
                                    us=new user(id,Name,email,qf,city);
                                    us.setWallet(0);}
                                else{
                                    us=new user(id,Name,email,qf,city,downloadUrl);
                                    us.setWallet(0);
                                }
                                dbr.child(id).setValue(us);
                                sp.edit().putString("userClass", "Tutor").apply();
                                sp.edit().putBoolean("loginStatus", true).apply();
                                startActivity(intobj);
                            }
                            else{
                                pb.dismiss();
                                Toast.makeText(getApplicationContext(),"Already Registered Please Login",Toast.LENGTH_LONG).show();
                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent intobj=new Intent(TutorRegistration.this,loginTutor.class);
                                startActivity(intobj);
                            }

                        }
                    });


                }
                else{
                    if(!pass.equals(cpass)){
                        cpassword.setError("");
                        Toast.makeText(getApplicationContext(),"Re-entered password does not match",Toast.LENGTH_LONG).show();}
                    else if(Name.length()==0||email.length()==0||pass.length()==0||cpass.length()==0||qf.length()==0||city.length()==0){
                        Toast.makeText(getApplicationContext(),"Please fill all the fields",Toast.LENGTH_LONG).show();}
                    else if(!email.matches(validEmail)){
                        emailview.setError("");
                        Toast.makeText(getApplicationContext(),"Invalid Email Address",Toast.LENGTH_LONG).show();}
                    else if(!Name.matches(validName)){
                        nameview.setError("");
                        Toast.makeText(getApplicationContext(),"Name should not contain digits/special characters)",Toast.LENGTH_LONG).show();}
                    else if(!pass.matches(validPass)){
                        password.setError("");
                        Toast.makeText(getApplicationContext(),"Password should be atleast 6 characters long containing special characters,digits and alphabets without spaces",Toast.LENGTH_LONG).show();
                    }
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
        /*b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

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
