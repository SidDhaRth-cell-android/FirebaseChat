package com.chatting.firebasechat.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class AdapterParticipantAdd extends RecyclerView.Adapter<AdapterParticipantAdd.HolderParticipantAdd> {

    private Context context;
    private List<Users> usersList;
    private String groupId, myGroupRole;


    public AdapterParticipantAdd(Context context, List<Users> usersList, String groupId, String myGroupRole) {
        this.context = context;
        this.usersList = usersList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_add_participant, parent, false);
        return new HolderParticipantAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantAdd holder, int position) {
        Users users = usersList.get(position);
        String name = users.getUsername();
        String uid = users.getId();

        holder.username.setText(name);

        checkIfAlreadyExists(users, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                reference.child(groupId).child("Participants").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String hisPreviousRole = "" + snapshot.child("role").getValue();
                            String[] options;
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Choose Options");
                            if (myGroupRole.equals("creator")) {
                                if (hisPreviousRole.equals("admin")) {
                                    //i'm creator he is admin
                                    options = new String[]{"Remove Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            if (which == 0) {
                                                //Remove admin clicked
                                                removeAdmin(users);

                                            } else {
                                                //Remove user clicked
                                                removeParticipant(users);
                                            }
                                        }
                                    }).show();
                                } else if (hisPreviousRole.equals("participant")) {
                                    //i'm creator, he is participant
                                    options = new String[]{"Make Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            if (which == 0) {
                                                //Remove admin clicked
                                                makeAdmin(users);

                                            } else {
                                                //Remove user clicked
                                                removeParticipant(users);
                                            }
                                        }
                                    }).show();
                                }
                            } else if (myGroupRole.equals("admin")) {
                                if (hisPreviousRole.equals("creator")) {
                                    //i'm admin, he is creator
                                    Toast.makeText(context, "Creator of Group...", Toast.LENGTH_SHORT).show();
                                } else if (hisPreviousRole.equals("admin")) {
                                    //I'm admin, he is admin too
                                    options = new String[]{"Remove Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            if (which == 0) {

                                            } else {

                                            }

                                        }
                                    }).show();
                                } else if (hisPreviousRole.equals("participant")) {
                                    //I'm admin, he is participant
                                    options = new String[]{"Make Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            if (which == 0) {

                                            } else {

                                            }

                                        }
                                    }).show();
                                }

                            }

                        } else {
                            //user doesn't exists
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Add Participant");
                            builder.setMessage("Add this user in this group?");
                            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    addParticipant(users);

                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                }
                            }).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void makeAdmin(Users users) {
        String timeStamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "admin");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //make admin
                Toast.makeText(context, "The user is now admin...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed making admin", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void addParticipant(Users users) {
        String timeStamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", users.getId());
        hashMap.put("role", "participant");
        hashMap.put("timestamp", timeStamp);
        //Add that user in Groups

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getId()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //added Successfully
                Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed adding user
                Toast.makeText(context, "Error adding participant in group", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void removeParticipant(Users users) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeAdmin(Users users) {
        String timeStamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "participant");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //make admin
                Toast.makeText(context, "The user is no longer admin...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed making admin", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkIfAlreadyExists(Users users, HolderParticipantAdd holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(users.getId()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String hisRole = "" + snapshot.child("role").getValue();
                            holder.status.setText(hisRole);

                        } else {
                            holder.status.setText("");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class HolderParticipantAdd extends RecyclerView.ViewHolder {
        private TextView username, status;

        public HolderParticipantAdd(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.userName);
            status = itemView.findViewById(R.id.status);
        }
    }
}
