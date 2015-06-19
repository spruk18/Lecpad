package com.spruk.lecpad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
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
public class FacultyLectureViewFragment extends Fragment {

    List<LectureList> data = new ArrayList<LectureList>();

    ProgressDialog progress;
    boolean connectivityOk;
    int id;
    int lecid ;
    String subjid;
    String ddate ;
    String title;
    String content ;
    int assignment;
    String uploadedfile ;

    TextView subject;
    WebView webview;
    Button btnScore;
    Button btnAttach;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        subject =(TextView)view.findViewById(R.id.txtSubject);
        webview = (WebView)view.findViewById(R.id.webView);
        btnAttach =(Button)view.findViewById(R.id.btn_attachment);
        btnScore =(Button)view.findViewById(R.id.btnScore);

        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.faculty_lecture_view,container,false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("subjid", 0);
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
            client.execute(getString(R.string.siteurl) + "?action=getfacultylectureinfo&lecid=" + id);


        }
        else
        {
            Toast.makeText(getActivity(),"No Connection",Toast.LENGTH_LONG).show();
        }

        return rootView;
    }


    public class GetSubjectInfo extends AsyncTask<String, String, JSONArray> {
        @Override
        protected void onPreExecute() {
            data.clear();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            subject.setText(title);
            webview.loadData(content, "text/html; charset=utf-8", "UTF-8");

            btnAttach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(uploadedfile.equals(""))
                    {
                        Toast.makeText(getActivity(),"No Attachment",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String icon = Utility.getFileExtension(uploadedfile);

                        if(icon.equals("jpg") || icon.equals("png"))
                        {
                            Bundle bundle = new Bundle();
                            bundle.putString("filename",uploadedfile);
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
                            bundle.putString("filename",uploadedfile);

                            Fragment fragment = new WebViewFragment();
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
                        }
                    }

                }
            });


            btnScore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(getActivity(),""+lecid,Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putInt("lecid", lecid);
                    Fragment fragment = new FacultyStudentListFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();


                }
            });


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
                            lecid = Integer.parseInt(jsonChildNode.optString("id"));
                            subjid = jsonChildNode.optString("subjid");
                            ddate = jsonChildNode.optString("ddate");
                            title = jsonChildNode.optString("title");
                            content = jsonChildNode.optString("content");
                            assignment = Integer.parseInt(jsonChildNode.optString("assignment"));
                            uploadedfile = jsonChildNode.optString("uploadedfile");

                            //data.add(id,subjid,ddate,title,content,assignment,uploadedfile);


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
