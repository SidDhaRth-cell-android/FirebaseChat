package com.chatting.firebasechat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.SaveRequest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chatting.firebasechat.Adapters.ChatAdapter;
import com.chatting.firebasechat.Models.MessageModel;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.Notifications.ApiInterface;
import com.chatting.firebasechat.Notifications.Client;
import com.chatting.firebasechat.Notifications.Data;
import com.chatting.firebasechat.Notifications.MyResponse;
import com.chatting.firebasechat.Notifications.Sender;
import com.chatting.firebasechat.Notifications.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView chats;
    private String name, receiverId;
    private EditText getMessage;
    private FirebaseDatabase database;
    private TextView username, status;
    private DatabaseReference reference;
    public static final String CHANNEL_1_ID = "channel 1";
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private static final String TAG = "ChattingDetailActivity";
    private Button send;
    private ChatAdapter chatAdapter;
    private List<MessageModel> messageModels;
    private boolean isChat;
    private ValueEventListener seenListener;
    private ApiInterface apiInterface;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_detail);


        apiInterface = Client.getClient("https://fcm.googleapis.com/").create(ApiInterface.class);
        toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.userName);
        getMessage = findViewById(R.id.message);
        status = findViewById(R.id.status);
        //   name = getIntent().getStringExtra("username");
        receiverId = getIntent().getStringExtra("userId");

        send = findViewById(R.id.send);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();


        toolbar.setNavigationIcon(R.drawable.ic_baseline_person_24);
        chats = findViewById(R.id.chats);
        chats.setLayoutManager(new LinearLayoutManager(this));


        messageModels = new ArrayList<>();

        Log.d(TAG, "onCreate: " + receiverId);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                username.setText(users.getUsername());
                if (isChat) {
                    if (users.getStatus().equals("online")) {
                        status.setText("online");
                    } else {
                        status.setText("offline");
                    }
                } else {
                    status.setText("");
                }
                readMessage(firebaseUser.getUid(), receiverId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(receiverId);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = getMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(firebaseUser.getUid(), receiverId, message);
                } else {
                    Toast.makeText(ChattingDetailActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                getMessage.setText("");


            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());


    }


    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void sendMessage(String senderId, final String receiverId, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("senderId", senderId);
        messageMap.put("receiverId", receiverId);
        messageMap.put("message", message);
        String time = new SimpleDateFormat("hh:mm aa").format(new Date().getTime());
        messageMap.put("time", time);
        messageMap.put("isseen", false);
        databaseReference.child("Chats").push().setValue(messageMap);

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(receiverId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(receiverId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiverId)
                .child(firebaseUser.getUid());

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef2.child("id").setValue(firebaseUser.getUid());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final String msg = message;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if (notify) {
                    sendNotifications(receiverId, users.getUsername(), msg);
                }
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotifications(final String receiverId, final String username, final String msg) {
        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiverId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, msg, username, receiverId, "single");
                    Sender sender = new Sender(data, token.getToken());
                    apiInterface.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(ChattingDetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.getLocalizedMessage());

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readMessage(final String myID, final String userId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel model = snapshot.getValue(MessageModel.class);
                    Log.d(TAG, "onDataChange: " + myID + "," + userId);
                    if (model.getReceiverId().equals(myID) && model.getSenderId().equals(userId) ||
                            model.getReceiverId().equals(userId) && model.getSenderId().equals(myID)) {
                        messageModels.add(model);
                    }
                }
                chatAdapter = new ChatAdapter(messageModels, ChattingDetailActivity.this);
                chats.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void seenMessage(final String userId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel messageModel = snapshot.getValue(MessageModel.class);
                    if (messageModel.getReceiverId().equals(firebaseUser.getUid()) && messageModel.getSenderId().equals(userId)) {
                        HashMap<String, Object> objectHashMap = new HashMap<>();
                        objectHashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(objectHashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        isChat = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        isChat = false;
    }
}