package com.spruk.lecpad;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.spruk.lecpad.data.LibraryContract;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by taray on 6/14/2015.
 */
public class StudentLibraryFragment extends Fragment {
    private static final String LOG_TAG = StudentLibraryFragment.class.getSimpleName();

    GridView gridView;
    List<LibraryList> data = new ArrayList<LibraryList>();

    long queryDatabase(String us)
    {

        long locationId=0;

        Cursor LibraryCursor = getActivity().getContentResolver().query(LibraryContract.LibraryEntry.CONTENT_URI,
                new String[]{LibraryContract.LibraryEntry._ID,
                        LibraryContract.LibraryEntry.COLUMN_FILENAME,
                        LibraryContract.LibraryEntry.COLUMN_FILE_PATH,
                        LibraryContract.LibraryEntry.COLUMN_ICON_TYLE},
                LibraryContract.LibraryEntry.COLUMN_USER + " = ?",
                new String[]{us},
                null);

        while(LibraryCursor.moveToNext()){
           // Log.w(LOG_TAG,LibraryCursor.getString(0)+"-"+LibraryCursor.getString(1));
            data.add(new LibraryList(LibraryCursor.getString(1),LibraryCursor.getString(3)));
        }

        LibraryCursor.close();
        return locationId;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.student_library_fragment,container,false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        String us = Utility.loadSavedPreferences(getActivity(),"user");
        queryDatabase(us);

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
