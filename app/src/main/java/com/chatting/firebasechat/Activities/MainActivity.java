package com.chatting.firebasechat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chatting.firebasechat.Models.Users;
import com.chatting.firebasechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText number, password, name;
    private Button signIN;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        number = findViewById(R.id.email);
      //  toolbar = findViewById(R.id.toolbar);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        signIN = findViewById(R.id.signUp);
       /* toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, MainDashboard.class);
            startActivity(intent);
        }


        signIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.createUserWithEmailAndPassword(number.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String userId = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("id", userId);
                            userMap.put("username", name.getText().toString());
                            userMap.put("mail", number.getText().toString());
                            userMap.put("password", password.getText().toString());

                            reference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(MainActivity.this, MainDashboard.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                          /*  Users users = new Users(name.getText().toString(), number.getText().toString(), password.getText().toString());
                            String uId = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(uId).setValue(users);*/
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }
}