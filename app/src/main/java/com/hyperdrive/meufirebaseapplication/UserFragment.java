package com.hyperdrive.meufirebaseapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperdrive.meufirebaseapplication.models.LoginModel;
import com.hyperdrive.meufirebaseapplication.models.UserModel;

import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {

    private static final String ARG_PARAM1 = "user";
    private static final String ARG_PARAM2 = "id";
    private UserModel mUser;
    private String mDocumentId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

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

    public UserFragment() {

    }

    public static UserFragment newInstance(UserModel mUser, String id) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, mUser);
        args.putString(ARG_PARAM2, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (UserModel) getArguments().getSerializable(ARG_PARAM1);
            mDocumentId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao carregar a localização",Toast.LENGTH_LONG);
        }

        progressBar = view.findViewById(R.id.progress_bar);

        setupEditTexts(view);
        setupIncrementButtons(view);
        if (mUser != null) {
            loadFieldsInformation(view);
            setupUpdateButton(view);
            setupDeleteButton(view);
        } else {
            setupRegistrarButton(view);
        }

        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if(mFusedLocationClient == null) {
            Toast.makeText(getContext(), "Erro ao obter localização", Toast.LENGTH_LONG);
            geographicPoint = "";
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastLocation = task.getResult();
                        geographicPoint = mLastLocation.getLatitude() + " " + mLastLocation.getLongitude();
                    } else {
                        geographicPoint = "";
                        Toast.makeText(getContext(), "Erro ao obter localização", Toast.LENGTH_LONG);
                    }
                });
    }

    private void loadFieldsInformation(View view) {
        email.setText(mUser.getEmail());
        phone.setText(mUser.getPhone());

        TextInputLayout emailLayout = view.findViewById(R.id.new_usuario_layout);
        emailLayout.setVisibility(View.GONE);

        TextInputLayout passwordLayout = view.findViewById(R.id.new_senha_layout);
        passwordLayout.setVisibility(View.GONE);

        name.setText(mUser.getName());
        street.setText(mUser.getStreet());
        city.setText(mUser.getCity());
        number.setText(mUser.getNumber());
        comp.setText(mUser.getComp());
        cep.setText(mUser.getCep());
        geographicPoint = mUser.getGeographicPoint();

        birthdayMonth = Integer.parseInt(mUser.getBirthdayMonth());
        displayBirthdayMonth(birthdayMonth, view);
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
        getLastLocation();

        if(validateFields()) {
            UserModel user = new UserModel();
            user.setEmail(email.getText().toString());
            user.setPhone(phone.getText().toString());
            user.setPassword(password.getText().toString());
            user.setName(name.getText().toString());
            user.setBirthdayMonth(birthdayMonth.toString());
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

    private UserModel getValuesFromFieldsToUpdate() {
        getLastLocation();

        if(validateFieldsUpdate()) {
            UserModel user = new UserModel();
            user.setEmail(email.getText().toString());
            user.setPhone(phone.getText().toString());
            user.setPassword(password.getText().toString());
            user.setName(name.getText().toString());
            user.setBirthdayMonth(birthdayMonth.toString());
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

    private boolean validateFieldsUpdate() {
        if(name.getText().toString().isEmpty() ||
                phone.getText().toString().isEmpty()) {
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
        registrar.setVisibility(View.VISIBLE);

        registrar.setOnClickListener(v -> {
            UserModel user = getValuesFromFields();
            if (user == null) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            registrar.setEnabled(false);

            setUserLoginWithEmail(user, registrar);
        });
    }

    private void setupUpdateButton(View view) {
        Button updateButton = view.findViewById(R.id.cadastrar_button);
        updateButton.setText("Atualizar");
        updateButton.setVisibility(View.VISIBLE);

        updateButton.setOnClickListener(v -> {
            UserModel user = getValuesFromFieldsToUpdate();
            if (user == null) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            updateUserInFireCloud(user);
        });
    }

    private void updateUserInFireCloud(UserModel user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("phone", user.getPhone());
        userMap.put("birthdayMonth", user.getBirthdayMonth());
        userMap.put("cep", user.getCep());
        userMap.put("city", user.getCity());
        userMap.put("comp", user.getComp());
        userMap.put("street", user.getStreet());
        userMap.put("number", user.getNumber());
        userMap.put("geographicPoint", user.getGeographicPoint());

        db.collection("users")
                .document(mDocumentId)
                .update(userMap)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Dados atualizados com sucesso",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erro ao atualizar os dados",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setupDeleteButton(View view) {
        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.VISIBLE);

        deleteButton.setOnClickListener(v -> {
            deleteUserFromAuthentication();
        });
    }

    private void setUserLoginWithEmail(UserModel user, Button registrar){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser userFire = mAuth.getCurrentUser();
                        user.setId(userFire.getUid());

                        setUserToFireCloud(user, registrar);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        registrar.setEnabled(true);

                        Toast.makeText(getContext(), task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUserToFireCloud(UserModel user, Button registrar) {
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

    private void deleteUserFromAuthentication() {
        mAuth.getCurrentUser()
                .delete()
                .addOnCompleteListener(documentReference -> {
                    deleteUserFromFireCloud();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erro ao deletar os dados",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteUserFromFireCloud() {
        db.collection("users")
                .document(mDocumentId)
                .delete()
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    callLoginActivity();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Erro ao deletar os dados",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void callLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
