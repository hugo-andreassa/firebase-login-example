package com.hyperdrive.meufirebaseapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperdrive.meufirebaseapplication.models.UserModel;

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuarioFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText phone;
    private TextInputEditText password;
    private Integer birthdayMonth = 1;
    private TextInputEditText street;
    private TextInputEditText city;
    private TextInputEditText number;
    private TextInputEditText comp;
    private TextInputEditText cep;
    private String geographicPoint;

    private ProgressBar progressBar;

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
        db = FirebaseFirestore.getInstance();

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        setupEditTexts(view);
        setupRegistrarButton(view);
        setupIncrementButtons(view);

        return view;
    }

    private void setupEditTexts(View view) {
        email = view.findViewById(R.id.new_usuario);
        phone = view.findViewById(R.id.new_telefone);
        password = view.findViewById(R.id.new_senha);
        name = view.findViewById(R.id.new_name);
        street = view.findViewById(R.id.new_street);
        city = view.findViewById(R.id.new_city);
        number = view.findViewById(R.id.new_number);
        comp = view.findViewById(R.id.new_comp);
        cep = view.findViewById(R.id.new_cep);
        geographicPoint = "";
    }

    private void setupIncrementButtons(View v) {
        Button incrementStock = v.findViewById(R.id.new_more_button);
        Button decrementStock = v.findViewById(R.id.new_less_button);

        displayBirthdayMonth(birthdayMonth, v);

        incrementStock.setOnClickListener((view) -> {
            if(birthdayMonth < 12) {
                birthdayMonth = birthdayMonth + 1;
                displayBirthdayMonth(birthdayMonth, v);
            }
        });

        decrementStock.setOnClickListener((view) -> {
            if(birthdayMonth > 1) {
                birthdayMonth = birthdayMonth - 1;
                displayBirthdayMonth(birthdayMonth, v);
            }
        });
    }

    private void displayBirthdayMonth(int number, View view) {
        TextView birthdayMonth = view.findViewById(R.id.new_birthday_month);
        birthdayMonth.setText(String.valueOf(number));
    }

    private UserModel getValuesFromFields() {

        if(validateFields()) {
            UserModel user = new UserModel();
            user.setEmail(email.getText().toString());
            user.setPhone(phone.getText().toString());
            user.setPassword(password.getText().toString());
            user.setName(name.getText().toString());
            user.setBirthdayMonth(birthdayMonth);
            user.setStreet(street.getText().toString());
            user.setCity(city.getText().toString());
            user.setNumber(number.getText().toString());
            user.setComp(comp.getText().toString());
            user.setCep(cep.getText().toString());
            user.setGeographicPoint(geographicPoint);

            return user;
        }

        return null;
    }

    private boolean validateFields() {

        if(email.getText().toString().isEmpty() ||
            name.getText().toString().isEmpty() ||
                phone.getText().toString().isEmpty() ||
                    password.getText().toString().isEmpty()) {
            String error = "Campo obrigatório";

            email.setError(error);
            name.setError(error);
            phone.setError(error);
            password.setError(error);

            return false;
        }

        return true;
    }

    private void setupRegistrarButton(View view) {
        Button registrar = view.findViewById(R.id.cadastrar_button);

        registrar.setOnClickListener(v -> {
            UserModel user = getValuesFromFields();
            if (user == null) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            registrar.setEnabled(false);

            setUserLoginWithEmail(user, registrar, progressBar);
        });
    }

    private void setUserLoginWithEmail(UserModel user, Button registrar, ProgressBar progressBar){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser userFire = mAuth.getCurrentUser();
                        user.setId(userFire.getUid());

                        setUserToFireCloud(user, registrar, progressBar);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        registrar.setEnabled(true);

                        Toast.makeText(getContext(), task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUserToFireCloud(UserModel user, Button registrar, ProgressBar progressBar) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("password", user.getPassword());
        userMap.put("phone", user.getPhone());
        userMap.put("birthdayMonth", user.getBirthdayMonth());
        userMap.put("cep", user.getCep());
        userMap.put("city", user.getCity());
        userMap.put("comp", user.getComp());
        userMap.put("street", user.getStreet());
        userMap.put("number", user.getNumber());
        userMap.put("geographicPoint", user.getGeographicPoint());

        db.collection("users")
                .add(userMap)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    registrar.setEnabled(true);

                    Toast.makeText(getContext(), "Cadastro realizado com sucesso",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    registrar.setEnabled(true);

                    Log.e("NEW_USER", e.getMessage());
                    Toast.makeText(getContext(), "Erro ao inserir os dados",
                            Toast.LENGTH_SHORT).show();
                });
    }
}
