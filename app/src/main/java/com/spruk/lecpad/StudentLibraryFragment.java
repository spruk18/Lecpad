package com.spruk.lecpad;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

    @Override
    public void onResume() {
        //data.clear();
        super.onResume();
    }



    long queryDatabase(String us)
    {

        data.clear();
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


        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(getActivity(),"" + data.get(position).getTitle(), Toast.LENGTH_SHORT).show();
        String icon = Utility.getFileExtension(data.get(position).getTitle());

        if(icon.equals("jpg") || icon.equals("png"))
        {
            Bundle bundle = new Bundle();
            bundle.putString("filename",data.get(position).getTitle());
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
            bundle.putString("filename",data.get(position).getTitle());

            Fragment fragment = new WebViewFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
        }



            }
        });


        return rootView;
    }


}
