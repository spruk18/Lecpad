package com.spruk.lecpad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
public class FacultyLectureFragment extends ListFragment {
    String subj;
    private List<String> subjectcode = new ArrayList<String>();
    private List<Integer> subjid = new ArrayList<Integer>();
    int id;
    boolean connectivityOk;
    ProgressDialog progress;

    @Override
    public void onResume() {
        subjectcode.clear();
        subjid.clear();
        super.onResume();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.faculty_lecture,container,false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("subjid", 0);
            subj = bundle.getString("subjectcode");
        }

        connectivityOk = checkInternetConnection();
        if(connectivityOk)
        {

            progress = new ProgressDialog(getActivity());
            progress.setMessage("Retrieving data");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();

             //Toast.makeText(getActivity(), "" + id,Toast.LENGTH_LONG).show();

            GetSubjectInfo client = new GetSubjectInfo();
            String uid = Utility.loadSavedPreferences(getActivity(),getString(R.string.user_login_key));
            client.execute(getString(R.string.siteurl) + "?action=getfacultylecture&subjid=" + id);


        }
        else
        {
            Toast.makeText(getActivity(),"No Connection",Toast.LENGTH_LONG).show();
        }


        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Bundle bundle = new Bundle();
        bundle.putInt("subjid", subjid.get(position));
       // bundle.putString("subjectcode", subjectcode.get(position));
        Fragment fragment = new FacultyLectureViewFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();



        super.onListItemClick(l, v, position, id);
    }

    public class GetSubjectInfo extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, subjectcode);

            setListAdapter(adapter);

            adapter.notifyDataSetChanged();
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
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("userinfo");

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            //Log.v("gigi pogi", jsonChildNode.optString("subjectcode") + "haha");
                            subjectcode.add(jsonChildNode.optString("title"));
                            subjid.add(Integer.parseInt(jsonChildNode.optString("subjid")));


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
