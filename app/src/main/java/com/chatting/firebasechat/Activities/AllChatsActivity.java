package com.chatting.firebasechat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.chatting.firebasechat.Adapters.ChattingAdapter;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.chatting.firebasechat.Configs.selectedList;

public class AllChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<Users> usersList = new ArrayList<>();
    private FirebaseDatabase database;
    private static final String TAG = "AllChatsActivity";
    private DatabaseReference reference;
    private ChattingAdapter chattingAdapter;
    private FloatingActionButton group;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main2);
        database = FirebaseDatabase.getInstance();
        group = findViewById(R.id.group);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        readUsers();


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("All Employees");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.allChats);
        recyclerView.addItemDecoration(new DividerItemDecoration(AllChatsActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        chattingAdapter = new ChattingAdapter(this, usersList, false,null);
        recyclerView.setAdapter(chattingAdapter);

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedList.clear();
                Intent intent = new Intent(AllChatsActivity.this, NewGroup.class);
                startActivity(intent);
            }
        });


    }

    private void readUsers() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: getChildrenCount"+dataSnapshot.getChildrenCount());
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users = snapshot.getValue(Users.class);
                    Log.d(TAG, "onDataChange: " + users.getId() + "=" + firebaseUser.getUid());
                    if (!users.getId().equals(firebaseUser.getUid())) {
                        usersList.add(users);
                    }
                }
                chattingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}