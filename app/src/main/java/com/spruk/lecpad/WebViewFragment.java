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

        webview.loadUrl("http://www.mathportal.org/");
        return rootView;
    }
}
