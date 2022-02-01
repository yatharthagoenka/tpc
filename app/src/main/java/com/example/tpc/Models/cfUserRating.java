package com.example.tpc.Models;


import java.util.List;
import javax.annotation.Generated;
import javax.xml.transform.Result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("cfUserRating")
public class cfUserRating {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("result")
    @Expose
    private List<cfUserRatingResult> result = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<cfUserRatingResult> getResult() {
        return result;
    }

    public void setResult(List<cfUserRatingResult> result) {
        this.result = result;
    }

}
