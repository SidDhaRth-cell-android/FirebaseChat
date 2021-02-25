package com.chatting.firebasechat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.Notifications.Token;
import com.chatting.firebasechat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.chatting.firebasechat.Configs.selectedList;

public class NewGroup extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton done;
    private EditText groupName;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private static final String TAG = "NewGroup";
    private String adminName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        toolbar = findViewById(R.id.toolbar);
        done = findViewById(R.id.done);
        groupName = findViewById(R.id.groupName);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users = snapshot.getValue(Users.class);
                    if (users.getId().equals(firebaseUser.getUid())) {
                        adminName = users.getUsername();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
                Intent intent = new Intent(NewGroup.this, MainDashboard.class);
                intent.putExtra("index", 1);
                startActivity(intent);

            }
        });

    }

    private void createGroup() {
        String groupId = System.currentTimeMillis() + "";
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("groupId", groupId);
        objectHashMap.put("groupTitle", groupName.getText().toString());
        objectHashMap.put("cratedBy", firebaseUser.getUid());
        objectHashMap.put("timestamp", groupId);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).setValue(objectHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", firebaseUser.getUid());
                hashMap.put("role", "creator");
                hashMap.put("timestamp", groupId);

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                reference1.child(groupId).child("Participants").child(firebaseUser.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewGroup.this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("GroupTokens");
                        String groupToken = FirebaseInstanceId.getInstance().getToken();
                        Token token = new Token(groupToken);
                        ref.child(groupId).setValue(token);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewGroup.this, "Error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewGroup.this, "Error on creating group" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}