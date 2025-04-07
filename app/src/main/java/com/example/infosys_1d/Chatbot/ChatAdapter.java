package com.example.infosys_1d.Chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<String> messages;

    public ChatAdapter(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        String message = messages.get(position);
        if (message.startsWith("You: ")) {
            // User message
            holder.userMessageText.setText(message.substring(5)); // Remove "You: "
            holder.userMessageText.setVisibility(View.VISIBLE);
            holder.botMessageText.setVisibility(View.GONE);
        } else if (message.startsWith("Bot: ")) {
            // Bot message
            holder.botMessageText.setText(message.substring(5)); // Remove "Bot: "
            holder.botMessageText.setVisibility(View.VISIBLE);
            holder.userMessageText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView botMessageText;
        TextView userMessageText;

        ChatViewHolder(View itemView) {
            super(itemView);
            botMessageText = itemView.findViewById(R.id.botMessageText);
            userMessageText = itemView.findViewById(R.id.userMessageText);
        }
    }
}