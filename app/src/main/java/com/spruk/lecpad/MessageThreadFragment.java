package com.spruk.lecpad;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taray on 6/16/2015.
 */
public class MessageThreadFragment extends ListFragment {
    private static final String LOG_TAG = MessageThreadFragment.class.getSimpleName();
    private ProgressDialog progress;
    private boolean connectivityOk;
    private List<String> msgfrom = new ArrayList<String>();
    private List<String> msgto = new ArrayList<String>();
    private List<String> content = new ArrayList<String>();
    private List<String> ddate = new ArrayList<String>();
    List<MessageList> data = new ArrayList<MessageList>();
    String us;
    String to;
    private Button btnEnter;
    private EditText EditChat;

    String frm ;
    String frto;
    String frmsg ;
    String frdate ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_layout,container,false);
        setHasOptionsMenu(true);
        to="";
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            to = bundle.getString("msgto", "");
        }

        connectivityOk = checkInternetConnection();
        if(connectivityOk)
        {
            // Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Retrieving data");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();


            GetSubjectInfo client = new GetSubjectInfo();
            us = Utility.loadSavedPreferences(getActivity(),getString(R.string.user_login_key));


            client.execute(getString(R.string.msg)+"?action=thread&msgfrm="+us+"&msgto="+to);


        }
        else
        {
            Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
        }
        btnEnter = (Button)rootView.findViewById(R.id.btn_enter);
        EditChat = (EditText)rootView.findViewById(R.id.chat_input);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),""+ EditChat.getText().toString(),Toast.LENGTH_LONG).show();
                data.clear();
                msgfrom.clear();
                msgto.clear();
                content.clear();
                ddate.clear();

                EnterChat zxc = new EnterChat();
                zxc.execute(getString(R.string.msg)+"?action=post&msgfrm="+us+"&msgto="+to+"&msg="+EditChat.getText().toString());
                EditChat.setText("");
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EditChat.getWindowToken(), 0);
            }
        });

        return rootView;
    }



    public class GetSubjectInfo extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            MessageListAdapter adapter = new MessageListAdapter(getActivity(),data);

            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            registerForContextMenu(getListView());
            getListView().setDivider(null);
            getListView().setSelection(adapter.getCount() - 1);
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, content);
            //setListAdapter(adapter);

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
                            msgto.add(jsonChildNode.optString("msgto"));
                            content.add(jsonChildNode.optString("msg"));
                            ddate.add(jsonChildNode.optString("ddate"));

                            frm = jsonChildNode.optString("msgfrm");
                            frto = jsonChildNode.optString("msgto");
                            frmsg = jsonChildNode.optString("msg");
                            frdate = jsonChildNode.optString("ddate");

                           // Log.v(LOG_TAG,frm + " " + frto +" " + frmsg);
                            int f;
                            if(frm.equals(us))
                            {
                                f=0;
                            }
                            else
                            {
                                f=1;
                            }
                            data.add(new MessageList(frm,frto,frmsg,frdate,f));
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

    public class EnterChat extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            MessageListAdapter adapter = new MessageListAdapter(getActivity(),data);

            setListAdapter(adapter);
            adapter.notifyDataSetChanged();
            registerForContextMenu(getListView());
            getListView().setDivider(null);
            getListView().setSelection(adapter.getCount() - 1);
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, content);
            //setListAdapter(adapter);




          //  data.add(new MessageList(frm,frto,EditChat.getText().toString(),frdate,0));

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
                            msgto.add(jsonChildNode.optString("msgto"));
                            content.add(jsonChildNode.optString("msg"));
                            ddate.add(jsonChildNode.optString("ddate"));

                            frm = jsonChildNode.optString("msgfrm");
                            frto = jsonChildNode.optString("msgto");
                            frmsg = jsonChildNode.optString("msg");
                            frdate = jsonChildNode.optString("ddate");

                            // Log.v(LOG_TAG,frm + " " + frto +" " + frmsg);
                            int f;
                            if(frm.equals(us))
                            {
                                f=0;
                            }
                            else
                            {
                                f=1;
                            }
                            data.add(new MessageList(frm,frto,frmsg,frdate,f));
                        }
                    } catch (JSONException e) {
                        //Toast.makeText(getActivity(), "LOGIN FAILED!", Toast.LENGTH_SHORT).show();
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

