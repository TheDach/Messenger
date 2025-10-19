package com.thedach.messenger_20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextYearsOld;
    private Button buttonSingUn;

    private RegistrationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        observeViewModel();
        setupClickListeners();
    }


    private void observeViewModel() {
        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser user) {
                // Да я осознаю что в данной реализации нет смысла проверять на null, так как с viewMoel ничего другого прилететь не может
                if (user != null) {
                    startActivity(UsersActivity.newIntent(RegistrationActivity.this, user.getUid()));
                    finish();
                }
            }
        });
    }
    private void setupClickListeners() {
        buttonSingUn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getTrimmedValue(editTextEmail);
                String password = getTrimmedValue(editTextPassword);
                String name = getTrimmedValue(editTextName);
                String lastname = getTrimmedValue(editTextLastName);
                String age = getTrimmedValue(editTextYearsOld);

                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || lastname.isEmpty() || age.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Enter all parameters", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.singUp(email, password, name, lastname, Integer.parseInt(age));

                }
            }
        });
    }
    private void initView() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextYearsOld = findViewById(R.id.editTextYearsOld);
        buttonSingUn = findViewById(R.id.buttonSingUp);
    }

    private String getTrimmedValue(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }
}