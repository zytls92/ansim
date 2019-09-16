package com.astuetz.viewpager.extensions.sample;

public class SessionMake {
    private static String session;
    public final static String httpUrls="http://192.168.43.25:3000/";

    public static void setSession(String session) {
        SessionMake.session = session;
    }

    public static String getSession(){
        return session;
    }

}