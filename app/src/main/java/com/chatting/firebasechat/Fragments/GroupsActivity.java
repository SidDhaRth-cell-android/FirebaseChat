package com.chatting.firebasechat.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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

import com.chatting.firebasechat.Adapters.GroupNameAdapter;
import com.chatting.firebasechat.Models.GroupMessageModel;
import com.chatting.firebasechat.Models.GroupNameModel;
import com.chatting.firebasechat.Models.MessageModel;
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

public class GroupsActivity extends Fragment {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<GroupNameModel> usersList = new ArrayList<>();
    private FloatingActionButton allChats;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private TextView noChats;
    private ProgressDialog dialog;
    private GroupNameAdapter groupNameAdapter;
    private String groupId;
    private List<String> groupIdlIst = new ArrayList<>();
    private static final String TAG = "GroupActivity";
    private List<GroupNameModel> nameModels = new ArrayList<>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.activity_group, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = view.findViewById(R.id.toolbar);
        noChats = view.findViewById(R.id.noChats);

        reference = FirebaseDatabase.getInstance().getReference("Groups");


        readData();
        recyclerView = view.findViewById(R.id.allChats);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        return view;
    }


    private void readData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Participants").child(firebaseUser.getUid()).exists()) {
                        GroupNameModel groupMessageModel = snapshot.getValue(GroupNameModel.class);
                        nameModels.add(groupMessageModel);
                    }
                }
                groupNameAdapter = new GroupNameAdapter(getContext(), nameModels, groupIdlIst);
                recyclerView.setAdapter(groupNameAdapter);
                groupNameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
