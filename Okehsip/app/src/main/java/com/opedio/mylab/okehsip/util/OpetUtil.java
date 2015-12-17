package com.opedio.mylab.okehsip.util;

import java.util.Random;

/**
 * Created by 247 on 12/16/2015.
 */
public class OpetUtil {
    public OpetUtil() {
        // TODO Auto-generated constructor stub
    }
    public static String getRandomString(){
        String pincode = "";
        String alphabet = "0123456789ABCDE";
        int N = alphabet.length();
        Random r = new Random();
        for(int i = 0; i<4 ;i++){
            pincode = pincode+alphabet.charAt(r.nextInt(N));
        }
        return pincode;
    }
    public static int parseInt(String str){
        try {
            return Integer.parseInt(str != null && !"".equalsIgnoreCase(str) ? str:"0");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return 0;
    }
    public static long parseLong(String str){
        try {
            return Long.parseLong(str != null && !"".equalsIgnoreCase(str) ? str:"0");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return 0;
    }

}
