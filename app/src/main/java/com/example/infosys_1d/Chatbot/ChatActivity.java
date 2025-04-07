package com.example.infosys_1d.Chatbot;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<String> chatMessages = new ArrayList<>();
    private OpenRouterApi openRouterApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openrouter.ai/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openRouterApi = retrofit.create(OpenRouterApi.class);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageInput.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    chatMessages.add("You: " + userMessage);
                    chatAdapter.notifyDataSetChanged();
                    messageInput.setText("");
                    sendMessageToOpenRouter(userMessage);
                }
            }
        });
    }

    private void sendMessageToOpenRouter(String message) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", message));
        ChatRequest request = new ChatRequest("deepseek/deepseek-chat", messages);

        Call<ChatResponse> call = openRouterApi.sendMessage("Bearer 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", request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botReply = response.body().getChoices().get(0).getMessage().getContent();
                    chatMessages.add("Bot: " + botReply);
                    chatAdapter.notifyDataSetChanged();
                    processBotReply(botReply);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                chatMessages.add("Bot: Error connecting to server");
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void processBotReply(String reply) {
        if (reply.contains("Add event")) {
            // Simple parsing for demo; enhance with regex or NLP logic
            String[] parts = reply.split(":");
            if (parts.length > 1) {
                String eventDetails = parts[1].trim();
                addEventToCalendar(eventDetails);
            }
        }
    }

    private void addEventToCalendar(String eventDetails) {
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis() + 3600000); // 1 hour from now
        values.put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 7200000);   // 2 hours
        values.put(CalendarContract.Events.TITLE, eventDetails);
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // Default calendar
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        cr.insert(CalendarContract.Events.CONTENT_URI, values);
        chatMessages.add("Bot: Event added to calendar");
        chatAdapter.notifyDataSetChanged();
    }
}