package com.example.infosys_1d.Chatbot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenRouterApi {
    @POST("chat/completions")
    Call<ChatResponse> sendMessage(
            @Header("Authorization") String auth,
            @Body ChatRequest request
    );
}
