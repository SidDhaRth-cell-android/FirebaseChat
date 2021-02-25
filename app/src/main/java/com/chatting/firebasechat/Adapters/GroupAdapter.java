package com.chatting.firebasechat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.Activities.MainActivity;
import com.chatting.firebasechat.ChattingDetailActivity;
import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;

import java.util.ArrayList;
import java.util.List;

import static com.chatting.firebasechat.Configs.selectedList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ChattingViewHolder> {

    private Context context;
    private List<Users> chatModels;
    private String lastMessage;
    int row_index = 0;
    private MainViewModel mainViewModel;
    private boolean isEnable = false;
    private boolean isSelectAll = false;


    public GroupAdapter(Context context, List<Users> chatModels) {
        this.context = context;
        this.chatModels = chatModels;

    }


    public class ChattingViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, lastMessage;
        private RelativeLayout mainLayout;
        private ImageView checkbox;

        public ChattingViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            checkbox = itemView.findViewById(R.id.checkbox);
        }

    }

    @NonNull
    @Override
    public ChattingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_layout, parent, false);

        mainViewModel = ViewModelProviders.of((FragmentActivity) context).get(MainViewModel.class);
        return new ChattingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChattingViewHolder holder, final int position) {
        Users users = chatModels.get(position);

        holder.userName.setText(users.getUsername());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View view) {
                if (!isEnable) {
                    android.view.ActionMode.Callback callback = new android.view.ActionMode.Callback2() {
                        @Override
                        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(final android.view.ActionMode actionMode, Menu menu) {
                            isEnable = true;
                            ClickItem(holder);
                            mainViewModel.getText().observe((LifecycleOwner) context, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    actionMode.setTitle(String.format("%s Selected", s));
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            if (id == R.id.selectAll) {
                                if (selectedList.size() == chatModels.size()) {
                                    isSelectAll = false;
                                    selectedList.clear();
                                } else {
                                    isSelectAll = true;
                                    selectedList.clear();
                                    selectedList.add(chatModels.get(position).getId());
                                }
                                mainViewModel.setText(String.valueOf(selectedList.size()));
                                notifyDataSetChanged();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(android.view.ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectedList.clear();
                            notifyDataSetChanged();
                        }
                    };
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    ClickItem(holder);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnable) {
                    ClickItem(holder);
                } else {
                    Toast.makeText(context, "You clicked " + chatModels.get(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isSelectAll) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }


    }

    private void ClickItem(ChattingViewHolder holder) {
        if (holder.checkbox.getVisibility() == View.GONE) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            selectedList.add(chatModels.get(holder.getAdapterPosition()).getId());
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            selectedList.remove(chatModels.get(holder.getAdapterPosition()).getId());
        }
        mainViewModel.setText(String.valueOf(selectedList.size()));
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }
}
