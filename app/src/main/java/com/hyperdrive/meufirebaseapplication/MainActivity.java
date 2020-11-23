package com.hyperdrive.meufirebaseapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hyperdrive.meufirebaseapplication.models.UserModel;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser mCurrentUser;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        progressBar = findViewById(R.id.progress_main);

        setupUserInformationButton();
    }

    private void setupUserInformationButton() {
        Button userInformation = findViewById(R.id.user_information_button);
        userInformation.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            getUserInformationFromFirebase();
        });
    }

    private void getUserInformationFromFirebase() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserModel mUser = document.toObject(UserModel.class);

                            if(mCurrentUser.getUid().equals(mUser.getId())) {
                                Log.e("MAIN", document.getId());
                                callUserFragment(mUser, document.getId());
                            }
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Erro ao carregar Usuário",
                                Toast.LENGTH_LONG).show();

                        Log.w("MAIN_ACTIVITY", "Erro ao carregar Usuário", task.getException());
                    }
                });
    }

    private void callUserFragment(UserModel mUser, String id) {
        UserFragment userFragment = UserFragment.newInstance(mUser, id);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_fragment, userFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}