package com.example.smarttravelguide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    EditText mEmail,mPassword;
    TextView tLogin,tRegister,tForget;
    FirebaseAuth fAuth;
    private SharedPreferences file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail=findViewById(R.id.Email);
        mPassword=findViewById(R.id.password);
        fAuth=FirebaseAuth.getInstance();
        tLogin=findViewById(R.id.login);
        tRegister=findViewById(R.id.register);
        tForget=findViewById(R.id.forget);

        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        tLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email=mEmail.getText().toString();
                String password=mPassword.getText().toString();

                if (TextUtils.isEmpty(email))
                {
                    mEmail.setError(" Email is Required !!! ");
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    mPassword.setError(" Password is Required !!! ");
                    return;
                }
                if (password.length()<6)
                {
                    mPassword.setError(" Password Must be >=6 Characters ");
                }
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            file.edit().putString("emailid",mEmail.getText().toString()).apply();
                            Toast.makeText(LoginActivity.this," Logged in Successfully ",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MapActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this," Error Occurred "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MapActivity.class);
            startActivity(intent);
            finish();
        }
        else {

        }
        tRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterUser.class));
            }
        });

        tForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ForgetActivity.class));
            }
        });
    }

}