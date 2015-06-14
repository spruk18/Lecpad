package com.spruk.lecpad;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by taray on 6/14/2015.
 */
public class StudentLibraryFragment extends Fragment {
    GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.student_library_fragment,container,false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        List<LibraryList> data = new ArrayList<LibraryList>();
        data.add(new LibraryList("title 1","pdf"));
        data.add(new LibraryList("title 2","doc"));
        data.add(new LibraryList("title 3","txt"));
        data.add(new LibraryList("title 4","jpg"));

        StudentLibraryAdapter adapter = new StudentLibraryAdapter(getActivity(),data);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(),"" + position, Toast.LENGTH_SHORT).show();

            }
        });


        return rootView;
    }


}
