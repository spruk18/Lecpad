package com.spruk.lecpad;

/**
 * Created by taray on 6/19/2015.
 */
public class NotificationList {
    int _id;
    String _topic;
    String _content;
    String _ddate;

    public NotificationList()
    {

    }

    public NotificationList(int id,String topic,String content,String ddate)
    {
        this._id = id;
        this._topic = topic;
        this._content = content;
        this._ddate = ddate;
    }

    public NotificationList(String topic,String content,String ddate)
    {
        this._topic = topic;
        this._content = content;
        this._ddate = ddate;
    }

    public void setId(int x)
    {
        this._id = x;
    }
    public int getId()
    {
        return this._id;
    }

    public void setTopic(String x)
    {
        this._topic = x;
    }
    public String getTopic()
    {
        return this._topic;
    }

    public void setContent(String x)
    {
        this._content = x;
    }
    public String getContent()
    {
        return this._content;
    }

    public void setDdate(String x)
    {
        this._ddate = x;
    }
    public String getDdate()
    {
        return this._ddate ;
    }

}
