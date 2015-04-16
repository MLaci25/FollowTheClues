package com.example.laszlo.followtheclues;

/**
 * Created by Unum on 11/04/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Laszlo on 04/04/2015.
 */
public class LoginScreen extends Activity
{
    //AQuery object
    AQuery aq;

    Button bClear;
    Button bSubmit;
    EditText nameText;
    EditText pwText;
    String user;
    String pass;



    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        aq = new AQuery(this);

        bClear = (Button)findViewById(R.id.clear);
        bSubmit =(Button)findViewById(R.id.submit);
        nameText = (EditText)findViewById(R.id.editName);
        pwText = (EditText)findViewById(R.id.editPassword);

        nameText.setText("Username");
        pwText.setText("Password");

        bClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nameText.setText("");
                pwText.setText("");
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Context context = getApplicationContext();
                CharSequence text = "Submit Clicked";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context,text,duration);
                //toast.show();

                user = nameText.getText().toString();
                pass = pwText.getText().toString();
                login(user,pass);

            }
        });


    }//ENDOFONCREATE

    public void login(String user,String pass)
    {
        //JSON URL
        String url = "http://laszlo-malina.com/App/login.php?user="+ user + "&pass=" + pass ;
        //Make Asynchronous call using AJAX method
        aq.ajax(url, JSONObject.class, this, "userCallback");
    }

    public void userCallback(String url, JSONObject json, AjaxStatus status)
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
                String jsonResponse = json.getJSONArray("List").toString();
                //Using fromJson method deserialize JSON response [Convert JSON array into Java array]
                values = gson.fromJson(jsonResponse, String[].class);

                int size = values.length;

                if(size > 0)
                {
                    startActivity(new Intent(LoginScreen.this, HuntScreen.class));
                }

                else
                {
                    Toast.makeText(aq.getContext(),"User has no authorized access", Toast.LENGTH_LONG).show();
                }

            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                Toast.makeText(aq.getContext(), "Invalid user Name or Password", Toast.LENGTH_LONG).show();

            }
            catch (Exception e)
            {
                Toast.makeText(aq.getContext(), "Cannot convert into Java Array", Toast.LENGTH_LONG).show();
            }

        }
        //When JSON is null
        else
        {

            //When response code is 500 (Internal Server Error)
            if(status.getCode() == 500)
            {
                Toast.makeText(aq.getContext(),"There is an error in your php file!",Toast.LENGTH_SHORT).show();
            }
            //When response code is 404 (Not found)
            else if(status.getCode() == 404)
            {
                Toast.makeText(aq.getContext(),"Cannot read the php file!",Toast.LENGTH_SHORT).show();
            }
            //When response code is other than 500 or 404
            else
            {
                Toast.makeText(aq.getContext(),"Possible Connection Error",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

