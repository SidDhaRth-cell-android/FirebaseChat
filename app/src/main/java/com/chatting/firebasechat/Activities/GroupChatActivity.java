package com.chatting.firebasechat.Activities;

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
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatting.firebasechat.Adapters.ChatAdapter;
import com.chatting.firebasechat.Adapters.GroupChatAdapter;
import com.chatting.firebasechat.ChattingDetailActivity;
import com.chatting.firebasechat.Models.GroupMessageModel;
import com.chatting.firebasechat.Models.GroupNameModel;
import com.chatting.firebasechat.Models.MessageModel;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.Notifications.ApiInterface;
import com.chatting.firebasechat.Notifications.Client;
import com.chatting.firebasechat.Notifications.Data;
import com.chatting.firebasechat.Notifications.MyResponse;
import com.chatting.firebasechat.Notifications.Sender;
import com.chatting.firebasechat.Notifications.Token;
import com.chatting.firebasechat.R;
import com.google.android.gms.tasks.OnFailureListener;
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

public class
GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView chats;
    private String groupName, groupId, myGroupRole;
    private EditText getMessage;
    private FirebaseDatabase database;
    private TextView username, status;
    private DatabaseReference reference;
    private ImageView addParticipants;
    public static final String CHANNEL_1_ID = "channel 1";
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private static final String TAG = "ChattingDetailActivity";
    private ApiInterface apiInterface;
    private Button send;
    private GroupChatAdapter chatAdapter;
    private List<GroupMessageModel> messageModels;
    private boolean isChat;
    private String adminName;
    private String adminId;
    private ValueEventListener seenListener;
    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        apiInterface = Client.getClient("https://fcm.googleapis.com/").create(ApiInterface.class);

        groupId = getIntent().getStringExtra("groupId");
        toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.userName);
        addParticipants = findViewById(R.id.addParticipants);
        getMessage = findViewById(R.id.message);
        send = findViewById(R.id.send);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);

        updateToken(FirebaseInstanceId.getInstance().getToken());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chats = findViewById(R.id.chats);
        chats.setLayoutManager(new LinearLayoutManager(this));
        loadGroupInfo();
        loadGroupMessages();
        loadMyGroupRole();

        addParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myGroupRole.equals("creator") || myGroupRole.equals("admin")) {
                    Intent intent = new Intent(GroupChatActivity.this, GroupAddParticipantActivity.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);

                } else {
                    Toast.makeText(GroupChatActivity.this, "You are not creator and admin", Toast.LENGTH_SHORT).show();
                }

            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupChatActivity.this, GroupSettingActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupRole", myGroupRole);
                startActivity(intent);

            }
        });


        messageModels = new ArrayList<>();


        chatAdapter = new GroupChatAdapter(messageModels, GroupChatActivity.this);
        chats.setAdapter(chatAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = getMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(firebaseUser.getUid(), groupId, message);
                } else {
                    Toast.makeText(GroupChatActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                getMessage.setText("");


            }
        });
    }

    private void loadMyGroupRole() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").orderByChild("id").equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    myGroupRole = "" + ds.child("role").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupMessages() {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Groups");
        mReference.child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    GroupMessageModel groupMessageModel = ds.getValue(GroupMessageModel.class);
                    messageModels.add(groupMessageModel);
                }
                chatAdapter = new GroupChatAdapter(messageModels, GroupChatActivity.this);
                chats.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String groupTitle = (String) ds.child("groupTitle").getValue();
                    adminName = (String) ds.child("cratedBy").getValue();

                    toolbar.setTitle(groupTitle);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String senderId, String groupId, String message) {
        String t_stamp = System.currentTimeMillis() + "";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");

        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("sender", senderId);
        messageMap.put("message", message);
        messageMap.put("timestamp", t_stamp);
        databaseReference.child(groupId).child("Messages").child(t_stamp).setValue(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(GroupChatActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, "Error on sending message" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.child("GroupChats").push().setValue(messageMap);

        final String msg = message;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                if (notify) {
                    sendNotification(groupId, users.getUsername(), msg);
                }
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String groupId, String username, String msg) {
        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("GroupTokens");
        Query query = tokens.orderByKey().equalTo(groupId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, msg, username, groupId, "group");
                    Sender sender = new Sender(data, token.getToken());
                    apiInterface.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(GroupChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("GroupTokens");
        Token token1 = new Token(token);
        reference.child(groupId).setValue(token1);

    }


}