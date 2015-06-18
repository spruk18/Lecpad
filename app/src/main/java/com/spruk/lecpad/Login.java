package com.spruk.lecpad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Login extends Activity {
    private ProgressDialog progress;
    Button btnLogin;
    Boolean connectivityOk;
    String username;
    String stat;
    EditText us;
    EditText pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        us = (EditText)findViewById(R.id.us);
        pw = (EditText)findViewById(R.id.pw);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectivityOk = checkInternetConnection();
                if(connectivityOk)
                {
                   // Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
                    progress = new ProgressDialog(v.getContext());
                    progress.setMessage("Retrieving data");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();


                    GetInfo client = new GetInfo();
                    client.execute(getString(R.string.siteurl) + "?action=logincp&us=" + us.getText().toString() + "&pw=" + pw.getText().toString());


                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Connection",Toast.LENGTH_LONG).show();
                }
            }
        });






    }

    public class GetInfo extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            progress.dismiss();
            if(username=="")
            {
                Toast.makeText(getApplicationContext(),"Wrong Username/Password",Toast.LENGTH_LONG).show();
            }
            else
            {
               // Toast.makeText(getApplicationContext(),"Logged",Toast.LENGTH_LONG).show();
                if(stat.equals("student"))
                {
                    Utility.savePreferences(getApplicationContext(),getString(R.string.user_login_key),us.getText().toString());
                    Intent i = new Intent(Login.this,StudentMainActivity.class);
                    startActivity(i);
                }
                else if(stat.equals("faculty"))
                {
                    Utility.savePreferences(getApplicationContext(),getString(R.string.user_login_key),us.getText().toString());
                    Intent i = new Intent(Login.this,FacultyMainActivity.class);
                    startActivity(i);
                }
            }
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            JSONArray response = new JSONArray();
            username="";
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpStatus.SC_OK){
                    String responseString = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient1", responseString);
                   // response = new JSONArray(responseString);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseString);
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("userinfo");

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            //Log.v("gigi pogi", jsonChildNode.optString("us") + "haha");
                            username =  jsonChildNode.optString("us");
                            stat = jsonChildNode.optString("stat");

                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "LOGIN FAILED!",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Log.v("CatalogClient2", "Response code:"+ responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }

            return response;
        }


        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }


    private boolean checkInternetConnection() {
        ConnectivityManager conn =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( conn.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                conn.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                conn.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                conn.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
           // Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                conn.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        conn.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
           // Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


}
