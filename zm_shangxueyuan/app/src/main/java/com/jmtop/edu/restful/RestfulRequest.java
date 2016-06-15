package com.jmtop.edu.restful;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

public interface RestfulRequest {
    @FormUrlEncoded
    @POST("/videos/sync/")
    void queryVideo(@Field("timestamp") long timestamp, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/gallery/categories/")
    void queryGallery(@Field("tmp") String tmp, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/config/")
    void queryConfig(@Field("tmp") String tmp, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/feedback/add/")
    void feedback(@Field("content") String content, @Field("contact_info") String contact_info, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/gallery/category/{id}/topics/")
    void queryGalleryTopics(@Path("id") long topicId, @Field("tmp") String tmp, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/gallery/topic/{id}/topics/")
    void queryGallerySubTopics(@Path("id") long topicId, @Field("tmp") String tmp, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/gallery/search/")
    void search(@Field("keyword") String keyword, Callback<JSONObject> callback);

    @FormUrlEncoded
    @POST("/api/apiLogin.json")
    void login(@Field("accounts") String accounts, @Field("password") String password, @Field("client_type") int client_type, Callback<JSONObject> callback);
}
