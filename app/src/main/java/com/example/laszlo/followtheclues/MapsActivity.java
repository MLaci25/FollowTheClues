package com.example.laszlo.followtheclues;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    EditText getAnswer;
    Button checkAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
            {
                //setUpMap();
                addNewLocation();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap()
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
    private void addNewLocation()
    {

        String clue = getIntent().getExtras().getString("clue");

        String lat = getIntent().getExtras().getString("lati");
        double newLatitude = Double.parseDouble(lat);
        String lng = getIntent().getExtras().getString("longi");
        double newLongitude = Double.parseDouble(lng);

        getAnswer = (EditText)findViewById(R.id.editText);
        checkAnswer = (Button)findViewById(R.id.button2);

        checkAnswer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String correct = getAnswer.getText().toString();
                String answer = getIntent().getExtras().getString("ans");

                if (correct.equals(answer))
                {
                    Toast.makeText(getApplicationContext(), "The answer is correct!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Wrong Answer!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        String hint = getIntent().getExtras().getString("hint");
        String newHint = "Hint: " + hint;
        LatLng latLng = new LatLng(newLatitude, newLongitude);

        String marker = "Clue: " +  clue;

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(marker)
                .snippet(newHint);

        mMap.addMarker(options);
        CameraUpdate newLocation = CameraUpdateFactory.newLatLng(new LatLng(newLatitude,newLongitude));
        mMap.moveCamera(newLocation);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13.5f));

    }
}
