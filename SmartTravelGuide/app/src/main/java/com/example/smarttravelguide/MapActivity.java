package com.example.smarttravelguide;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.api.directions.v5.DirectionsCriteria.GEOMETRY_POLYLINE;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private CarmenFeature home;
    private AlertDialog.Builder dialog;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    private PermissionsManager permissionsManager;
    private MapboxDirections mapboxDirectionsClient;
    private Handler handler = new Handler();
    private Runnable runnable;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "AbMapActivity";

    private static final float NAVIGATION_LINE_WIDTH = 6;
    private static final float NAVIGATION_LINE_OPACITY = .8f;
    private static final String DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID = "DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID";
    private static final String DRIVING_ROUTE_POLYLINE_SOURCE_ID = "DRIVING_ROUTE_POLYLINE_SOURCE_ID";
    private static final int DRAW_SPEED_MILLISECONDS = 500;

    //Fetch Location
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ResultReceiver resultReceiver,resultReceiver1;
    private static final int REQUEST_CALL = 1;
    String address ,address1;
    double soulat, soulon;
    double deslat, deslon;

    //files
    private CardView cd1, cd2, cd3, cd4, cd5, cd6, cd7, cd8;
    private SharedPreferences file;
    private TextView distance;
    private EditText sourceet,destinationet;
    private FloatingActionButton add,logout;
    private Intent intent = new Intent();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapboxtoken));

        setContentView(R.layout.activity_map);

// This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map);

//        mapView = findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);

        com.google.firebase.FirebaseApp.initializeApp(this);
        getCurrentLocation();
        resultReceiver = new AddressResultReceiver(new Handler());

        resultReceiver1 = new AddressResultReceiver1(new Handler());
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        dialog = new AlertDialog.Builder(this);
        distance = findViewById(R.id.distance);
        cd1 = findViewById(R.id.UI_cardview1);
        cd2 = findViewById(R.id.UI_cardview2);
        cd3 = findViewById(R.id.UI_cardview3);
        cd4 = findViewById(R.id.UI_cardview4);
        cd5 = findViewById(R.id.UI_cardview5);
        cd6 = findViewById(R.id.UI_cardview6);
        cd7 = findViewById(R.id.UI_cardview7);
        cd8 = findViewById(R.id.UI_cardview8);
        sourceet = findViewById(R.id.source);
        add = findViewById(R.id.add);
        destinationet = findViewById(R.id.destination);
        logout = (FloatingActionButton) findViewById(R.id.logout);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
//        if (file.getString("emailid", "").equals("admin@gmail.com")) {
//            _fab.setVisibility(View.VISIBLE);
//        } else {
//            _fab.setVisibility(View.GONE);
//        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setTitle("Logout");
                dialog.setMessage("Do you want to logout?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {
                        FirebaseAuth.getInstance().signOut();
                        intent.setClass(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface _dialog, int _which) {

                    }
                });
                dialog.create().show();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getApplicationContext(), AddSpotActivity.class);
                startActivity(intent);
            }
        });
        cd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BeachActivity.class);
                startActivity(intent);
            }
        });

        cd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HillActivity.class);
                startActivity(intent);
            }
        });

        cd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WaterfallActivity.class);
                startActivity(intent);
            }
        });

        cd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MonumentActivity.class);
                startActivity(intent);
            }
        });

        cd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReligiousActivity.class);
                startActivity(intent);
            }
        });

        cd6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), IslandActivity.class);
                startActivity(intent);
            }
        });

        cd7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WildLifeActivity.class);
                startActivity(intent);
            }
        });

        cd8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EducationalActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
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

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
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
                            soulat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            soulon = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Log.e("Lat",String.valueOf(soulat));
                            Log.e("lon",String.valueOf(soulon));

                            DecimalFormat df = new DecimalFormat("##.####");
                            Location location = new Location("ProvideNA");
                            location.setLatitude(Double.parseDouble(df.format(soulat)));
                            location.setLongitude(Double.parseDouble(df.format(soulon)));
                            origin = Point.fromLngLat(location.getLongitude(),location.getLatitude());
                            fetchAddressFromLatLong(location);
                        }
                    }
                }, Looper.getMainLooper());
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
//            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler){
            super(handler);
        } protected void onReceiveResult(int resultCode,Bundle resultData){
            super.onReceiveResult(resultCode,resultData);
            if(resultCode == Constants.SUCCESS_RESULT){
                address = resultData.getString(Constants.RESULT_DATA_KEY);
                sourceet.setText(address);
                Log.e("RESULT",address);
                if(destinationet.getText().toString().trim().equals(""))
                {
                    if(sourceet.getText().toString().trim().contains("Mumbai"))
                    {
                        file.edit().putString("state","Mumbai").apply();
                    }else if(sourceet.getText().toString().trim().contains("Delhi"))
                    {
                        file.edit().putString("state","Delhi").apply();
                    }else if(sourceet.getText().toString().trim().contains("Kerala"))
                    {
                        file.edit().putString("state","Kerala").apply();
                    }else if(sourceet.getText().toString().trim().contains("Rajasthan"))
                    {
                        file.edit().putString("state","Rajasthan").apply();
                    }else if(sourceet.getText().toString().trim().contains("Karanataka"))
                    {
                        file.edit().putString("state","Karanataka").apply();
                    }else if(sourceet.getText().toString().trim().contains("Goa"))
                    {
                        file.edit().putString("state","Goa").apply();
                    }else if(sourceet.getText().toString().trim().contains("Arunachal Pradesh"))
                    {
                        file.edit().putString("state","Arunachal Pradesh").apply();
                    }else if(sourceet.getText().toString().trim().contains("Uttar Pradesh"))
                    {
                        file.edit().putString("state","Uttar Pradesh").apply();
                    }else if(sourceet.getText().toString().trim().contains("Jammu and Kashmir"))
                    {
                        file.edit().putString("state","Jammu and Kashmir").apply();
                    }else if(sourceet.getText().toString().trim().contains("Tamil Nadu"))
                    {
                        file.edit().putString("state","Tamil Nadu").apply();
                    }else if(sourceet.getText().toString().trim().contains("Bihar"))
                    {
                        file.edit().putString("state","Bihar").apply();
                    }else if(sourceet.getText().toString().trim().contains("Gujarat"))
                    {
                        file.edit().putString("state","Gujarat").apply();
                    }else if(sourceet.getText().toString().trim().contains("Punjab"))
                    {
                        file.edit().putString("state","Punjab").apply();
                    }else if(sourceet.getText().toString().trim().contains("Madhya Pradesh"))
                    {
                        file.edit().putString("state","Madhya Pradesh").apply();
                    }else if(sourceet.getText().toString().trim().contains("Telangana"))
                    {
                        file.edit().putString("state","Telangana").apply();
                    }else if(sourceet.getText().toString().trim().contains("Andhra Pradesh"))
                    {
                        file.edit().putString("state","Andhra Pradesh").apply();
                    }else if(sourceet.getText().toString().trim().contains("Haryana"))
                    {
                        file.edit().putString("state","Haryana").apply();
                    }else if(sourceet.getText().toString().trim().contains("Chhattisgarh"))
                    {
                        file.edit().putString("state","Chhattisgarh").apply();
                    }else if(sourceet.getText().toString().trim().contains("West Bengal"))
                    {
                        file.edit().putString("state","West Bengal").apply();
                    }else if(sourceet.getText().toString().trim().contains("Uttarakhand"))
                    {
                        file.edit().putString("state","Uttarakhand").apply();
                    }else if(sourceet.getText().toString().trim().contains("Assam"))
                    {
                        file.edit().putString("state","Assam").apply();
                    }else if(sourceet.getText().toString().trim().contains("Odisha"))
                    {
                        file.edit().putString("state","Odisha").apply();
                    }else if(sourceet.getText().toString().trim().contains("Jharkhand"))
                    {
                        file.edit().putString("state","Jharkhand").apply();
                    }else if(sourceet.getText().toString().trim().contains("Chandigarh"))
                    {
                        file.edit().putString("state","Chandigarh").apply();
                    }else if(sourceet.getText().toString().trim().contains("Himachal Pradesh"))
                    {
                        file.edit().putString("state","Himachal Pradesh").apply();
                    }else if(sourceet.getText().toString().trim().contains("Nagaland"))
                    {
                        file.edit().putString("state","Nagaland").apply();
                    }else if(sourceet.getText().toString().trim().contains("Sikkim"))
                    {
                        file.edit().putString("state","Sikkim").apply();
                    }else if(sourceet.getText().toString().trim().contains("Manipur"))
                    {
                        file.edit().putString("state","Manipur").apply();
                    }else if(sourceet.getText().toString().trim().contains("Andaman and Nicobar Island"))
                    {
                        file.edit().putString("state","Andaman and Nicobar Island").apply();
                    }else if(sourceet.getText().toString().trim().contains("Tripura"))
                    {
                        file.edit().putString("state","Tripura").apply();
                    }else if(sourceet.getText().toString().trim().contains("Lakshadweep"))
                    {
                        file.edit().putString("state","Lakshadweep").apply();
                    }else if(sourceet.getText().toString().trim().contains("Meghalaya"))
                    {
                        file.edit().putString("state","Meghalaya").apply();
                    }else if(sourceet.getText().toString().trim().contains("Mizoram"))
                    {
                        file.edit().putString("state","Mizoram").apply();
                    }else if(sourceet.getText().toString().trim().contains("Puducherry"))
                    {
                        file.edit().putString("state","Puducherry").apply();
                    }else{
                        file.edit().putString("state","").apply();
                    }
                }
            }else{
                Toast.makeText(getApplicationContext(),resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void fetchAddressFromLatLong(Location location) {
        Intent intent=new Intent(MapActivity.this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        getApplicationContext().startService(intent);
    }
    private class AddressResultReceiver1 extends ResultReceiver {
        AddressResultReceiver1(Handler handler){
            super(handler);
        } protected void onReceiveResult(int resultCode,Bundle resultData){
            super.onReceiveResult(resultCode,resultData);
            if(resultCode == Constants.SUCCESS_RESULT){
                address1 = resultData.getString(Constants.RESULT_DATA_KEY);
                destinationet.setText(address1);
                Log.e("RESULT",address);
                if(destinationet.getText().toString().trim().contains("Mumbai"))
                {
                    file.edit().putString("state","Mumbai").apply();
                }else if(destinationet.getText().toString().trim().contains("Delhi"))
                {
                    file.edit().putString("state","Delhi").apply();
                }else if(destinationet.getText().toString().trim().contains("Kerala"))
                {
                    file.edit().putString("state","Kerala").apply();
                }else if(destinationet.getText().toString().trim().contains("Rajasthan"))
                {
                    file.edit().putString("state","Rajasthan").apply();
                }else if(destinationet.getText().toString().trim().contains("Karanataka"))
                {
                    file.edit().putString("state","Karanataka").apply();
                }else if(destinationet.getText().toString().trim().contains("Goa"))
                {
                    file.edit().putString("state","Goa").apply();
                }else if(destinationet.getText().toString().trim().contains("Arunachal Pradesh"))
                {
                    file.edit().putString("state","Arunachal Pradesh").apply();
                }else if(destinationet.getText().toString().trim().contains("Uttar Pradesh"))
                {
                    file.edit().putString("state","Uttar Pradesh").apply();
                }else if(destinationet.getText().toString().trim().contains("Jammu and Kashmir"))
                {
                    file.edit().putString("state","Jammu and Kashmir").apply();
                }else if(destinationet.getText().toString().trim().contains("Tamil Nadu"))
                {
                    file.edit().putString("state","Tamil Nadu").apply();
                }else if(destinationet.getText().toString().trim().contains("Bihar"))
                {
                    file.edit().putString("state","Bihar").apply();
                }else if(destinationet.getText().toString().trim().contains("Gujarat"))
                {
                    file.edit().putString("state","Gujarat").apply();
                }else if(destinationet.getText().toString().trim().contains("Punjab"))
                {
                    file.edit().putString("state","Punjab").apply();
                }else if(destinationet.getText().toString().trim().contains("Madhya Pradesh"))
                {
                    file.edit().putString("state","Madhya Pradesh").apply();
                }else if(destinationet.getText().toString().trim().contains("Telangana"))
                {
                    file.edit().putString("state","Telangana").apply();
                }else if(destinationet.getText().toString().trim().contains("Andhra Pradesh"))
                {
                    file.edit().putString("state","Andhra Pradesh").apply();
                }else if(destinationet.getText().toString().trim().contains("Haryana"))
                {
                    file.edit().putString("state","Haryana").apply();
                }else if(destinationet.getText().toString().trim().contains("Chhattisgarh"))
                {
                    file.edit().putString("state","Chhattisgarh").apply();
                }else if(destinationet.getText().toString().trim().contains("West Bengal"))
                {
                    file.edit().putString("state","West Bengal").apply();
                }else if(destinationet.getText().toString().trim().contains("Uttarakhand"))
                {
                    file.edit().putString("state","Uttarakhand").apply();
                }else if(destinationet.getText().toString().trim().contains("Assam"))
                {
                    file.edit().putString("state","Assam").apply();
                }else if(destinationet.getText().toString().trim().contains("Odisha"))
                {
                    file.edit().putString("state","Odisha").apply();
                }else if(destinationet.getText().toString().trim().contains("Jharkhand"))
                {
                    file.edit().putString("state","Jharkhand").apply();
                }else if(destinationet.getText().toString().trim().contains("Chandigarh"))
                {
                    file.edit().putString("state","Chandigarh").apply();
                }else if(destinationet.getText().toString().trim().contains("Himachal Pradesh"))
                {
                    file.edit().putString("state","Himachal Pradesh").apply();
                }else if(destinationet.getText().toString().trim().contains("Nagaland"))
                {
                    file.edit().putString("state","Nagaland").apply();
                }else if(destinationet.getText().toString().trim().contains("Sikkim"))
                {
                    file.edit().putString("state","Sikkim").apply();
                }else if(destinationet.getText().toString().trim().contains("Manipur"))
                {
                    file.edit().putString("state","Manipur").apply();
                }else if(destinationet.getText().toString().trim().contains("Andaman and Nicobar Island"))
                {
                    file.edit().putString("state","Andaman and Nicobar Island").apply();
                }else if(destinationet.getText().toString().trim().contains("Tripura"))
                {
                    file.edit().putString("state","Tripura").apply();
                }else if(destinationet.getText().toString().trim().contains("Lakshadweep"))
                {
                    file.edit().putString("state","Lakshadweep").apply();
                }else if(destinationet.getText().toString().trim().contains("Meghalaya"))
                {
                    file.edit().putString("state","Meghalaya").apply();
                }else if(destinationet.getText().toString().trim().contains("Mizoram"))
                {
                    file.edit().putString("state","Mizoram").apply();
                }else if(destinationet.getText().toString().trim().contains("Puducherry"))
                {
                    file.edit().putString("state","Puducherry").apply();
                }else{
                    file.edit().putString("state","").apply();
                }

            }else{
                Toast.makeText(getApplicationContext(),resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void fetchAddressFromLatLong1(Location location) {
        Intent intent=new Intent(MapActivity.this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver1);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        getApplicationContext().startService(intent);
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);

                initSearchFab();

                addUserLocations();
                Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.blue_marker, null);
                Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                style.addImage(symbolIconId, mBitmap);
// Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);

// Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);

                if(origin != null && destination != null)
                    initSource(style);

                initLayers(style);

            }
        });
    }


    private static class DrawRouteRunnable implements Runnable {
        private MapboxMap mapboxMap;
        private List<LegStep> steps;
        private List<Feature> drivingRoutePolyLineFeatureList;
        private Handler handler;
        private int counterIndex;

        DrawRouteRunnable(MapboxMap mapboxMap, List<LegStep> steps, Handler handler) {
            this.mapboxMap = mapboxMap;
            this.steps = steps;
            this.handler = handler;
            this.counterIndex = 0;
            drivingRoutePolyLineFeatureList = new ArrayList<>();
        }

        @Override
        public void run() {
            if (counterIndex < steps.size()) {
                LegStep singleStep = steps.get(counterIndex);
                if (singleStep != null && singleStep.geometry() != null) {
                    LineString lineStringRepresentingSingleStep = LineString.fromPolyline(
                            singleStep.geometry(), Constants.PRECISION_6);
                    Feature featureLineString = Feature.fromGeometry(lineStringRepresentingSingleStep);
                    drivingRoutePolyLineFeatureList.add(featureLineString);
                }
                if (mapboxMap.getStyle() != null) {
                    GeoJsonSource source = mapboxMap.getStyle().getSourceAs(DRIVING_ROUTE_POLYLINE_SOURCE_ID);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(drivingRoutePolyLineFeatureList));
                    }
                }
                counterIndex++;
                handler.postDelayed(this, DRAW_SPEED_MILLISECONDS);
            }
        }
    }


    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.red_marker)));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }


//    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
//        client = MapboxDirections.builder()
//                .origin(origin)
//                .destination(destination)
//                .overview(DirectionsCriteria.OVERVIEW_FULL)
//                .spotimg(DirectionsCriteria.spotimg_DRIVING)
//                .accessToken(getString(R.string.mapboxtoken))
//                .build();
//
//        client.enqueueCall(new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//// You can get the generic HTTP info about the response
//                Timber.d("Response code: " + response.code());
//                if (response.body() == null) {
//                    Timber.e("No routes found, make sure you set the right user and access token.");
//                    return;
//                } else if (response.body().routes().size() < 1) {
//                    Timber.e("No routes found");
//                    return;
//                }
//
//// Get the directions route
//                currentRoute = response.body().routes().get(0);
//
//// Make a toast which displays the route's distance
////                Toast.makeText(MapActivity.this, String.format(
////                        getString(R.string.directions_activity_toast_message),
////                        currentRoute.distance()), Toast.LENGTH_SHORT).show();
//
//                if (mapboxMap != null) {
//                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
//                        @Override
//                        public void onStyleLoaded(@NonNull Style style) {
//
//// Retrieve and update the source designated for showing the directions route
//                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
//
//// Create a LineString with the directions route's geometry and
//// reset the GeoJSON source for the route LineLayer source
//                            if (source != null) {
//                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
//                Timber.e("Error: " + throwable.getMessage());
//                Toast.makeText(MapActivity.this, "Error: " + throwable.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void getRoute(Point origin, Point destination)
    {
        NavigationRoute.builder(this).accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if(response.body() == null)
                        {
                            Log.e(TAG,"No Route Found Check User Access Token");
                            return;
                        }else if( response.body().routes().size() == 0)
                        {
                            Log.e(TAG,"No Route Found");
                            return;
                        }
                        DirectionsRoute currentRoute = response.body().routes().get(0);
                        if(navigationMapRoute != null)
                        {
                            navigationMapRoute.removeRoute();
                        }else{
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error : "+t.getMessage());
                    }
                });
    }

    private void initSearchFab() {
        findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapboxtoken))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .addInjectedFeature(home)
                                .addInjectedFeature(work)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MapActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[] {0f, -8f})
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

// Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

// Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
// Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }

// Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                            Log.e("DestLocation",String.valueOf(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude())));
                            Log.e("DestLatitude",String.valueOf(((Point) selectedCarmenFeature.geometry()).latitude()));
                            Log.e("DestLongitude",String.valueOf(((Point) selectedCarmenFeature.geometry()).longitude()));
                            deslat = (((Point) selectedCarmenFeature.geometry()).latitude());
                            deslon = (((Point) selectedCarmenFeature.geometry()).longitude());
                            Location destination1 = new Location("ProvideNA");
                            destination1.setLatitude(deslat);
                            destination1.setLongitude(deslon);
                            fetchAddressFromLatLong1(destination1);
                            destination = Point.fromLngLat(destination1.getLongitude(),destination1.getLatitude());
                            Log.e("SourLocation",String.valueOf(origin));
                            Log.e("SourLatitude",String.valueOf(origin.latitude()));
                            Log.e("SourLongitude",String.valueOf(origin.longitude()));
                            getRoute(origin, destination);
//                            getDirectionsRoute(origin, destination);
                            float result[] = new float[10];
                            android.location.Location.distanceBetween(origin.latitude(),origin.longitude(),destination.latitude(),destination.longitude(),result);
                            distance.setText("Distance : "+result[0]/1000+" Km");
                }
            }
        }
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}