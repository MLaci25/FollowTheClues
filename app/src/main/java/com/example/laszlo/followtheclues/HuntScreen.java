package com.example.laszlo.followtheclues;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Laszlo on 25/03/2015.
 */
public class HuntScreen extends Activity {
    //AQuery object
    AQuery aq;
    //list Object
    Button button;
    //list Spinner Ctrl Object
    ListView list;

    ProgressBar newProgressBar;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_hunt);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //Instantiate AQuery Object

        aq = new AQuery(this);

        button = (Button) findViewById(R.id.button);
        list = (ListView) findViewById(R.id.listView);
        list.setClickable(false);

        newProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        newProgressBar.setVisibility(View.GONE);

        //set listener on button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getHunt("select");
                newProgressBar.setVisibility(View.VISIBLE);
            }
        });

    }//ENDOFONCREATE

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_logout, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_logout:

                Intent myI = new Intent(HuntScreen.this, LoginScreen.class);
                //sending extra stuff to the next class
                startActivity(myI);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getHunt(String hunt) {
        //JSON URL
        String url = "http://laszlo-malina.com/App/downloadHunt.php?hunt=" + hunt;
        //Make Asynchronous call using AJAX method
        aq.progress(newProgressBar).ajax(url, JSONObject.class, this, "jsonCallBack");
    }

    public void jsonCallBack(String url, JSONObject json, AjaxStatus status) {
        //When JSON is not null
        if (json != null) {
            String[] values = null;
            //Create GSON object
            Gson gson = new GsonBuilder().create();
            try {
                //Get JSON response by converting JSONArray into String
                String jsonResponse = json.getJSONArray("List").toString();
                //Using fromJson method deserialize JSON response [Convert JSON array into Java array]
                values = gson.fromJson(jsonResponse, String[].class);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Toast.makeText(aq.getContext(), "Error in parsing JSON", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(aq.getContext(), "Cannot convert into Java Array", Toast.LENGTH_LONG).show();
            }
            //Set list adapter with created Java array 'values'
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getApplicationContext(), android.R.layout.simple_dropdown_item_1line, values);
            list.setAdapter(adapter);

            //store array values into new final array
            final String[] newValues = values;

            //set listener on list
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String name = newValues[position];

                    //Toast.makeText(getApplicationContext(), name + " is chosen", Toast.LENGTH_LONG).show();

                    Intent myIntent = new Intent(HuntScreen.this,Process.class);
                    //sending extra stuff to the next class
                    myIntent.putExtra("hunt",name);

                    //displayHunt(name);
                    /*Get details for each clue
                    then use them in the next actvity
                    to display on/with marker
                    */
                    startActivity(myIntent);
                }
            });

        }
        //When JSON is null
        else {
            //When response code is 500 (Internal Server Error)
            if (status.getCode() == 500) {
                Toast.makeText(aq.getContext(), "Status Error: 500", Toast.LENGTH_SHORT).show();
            }
            //When response code is 404 (Not found)
            else if (status.getCode() == 404) {
                Toast.makeText(aq.getContext(), "Status Error: 404", Toast.LENGTH_SHORT).show();
            }
            //When response code is other than 500 or 404
            else {
                Toast.makeText(aq.getContext(), "Connection required", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
