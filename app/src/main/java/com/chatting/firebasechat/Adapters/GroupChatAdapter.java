package com.chatting.firebasechat.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.Models.GroupMessageModel;
import com.chatting.firebasechat.Models.MessageModel;
import com.chatting.firebasechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.SingleViewHolder> {

    List<GroupMessageModel> messageModels;
    Context context;
    private static final String TAG = "ChatAdapter";
    int SENDER_VIEW_TYPE = 0;
    int RECEIVER_VIEW_TYPE = 1;
    private FirebaseUser firebaseUser;


    public GroupChatAdapter(List<GroupMessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }


    public class SingleViewHolder extends RecyclerView.ViewHolder {
        TextView receiverText, receiverTime, txt_seen, sentBy;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverText = itemView.findViewById(R.id.senderText);
            receiverTime = itemView.findViewById(R.id.senderTime);
            txt_seen = itemView.findViewById(R.id.status);
            sentBy = itemView.findViewById(R.id.userName);
        }
    }

    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENDER_VIEW_TYPE) {
            view = LayoutInflater.from(context).inflate(R.layout.group_sample_sender, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.group_sample_receiver, parent, false);
        }
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder holder, int position) {
        GroupMessageModel model = messageModels.get(position);
        String message = model.getMessage();
        holder.receiverText.setText(message);
        String timeStamp = model.getTimestamp();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.receiverTime.setText(datetime);

        setSendersNameing(model, holder);



    }

    private void setSendersNameing(GroupMessageModel senderId, final SingleViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("id").equalTo(senderId.getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("username").getValue().toString();
                    holder.sentBy.setText(name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageModels.get(position).getSender().equals(firebaseUser.getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }



}
