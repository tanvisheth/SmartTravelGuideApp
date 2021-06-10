package com.example.smarttravelguide;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class AddSpotActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    EditText name,add,id,category;
    Button addspot,locatebtn;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ResultReceiver resultReceiver;
    private static final int REQUEST_CALL = 1;
    String address;
    double soslat, soslon;
    private ImageView spotimgimg;

    public final int REQ_CD_spotimgPIC = 101;
    private String spotimgpath = "";
    private String spotimgname = "";
    private double img = 0;
    private SharedPreferences file;

    private DatabaseReference spot = _firebase.getReference("spot");
    private ChildEventListener _spot_child_listener;
    private HashMap<String, Object> hashmap = new HashMap<>();
    private ArrayList<String> liststring = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private StorageReference spotimg = _firebase_storage.getReference("spotimg");
    private OnCompleteListener<Uri> _spotimg_upload_success_listener;
    private OnSuccessListener<FileDownloadTask.TaskSnapshot> _spotimg_download_success_listener;
    private OnSuccessListener _spotimg_delete_success_listener;
    private OnProgressListener _spotimg_upload_progress_listener;
    private OnProgressListener _spotimg_download_progress_listener;
    private OnFailureListener _spotimg_failure_listener;
    private long spotimgimgsize=0;


    private Intent spotimgpic = new Intent(Intent.ACTION_GET_CONTENT);
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);
        com.google.firebase.FirebaseApp.initializeApp(this);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);

        }
        img = 0;
        name=findViewById(R.id.spotname);
        add=findViewById(R.id.spotaddress);
        locatebtn=findViewById(R.id.locatebtn);
        addspot=findViewById(R.id.btnRegister);
        id=findViewById(R.id.spotid);
        spotimgimg = findViewById(R.id.imageview1);
        category=findViewById(R.id.spotcategory);
        getCurrentLocation();
        resultReceiver = new AddressResultReceiver(new Handler());
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        Random random = new Random();
        id.setText(String.valueOf(random.nextInt(9999)));
        spotimgpic.setType("image/*");
        spotimgpic.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        int i = 6;
        final String randomText = "1234567890qwertyuiopasdfghjklzxcvbnm";
        StringBuilder result = new StringBuilder();
        while (i > 0) {
            Random r = new Random();
            result.append(randomText.charAt(r.nextInt(randomText.length())));
            i--;
        }

        id.setText(result.toString());

        spotimgimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(spotimgpic,REQ_CD_spotimgPIC);
            }
        });
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddSpotActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }

        locatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        addspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("") && (add.getText().toString().equals("") && (category.getText().toString().equals("") && id.getText().toString().equals("")))) {
                    Toast.makeText(getApplicationContext(),"Enter Valid Credentials",Toast.LENGTH_LONG).show();
                }
                else {
                    if (img == 0) {
                    hashmap = new HashMap<>();
                    hashmap.put("name", name.getText().toString());
                    hashmap.put("address", add.getText().toString());
                    hashmap.put("spotcategory", category.getText().toString());
                    hashmap.put("spotid", id.getText().toString());
                    hashmap.put("spotimgurl", "");
                    hashmap.put("spotimgname", "");
                    calendar = Calendar.getInstance();
                    hashmap.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));

                    spot.child(id.getText().toString()).updateChildren(hashmap);
                    finish();
                    Toast.makeText(getApplicationContext(),"Register Successful",Toast.LENGTH_LONG).show();
                }else{
                        spotimg.child(spotimgname).putFile(Uri.fromFile(new File(spotimgpath))).addOnFailureListener(_spotimg_failure_listener).addOnProgressListener(_spotimg_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                return spotimg.child(spotimgname).getDownloadUrl();
                            }
                        }).addOnCompleteListener(_spotimg_upload_success_listener);
                    }
                }
            }
        });


        _spotimg_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _spotimg_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
                double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();

            }
        };

        _spotimg_upload_success_listener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> _param1) {
                final String _downloadUrl = _param1.getResult().toString();
                hashmap = new HashMap<>();
                hashmap.put("name", name.getText().toString());
                hashmap.put("address", add.getText().toString());
                hashmap.put("spotcategory", category.getText().toString());
                hashmap.put("spotid", id.getText().toString());
                hashmap.put("spotimgurl", _downloadUrl);
                hashmap.put("spotimgname", spotimgname);
                calendar = Calendar.getInstance();
                hashmap.put("time", new SimpleDateFormat("E dd/MM hh:mm a").format(calendar.getTime()));

                spot.child(id.getText().toString()).updateChildren(hashmap);
                finish();
                Toast.makeText(getApplicationContext(),"Register Successful",Toast.LENGTH_LONG).show();
                finish();
            }
        };

        _spotimg_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
                final long _totalByteCount = _param1.getTotalByteCount();

            }
        };

        _spotimg_delete_success_listener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object _param1) {

            }
        };

        _spotimg_failure_listener = new OnFailureListener() {
            @Override
            public void onFailure(Exception _param1) {
                final String _message = _param1.getMessage();

            }
        };
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

        if (ActivityCompat.checkSelfPermission(AddSpotActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
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
        Intent intent=new Intent(AddSpotActivity.this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        getApplicationContext().startService(intent);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_spotimgPIC:
                if (_resultCode == Activity.RESULT_OK) {
                    ArrayList<String> _filePath = new ArrayList<>();
                    if (_data != null) {
                        if (_data.getClipData() != null) {
                            for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                                ClipData.Item _item = _data.getClipData().getItemAt(_index);
                                _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                            }
                        }
                        else {
                            _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                        }
                    }

                    spotimgname = "";
                    spotimgpath = "";
                    spotimgimgsize = 0;
                    img = 1;
                    spotimgpath = _filePath.get((int)(0));
                    spotimgname = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
                    spotimgimg.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
                    spotimgimg.setVisibility(View.VISIBLE);
                    spotimgimgsize= new java.io.File(spotimgpath).length()/1024;
                    if ((spotimgimgsize < 2000) && (spotimgimgsize > 0)) {
                        spotimgpath = _filePath.get((int)(0));
                        spotimgname = Uri.parse(spotimgpath).getLastPathSegment();
                        spotimgimg.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(spotimgpath, 1024, 1024));
                        img=1;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "spotimg Pic must be less than 2Mb",Toast.LENGTH_LONG).show();
                        spotimgpath = "";
                        spotimgname = "";
                        spotimgimg.setImageResource(R.drawable.default_image);
                        img = 0;
                    }

                }
                else {

                }
                break;
            default:
                break;
        }
    }

}


