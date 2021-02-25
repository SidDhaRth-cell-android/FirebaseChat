package com.chatting.firebasechat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupAddParticipantActivity extends AppCompatActivity {

    private RecyclerView userRecycler;
    private FirebaseUser firebaseUser;
    private static final String TAG = "GroupAddParticipantActi";
    private String groupId, myGroupRole = "";
    private List<Users> usersList = new ArrayList<>();
    private AdapterParticipantAdd adapterParticipantAdd;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add_participant);

        groupId = getIntent().getStringExtra("groupId");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userRecycler = findViewById(R.id.userRecyclerView);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbar);


        loadGroupInfo();

    }

    private void getAllUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                Log.d(TAG, "onDataChange: "+snapshot.getChildrenCount());
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Users usersModel = ds.getValue(Users.class);
                    if (!firebaseUser.getUid().equals(usersModel.getId())) {
                        usersList.add(usersModel);

                    }
                    adapterParticipantAdd = new AdapterParticipantAdd(GroupAddParticipantActivity.this, usersList, groupId, myGroupRole);
                    userRecycler.setAdapter(adapterParticipantAdd);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Groups");
        reference2.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String groupId = "" + ds.child("groupId").getValue();
                    String groupTitle = "" + ds.child("groupTitle").getValue();
                    String createdBy = "" + ds.child("createdBy").getValue();
                    String timestamp = "" + ds.child("timestamp").getValue();

                    reference.child(groupId).child("Participants").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                myGroupRole = "" + snapshot.child("role").getValue();
                                toolbar.setTitle(groupTitle + "(" + myGroupRole + ")");
                                getAllUsers();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}