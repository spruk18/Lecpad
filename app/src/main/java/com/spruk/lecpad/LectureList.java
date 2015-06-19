package com.spruk.lecpad;

/**
 * Created by taray on 6/19/2015.
 */
public class LectureList {
    int _id;
    String _subjid;
    String _ddate;
    String _title;
    String _content;
    int _assignment;
    String _uploadedfile;

    public LectureList()
    {

    }

    public LectureList(int id,String subjid,String ddate,String title, String content,int assignment,String uploadedfile)
    {
        this._id = id;
        this._subjid = subjid;
        this._ddate = ddate;
        this._title = title;
        this._content = content;
        this._assignment = assignment;
        this._uploadedfile = uploadedfile;
    }

    public LectureList(String subjid,String ddate,String title, String content,int assignment,String uploadedfile)
    {
        this._subjid = subjid;
        this._ddate = ddate;
        this._title = title;
        this._content = content;
        this._assignment = assignment;
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

    public void setAssignment(int x)
    {
        this._assignment = x;
    }
    public int getAssignment()
    {
        return this._assignment;
    }

    public void setUploadedFile(String x)
    {
        this._uploadedfile = x;
    }
    public String getUploadedfile()
    {
        return this._uploadedfile;
    }


}
