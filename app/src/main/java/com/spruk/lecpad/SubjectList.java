package com.spruk.lecpad;

/**
 * Created by taray on 6/11/2015.
 */
public class SubjectList {
    int _id;
    String _subjid;
    String _ddate;
    String _title;
    String _content;
    String _uploadedfile;

    public SubjectList()
    {

    }

    public SubjectList(int id,String subjid,String ddate,String title,String content,String uploadedfile)
    {
        this._id = id;
        this._subjid = subjid;
        this._ddate = ddate;
        this._title = title;
        this._content = content;
        this._uploadedfile = uploadedfile;
    }
    public SubjectList(String subjid,String ddate,String title,String content,String uploadedfile)
    {
        this._subjid = subjid;
        this._ddate = ddate;
        this._title = title;
        this._content = content;
        this._uploadedfile = uploadedfile;
    }

    public void setId(int x)
    {
        this._id = x;
    }
    public int getId()
    {
        return this._id;
    }

    public void setSubjid(String x)
    {
        this._subjid = x;
    }
    public String getSubjid()
    {
        return this._subjid;
    }

    public void setDdate(String x)
    {
        this._ddate = x;
    }
    public String getDdate()
    {
        return this._ddate;
    }

    public void setTitle(String x)
    {
        this._title = x;
    }
    public String getTitle()
    {
        return this._title;
    }


    public void setContent(String x)
    {
        this._content = x;
    }
    public String getContent()
    {
        return this._content;
    }


    public void setUploadedfile(String x)
    {
        this._uploadedfile = x;
    }
    public String getUploadedfile()
    {
        return this._uploadedfile;
    }


}
