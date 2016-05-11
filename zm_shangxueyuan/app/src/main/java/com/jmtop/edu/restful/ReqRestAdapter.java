package com.jmtop.edu.restful;

import android.content.Context;

import com.jmtop.edu.constant.CommonConstant;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.Converter;

public class ReqRestAdapter {

    public static RestAdapter getInstance(final Context context) {
        return getInstance(context, CommonConstant.REQUEST_RES_URL);
    }

    public static RestAdapter getInstance(final Context context, String baseUrl) {
        RestAdapter restAdapter = new RestAdapter.Builder().setRequestInterceptor(new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade requestFacade) {
                requestFacade.addHeader("from_client", context.getPackageName());
                if (context != null) {

                }
            }

        }).setConverter(new JsonObjectConverter()).setEndpoint(baseUrl).build();
        return restAdapter;
    }

    public static RestAdapter getInstance(final Context context, String baseUrl, Converter converter) {
        RestAdapter restAdapter = new RestAdapter.Builder().setRequestInterceptor(new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade requestFacade) {
                requestFacade.addHeader("from_client", context.getPackageName());
                if (context != null) {

                }
            }

        }).setConverter(converter).setEndpoint(baseUrl).build();
        return restAdapter;
    }

}
