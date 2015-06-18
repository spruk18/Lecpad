package com.spruk.lecpad;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taray on 6/16/2015.
 */
public class MessageListAdapter extends ArrayAdapter<MessageList> {
    private LayoutInflater inflater;
    private List<MessageList> data;
    private LinearLayout msglist;
    Context cont;

    public MessageListAdapter(Context context, List<MessageList> objects) {
        super(context, R.layout.msglist, objects);
        cont = context;
        inflater = LayoutInflater.from(context);
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.msglist, null);
            holder = new ViewHolder();

            holder.ddate = (TextView) convertView.findViewById(R.id.ddate);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.msglist = (LinearLayout) convertView.findViewById(R.id.msglist);
            convertView.setTag(holder);
        }

        else
        {
            holder = (ViewHolder) convertView.getTag();

        }

        //Log.v("JAKOL", data.get(position).getMsg() + data.get(position).getDdate());
        int d = data.get(position).getDel();
        if(d==0)
        {
            holder.title.setGravity(Gravity.RIGHT);
            holder.ddate.setGravity(Gravity.RIGHT);

        }
        else
        {
            holder.title.setGravity(Gravity.LEFT);
            holder.ddate.setGravity(Gravity.LEFT);
        }


        holder.title.setText((CharSequence) data.get(position).getMsg());
        holder.ddate.setText((CharSequence) data.get(position).getDdate());



        return convertView;
    }

    static class ViewHolder {
        TextView ddate;
        TextView title;
        LinearLayout msglist;


    }
}
