package com.zm.shangxueyuan.restful;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface RestfulRequest {
    @FormUrlEncoded
    @POST("/videos/sync/")
    void queryVideo(@Field("timestamp") long timestamp, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/gallery/categories/")
    void queryGallery(@Field("tmp") String tmp,Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/config/")
    void queryConfig(@Field("tmp") String tmp,Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/feedback/add/")
    void feedback(@Field("content") String content, @Field("contact_info") String contact_info, Callback<JSONObject> callback);
}
