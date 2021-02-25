package com.chatting.firebasechat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.chatting.firebasechat.Adapters.ChattingAdapter;
import com.chatting.firebasechat.Adapters.GroupAdapter;
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
import java.util.HashMap;
import java.util.List;

import static com.chatting.firebasechat.Configs.selectedList;


public class CreateGroup extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<Users> usersList = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private GroupAdapter chattingAdapter;
    private static final String TAG = "CreateGroup";
    private FloatingActionButton group;
    private String groupId;


    private FirebaseUser firebaseUser;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main3);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        from = getIntent().getStringExtra("from");
        groupId = getIntent().getStringExtra("groupId");


        database = FirebaseDatabase.getInstance();
        group = findViewById(R.id.group);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");


        readUsers();


        recyclerView = findViewById(R.id.allChats);
        recyclerView.addItemDecoration(new DividerItemDecoration(CreateGroup.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from != null) {
                    if (from.equals("GroupSettingActivity")) {
                        reference = FirebaseDatabase.getInstance().getReference("GroupDetails").child(groupId).child("groupMembers");
                        reference.setValue(selectedList);
                        Intent intent = new Intent(CreateGroup.this, MainDashboard.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CreateGroup.this, NewGroup.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(CreateGroup.this, NewGroup.class);
                    startActivity(intent);
                }
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_items, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) CreateGroup.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(CreateGroup.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void readUsers() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users = snapshot.getValue(Users.class);
                    if (!users.getId().equals(firebaseUser.getUid())) {
                        usersList.add(users);
                    }
                }
                chattingAdapter = new GroupAdapter(CreateGroup.this, usersList);
                recyclerView.setAdapter(chattingAdapter);
                chattingAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}