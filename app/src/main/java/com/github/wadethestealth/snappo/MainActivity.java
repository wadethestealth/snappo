package com.github.wadethestealth.snappo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    String githubURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void snappoButton(View v) {
        githubCalls();
    }
    public void githubCalls(){
        RequestQueue queue = Volley.newRequestQueue(this);
        EditText edit = findViewById(R.id.gituser);
        EditText edit2 = findViewById(R.id.gitrepo);
        TextView text = findViewById(R.id.editText3);
        text.setText("Loading...");
        String url = "https://api.github.com/repos/"+ edit.getText() +"/"+ edit2.getText();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        EditText text = findViewById(R.id.editText3);
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            githubURL = jsonObj.get("html_url").toString();
                            text.setText("Repo Id: " + jsonObj.get("id").toString());
                        } catch(JSONException e) {
                            githubURL = "null";
                            text.setText("Error parsing json.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EditText text = findViewById(R.id.editText3);
                text.setText("This repo does not exist by that user.");
            }
        });
        queue.add(stringRequest);
    }
}
