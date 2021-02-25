package com.chatting.firebasechat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chatting.firebasechat.Adapters.GroupMembersAdapter;
import com.chatting.firebasechat.Models.GroupNameModel;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.chatting.firebasechat.Configs.selectedList;

public class GroupSettingActivity extends AppCompatActivity {


    private String groupRole, adminName, groupId, adminId;
    private TextView txt_group_name, txt_admin_name;
    private RecyclerView membersRecyclerView;
    private DatabaseReference reference;
    private static final String TAG = "GroupSettingActivity";
    private List<String> groupMembersId = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private List<Users> groupMembers = new ArrayList<>();
    private GroupMembersAdapter groupMembersAdapter;
    private TextView removeGroup;
    private TextView leaveGroup;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        groupRole = getIntent().getStringExtra("groupRole");
        groupId = getIntent().getStringExtra("groupId");
        leaveGroup = findViewById(R.id.addMembers);
        removeGroup = findViewById(R.id.removeGroup);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txt_admin_name = findViewById(R.id.adminName);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setHasFixedSize(true);

        loadGroupInfo();


        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingActivity.this);
                builder.setTitle("Leave Group");
                builder.setMessage("Are you really want to leave this group?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        leaveGroup();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        removeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupRole.equals("admin") || groupRole.equals("creator")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupSettingActivity.this);
                    builder.setTitle("Delete Group!");
                    builder.setMessage("Do you really want to delete this group?");
                    builder.setIcon(R.drawable.ic_baseline_delete_24);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteGroup();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(GroupSettingActivity.this, "You are not admin and creator", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void leaveGroup() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(GroupSettingActivity.this, "You are no longer participant of this group", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GroupSettingActivity.this, MainDashboard.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupSettingActivity.this, "Error leaving group", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteGroup() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(GroupSettingActivity.this, "Group Deleted Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GroupSettingActivity.this, MainDashboard.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupSettingActivity.this, "Error Deleting Group", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String groupTitle = "" + ds.child("groupTitle").getValue();
                    String createdBy = "" + ds.child("groupTitle").getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}