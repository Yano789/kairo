package com.example.infosys_1d.Chatbot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.Event.EventRepository;
import com.example.infosys_1d.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<String> chatMessages = new ArrayList<>();
    private OpenRouterApi openRouterApi;
    private String userEmail;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Get user email from intent
        userEmail = requireActivity().getIntent().getStringExtra("user_email");
        if (userEmail == null) {
            Log.w(TAG, "No user email found in intent");
            chatMessages.add("Kai: Please log in to add events.");
        }

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

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

        return view;
    }

    private void sendMessageToOpenRouter(String message) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are a helpful assistant named Kai. Always reply in short, simple sentences. If the user asks to add an event, interpret the request (including any date like 'tomorrow', 'next Monday', '2025-04-20', 'in 2 weeks', etc.) and respond with: 'I have successfully added the event into your calendar.' Include the event details in a hidden comment like this: <!-- {\"action\": \"add_event\", \"title\": \"event title\", \"date\": \"yyyy-MM-dd\", \"start_time\": \"h:mm AM/PM\", \"end_time\": \"h:mm AM/PM\", \"location\": \"event location\"} -->. Format the date as 'yyyy-MM-dd' based on today's date (e.g., 'tomorrow' is the next day). If unsure, include the raw date string (e.g., 'tomorrow', 'in 2 weeks'). Use today's date if no date is specified. Provide defaults (e.g., 1-hour duration, 'Not specified' location) if details are missing. For other queries, respond normally."));
        messages.add(new Message("user", message));
        ChatRequest request = new ChatRequest("deepseek/deepseek-chat", messages);

        Log.d(TAG, "Sending request: " + message);
        Call<ChatResponse> call = openRouterApi.sendMessage("Bearer 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", request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                Log.d(TAG, "Response received: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    String botReply = response.body().getChoices().get(0).getMessage().getContent();
                    Log.d(TAG, "Bot reply: " + botReply);
                    // Extract visible message (before comment)
                    String visibleReply = botReply.split("<!--")[0].trim();
                    chatMessages.add("Bot: " + visibleReply);
                    chatAdapter.notifyDataSetChanged();
                    processBotReply(botReply);
                } else {
                    Log.d(TAG, "Unsuccessful response: " + response.message());
                    chatMessages.add("Bot: Error - " + response.message());
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Log.e(TAG, "Request failed: " + t.getMessage());
                chatMessages.add("Bot: Error connecting to server - " + t.getMessage());
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    private void processBotReply(String reply) {
        if (reply.contains("add_event")) {
            parseAndAddEvent(reply);
        }
    }

    private void parseAndAddEvent(String reply) {
        try {
            Log.d(TAG, "Parsing reply: " + reply);

            // Extract JSON from comment
            Pattern jsonPattern = Pattern.compile("<!--\\s*(\\{.*\\})\\s*-->");
            Matcher matcher = jsonPattern.matcher(reply);
            if (matcher.find()) {
                String jsonStr = matcher.group(1);
                Log.d(TAG, "Extracted JSON: " + jsonStr);
                JSONObject json = new JSONObject(jsonStr);
                if (json.has("action") && json.getString("action").equals("add_event")) {
                    String title = json.getString("title");
                    String date = json.getString("date");
                    String startTimeStr = json.getString("start_time");
                    String endTimeStr = json.getString("end_time");
                    String location = json.getString("location");
                    Log.d(TAG, "JSON parsed: title=" + title + ", date=" + date + ", start=" + startTimeStr + ", end=" + endTimeStr + ", location=" + location);

                    addEventFromParsedData(title, date, startTimeStr, endTimeStr, location);
                } else {
                    throw new Exception("Invalid JSON action");
                }
            } else {
                Log.w(TAG, "No JSON comment found in reply");
                chatMessages.add("Kai: Sorry, I couldn't process the event. Please try again.");
                chatAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing event: " + e.getMessage());
            chatMessages.add("Kai: Sorry, I couldn't add the event. Please try again.");
            chatAdapter.notifyDataSetChanged();
        }
    }

    private void addEventFromParsedData(String title, String date, String startTimeStr, String endTimeStr, String location) {
        try {
            // Parse date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            dateFormat.setTimeZone(TimeZone.getDefault());
            Calendar calendar = Calendar.getInstance();
            Calendar today = Calendar.getInstance(); // For validating future dates
            String dateStr;

            // Prioritize relative date parsing from user input
            date = date.toLowerCase().trim();
            if (date.equals("today")) {
                dateStr = dateFormat.format(calendar.getTime());
                Log.d(TAG, "Parsed date as today: " + dateStr);
            } else if (date.equals("tomorrow")) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                dateStr = dateFormat.format(calendar.getTime());
                Log.d(TAG, "Parsed date as tomorrow: " + dateStr);
            } else if (date.startsWith("in ")) {
                String[] parts = date.split(" ");
                if (parts.length >= 3) {
                    int amount = Integer.parseInt(parts[1]);
                    if (parts[2].startsWith("day")) {
                        calendar.add(Calendar.DAY_OF_YEAR, amount);
                    } else if (parts[2].startsWith("week")) {
                        calendar.add(Calendar.WEEK_OF_YEAR, amount);
                    } else {
                        throw new IllegalArgumentException("Unsupported relative date: " + date);
                    }
                    dateStr = dateFormat.format(calendar.getTime());
                    Log.d(TAG, "Parsed relative date: " + dateStr);
                } else {
                    throw new IllegalArgumentException("Invalid relative date format: " + date);
                }
            } else {
                // Try parsing as yyyy-MM-dd
                try {
                    calendar.setTime(dateFormat.parse(date));
                    // Validate date is not in the past
                    if (calendar.before(today)) {
                        Log.w(TAG, "Date is in the past: " + date + ", defaulting to today");
                        calendar = Calendar.getInstance();
                    }
                    dateStr = dateFormat.format(calendar.getTime());
                    Log.d(TAG, "Parsed date as yyyy-MM-dd: " + dateStr);
                } catch (Exception e) {
                    // Fallback to today
                    dateStr = dateFormat.format(calendar.getTime());
                    Log.w(TAG, "Unrecognized date '" + date + "', defaulting to today");
                }
            }

            // Parse times
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
            timeFormat.setTimeZone(TimeZone.getDefault());
            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();

            startTime.setTime(timeFormat.parse(startTimeStr));
            endTime.setTime(timeFormat.parse(endTimeStr));

            // Set date for times
            startTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            startTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            startTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
            endTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            endTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            endTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

            // Adjust for PM if necessary
            if (startTimeStr.toLowerCase().contains("pm") && startTime.get(Calendar.HOUR_OF_DAY) < 12) {
                startTime.add(Calendar.HOUR_OF_DAY, 12);
            }
            if (endTimeStr.toLowerCase().contains("pm") && endTime.get(Calendar.HOUR_OF_DAY) < 12) {
                endTime.add(Calendar.HOUR_OF_DAY, 12);
            }

            // Check context for color
            int eventColor = 0xFF0000FF; // Default blue if context is missing
            if (getContext() != null) {
                eventColor = ContextCompat.getColor(getContext(), R.color.light_blue);
            } else {
                Log.w(TAG, "Context is null, using default event color");
            }

            // Create Event object
            Event event = new Event(
                    title, // name
                    title + " scheduled by Kai", // description
                    location,
                    startTime.getTimeInMillis(),
                    endTime.getTimeInMillis(),
                    dateStr,
                    new ArrayList<>(List.of("personal", "meeting")),
                    eventColor,
                    title,
                    "Scheduled Event",
                    R.drawable.default_event_image
            );

            // Add event to user's personal calendar
            if (userEmail != null && !userEmail.isEmpty()) {
                EventRepository.addPersonalEventToCalendar(userEmail, event);
                Log.d(TAG, "Event added for user: " + userEmail + ", event: " + event.getName() + ", date: " + dateStr);
            } else {
                chatMessages.add("Kai: Sorry, I couldn't add the event. No user logged in.");
                chatAdapter.notifyDataSetChanged();
                Log.w(TAG, "Event not added: No user email");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating event: " + e.getMessage());
            chatMessages.add("Kai: Sorry, I couldn't add the event. Please try again.");
            chatAdapter.notifyDataSetChanged();
        }
    }
}