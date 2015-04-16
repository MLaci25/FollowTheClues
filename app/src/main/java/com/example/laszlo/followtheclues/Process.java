package com.example.laszlo.followtheclues;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Unum on 15/04/2015.
 */
public class Process extends Activity
{
    AQuery aq;
    Boolean values;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        aq = new AQuery(this);

        String newHunt = getIntent().getExtras().getString("hunt");

        displayHunt(newHunt);
    }
    public void displayHunt(String hunt)
    {

        String url = "http://laszlo-malina.com/App/displayHunt.php?hunt="+ hunt;
        aq.ajax(url, JSONObject.class, this, "huntCallback");
    }
    public void huntCallback(String url,JSONObject json, AjaxStatus status)
    {

        //When JSON is not null
        if (json != null)
        {

            String[] info = null;

            //Create GSON object
            Gson gson = new GsonBuilder().create();

            try
            {
                //Get JSON response by converting JSONArray into String
                String jsonResponse = json.getJSONArray("Info").toString();

                //Using fromJson method deserialize JSON response [Convert JSON array into Java array]
                info = gson.fromJson(jsonResponse, String[].class);

                if(info.length>0)
                {
                   values = true;
                }
                else
                {
                    values = false;
                }

                //Toast.makeText(aq.getContext(), "Data: " + info[i] + " at index " + i, Toast.LENGTH_SHORT).show();
                String iClue = info[0];
                String iAnswer = info[1] ;
                String iLat = info[2];
                String iLng = info[3];
                String iHint = info[4];

                //Toast.makeText(aq.getContext(), iClue, Toast.LENGTH_SHORT).show();
                String pHunt = getIntent().getExtras().getString("hunt");

                Intent myIntent = new Intent(Process.this, MapsActivity.class);
                //sending extra stuff to the next class
                myIntent.putExtra("clue",iClue);
                myIntent.putExtra("ans",iAnswer);
                myIntent.putExtra("lati",iLat);
                myIntent.putExtra("longi",iLng);
                myIntent.putExtra("hint",iHint);
                myIntent.putExtra("VALUE",values);


                myIntent.putExtra("NAME",pHunt);

                startActivity(myIntent);

            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                Toast.makeText(aq.getContext(), "Treasure Hunt finished, Try another one", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(Process.this,HuntScreen.class);
                startActivity(myIntent);
                finish();
            }
            catch (Exception e)
            {
                Toast.makeText(aq.getContext(), "Cannot convert Array", Toast.LENGTH_LONG).show();
            }
        }
        //When JSON is null
        else
        {
            //When response code is 500 (Internal Server Error)
            if (status.getCode() == 500)
            {
                Toast.makeText(aq.getContext(), "Error in file!", Toast.LENGTH_SHORT).show();
            }
            //When response code is 404 (Not found)
            else if (status.getCode() == 404)
            {
                Toast.makeText(aq.getContext(), "Cannot read  file!", Toast.LENGTH_SHORT).show();
            }
            //When response code is other than 500 or 404
            else
            {
                Toast.makeText(aq.getContext(), "Treasure Hunt finished, Try another one", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(Process.this,HuntScreen.class);
                startActivity(myIntent);
                finish();
            }
        }
    }

}
