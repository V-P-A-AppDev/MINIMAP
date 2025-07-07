package com.v_p_a_appdev.minimap.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.v_p_a_appdev.minimap.R;
import com.v_p_a_appdev.minimap.Utils.SecurityUtils;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatList> chatList;
    private Context context;

    public ChatAdapter(ArrayList<ChatList> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.messageview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatList chatList = this.chatList.get(position);
        
        // Validate and sanitize message content
        String sanitizedMessage = SecurityUtils.validateChatMessage(chatList.getMessage());
        if (sanitizedMessage == null) {
            // If message is invalid, show a placeholder
            holder.message.setText("[Message content removed for security]");
        } else {
            holder.message.setText(sanitizedMessage);
        }
        
        // Validate and sanitize user name
        String sanitizedName = SecurityUtils.validateAndSanitizeName(chatList.getUserName());
        if (sanitizedName == null) {
            holder.userName.setText("Unknown User");
        } else {
            holder.userName.setText(sanitizedName);
        }
        
        // Validate URL before loading image
        if (chatList.getUserImageUrl() != null && SecurityUtils.isValidUrl(chatList.getUserImageUrl())) {
            Glide.with(context).load(chatList.getUserImageUrl()).into(holder.userImage);
        } else {
            // Load default image if URL is invalid
            holder.userImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        if (chatList.isCurrUser()) {
            holder.message.setGravity(Gravity.END);
            holder.message.setTextColor(Color.BLUE);
        } else {
            holder.message.setGravity(Gravity.START);
            holder.message.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView message;
        private final TextView userName;
        private final ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageView);
            userName = itemView.findViewById(R.id.userName);
            userImage = itemView.findViewById(R.id.userImage);
        }

        public TextView getTextView() {
            return message;
        }
    }
}