package com.chatting.firebasechat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.ChattingDetailActivity;
import com.chatting.firebasechat.Helper.UnreadMessageLayout;
import com.chatting.firebasechat.Models.ChatModel;
import com.chatting.firebasechat.Models.MessageCounterModel;
import com.chatting.firebasechat.Models.MessageModel;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.android.material.internal.TextDrawableHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ChattingViewHolder> {

    private Context context;
    private List<Users> chatModels;
    private String lastMessage;
    private static final String TAG = "ChattingAdapter";
    private boolean isChat;
    private List<MessageCounterModel> unread;


    public ChattingAdapter(Context context, List<Users> chatModels, boolean isChat, List<MessageCounterModel> unread) {
        this.context = context;
        this.isChat = isChat;
        this.unread = unread;
        this.chatModels = chatModels;

    }


    public class ChattingViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, lastMessage, messageCount;
        private ImageView newMessage;
        private RelativeLayout relativeLayout;

        public ChattingViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            newMessage = itemView.findViewById(R.id.newMessage);
            messageCount = itemView.findViewById(R.id.messageCount);
            relativeLayout = itemView.findViewById(R.id.unRead);
        }
    }

    @NonNull
    @Override
    public ChattingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false);
        return new ChattingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingViewHolder holder, int position) {
        final Users chatModel = chatModels.get(position);
      //  MessageCounterModel counterModel = unread.get(position);
        holder.userName.setText(chatModel.getUsername());
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (isChat) {
            lastMessage(chatModel.getId(), holder.lastMessage);
        } else {
            holder.lastMessage.setVisibility(View.GONE);
        }

       /* DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist");
        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String receriverId = "" + dataSnapshot.child("id").getValue().toString();
                    Log.d(TAG, "onDataChange: " + counterModel.getId() + "=" + receriverId);
                    if (counterModel.getId().equals(receriverId) && counterModel.getCounter() >0) {
                        holder.relativeLayout.setVisibility(View.VISIBLE);
                        holder.messageCount.setText("" + counterModel.getCounter());
                        notifyDataSetChanged();
                    } else {
                        holder.relativeLayout.setVisibility(View.GONE);
                    }
                    //unread count kahan hai

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChattingDetailActivity.class);
                intent.putExtra("userId", chatModel.getId());
                context.startActivity(intent);
            }
        });

        messageCounter(holder, chatModel);


    }

    private void messageCounter(ChattingViewHolder holder, Users chatModel) {

    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }

    private void checkForNewMessages(ChattingViewHolder holder, Users chatModel) {
        final int[] unreadMessages = {0};
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = reference.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);
                    if (!messageModel.isIsseen() == true) {
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chatlist");
                        reference1.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Log.d(TAG, "onDataChange: " + messageModel.getReceiverId() + "=" + dataSnapshot.child("id").getValue().toString());
                                    if (messageModel.getReceiverId().equals(dataSnapshot.child("id").getValue().toString())) {
                                        unreadMessages[0]++;
                                        if (unreadMessages[0] > 0) {
                                            holder.relativeLayout.setVisibility(View.VISIBLE);
                                            holder.messageCount.setText("" + unreadMessages[0]);
                                        } else {
                                            holder.relativeLayout.setVisibility(View.GONE);
                                        }
                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                    /*if (messageModel.re().equals(firebaseUser.getUid()) || messageModel && messageModel.isIsseen() == false) {
                        unreadMessages[0]++;
                    }*/
                }


               /* for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);
                    if (messageModel.getSenderId().equals(firebaseUser.getUid()) || messageModel&& messageModel.isIsseen() == false) {
                        unreadMessages[0]++;
                    }
                   *//* if (!snapshot1.child("isseen").getValue().toString().equals("true")) {
                        unreadMessages[0]++;
                    }*//*
                }
                if (unreadMessages[0] > 0) {
                        holder.relativeLayout.setVisibility(View.VISIBLE);
                    holder.messageCount.setText("" + unreadMessages[0]);
                } else {
                    holder.relativeLayout.setVisibility(View.GONE);
                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void lastMessage(String userId, TextView last_msg) {
        lastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);
                    if (messageModel.getReceiverId().equals(firebaseUser.getUid()) && messageModel.getSenderId().equals(userId) ||
                            messageModel.getReceiverId().equals(userId) && messageModel.getSenderId().equals(firebaseUser.getUid())) {
                        lastMessage = messageModel.getMessage();
                    }
                }
                switch (lastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(lastMessage);
                        break;
                }

                lastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
