package com.kowsar.gs.apod.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("code")
    @Expose
    private String error_code;
    @SerializedName("msg")
    @Expose
    private String error_msg;
    @SerializedName("service_version")
    @Expose
    private String version;

}
