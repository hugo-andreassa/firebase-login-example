package com.hyperdrive.meufirebaseapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hyperdrive.meufirebaseapplication.models.LoginModel;

import java.util.concurrent.Executor;

public class RegistrarUsuarioFragment extends Fragment {

    private FirebaseAuth mAuth;

    private ProgressBar progressBar;
    private TextInputEditText email;
    private TextInputEditText telefone;
    private TextInputEditText senha;

    public RegistrarUsuarioFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registrar_usuario, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        setupEditTexts(view);
        setupRegistrarButton(view);

        return view;
    }

    private void setupEditTexts(View view) {
        email = view.findViewById(R.id.new_usuario);
        telefone = view.findViewById(R.id.new_telefone);
        senha = view.findViewById(R.id.new_senha);
    }

    private LoginModel getValuesFromFields() {

        if(validateFields()) {
            LoginModel login = new LoginModel();
            login.setEmail(email.getText().toString());
            login.setTelefone(telefone.getText().toString());
            login.setPassword(senha.getText().toString());

            return login;
        }

        return null;
    }

    private boolean validateFields() {

        if(email.getText().toString().isEmpty() ||
            telefone.getText().toString().isEmpty() ||
                senha.getText().toString().isEmpty()) {
            String error = "Campo obrigatório";

            email.setError(error);
            telefone.setError(error);
            senha.setError(error);

            return false;
        }

        return true;
    }

    private void setupRegistrarButton(View view) {
        Button registrar = view.findViewById(R.id.cadastrar_button);

        registrar.setOnClickListener(v -> {
            LoginModel login = getValuesFromFields();
            if(login == null) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            registrar.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(login.getEmail(), login.getPassword())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FRAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressBar.setVisibility(View.INVISIBLE);
                            registrar.setEnabled(true);
                            Toast.makeText(getContext(), "Cadastro realizado com sucesso",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FRAG", "createUserWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                            registrar.setEnabled(true);
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
