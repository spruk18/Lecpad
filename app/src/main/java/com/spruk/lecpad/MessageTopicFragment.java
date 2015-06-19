package com.spruk.lecpad;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
 * Created by taray on 6/16/2015.
 */
public class MessageTopicFragment extends ListFragment {
    private static final String LOG_TAG = MessageTopicFragment.class.getSimpleName();

    private ProgressDialog progress;
    private boolean connectivityOk;
    private List<String> msgfrom = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_fragment,container,false);
        setHasOptionsMenu(true);
        //SELECT * FROM `tblmsg` WHERE (msgto="cale" || msgfrm="cale") && (msgto="admin" || msgfrm="admin")
        connectivityOk = checkInternetConnection();
        if(connectivityOk)
        {
            // Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Retrieving data");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();


            GetSubjectInfo client = new GetSubjectInfo();
            String msg = Utility.loadSavedPreferences(getActivity(),getString(R.string.user_login_key));

            client.execute(getString(R.string.msg)+"?action=list&msgfrm="+msg);


        }
        else
        {
            Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        msgfrom.clear();
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub

        //Toast.makeText(getActivity(),""+msgfrom.get(position),Toast.LENGTH_SHORT).show();
//

        Bundle bundle = new Bundle();
        bundle.putString("msgto", msgfrom.get(position));
        Fragment fragment = new MessageThreadFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

        //  fragmentManager.beginTransaction().add(R.id.content_frame, fragment).commit();



        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.message_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                //Toast.makeText(getActivity(),"TOAST",Toast.LENGTH_LONG).show();


                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_dialog_new_message);
                dialog.setTitle("Create New Message...");



                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectivityOk = checkInternetConnection();
                        if(connectivityOk)
                        {
                            TextView text = (TextView) dialog.findViewById(R.id.sendto);
                            TextView content = (TextView) dialog.findViewById(R.id.msg);
                            newMessage asd = new newMessage();
                            String user = Utility.loadSavedPreferences(getActivity(),getString(R.string.user_login_key));
                            asd.execute(getString(R.string.msg)+"?action=new&msgfrm="+user+"&msgto="+text.getText().toString()+"&msg="+content.getText().toString ());

                        }
                        else
                        {
                            Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
                        }


                        dialog.dismiss();
                    }
                });

                dialog.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public class newMessage extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            msgfrom.clear();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Retrieving data");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();
            GetSubjectInfo client = new GetSubjectInfo();
            String user = Utility.loadSavedPreferences(getActivity(),getString(R.string.user_login_key));
            client.execute(getString(R.string.msg)+"?action=list&msgfrm="+user);

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
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("msginfo");

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                            // msgfrom.add(jsonChildNode.optString("msgfrm"));

                        }
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "LOGIN FAILED!",Toast.LENGTH_SHORT).show();
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

    public class GetSubjectInfo extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

              ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, msgfrom);
              setListAdapter(adapter);

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
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("msginfo");

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                            msgfrom.add(jsonChildNode.optString("msgfrm"));

                        }
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "LOGIN FAILED!",Toast.LENGTH_SHORT).show();
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
