package com.spruk.lecpad;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
 * Created by taray on 6/19/2015.
 */
public class FacultyStudentListFragment extends ListFragment {
    public final String LOG_TAG = FacultyStudentListFragment.class.getSimpleName();
    ProgressDialog progress;
    boolean connectivityOk;
    int id;
    List<FacultyStudentList> data = new ArrayList<FacultyStudentList>();
    String mescore;
    int studid;

    @Override
    public void onListItemClick(ListView l, View v, int position, long zxc) {
      //  Toast.makeText(getActivity(), data.get(position).getId() + " " + id,Toast.LENGTH_LONG).show();
        studid = data.get(position).getId();
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dailog_score);
        dialog.setTitle("Set Score");

        EditText score = (EditText) dialog.findViewById(R.id.myscore);
         mescore = data.get(position).getLecscore();

         score.setText(mescore);

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connectivityOk = checkInternetConnection();
                if(connectivityOk)
                {

                    progress = new ProgressDialog(getActivity());
                    progress.setMessage("Saving Score");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.show();
                    setScore client = new setScore();
                    EditText newscore = (EditText) dialog.findViewById(R.id.myscore);
                    client.execute(getString(R.string.siteurl) + "?action=savescorestud&lecid=" + id + "&studid=" + studid + "&score=" + newscore.getText().toString());
                    //Toast.makeText(getActivity(), getString(R.string.siteurl) + "?action=savescorestud&lecid=" + id + "&studid=" + studid + "&score=" + newscore.getText().toString(), Toast.LENGTH_LONG).show();
                    Log.v(LOG_TAG,getString(R.string.siteurl) + "?action=savescorestud&lecid=" + id + "&studid=" + studid + "&score=" + newscore.getText().toString());




                }
                else
                {
                    Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
        super.onListItemClick(l, v, position, zxc);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.faculty_student_list,container,false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("lecid", 0);
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

            client.execute(getString(R.string.siteurl) + "?action=getfacultystudentlist&lecid=" + id);


        }
        else
        {
            Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    public class setScore extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            progress.dismiss();

            connectivityOk = checkInternetConnection();
            if(connectivityOk)
            {

                progress = new ProgressDialog(getActivity());
                progress.setMessage("Retrieving data");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();
                data.clear();
                GetSubjectInfo client = new GetSubjectInfo();
                client.execute(getString(R.string.siteurl) + "?action=getfacultystudentlist&lecid=" + id);


            }
            else
            {
                Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
            }

           // Bundle bundle = new Bundle();
           // bundle.putInt("lecid", id);
           // Fragment fragment = new FacultyStudentListFragment();
           // fragment.setArguments(bundle);
           // FragmentManager fragmentManager = getFragmentManager();
           // fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).detach(fragment).attach(fragment).commit();

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


    public class GetSubjectInfo extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {
            data.clear();
            super.onPreExecute();
        }
        public class FacultyStudentListAdapter extends ArrayAdapter<FacultyStudentList> {
            private LayoutInflater inflater;
            private List<FacultyStudentList> data;
            Context cont;

            public FacultyStudentListAdapter(Context context, List<FacultyStudentList> objects) {
                super(context, R.layout.faculty_student_list_item, objects);
                cont = context;
                inflater = LayoutInflater.from(context);
                this.data = objects;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.faculty_student_list_item, null);
                    holder = new ViewHolder();

                    holder.studname = (TextView) convertView.findViewById(R.id.txtstudname);
                    holder.lecscore = (TextView) convertView.findViewById(R.id.txtscore);
                    holder.attachment = (ImageButton) convertView.findViewById(R.id.btn_attachment);
                    holder.attachment.setFocusable(false);
                    convertView.setTag(holder);
                }

                else
                {
                    holder = (ViewHolder) convertView.getTag();

                }


                holder.studname.setText((CharSequence) data.get(position).getStudname());
                holder.lecscore.setText((CharSequence) data.get(position).getLecscore());

                if(data.get(position).getStudAttachment().equals(""))
                {
                    holder.attachment.setVisibility(View.INVISIBLE);
                }
                else
                {
                    holder.attachment.setVisibility(View.VISIBLE);
                }

                holder.attachment.setOnClickListener(mBuyButtonClickListener);

                return convertView;
            }

            class ViewHolder {
                TextView studname;
                TextView lecscore;
                ImageButton attachment;


            }
        }

        private View.OnClickListener mBuyButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = getListView().getPositionForView(v);
                if (position != ListView.INVALID_POSITION) {


                    String icon = Utility.getFileExtension(data.get(position).getStudAttachment());
                    //Toast.makeText(getActivity(),data.get(position).getStudAttachment() + " " + icon,Toast.LENGTH_LONG).show();
                    if(icon.equals("jpg") || icon.equals("png"))
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("filename",data.get(position).getStudAttachment());
                        Fragment fragment = new ImageViewFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                    }
                    else
                    {
                        Bundle bundle = new Bundle();
                        bundle.putInt("subjid", 0);
                        bundle.putInt("lecture", 0);
                        bundle.putString("filename",data.get(position).getStudAttachment());

                        Fragment fragment = new WebViewFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                    }

                }
            }
        };

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            FacultyStudentListAdapter adapter = new FacultyStudentListAdapter(getActivity(),data);

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
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("userinfo");

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            //Log.v("gigi pogi", jsonChildNode.optString("subjectcode") + "haha");
                            // subjectcode.add(jsonChildNode.optString("title"));
                            //  subjid.add(Integer.parseInt(jsonChildNode.optString("subjid")));
                            int stuidid = Integer.parseInt(jsonChildNode.optString("studid"));
                            String studname = jsonChildNode.optString("studname");
                            String studattachment = jsonChildNode.optString("studattachment");
                            String lecscore = jsonChildNode.optString("lecscore");

                           data.add(new FacultyStudentList(stuidid,studname,studattachment,lecscore));


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
