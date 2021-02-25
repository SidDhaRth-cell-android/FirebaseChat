package com.chatting.firebasechat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.Activities.GroupChatActivity;
import com.chatting.firebasechat.ChattingDetailActivity;
import com.chatting.firebasechat.Models.GroupMessageModel;
import com.chatting.firebasechat.Models.GroupNameModel;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GroupNameAdapter extends RecyclerView.Adapter<GroupNameAdapter.ChattingViewHolder> {

    private Context context;
    private List<GroupNameModel> chatModels;
    private String lastMessage;
    private String groupId;
    private List<String> groupIdLIst;
    private static final String TAG = "GroupNameAdapter";


    public GroupNameAdapter(Context context, List<GroupNameModel> chatModels, List<String> groupIdLIst) {
        this.groupIdLIst = groupIdLIst;
        this.context = context;
        this.chatModels = chatModels;

    }


    public class ChattingViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, senderId, senderMsg;

        public ChattingViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            senderMsg = itemView.findViewById(R.id.senderMsg);
            senderId = itemView.findViewById(R.id.senderId);
        }
    }

    @NonNull
    @Override
    public ChattingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chat_layout, parent, false);
        return new ChattingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingViewHolder holder, final int position) {
        final GroupNameModel chatModel = chatModels.get(position);
        holder.userName.setText(chatModel.getGroupTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", chatModel.getGroupId());
                context.startActivity(intent);
            }
        });

        loadLastMessage(chatModel, holder);


    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    private void loadLastMessage(GroupNameModel model, ChattingViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(model.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String message = "" + ds.child("message").getValue();
                            String sender = "" + ds.child("sender").getValue();

                            holder.senderMsg.setText(message);


                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                            reference1.orderByChild("id").equalTo(sender).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String name = "" + dataSnapshot.child("username").getValue();
                                        holder.senderId.setText(name);
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
