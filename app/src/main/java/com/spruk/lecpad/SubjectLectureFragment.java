package com.spruk.lecpad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.spruk.lecpad.data.LibraryContract;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taray on 6/11/2015.
 */
public class SubjectLectureFragment extends ListFragment {
    private static final String LOG_TAG = SubjectLectureFragment.class.getSimpleName();

    private ProgressDialog progress;
    private boolean connectivityOk;
    private List<String> subjectcode = new ArrayList<String>();
    private List<Integer> subjid = new ArrayList<Integer>();
    List<SubjectList> data = new ArrayList<SubjectList>();

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    String file_url;
    String file_name;

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
        bundle.putInt("lecture", 1);
        bundle.putString("filename","");

        Fragment fragment = new WebViewFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();


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
                //Toast.makeText(getActivity(),data.get(position).getUploadedfile() + "",Toast.LENGTH_LONG).show();
                file_url = getString(R.string.docs) + "/" + data.get(position).getUploadedfile();
                file_name = data.get(position).getUploadedfile();
                new DownloadFileFromURL().execute(file_url);
            }
        }
    };



    class DownloadFileFromURL extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/lecpad");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }

                OutputStream output = new FileOutputStream("/sdcard/lecpad/" + file_name);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            String file_path = Environment.getExternalStorageDirectory().toString() + "/lecpad/" + file_name;

            String us = Utility.loadSavedPreferences(getActivity(),"user");

            String extension = Utility.getFileExtension(file_name);
            long res = addLibrary(file_name,file_path,extension,us);
            if(res==0)
            {
                Toast.makeText(getActivity(),"Error Downloading",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getActivity(),"Added to Library!" ,Toast.LENGTH_LONG).show();
                Log.v(LOG_TAG, extension + " " + file_name);

            }


        }



    }



    long addLibrary(String name,String path,String doc,String us) {
        long locationId;
        Cursor LibraryCursor = getActivity().getContentResolver().query(LibraryContract.LibraryEntry.CONTENT_URI,
                new String[]{LibraryContract.LibraryEntry._ID}, LibraryContract.LibraryEntry.COLUMN_FILENAME + " = ?",
                new String[]{name},
                null);

        if (LibraryCursor.moveToFirst()) {
            int locationIdIndex = LibraryCursor.getColumnIndex(LibraryContract.LibraryEntry._ID);
            locationId = LibraryCursor.getLong(locationIdIndex);
        } else {

            ContentValues values = new ContentValues();
            values.put(LibraryContract.LibraryEntry.COLUMN_FILENAME, name);
            values.put(LibraryContract.LibraryEntry.COLUMN_FILE_PATH, path);
            values.put(LibraryContract.LibraryEntry.COLUMN_ICON_TYLE, doc);
            values.put(LibraryContract.LibraryEntry.COLUMN_USER, us);

            Uri insertedUri = getActivity().getContentResolver().insert(LibraryContract.LibraryEntry.CONTENT_URI,values);
            locationId = ContentUris.parseId(insertedUri);
        }

        LibraryCursor.close();
        return locationId;
    }


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

