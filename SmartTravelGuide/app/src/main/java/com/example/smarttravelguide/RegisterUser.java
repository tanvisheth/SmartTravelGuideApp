package com.example.smarttravelguide;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class RegisterUser extends AboutMenu {


    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    EditText name,phone,email,pass,cnfpass,add,id;
    Button Register,locatebtn;
    DatabaseReference ref;
    Users users;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ResultReceiver resultReceiver;
    private static final int REQUEST_CALL = 1;
    String address;
    double soslat, soslon;
    private SharedPreferences file;

    private DatabaseReference Currdata = _firebase.getReference("Data");
    private ChildEventListener _Currdata_child_listener;
    private FirebaseAuth Currauth;
    private OnCompleteListener<AuthResult> _Currauth_create_user_listener;
    private OnCompleteListener<AuthResult> _Currauth_sign_in_listener;
    private OnCompleteListener<Void> _Currauth_reset_password_listener;
    private HashMap<String, Object> hashmap = new HashMap<>();
    private ArrayList<String> liststring = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    private Intent spotimgpic = new Intent(Intent.ACTION_GET_CONTENT);
    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.google.firebase.FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_register_user);
        name=findViewById(R.id.txtname);
        add=findViewById(R.id.txtaddress);
        phone=findViewById(R.id.txtmobile);
        email=findViewById(R.id.txtemail);
        pass=findViewById(R.id.txtpass);
        cnfpass=findViewById(R.id.txtcnfpass);
        locatebtn=findViewById(R.id.locatebtn);
        Register=findViewById(R.id.btnRegister);
        id=findViewById(R.id.userid);
        getCurrentLocation();
        Currauth = FirebaseAuth.getInstance();
        resultReceiver = new AddressResultReceiver(new Handler());
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        Random random = new Random();
        id.setText(String.valueOf(random.nextInt(9999)));

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("") && (email.getText().toString().equals("") && (pass.getText().toString().equals("") && (add.getText().toString().equals("") && (phone.getText().toString().equals("") && id.getText().toString().equals("")))))) {
                    Toast.makeText(getApplicationContext(),"Enter Valid Credentials",Toast.LENGTH_LONG).show();
                }
                else {
                    Currauth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(RegisterUser.this, _Currauth_create_user_listener);
                    hashmap = new HashMap<>();
                    hashmap.put("name", name.getText().toString());
                    hashmap.put("emailid", email.getText().toString());
                    hashmap.put("password", pass.getText().toString());
                    hashmap.put("address", add.getText().toString());
                    hashmap.put("phonenumber", phone.getText().toString());
                    hashmap.put("userid", id.getText().toString());
                    calendar = Calendar.getInstance();
                    hashmap.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));

                    Currdata.child(id.getText().toString()).updateChildren(hashmap);
                    finish();

//                users=new Users();
//                ref= FirebaseDatabase.getInstance().getReference().child("Users");
//                Long ph=Long.parseLong(phone.getText().toString().trim());
//                users.setName(name.getText().toString());
//                users.setAddress(add.getText().toString());
//                users.setEmail(email.getText().toString());
//                users.setPassword(pass.getText().toString());
//                users.setPhone(ph);
//                ref.push().setValue(users);
                Toast.makeText(getApplicationContext(),"Register Successful",Toast.LENGTH_LONG).show();
            }
            }
        });
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterUser.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
        _Currauth_create_user_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";

            }
        };

        _Currauth_sign_in_listener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> _param1) {
                final boolean _success = _param1.isSuccessful();
                final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";

            }
        };

        _Currauth_reset_password_listener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> _param1) {
                final boolean _success = _param1.isSuccessful();

            }
        };


        locatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(this, "Permsission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(RegisterUser.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext()).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            soslat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            soslon = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Log.e("Lat",String.valueOf(soslat));
                            Log.e("lon",String.valueOf(soslon));

                            Location location = new Location("ProvideNA");
                            location.setLatitude(soslat);
                            location.setLongitude(soslon);

                            fetchAddressFromLatLong(location);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler){
            super(handler);
        } protected void onReceiveResult(int resultCode,Bundle resultData){
            super.onReceiveResult(resultCode,resultData);
            if(resultCode == Constants.SUCCESS_RESULT){
                address = resultData.getString(Constants.RESULT_DATA_KEY);
                add.setText(address);
                file.edit().putString("Current_address",address).apply();
                Log.e("RESULT",address);

            }else{
                Toast.makeText(getApplicationContext(),resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchAddressFromLatLong(Location location) {
        Intent intent=new Intent(RegisterUser.this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        getApplicationContext().startService(intent);
    }
}


