package com.example.heartratealarm.websocket;

import com.google.gson.annotations.SerializedName;

// GenericData is how every Json must be passed and received through the websocket. The
// Method name will give context on how to deserialize and cast the data object.
public class GenericData<T> {
    @SerializedName("method")
    public String method;

    @SerializedName("data")
    public T data;

    public GenericData() {
    }

    public GenericData(String method, T data) {
        this.method = method;
        this.data = data;
    }
}
