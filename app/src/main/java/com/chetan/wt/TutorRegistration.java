package com.chetan.wt;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    Double latittude,longitude;
    double[] latarray = new double[5];
    double[] lonarray = new double[5];
    String[] places = new String[5];
    String city;
    int a=1;


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
        b=(Button)findViewById(R.id.registerbutton);
        //b2=(Button)findViewById(R.id.backbut);
        profiledp=(ImageButton)findViewById(R.id.dp);
        pb=new ProgressDialog(this);
        fa=FirebaseAuth.getInstance();
        dbr= FirebaseDatabase.getInstance().getReference("users");
        latarray[0] = 12.917;
        latarray[1] = 13.3323;
        latarray[2] = 12.9716;
        latarray[3] = 12.5033;
        latarray[4] = 15.2993;
        lonarray[0] = 74.85603;
        lonarray[1] = 74.746;
        lonarray[2] = 77.5946;
        lonarray[3] = 74.9896;
        lonarray[4] = 74.124;
        places[0] = "mangalore";
        places[1] = "udupi";
        places[2] = "bangalore";
        places[3] = "kasaragod";
        places[4] = "goa";

        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Name=nameview.getText().toString().trim();
                email=emailview.getText().toString().trim();
                pass=password.getText().toString();
                cpass=cpassword.getText().toString();
                qf=qfview.getText().toString().trim();
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(TutorRegistration.this);
                fetchLocation();
                if(latittude==null && longitude==null){
                    latittude=13.01120967137455;
                    longitude=74.79212522493073;
                }
                int i,index=0;
                Double dis=0.0,near=1000000.0;
                for(i=0;i<5;i++){
                    dis=(latittude-latarray[i])*(latittude-latarray[i])+(longitude-lonarray[i])*(longitude-lonarray[i]);
                    if(dis<near){
                        near=dis;
                        index=i;
                    }
                }
                city=places[index];
                latittude=latarray[index];
                longitude=lonarray[index];
                Toast.makeText(getApplicationContext(),"the location is "+city,Toast.LENGTH_SHORT).show();
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
                if(pass.matches(validPass)&&Name.matches(validName)&&email.matches(validEmail)&&pass.equals(cpass)&&Name.length()!=0&&email.length()!=0&&pass.length()!=0&&cpass.length()!=0&&qf.length()!=0)
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
                                    us=new user(id,Name,email,qf,city,latittude,longitude);
                                    us.setWallet(0);}
                                else{
                                    us=new user(id,Name,email,qf,city,downloadUrl,latittude,longitude);
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
    private void fetchLocation() {

    if(a==0) {
        if (ContextCompat.checkSelfPermission(TutorRegistration.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TutorRegistration.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(TutorRegistration.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(TutorRegistration.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latittude = location.getLatitude();
                                longitude = location.getLongitude();
                            }

                        }
                    });

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //abc
            }else{

            }
        }
    }
}
