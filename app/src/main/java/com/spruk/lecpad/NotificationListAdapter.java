package com.spruk.lecpad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taray on 6/19/2015.
 */
public class NotificationListAdapter  extends ArrayAdapter<NotificationList> {
    private LayoutInflater inflater;
    private List<NotificationList> data;
    private LinearLayout msglist;
    Context cont;

    public NotificationListAdapter(Context context, List<NotificationList> objects) {
        super(context, R.layout.notificationlist, objects);
        cont = context;
        inflater = LayoutInflater.from(context);
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.notificationlist, null);
            holder = new ViewHolder();

            holder.ddate = (TextView) convertView.findViewById(R.id.ddate);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(holder);
        }

        else
        {
            holder = (ViewHolder) convertView.getTag();

        }


        holder.title.setText((CharSequence) data.get(position).getTopic());
        holder.ddate.setText((CharSequence) data.get(position).getDdate());
        holder.content.setText((CharSequence) data.get(position).getContent());


        return convertView;
    }

    static class ViewHolder {
        TextView ddate;
        TextView title;
        TextView content;


    }
}
