package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ScoreboardActivity extends MyAppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ScoreboardActivity";
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mGetReference;
    private SupportMapFragment mapFragment;
    private double lat;
    private double lng;
    private final String DB_CHILD = "Score";
    private final int CAMER_ZOOM = 10;
    private int PERMISSION_ID = 44;
    private FusedLocationProviderClient mFusedLocationClient;

    private Score userScore;
    private int LAST_ID;
    private LocationCallback mLocationCallback;
    private ArrayList<Score> scores;

    private final ArrayList<Integer> img = new ArrayList<>(
            Arrays.asList
                    (R.drawable.first, R.drawable.second, R.drawable.third,
                            R.drawable.fourth, R.drawable.fifth,
                            R.drawable.default_img, R.drawable.default_img,
                            R.drawable.default_img, R.drawable.default_img,
                            R.drawable.default_img));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        this.scores = new ArrayList<>();

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        this.mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                lat = mLastLocation.getLatitude();
                lng = mLastLocation.getLongitude();
            }
        };

        getLastLocation();


        this.mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            userScore = (Score) getIntent().getSerializableExtra(USER_DATA);
            LAST_ID = extra.getInt(ID);
        } else {
            userScore = null;
        }


        this.mGetReference = mDatabase.getReference().child(DB_CHILD);
        Query mDbQuery = mGetReference.orderByChild(EXT_SCORE).limitToLast(10);
        mDbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    scores.add(child.getValue(Score.class));
                }
                Collections.reverse(scores);
                initScoreboard();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        findViewById(R.id.menuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void saveScoreToDb() {
        this.mGetReference = mDatabase.getReference().child(DB_CHILD);
        this.mGetReference.push().setValue(userScore);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Your Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMER_ZOOM));
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude();
                                    lng = location.getLongitude();
                                    if (userScore != null) {
                                        userScore.setY(lat);
                                        userScore.setX(lng);
                                    }
                                    if (LAST_ID == DEF_TAG) {
                                        saveScoreToDb();
                                    }
                                    show();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void show() {
        if (this.mapFragment != null) {
            this.mapFragment.getMapAsync(this);
        }
        initScoreboard();
    }

    private void initScoreboard() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, img, scores, userScore);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, this.mLocationCallback,
                Looper.myLooper()
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            );
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}


