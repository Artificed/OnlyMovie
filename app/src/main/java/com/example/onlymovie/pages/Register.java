        package com.example.onlymovie.pages;

        import android.content.Intent;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import com.example.onlymovie.R;
        import com.example.onlymovie.models.User;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.android.material.textfield.TextInputEditText;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.FirebaseFirestore;

        public class Register extends AppCompatActivity {

            TextInputEditText editTextUsername, editTextFullName, editTextEmail, editTextPassword;
            Button buttonReg;
            FirebaseAuth mAuth;
            FirebaseFirestore db;
            ProgressBar progressBar;
            TextView textView;

            @Override
            public void onStart() {
                super.onStart();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){

                }
            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_register);
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                editTextFullName = findViewById(R.id.fullName);
                editTextUsername = findViewById(R.id.username);
                editTextEmail = findViewById(R.id.email);
                editTextPassword = findViewById(R.id.password);
                buttonReg = findViewById(R.id.btnRegister);
                progressBar = findViewById(R.id.progressBar);
                textView = findViewById(R.id.loginNow);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    }
                });

                buttonReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        String fullName, email, password, username;
                        username = String.valueOf(editTextUsername.getText());
                        fullName = String.valueOf(editTextFullName.getText());
                        email = String.valueOf(editTextEmail.getText());
                        password = String.valueOf(editTextPassword.getText());

                        if(TextUtils.isEmpty(email)) {
                            Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(TextUtils.isEmpty(password)) {
                            Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(TextUtils.isEmpty(username)) {
                            Toast.makeText(Register.this, "Enter username", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(TextUtils.isEmpty(fullName)) {
                            Toast.makeText(Register.this, "Enter fullname", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            String userId = mAuth.getCurrentUser().getUid();

                                            User user = new User(fullName, email, username);

                                            db.collection("users").document(userId)
                                                            .set(user)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(getApplicationContext(), Login.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Register.this, "Failed to save user data.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(Register.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
            }
        }