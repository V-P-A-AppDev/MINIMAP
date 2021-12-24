package com.v_p_a_appdev.minimap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private Button sendButton, backButton;
    private TextView nameview;
    private EditText messageBox;
    private String userId, chatId;
    private ChatAdapter chatAdapter;
    private RecyclerView chatrecyclerView;
    private ArrayList<ChatList> chatMessages;
    private ImageView imageView;
    DatabaseReference chatDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
        nameview.setText(getIntent().getStringExtra("UserName"));

        String imageUrl = getIntent().getStringExtra("imageView");
        if (imageUrl != "") {
            Glide.with(getApplication()).load(imageUrl).into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher_foreground);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
    }

    private void SendMessage() {
        String message = messageBox.getText().toString();
        if (!message.isEmpty()) {
            Map newMessage = new HashMap();
            newMessage.put("createdBy", userId);
            newMessage.put("message", message);
            chatDatabaseReference.push().setValue(newMessage);
        }
        messageBox.setText(null);
    }

    private void getMessages() {
        chatDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    String message = null;
                    String messageCreator = null;

                    if (map.get("message") != null) {
                        message = map.get("message").toString();
                    }
                    if (map.get("createdBy") != null) {
                        messageCreator = map.get("createdBy").toString();
                    }
                    if (message != null && messageCreator != null) {
                        chatMessages.add(new ChatList(message, messageCreator == userId));
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.goBack);
        nameview = findViewById(R.id.nameView);
        messageBox = findViewById(R.id.messageBox);

        chatId = getIntent().getStringExtra("ConnectionId");
        userId = FirebaseAuth.getInstance().getUid();
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        chatMessages = new ArrayList<>();
        getMessages();
        chatAdapter = new ChatAdapter(chatMessages, this);
        chatrecyclerView = findViewById(R.id.chatsView);
        chatrecyclerView.setNestedScrollingEnabled(false);
        chatrecyclerView.setHasFixedSize(false);
        chatrecyclerView.setAdapter(chatAdapter);
        chatrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView = findViewById(R.id.imageView);
    }
}