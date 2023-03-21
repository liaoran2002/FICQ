package com.fcwr.ficq.utils;

import java.util.Base64;

public class Base64Utils {
    public static String encode(String old){
        return Base64.getEncoder().encodeToString(old.getBytes());
    }
    public static String decode(String old){
        return new String(Base64.getDecoder().decode(old));
    }
}
