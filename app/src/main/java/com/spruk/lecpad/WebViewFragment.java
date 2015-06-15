package com.spruk.lecpad;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by taray on 6/12/2015.
 */
public class WebViewFragment extends Fragment {
    WebView webview;
    private final String LOG_TAG = WebView.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.webview,container,false);
        webview = (WebView) rootView.findViewById(R.id.webView);

        int i=0;
        int lecture=0;
        String filename="";

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            i = bundle.getInt("subjid", 0);
            lecture = bundle.getInt("lecture");
            filename = bundle.getString("filename");
        }
        if(lecture==1)
        {
            webview.loadUrl(getString(R.string.webview)+"?lecid="+i);
            Log.v(LOG_TAG,"1");
        }
        else
        {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl(getString(R.string.googledocs)+getString(R.string.docs)+"/"+filename);

            Log.v(LOG_TAG,"2");
        }


       // Toast.makeText(getActivity(),getString(R.string.googledocs)+getString(R.string.docs)+"/"+filename , Toast.LENGTH_SHORT).show();
        Log.v(LOG_TAG,getString(R.string.googledocs)+getString(R.string.docs)+"/"+filename );
        return rootView;
    }
}
