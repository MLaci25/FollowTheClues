package com.example.laszlo.followtheclues;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    EditText getAnswer;
    Button checkAnswer;

    AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setUpMapIfNeeded();

        aq = new AQuery(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_logout, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
            case R.id.action_logout:

                Intent myI = new Intent(MapsActivity.this, LoginScreen.class);
                //sending extra stuff to the next class
                startActivity(myI);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                setUpMap();
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
        //Serializable serialized = getIntent().getSerializableExtra("clue");

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
                Boolean go = getIntent().getExtras().getBoolean("VALUE");

                if (correct.equals(answer))
                {


                    if(go == true)
                    {
                        //Toast.makeText(getApplicationContext(), "The answer is correct!!", Toast.LENGTH_SHORT).show();
                        setVisited(answer);

                        String pHunt = getIntent().getExtras().getString("NAME");

                        Intent myIntent = new Intent(MapsActivity.this,Process.class);
                        myIntent.putExtra("hunt",pHunt);

                        startActivity(myIntent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Hunt finished!!!!!", Toast.LENGTH_SHORT).show();
                    }
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
    public void setVisited(String clue)
    {

        String url = "http://laszlo-malina.com/App/setVisited.php?clue="+ clue;
        aq.ajax(url, JSONObject.class, this, "visitCallBack");
        Toast.makeText(getApplicationContext(), "in visited >>"+ clue, Toast.LENGTH_SHORT).show();

    }
    public void visitCallBack(String url, JSONObject json, AjaxStatus status)
    {
        //When JSON is not null
        if (json != null)
        {
            String[] values = null;
            //Create GSON object
            Gson gson = new GsonBuilder().create();
            try
            {
                //Get JSON response by converting JSONArray into String
                String jsonResponse = json.getJSONArray("Update").toString();
                //Using fromJson method deserialize JSON response [Convert JSON array into Java array]
                values = gson.fromJson(jsonResponse, String[].class);

                Toast.makeText(aq.getContext(), "results", Toast.LENGTH_SHORT).show();

            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                //Toast.makeText(aq.getContext(), "Not updated", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(aq.getContext(), "Cannot convert into Java Array", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
                Toast.makeText(aq.getContext(), "The Unknown Error", Toast.LENGTH_SHORT).show();
        }
    }

}
