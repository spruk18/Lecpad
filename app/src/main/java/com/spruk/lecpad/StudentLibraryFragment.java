package com.spruk.lecpad;


import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by taray on 6/9/201   5.
 */
public class StudentLibraryFragment  extends ListFragment {


    public StudentLibraryFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);


        View rootView = inflater.inflate(R.layout.student_library_fragment, container, false);


        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Toast.makeText(getActivity(),"ads"+position,Toast.LENGTH_SHORT).show();
        super.onListItemClick(l, v, position, id);
    }


}

