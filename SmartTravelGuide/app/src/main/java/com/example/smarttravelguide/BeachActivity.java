package com.example.smarttravelguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class BeachActivity extends AppCompatActivity {

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
    private ListView listview1;
    private FloatingActionButton _fab;
    private Intent intent = new Intent();
    private SharedPreferences file;
    private AlertDialog.Builder dialog;

    //Database
    public final int REQ_CD_CAMERA = 101;
    private HashMap<String, Object> usermaps = new HashMap<>();
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> spotlistmap = new ArrayList<>();
    private ArrayList<String> spotliststring = new ArrayList<>();

    private DatabaseReference spot = _firebase.getReference("spot");
    private ChildEventListener _spot_child_listener;
    private Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private File _file_camera;
//    private DatabaseReference users = _firebase.getReference("users");
//    private ChildEventListener _users_child_listener;

    private FirebaseAuth fauth;
    private OnCompleteListener<AuthResult> _fauth_create_user_listener;
    private OnCompleteListener<AuthResult> _fauth_sign_in_listener;
    private OnCompleteListener<Void> _fauth_reset_password_listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);
        com.google.firebase.FirebaseApp.initializeApp(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        } else {

        }
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
//        _fab = findViewById(R.id._fabsearch);
        listview1 = findViewById(R.id.listview1);
        dialog = new AlertDialog.Builder(this);
        _file_camera = FileUtil.createNewPictureFile(getApplicationContext());
        Uri _uri_camera = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            _uri_camera = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_camera);
//        } else {
            _uri_camera = Uri.fromFile(_file_camera);
        }
        camera.putExtra(MediaStore.EXTRA_OUTPUT, _uri_camera);
        camera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);



        _spot_child_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("address").toString().contains(file.getString("state", "")) && (_childValue.get("spotcategory").toString().trim().toLowerCase().equals("beach")))
                {
                    map = new HashMap<>();
                    map.put("name", _childValue.get("name").toString());
                    map.put("address", _childValue.get("address").toString());
                    map.put("spotid", _childValue.get("spotid").toString());
                    map.put("spotcategory", _childValue.get("spotcategory").toString());
                    map.put("spotimgurl",_childValue.get("spotimgurl").toString());
                    map.put("spotimgname",_childValue.get("spotimgname").toString());
                    spotlistmap.add(map);
                } else {
                }
                listview1.setAdapter(new Listview1Adapter(spotlistmap));
                ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot _param1, String _param2) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("address").toString().contains(file.getString("state", "")) && (_childValue.get("spotcategory").toString().trim().toLowerCase().equals("beach")))
                {
                    map = new HashMap<>();
                    map.put("name", _childValue.get("name").toString());
                    map.put("address", _childValue.get("address").toString());
                    map.put("spotid", _childValue.get("spotid").toString());
                    map.put("spotcategory", _childValue.get("spotcategory").toString());
                    map.put("spotimgurl",_childValue.get("spotimgurl").toString());
                    map.put("spotimgname",_childValue.get("spotimgname").toString());
                    spotlistmap.add(map);
                } else {
                }
                listview1.setAdapter(new Listview1Adapter(spotlistmap));
                ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot _param1, String _param2) {

            }


            @Override
            public void onChildRemoved(DataSnapshot _param1) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = _param1.getKey();
                final HashMap<String, Object> _childValue = _param1.getValue(_ind);
                if (_childValue.get("address").toString().contains(file.getString("state", "")) && (_childValue.get("spotcategory").toString().trim().toLowerCase().equals("beach")))
                {
                    map = new HashMap<>();
                    map.put("name", _childValue.get("name").toString());
                    map.put("address", _childValue.get("address").toString());
                    map.put("spotid", _childValue.get("spotid").toString());
                    map.put("spotcategory", _childValue.get("spotcategory").toString());
                    map.put("spotimgurl",_childValue.get("spotimgurl").toString());
                    map.put("spotimgname",_childValue.get("spotimgname").toString());
                    spotlistmap.add(map);
                } else {
                }
                listview1.setAdapter(new Listview1Adapter(spotlistmap));
                ((BaseAdapter) listview1.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError _param1) {
                final int _errorCode = _param1.getCode();
                final String _errorMessage = _param1.getMessage();

            }
        };
        spot.addChildEventListener(_spot_child_listener);

    }



    public class Listview1Adapter extends BaseAdapter {
        ArrayList<HashMap<String, Object>> _data;
        public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public HashMap<String, Object> getItem(int _index) {
            return _data.get(_index);
        }

        @Override
        public long getItemId(int _index) {
            return _index;
        }
        @Override
        public View getView(final int _position, View _view, ViewGroup _viewGroup) {
            LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _v = _view;
            if (_v == null) {
                _v = _inflater.inflate(R.layout.spot, null);
            }


            final LinearLayout linear1 = (LinearLayout) _v.findViewById(R.id.linear1);
            final LinearLayout linear2 = (LinearLayout) _v.findViewById(R.id.linear2);
            final ImageView imageview1 = (ImageView) _v.findViewById(R.id.imageview1);
            final TextView address = (TextView) _v.findViewById(R.id.address);
            final TextView spotcategory = (TextView) _v.findViewById(R.id.spotcategory);
            final TextView name = (TextView) _v.findViewById(R.id.name);

            address.setText(spotlistmap.get((int)_position).get("address").toString());
            name.setText(spotlistmap.get((int)_position).get("name").toString());
            spotcategory.setText(spotlistmap.get((int)_position).get("spotcategory").toString());
//            totalstudents.setText("Total Students : ".concat(spotlistmap.get((int)_position).get("totalstudents").toString()));
            if (spotlistmap.get((int)_position).get("spotimgurl").toString().equals("")) {
                imageview1.setVisibility(View.GONE);
            }
            else {
                imageview1.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(Uri.parse(spotlistmap.get((int)_position).get("spotimgurl").toString())).into(imageview1);
            }

            return _v;
        }


    }
}