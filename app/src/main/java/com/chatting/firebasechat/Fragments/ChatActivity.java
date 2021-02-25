package com.chatting.firebasechat.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.Activities.AllChatsActivity;
import com.chatting.firebasechat.Adapters.ChattingAdapter;
import com.chatting.firebasechat.Models.Chatlist;
import com.chatting.firebasechat.Models.MessageCounterModel;
import com.chatting.firebasechat.Models.MessageModel;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.Notifications.Token;
import com.chatting.firebasechat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends Fragment {

    private View view;
    private List<MessageCounterModel> counterModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<Users> mUsers = new ArrayList<>();
    private FirebaseDatabase database;
    private FloatingActionButton allChats;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private TextView noChats;
    private ProgressDialog dialog;
    private static final String TAG = "ChatActivity";

    private ChattingAdapter chattingAdapter;
    int unread = 0;
    private List<Chatlist> usersList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.content_main, container, false);

        database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Fetching Chats");
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        dialog.show();
        noChats = view.findViewById(R.id.noChats);

        recyclerView = view.findViewById(R.id.allChats);
        allChats = view.findViewById(R.id.allchats);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Chatlist chatlist = snapshot1.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }
                getMessageCounts(); //yeh wala
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        updateToken(FirebaseInstanceId.getInstance().getToken());

        allChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AllChatsActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void getMessageCounts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override//crash kahan hai/?
            //crash ni aa rha bas bol deta hai ANR
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    MessageCounterModel counterModel = new MessageCounterModel();
                    while (!messageModel.isIsseen()) {
                        unread++;
                    }
                    //yahan pe unread ka count kardiyo ya tph counter hata diyo sirf unread use karliyo
                    counterModel.setCounter(unread);
                    counterModel.setId(messageModel.getReceiverId());
                    counterModelList.add(counterModel);
                    unread = 0;
                    //set kahan kar raha hai?

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    int count = 0;

    private void chatList() {
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    for (Chatlist chatlist : usersList) {
                        if (users.getId().equals(chatlist.getId())) {
                            mUsers.add(users);
                        }
                    }
                }
                chattingAdapter = new ChattingAdapter(getContext(), mUsers, true, counterModelList);
                recyclerView.setAdapter(chattingAdapter);
                chattingAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.dismiss();
    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);

    }


    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }

}
