package com.example.tpc.Client;

import com.example.tpc.Models.cfUserRating;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RatingClient {

    @GET("user.rating")
    Call<cfUserRating> getUserRating(@Query("handle") String user_id);

}
