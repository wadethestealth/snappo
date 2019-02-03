package com.github.wadethestealth.snappo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.snapchat.kit.sdk.SnapCreative;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.creative.api.SnapCreativeKitApi;
import com.snapchat.kit.sdk.creative.models.SnapLiveCameraContent;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    String githubURL;
    String snapID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isUserLoggedIn = SnapLogin.isUserLoggedIn(this);
        //addSnapLoginListeners();
        View root = findViewById(R.id.button);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                githubCalls();

                //
            }});

    }
    public void openSnapCamera() {
        SnapCreativeKitApi snapCreativeKitApi = SnapCreative.getApi(this);
        SnapLiveCameraContent snapLiveCameraContent = new SnapLiveCameraContent();
        snapCreativeKitApi.send(snapLiveCameraContent);
    }
    public void retrieveSnapID() {
        String query = "{me{externalId}}";
        SnapLogin.fetchUserData(this, query, null, new FetchUserDataCallback() {
            @Override
            public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                if (userDataResponse == null || userDataResponse.getData() == null) {
                    return;
                }

                MeData meData = userDataResponse.getData().getMe();
                if (meData == null) {
                    return;
                }

                snapID = userDataResponse.getData().getMe().getExternalId();
            }

            @Override
            public void onFailure(boolean isNetworkError, int statusCode) {

            }
        });
    }
    public void addSnapLoginListeners() {
        final LoginStateController.OnLoginStateChangedListener mLoginStateChangedListener =
                new LoginStateController.OnLoginStateChangedListener() {
                    @Override
                    public void onLoginSucceeded() {
                        // Here you could update UI to show login success
                        retrieveSnapID();
                    }

                    @Override
                    public void onLoginFailed() {
                        // Here you could update UI to show login failure
                    }

                    @Override
                    public void onLogout() {
                        // Here you could update UI to reflect logged out state
                    }
                };
        SnapLogin.getLoginStateController(this).addOnLoginStateChangedListener(mLoginStateChangedListener);
    }
    public void startSnapLogin(){
        SnapLogin.getAuthTokenManager(this).startTokenGrant();
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
                            text.setText(
                                    "Repo Id: " + jsonObj.get("id").toString()
                                    +"\nDescription: " + jsonObj.get("description").toString()
                                    +"\nStars: " + jsonObj.get("stargazers_count").toString()
                                    +"\nFollowers: " + jsonObj.get("watchers_count").toString()
                                    +"\nForks: " + jsonObj.get("forks_count").toString()
                            );
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    openSnapCamera();
                                }
                            }, 7000);
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
