package com.example.infosys_1d.Chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<String> messages;

    public ChatAdapter(List<String> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = messages.get(position);
        if (message.startsWith("You: ")) {
            holder.userMessageText.setText(message.substring(5)); // Remove "You: "
            holder.userMessageText.setVisibility(View.VISIBLE);
            holder.botMessageText.setVisibility(View.GONE);
        } else if (message.startsWith("Kai: ")) {
            holder.botMessageText.setText(message.substring(5)); // Remove "Kai: "
            holder.botMessageText.setVisibility(View.VISIBLE);
            holder.userMessageText.setVisibility(View.GONE);
        } else {
            // Fallback: treat as bot message
            holder.botMessageText.setText(message);
            holder.botMessageText.setVisibility(View.VISIBLE);
            holder.userMessageText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageText;
        TextView botMessageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            botMessageText = itemView.findViewById(R.id.botMessageText);
        }
    }
}