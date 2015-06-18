package com.spruk.lecpad;

/**
 * Created by taray on 6/16/2015.
 */
public class MessageList {
    int _id;
    String _msgto;
    String _msgfrm;
    String _msg;
    String _ddate;
    int _del;

    public MessageList()
    {

    }
    public MessageList(int id,String msgto,String msgfrm,String msg,String ddate,int del)
    {

    }

    public MessageList(String msgto,String msgfrm,String msg,String ddate,int del)
    {
        this._msgto = msgto;
        this._msgfrm = msgfrm;
        this._msg = msg;
        this._ddate = ddate;
        this._del = del;
    }
    public MessageList(String msgto,String msgfrm,String msg,String ddate)
    {
        this._msgto = msgto;
        this._msgfrm = msgfrm;
        this._msg = msg;
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

    public void setMsgto(String x)
    {
        this._msgto = x;
    }
    public String getMsgto()
    {
        return this._msgto;
    }

    public void setMsgfrm(String x)
    {
        this._msgfrm = x;
    }
    public String getMsgfrm()
    {
        return this._msgfrm;
    }


    public void setMsg(String x)
    {
        this._msg = x;
    }
    public String getMsg()
    {
        return this._msg;
    }

    public void setDdate(String x)
    {
        this._ddate = x;
    }
    public String getDdate()
    {
        return this._ddate;
    }

    public void setDel(int x)
    {
        this._del = x;
    }
    public int getDel()
    {
        return this._del;
    }

}

