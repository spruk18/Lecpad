package com.spruk.lecpad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taray on 6/14/2015.
 */
public class StudentLibraryAdapter extends  ArrayAdapter<LibraryList> {
private LayoutInflater inflater;
private List<LibraryList> data;

public StudentLibraryAdapter(Context context, List<LibraryList> objects) {
        super(context, R.layout.library_grid_item, objects);

        inflater = LayoutInflater.from(context);
        this.data = objects;
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.library_grid_item, null);
            holder = new ViewHolder();

            holder.ic = (ImageView) convertView.findViewById(R.id.grid_item_image);
            holder.title = (TextView) convertView.findViewById(R.id.grid_item_label);
            convertView.setTag(holder);
        }

        else
        {
        holder = (ViewHolder) convertView.getTag();

        }

        if(data.get(position).getIcon().equals("pdf"))
        {
            holder.ic.setImageResource(R.drawable.ic_pdf);
        }
        else if(data.get(position).getIcon().equals("doc"))
        {
            holder.ic.setImageResource(R.drawable.ic_doc);
        }
        else if(data.get(position).getIcon().equals("txt"))
        {
            holder.ic.setImageResource(R.drawable.ic_txt);
        }
        else if(data.get(position).getIcon().equals("ppt"))
        {
            holder.ic.setImageResource(R.drawable.ic_ppt);
        }
        else if(data.get(position).getIcon().equals("xls"))
        {
            holder.ic.setImageResource(R.drawable.ic_xls);
        }
        else if(data.get(position).getIcon().equals("jpg"))
        {
            holder.ic.setImageResource(R.drawable.ic_jpg);
        }
        else if(data.get(position).getIcon().equals("png"))
        {
            holder.ic.setImageResource(R.drawable.ic_png);
        }
        holder.title.setText((CharSequence) data.get(position).getTitle());



        return convertView;
        }

    static class ViewHolder {
        ImageView ic;
        TextView title;


    }

}
