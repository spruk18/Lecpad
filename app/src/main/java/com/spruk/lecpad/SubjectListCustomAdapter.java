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
 * Created by taray on 6/11/2015.
 */
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

            holder.ic = (ImageView) convertView	.findViewById(R.id.bookmark);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.ddate = (TextView) convertView.findViewById(R.id.ddate);

            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText((CharSequence) data.get(position).getTitle());
        holder.ddate.setText("posted on :" + (CharSequence) data.get(position).getDdate());


        return convertView;
    }

    static class ViewHolder {
        ImageView ic;
        TextView title;
        TextView ddate;

    }

}
