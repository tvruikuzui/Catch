package com.example.acatch;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signup extends Fragment {

    Button btnSignup;
    EditText etEmail;
    EditText etPassword;
    EditText etAge;
    EditText etPhone;
    EditText etFullName;
    ProgressBar pb;
    FirebaseFirestore firestore;
    String userID;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public signup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment signup.
     */
    // TODO: Rename and change types and number of parameters
    public static signup newInstance(String param1, String param2) {
        signup fragment = new signup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_signup, container, false);

        firestore = FirebaseFirestore.getInstance();

        etEmail = view.findViewById(R.id.editTextTextEmailAddress);
        etPassword = view.findViewById(R.id.editTextTextPassword);
        etAge = view.findViewById(R.id.editTextNumber);
        etPhone = view.findViewById(R.id.editTextPhone);
        etFullName = view.findViewById(R.id.editTextTextPersonName);
        btnSignup = view.findViewById(R.id.btnSignupPage);
        pb = view.findViewById(R.id.progressBar2);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                //TODO:validate make the app crash
                //if (!etEmail.toString().isEmpty() && !etPassword.toString().isEmpty()){
                if (validateEmail(etEmail) && validateName(etFullName) && validatePassword(etPassword) && validatePhone(etPhone)){
                    firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                                DocumentReference reference = firestore.collection("users").document(userID);
                                Map<String,Object> user = new HashMap<>();
                                user.put("fName", etFullName.getText().toString());
                                user.put("email", etEmail.getText().toString().trim());
                                user.put("phone", etPhone.getText().toString());
                                user.put("age", etAge.getText().toString());
                                reference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Sign Up Successfully!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getContext(), CatchMap.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });
                            }else {
                                Toast.makeText(getContext(), "Sign Up Not Successfully!", Toast.LENGTH_LONG).show();
                                Log.d("signup", task.getResult().toString());
                                pb.setVisibility(View.GONE);
                            }
                        }
                    });
                }


            }
        });

        return view;
    }
    private boolean validateEmail(EditText etEmail) {
        String st = etEmail.getText().toString().trim();
        return Patterns.EMAIL_ADDRESS.matcher(st).matches();
    }

    private boolean validatePassword(EditText etPassword) {
        String st = etPassword.getText().toString().trim();
        return st.length() > 6;

    }

    private boolean validatePhone(EditText etPhone) {
        String st = etPhone.getText().toString();
        return Patterns.PHONE.matcher(st).matches();
    }
    private boolean validateName(EditText etName) {
        String st = etName.getText().toString();
        return st.length() > 4;
    }

//    private boolean validateAge(EditText etage) {
//
//        int st = Integer.valueOf(etage.toString());
//        return st > 0 && st < 120;
//    }
}