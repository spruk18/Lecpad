package com.spruk.lecpad;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by taray on 6/12/2015.
 */
public class WebViewFragment extends Fragment {
    WebView webview;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.webview,container,false);
        webview = (WebView) rootView.findViewById(R.id.webView);

        int i=0;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            i = bundle.getInt("subjid", 0);
        }


        webview.loadUrl(getString(R.string.webview)+"?lecid="+i);
       // Toast.makeText(getActivity(),"wtf " + getString(R.string.webview)+"?lecid="+i ,Toast.LENGTH_SHORT).show();
        return rootView;
    }
}
