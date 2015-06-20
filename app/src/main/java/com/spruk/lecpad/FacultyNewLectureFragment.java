package com.spruk.lecpad;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * Created by taray on 6/20/2015.
 */
public class FacultyNewLectureFragment extends Fragment {
    private final String LOG_TAG = FacultyNewLectureFragment.class.getSimpleName();
    ProgressDialog progress = null;

    String upLoadServerUri = null;

    Button btnPreview;
    EditText content;
    EditText title;
    boolean connectivityOk;
    private static final int FILE_SELECT_CODE = 0;
    String path;
    int RESULT_OK = -1;
    File file;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();
                    Log.v(LOG_TAG, "File Uri: " + uri.getPath());

                    upLoadServerUri = "http://www.highoctane-restobar.com/lecpad/auth.php?action=uploadfile";
                    path = getPath(getActivity(),uri);



                   file = new File(path);
                    Log.i("Image File Name",file.getName());
                    connectivityOk = checkInternetConnection();
                    if(connectivityOk)
                    {

                        progress = new ProgressDialog(getActivity());
                        progress.setMessage("Uploading File");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.show();

                        new uploadFile().execute();


                    }
                    else
                    {
                        Toast.makeText(getActivity(),"No Connection",Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    class uploadFile extends AsyncTask<String ,Void ,String>
    {
        @Override
        protected void onPostExecute(String s) {
            progress.dismiss();
            Toast.makeText(getActivity(),"Upload Complete",Toast.LENGTH_LONG).show();
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://www.highoctane-restobar.com/lecpad/auth.php?action=uploadfile");

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


                if(file != null)
                {
                    entityBuilder.addBinaryBody("uploaded_file", file);
                }

                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);
                Log.v("result", result);
            } catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.faculty_attachment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                showFileChooser();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        content = (EditText)view.findViewById(R.id.content);
        title = (EditText)view.findViewById(R.id.title);
        btnPreview = (Button)view.findViewById(R.id.btnpreview);

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("filename", content.getText().toString());
                bundle.putInt("lecture", 2);
                bundle.putInt("subjid",0);
                Fragment fragment = new WebViewFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.faculty_lecture_new,container,false);

        setHasOptionsMenu(true);

        return rootView;

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
