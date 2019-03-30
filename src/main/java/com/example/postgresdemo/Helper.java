package com.example.postgresdemo;

public class Helper {

    public static final String me="me";

    public static String getGreeting( String whom ) {
        return "laters " + whom;
    }

    public static String getOverride( String toOverride ) {
        return toOverride.toUpperCase();
    }


}
