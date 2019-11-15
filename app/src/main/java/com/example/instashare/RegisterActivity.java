package com.example.instashare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, full_name,  email, password;
    Button btn_register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        full_name = findViewById(R.id.full_name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        txt_login = findViewById(R.id.txt_login);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Please wait..");
                progressDialog.show();

                String username_str = username.getText().toString();
                String full_name_str = full_name.getText().toString();
                String email_str = email.getText().toString();
                String password_str = password.getText().toString();

                if (    TextUtils.isEmpty(username_str) ||
                        TextUtils.isEmpty(full_name_str) ||
                        TextUtils.isEmpty(email_str) ||
                        TextUtils.isEmpty(password_str) ) {
                    progressDialog.dismiss();

                    Toast.makeText(RegisterActivity.this, "All fields are Required to Fill...", Toast.LENGTH_SHORT).show();
                } else if (password_str.length() <6) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters or more", Toast.LENGTH_SHORT).show();
                } else  {
                    register(username_str, full_name_str, email_str, password_str);
                }

            }
        });
    }

    private void register(final String username, final String full_name, String email, String password) {
        Log.i("REGISTER", "GOT Called");
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.i("SignUp", "GOT Completed");
                    FirebaseUser fCurrentUser = auth.getCurrentUser();
                    String uid = fCurrentUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    Log.i("Reference", "GOT REFERENCE");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", uid);
                    hashMap.put("username", username.toLowerCase());
                    hashMap.put("fullname", full_name);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "default");
//                    hashMap.put("status", "offline");
//                    hashMap.put("search", username.toLowerCase());


                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Log.i("Database", "Database VALUE is SET");
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Something went wrong,\nPlz check your Internet connection and Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
