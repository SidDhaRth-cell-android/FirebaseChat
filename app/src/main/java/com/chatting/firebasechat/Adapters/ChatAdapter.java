package com.chatting.firebasechat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.Models.MessageModel;
import com.chatting.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.SingleViewHolderForSingleChat> {

    List<MessageModel> messageModels;
    Context context;
    private static final String TAG = "ChatAdapter";
    int SENDER_VIEW_TYPE = 0;
    int RECEIVER_VIEW_TYPE = 1;
    private FirebaseUser firebaseUser;

    public ChatAdapter(List<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    public class SingleViewHolderForSingleChat extends RecyclerView.ViewHolder {
        TextView senderText, senderTime, txt_seen;

        public SingleViewHolderForSingleChat(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            txt_seen = itemView.findViewById(R.id.status);
        }
    }

    @NonNull
    @Override
    public SingleViewHolderForSingleChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENDER_VIEW_TYPE) {
            view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
        }
        return new SingleViewHolderForSingleChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolderForSingleChat holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        holder.senderText.setText(messageModel.getMessage());
        holder.senderTime.setText(messageModel.getTime());

        if (position == messageModels.size() - 1) {
            if (messageModel.isIsseen()) {
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageModels.get(position).getSenderId().equals(firebaseUser.getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }



}
