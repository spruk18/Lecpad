package com.spruk.lecpad;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taray on 6/19/2015.
 */
public class NotificationFragment extends ListFragment {
    private static final String LOG_TAG = MessageTopicFragment.class.getSimpleName();
    private ProgressDialog progress;
    List<NotificationList> data = new ArrayList<NotificationList>();
    boolean connectivityOk;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_layout,container,false);

        connectivityOk = checkInternetConnection();
        if(connectivityOk)
        {
            // Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Retrieving data");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();


            GetSubjectInfo client = new GetSubjectInfo();
           // String msg = Utility.loadSavedPreferences(getActivity(),getString(R.string.user_login_key));

            client.execute(getString(R.string.notif));


        }
        else
        {
            Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
        }


        return rootView;
    }

    public class GetSubjectInfo extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            //  ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, subjectcode);
            //  setListAdapter(adapter);
            NotificationListAdapter adapter = new NotificationListAdapter(getActivity(),data);

            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            registerForContextMenu(getListView());
            progress.dismiss();




        }

        @Override
        protected JSONArray doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            JSONArray response = new JSONArray();

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
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("notifinfo");

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                            //Log.v("gigi pogi", jsonChildNode.optString("subjectcode") + "haha");
//                            subjectcode.add(jsonChildNode.optString("title"));
//                            subjid.add(Integer.parseInt(jsonChildNode.optString("id")));
//                            int id = Integer.parseInt(jsonChildNode.optString("id"));
//                            String sub = jsonChildNode.optString("subjectcode");
//                            String ddate = jsonChildNode.optString("ddate");
//                            String title = jsonChildNode.optString("title");
//                            String content = jsonChildNode.optString("content");
//                            String uploadedfile = jsonChildNode.optString("uploadedfile");
//                            data.add(new SubjectList(id,sub,ddate,title,content, uploadedfile));
                            String topic =  jsonChildNode.optString("topic");
                            String content =  jsonChildNode.optString("content");
                            String ddate =  jsonChildNode.optString("ddate");
                            data.add(new NotificationList(topic,content,ddate));

                        }
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
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
        ConnectivityManager conn =(ConnectivityManager)getActivity().getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);

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
