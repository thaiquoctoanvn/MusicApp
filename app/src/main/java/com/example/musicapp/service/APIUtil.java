package com.example.musicapp.service;

public class APIUtil {

    private static String baseUrl = "https://bracteolate-honks.000webhostapp.com/Server/";
    public static RetrofitInterface getRetrofitInterface() {
        return RetrofitClient.getRetrofitClient(baseUrl).create(RetrofitInterface.class);
    }
}
