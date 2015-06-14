package com.spruk.lecpad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by taray on 6/11/2015.
 */
public class SubjectLectureFragment extends ListFragment {

    private ProgressDialog progress;
    private boolean connectivityOk;
    private List<String> subjectcode = new ArrayList<String>();
    private List<Integer> subjid = new ArrayList<Integer>();
    List<SubjectList> data = new ArrayList<SubjectList>();

    public SubjectLectureFragment()
    {

    }

    @Override
    public void onResume() {
        subjectcode.clear();
        subjid.clear();
        data.clear ();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int i=0;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
             i = bundle.getInt("subjid", 0);
        }
      //Toast.makeText(getActivity()," " + i,Toast.LENGTH_LONG).show();
        connectivityOk = checkInternetConnection();
        if(connectivityOk)
        {
            // Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Retrieving data");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.show();


            GetSubjectInfo client = new GetSubjectInfo();

            client.execute(getString(R.string.siteurl) + "?action=getsubjectlecture&subjid=" + i);


        }
        else
        {
            Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_LONG).show();
        }



        View rootView = inflater.inflate(R.layout.subject_lecture_fragment, container, false);


        return rootView;
    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub

        Bundle bundle = new Bundle();
        bundle.putInt("subjid", subjid.get(position));

        Fragment fragment = new WebViewFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        //fragmentManager.beginTransaction().add(R.id.content_frame, fragment).commit();

        super.onListItemClick(l, v, position, id);
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        int listPosition = info.position;
//
//        Toast.makeText(getActivity()," "+listPosition,Toast.LENGTH_LONG).show();
//        return super.onContextItemSelected(item);
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Attachment");
//        menu.add(0, v.getId(), 0, "Download");
//
//    }



    public class SubjectListCustomAdapter extends ArrayAdapter<SubjectList> {
        private LayoutInflater inflater;
        private List<SubjectList> data;

        public SubjectListCustomAdapter(Context context, List<SubjectList> objects) {
            super(context, R.layout.lessonlist, objects);

            inflater = LayoutInflater.from(context);
            this.data = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.lessonlist, null);

                holder = new ViewHolder();

                holder.ic = (ImageButton) convertView.findViewById(R.id.btn_attachment);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.ddate = (TextView) convertView.findViewById(R.id.ddate);

                convertView.setTag(holder);
            }

            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText((CharSequence) data.get(position).getTitle());
            holder.ddate.setText("posted on :" + (CharSequence) data.get(position).getDdate());

            if(data.get(position).getUploadedfile().equals(""))
            {
                holder.ic.setVisibility(View.INVISIBLE);
            }
            else
            {
                holder.ic.setVisibility(View.VISIBLE);
            }

            holder.ic.setOnClickListener(mBuyButtonClickListener);
            return convertView;
        }

        class ViewHolder {
            ImageButton ic;
            TextView title;
            TextView ddate;

        }

    }
    private View.OnClickListener mBuyButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = getListView().getPositionForView(v);
            if (position != ListView.INVALID_POSITION) {
                Toast.makeText(getActivity(),data.get(position).getUploadedfile() + "",Toast.LENGTH_LONG).show();
            }
        }
    };


    public class GetSubjectInfo extends AsyncTask<String, String, JSONArray> {

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

          //  ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, subjectcode);
          //  setListAdapter(adapter);
            SubjectListCustomAdapter adapter = new SubjectListCustomAdapter(getActivity(),data);

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
                            subjectcode.add(jsonChildNode.optString("title"));
                            subjid.add(Integer.parseInt(jsonChildNode.optString("id")));
                            int id = Integer.parseInt(jsonChildNode.optString("id"));
                            String sub = jsonChildNode.optString("subjectcode");
                            String ddate = jsonChildNode.optString("ddate");
                            String title = jsonChildNode.optString("title");
                            String content = jsonChildNode.optString("content");
                            String uploadedfile = jsonChildNode.optString("uploadedfile");
                            data.add(new SubjectList(id,sub,ddate,title,content, uploadedfile));
                           // subjid.add(Integer.parseInt(jsonChildNode.optString("subjid")));

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

