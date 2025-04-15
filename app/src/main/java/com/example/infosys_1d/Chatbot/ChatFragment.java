package com.example.infosys_1d.Chatbot;

import android.content.SharedPreferences;
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

import java.text.ParseException;
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
    private String originalUserInput;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Get user email from intent or SharedPreferences
        userEmail = requireActivity().getIntent().getStringExtra("user_email");
        if (userEmail == null) {
            SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
            userEmail = prefs.getString("user_email", null);
        }
        if (userEmail == null) {
            Log.w(TAG, "No user email found");
            chatMessages.add("Kai: Please log in to add events.");
        } else {
            Log.d(TAG, "User email: " + userEmail);
        }

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        chatAdapter = new ChatAdapter(chatMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Start from bottom
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openrouter.ai/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openRouterApi = retrofit.create(OpenRouterApi.class);

        sendButton.setOnClickListener(v -> {
            String userMessage = messageInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                originalUserInput = userMessage; // Store for date validation
                String userMsg = "You: " + userMessage;
                chatMessages.add(userMsg);
                Log.d(TAG, "Added user message: " + userMsg);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                messageInput.setText("");
                sendMessageToOpenRouter(userMessage);
            }
        });

        return view;
    }

    private void sendMessageToOpenRouter(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String today = sdf.format(Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).getTime());

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are a helpful assistant named Kai. Today's date is " + today + " in Singapore (Asia/Singapore). Always reply in short, simple sentences. If the user asks to add an event, interpret the request (including dates like 'tomorrow', 'in 2 days', 'in 2 weeks', 'in 1 month', '15/02/2025', '15th Feb 2025', '15 February 2025', etc.) and respond with: 'I have successfully added the event into your calendar.' Include the event details in a hidden comment: <!-- {\"action\": \"add_event\", \"title\": \"event title\", \"date\": \"yyyy-MM-dd\", \"start_time\": \"h:mm a\", \"end_time\": \"h:mm a\", \"location\": \"event location\"} -->. Calculate dates relative to " + today + " (e.g., 'tomorrow' is " + today + " + 1 day, 'in 2 days' is " + today + " + 2 days). Always format date as 'yyyy-MM-dd'. Do not use past dates. If no date is specified, use " + today + ". Provide defaults (1-hour duration, 'Not specified' location) if details are missing. For other queries, respond normally."));
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
                    String visibleReply = botReply.split("<!--")[0].trim();
                    String kaiMsg = "Kai: " + visibleReply;
                    chatMessages.add(kaiMsg);
                    Log.d(TAG, "Added bot message: " + kaiMsg);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                    processBotReply(botReply);
                } else {
                    Log.d(TAG, "Unsuccessful response: " + response.message());
                    String errorMsg = "Kai: Error - " + response.message();
                    chatMessages.add(errorMsg);
                    Log.d(TAG, "Added error message: " + errorMsg);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Log.e(TAG, "Request failed: " + t.getMessage());
                String errorMsg = "Kai: Error connecting to server - " + t.getMessage();
                chatMessages.add(errorMsg);
                Log.d(TAG, "Added failure message: " + errorMsg);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
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
                String errorMsg = "Kai: Sorry, I couldn't process the event. Please try again.";
                chatMessages.add(errorMsg);
                Log.d(TAG, "Added error message: " + errorMsg);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing event: " + e.getMessage());
            String errorMsg = "Kai: Sorry, I couldn't add the event. Please try again.";
            chatMessages.add(errorMsg);
            Log.d(TAG, "Added error message: " + errorMsg);
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        }
    }

    private void addEventFromParsedData(String title, String date, String startTimeStr, String endTimeStr, String location) {
        try {
            // Get current date for reference
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
            Calendar todayCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            String currentDate = sdf.format(todayCal.getTime());

            // Parse date using EventRepository
            String dateStr = EventRepository.getDateString(date, currentDate);
            Log.d(TAG, "Parsed date: " + date + " -> " + dateStr);

            // Validate date
            Calendar eventCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            try {
                eventCal.setTime(sdf.parse(dateStr));
                eventCal.set(Calendar.HOUR_OF_DAY, 0);
                eventCal.set(Calendar.MINUTE, 0);
                eventCal.set(Calendar.SECOND, 0);
                eventCal.set(Calendar.MILLISECOND, 0);
            } catch (ParseException e) {
                throw new Exception("Invalid parsed date: " + dateStr);
            }

            if (eventCal.before(todayCal)) {
                Log.w(TAG, "Parsed date is before today: " + dateStr);
                String errorMsg = "Kai: Sorry, I can't add events before today. Please choose today or a future date.";
                chatMessages.add(errorMsg);
                Log.d(TAG, "Added error message: " + errorMsg);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                return;
            }

            // Override for relative dates
            if (originalUserInput != null) {
                String inputLower = originalUserInput.toLowerCase();
                if (inputLower.contains("tomorrow")) {
                    Calendar expectedCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
                    expectedCal.setTime(todayCal.getTime());
                    expectedCal.add(Calendar.DAY_OF_MONTH, 1);
                    String expectedDate = sdf.format(expectedCal.getTime());
                    if (!dateStr.equals(expectedDate)) {
                        Log.w(TAG, "Date mismatch for tomorrow: expected " + expectedDate + ", got " + dateStr);
                        dateStr = expectedDate;
                    }
                } else if (inputLower.contains("days from now")) {
                    Pattern pattern = Pattern.compile("(\\d+)\\s*days\\s*from\\s*now", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(inputLower);
                    if (matcher.find()) {
                        int expectedDays = Integer.parseInt(matcher.group(1));
                        Calendar expectedCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
                        expectedCal.setTime(todayCal.getTime());
                        expectedCal.add(Calendar.DAY_OF_MONTH, expectedDays);
                        String expectedDate = sdf.format(expectedCal.getTime());
                        if (!dateStr.equals(expectedDate)) {
                            Log.w(TAG, "Date mismatch: expected " + expectedDate + ", got " + dateStr);
                            dateStr = expectedDate;
                        }
                    }
                } else if (inputLower.contains("today")) {
                    if (!dateStr.equals(currentDate)) {
                        Log.w(TAG, "Date mismatch for today: expected " + currentDate + ", got " + dateStr);
                        dateStr = currentDate;
                    }
                }
            }

            // Parse times with flexible formats
            SimpleDateFormat[] timeFormats = {
                    new SimpleDateFormat("h:mm a", Locale.getDefault()),
                    new SimpleDateFormat("h:mma", Locale.getDefault()),
                    new SimpleDateFormat("HH:mm", Locale.getDefault()),
                    new SimpleDateFormat("h a", Locale.getDefault()),
                    new SimpleDateFormat("ha", Locale.getDefault())
            };
            for (SimpleDateFormat format : timeFormats) {
                format.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
            }

            long startTimeMillis = 0;
            long endTimeMillis = 0;
            boolean startParsed = false;
            boolean endParsed = false;

            // Try parsing start time
            for (SimpleDateFormat format : timeFormats) {
                try {
                    startTimeMillis = format.parse(startTimeStr).getTime();
                    startParsed = true;
                    break;
                } catch (ParseException e) {
                    // Try next format
                }
            }

            // Try parsing end time
            for (SimpleDateFormat format : timeFormats) {
                try {
                    endTimeMillis = format.parse(endTimeStr).getTime();
                    endParsed = true;
                    break;
                } catch (ParseException e) {
                    // Try next format
                }
            }

            if (!startParsed) {
                throw new ParseException("Invalid start time format: " + startTimeStr, 0);
            }

            if (!endParsed) {
                // Default to 1 hour later
                Calendar endCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
                endCal.setTimeInMillis(startTimeMillis);
                endCal.add(Calendar.HOUR_OF_DAY, 1);
                endTimeMillis = endCal.getTimeInMillis();
                endTimeStr = timeFormats[0].format(endCal.getTime());
                Log.d(TAG, "Defaulted end time to: " + endTimeStr);
            }

            // Combine date and time for Event
            startTimeMillis = EventRepository.convertTimeToMillis(dateStr, startTimeStr);
            endTimeMillis = EventRepository.convertTimeToMillis(dateStr, endTimeStr);

            if (startTimeMillis == 0 || endTimeMillis == 0) {
                throw new ParseException("Failed to convert times: start=" + startTimeStr + ", end=" + endTimeStr, 0);
            }

            // Create tags based on title
            List<String> tags = new ArrayList<>();
            tags.add("personal");
            String titleLower = title.toLowerCase();
            if (titleLower.contains("meeting")) tags.add("meeting");
            if (titleLower.contains("festival")) tags.add("festival");
            if (titleLower.contains("party")) tags.add("party");
            if (titleLower.contains("seminar")) tags.add("seminar");

            // Create Event object with resource ID for color
            Event event = new Event(
                    title,
                    "Scheduled by Kai",
                    location != null && !location.isEmpty() ? location : "Not specified",
                    startTimeMillis,
                    endTimeMillis,
                    dateStr,
                    tags,
                    R.color.light_purple, // Use resource ID
                    title,
                    "Scheduled Event",
                    R.drawable.default_event_image
            );

            // Add event to calendar
            if (userEmail != null && !userEmail.isEmpty()) {
                EventRepository.addPersonalEventToCalendar(userEmail, event);
                Log.d(TAG, "Event added: title=" + title + ", date=" + dateStr + ", start=" + startTimeStr + ", end=" + endTimeStr + ", location=" + location);
                // Add confirmation message with details
                String confirmationMessage = String.format(
                        "Kai: Event added: %s on %s from %s to %s at %s.",
                        title, dateStr, startTimeStr, endTimeStr, location
                );
                chatMessages.add(confirmationMessage);
                Log.d(TAG, "Added confirmation: " + confirmationMessage);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            } else {
                String errorMsg = "Kai: Sorry, I couldn't add the event. No user logged in.";
                chatMessages.add(errorMsg);
                Log.d(TAG, "Added error message: " + errorMsg);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating event: " + e.getMessage());
            String errorMsg = "Kai: Sorry, I couldn't add the event due to an error: " + e.getMessage();
            chatMessages.add(errorMsg);
            Log.d(TAG, "Added error message: " + errorMsg);
            chatAdapter.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        }
    }

}