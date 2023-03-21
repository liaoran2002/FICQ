package com.fcwr.ficq.utils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

public class MySessionContext {
    private static HashMap<String, HttpSession> mymap= new HashMap<>();

    public static synchronized void AddSession(HttpSession session){
        if(session !=null){
            mymap.put(session.getId(),session);
        }
    }
    public static synchronized void DelSession(HttpSession session){
        if(session !=null){
            mymap.remove(session.getId());
        }
    }
    public static synchronized HttpSession getSession(String session_id){
        if(session_id==null) return null;
        return mymap.get(session_id);
    }
}
