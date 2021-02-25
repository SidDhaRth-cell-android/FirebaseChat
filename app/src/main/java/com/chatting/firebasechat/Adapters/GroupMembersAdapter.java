package com.chatting.firebasechat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.ChattingDetailActivity;
import com.chatting.firebasechat.Helper.DatabaseHelper;
import com.chatting.firebasechat.Models.GroupNameModel;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import static com.chatting.firebasechat.Configs.selectedList;


public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ChattingViewHolder> {

    private Context context;
    private List<Users> chatModels;
    private String lastMessage;
    private DatabaseReference reference;
    private static final String TAG = "GroupMembersAdapter";
    private FirebaseUser firebaseUser;
    private String groupAdminId;
    private String groupId;


    public GroupMembersAdapter(Context context, List<Users> chatModels, String groupId) {
        this.context = context;
        this.groupId = groupId;
        this.chatModels = chatModels;

    }


    public class ChattingViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, lastMessage;

        public ChattingViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }

    @NonNull
    @Override
    public ChattingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
        return new ChattingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingViewHolder holder, final int position) {
        final Users chatModel = chatModels.get(position);
        holder.userName.setText(chatModel.getUsername());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("GroupDetails");
       /* reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final GroupNameModel groupNameModel = snapshot.getValue(GroupNameModel.class);
                    groupAdminId = groupNameModel.getAdminId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (groupAdminId.equals(firebaseUser.getUid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Removing this User...");
                    builder.setMessage("Do you want to remove this User?");
                    builder.setIcon(R.drawable.ic_baseline_delete_24);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            reference.child(groupId).child("groupMembers").child(String.valueOf(position)).removeValue();
                            getMembers();
                            dialogInterface.dismiss();
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
                }
                return true;
            }
        });
    }

    private void getMembers() {
        selectedList.clear();
        reference = FirebaseDatabase.getInstance().getReference("GroupDetails").child(groupId).child("groupMembers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    selectedList.add(Objects.requireNonNull(snapshot.getValue()).toString());
                }
                reference.setValue(selectedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return chatModels.size();
    }

}
