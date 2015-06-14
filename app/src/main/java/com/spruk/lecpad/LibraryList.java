package com.spruk.lecpad;

/**
 * Created by taray on 6/14/2015.
 */
public class LibraryList {
    int _id;
    String _title;
    String _icon;


    public LibraryList()
    {

    }

    public LibraryList(int id,String title,String icon)
    {
        this._id = id;
        this._title = title;
        this._icon = icon;
    }
    public LibraryList(String title,String icon)
    {
        this._title = title;
        this._icon = icon;
    }

    public void setId(int x)
    {
        this._id = x;
    }
    public int getId()
    {
        return this._id;
    }

    public void setTitle(String x)
    {
        this._title = x;
    }
    public String getTitle()
    {
        return this._title;
    }

    public void setIcon(String x)
    {
        this._icon  = x;
    }
    public String getIcon()
    {
        return this._icon;
    }




}
