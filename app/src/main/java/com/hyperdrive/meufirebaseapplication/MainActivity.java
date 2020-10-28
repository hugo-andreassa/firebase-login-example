package com.hyperdrive.meufirebaseapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hyperdrive.meufirebaseapplication.models.LoginModel;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private TextInputEditText email;
    private TextInputEditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        progressBar = findViewById(R.id.progress_bar_login);
        progressBar.setVisibility(View.INVISIBLE);

        setupEditTexts();
        setupRegistrarUsuario();
        setupLoginButton();
    }

    private void setupEditTexts() {
        email = findViewById(R.id.usuario);
        senha = findViewById(R.id.senha);
    }

    private void setupLoginButton() {
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            LoginModel login = getValuesFromFields();
            if(login == null) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            mAuth.signInWithEmailAndPassword(login.getEmail(), login.getPassword())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("ACT", "Login realizado com sucesso");
                                FirebaseUser user = mAuth.getCurrentUser();
                                progressBar.setVisibility(View.INVISIBLE);
                                loginButton.setEnabled(true);
                                Toast.makeText(getApplicationContext(),
                                        "Login realizado com sucesso", Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("ACT", "signInWithEmail:failure", task.getException());
                                loginButton.setEnabled(true);
                                Toast.makeText(getApplicationContext(),
                                        "Erro no login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });


    }

    private LoginModel getValuesFromFields() {
        if(validateFields()) {
            LoginModel login = new LoginModel();
            login.setEmail(email.getText().toString());
            login.setPassword(senha.getText().toString());

            return login;
        }

        return null;
    }

    private boolean validateFields() {
        if(email.getText().toString().isEmpty() ||
                senha.getText().toString().isEmpty()) {
            String error = "Campo obrigatório";

            email.setError(error);
            senha.setError(error);

            return false;
        }
        return true;
    }

    private void setupRegistrarUsuario() {
        View view = findViewById(R.id.registrar_layout);
        view.setOnClickListener(v -> {
            RegistrarUsuarioFragment registrarUsuarioFragment = new RegistrarUsuarioFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.registrar_usuario_fragment, registrarUsuarioFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}