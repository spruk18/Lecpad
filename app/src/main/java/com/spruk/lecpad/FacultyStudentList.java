package com.spruk.lecpad;

/**
 * Created by taray on 6/19/2015.
 */
public class FacultyStudentList  {

    int _id;
    String _studname;
    String _studattachment;
    String _lecscore;
    public FacultyStudentList()
    {

    }
    public FacultyStudentList(int id, String studname, String studentattachment,String lecscore)
    {
        this._id = id;
        this._studname = studname;
        this._studattachment = studentattachment;
        this._lecscore = lecscore;
    }

    public void setId(int x)
    {
        this._id = x;
    }
    public int getId()
    {
        return this._id;
    }

    public void setStudname(String x)
    {
        this._studname = x;
    }
    public String getStudname()
    {
        return this._studname;
    }

    public void setStudAttachment(String x)
    {
        this._studattachment = x;
    }
    public String getStudAttachment()
    {
        return this._studattachment;
    }

    public void setLecscore(String x)
    {
        this._lecscore = x;
    }
    public String getLecscore()
    {
        return this._lecscore;
    }


}
