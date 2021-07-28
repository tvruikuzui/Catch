package com.example.acatch.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.acatch.R;
import com.example.acatch.model.Model;
import com.example.acatch.model.User;


public class signup extends Fragment {

    View view;
    Button btnSignup;
    EditText etEmail, etPassword, ageEt, etPhone, etFullName;
    TextView validationTv,loginTv;
    ProgressBar loadingPb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Params
        view =  inflater.inflate(R.layout.fragment_signup, container, false);
        etEmail = view.findViewById(R.id.editTextTextEmailAddress);
        etPassword = view.findViewById(R.id.signup_password_et);
        ageEt = view.findViewById(R.id.signup_age_et);
        etPhone = view.findViewById(R.id.signup_phone_et);
        etFullName = view.findViewById(R.id.signup_fullName_et);
        btnSignup = view.findViewById(R.id.signup_signup_btn);
        loginTv = view.findViewById(R.id.signup_login_tv);
        validationTv = view.findViewById(R.id.signup_validation_tv);
        loadingPb =  view.findViewById(R.id.signup_progressBar);

        //Listeners
        loginTv.setOnClickListener(v-> Navigation.findNavController(v).navigateUp());
        btnSignup.setOnClickListener(v->save());

        return view;
    }

    private void save() {
        if(!etEmail.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty() ){

            loadingPb.setVisibility(View.VISIBLE);
            validationTv.setVisibility(View.INVISIBLE);
            btnSignup.setEnabled(false);
            User user = new User();
            user.setEmail(etEmail.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.setFullName(etFullName.getText().toString());
            user.setPhone(etPhone.getText().toString());
            user.setAge(ageEt.getText().toString());
            user.setDeleted(false);
            user.setImage("");

            Model.instance.saveUser(user,(isSuccess)->{

                if(isSuccess){
                    while(Navigation.findNavController(view).popBackStack()){}
                    Navigation.findNavController(view).navigate(R.id.nav_catchOrGiveRideFragment);
                }
                else{
                    validationTv.setVisibility(View.VISIBLE);
                    btnSignup.setEnabled(true);

                }
                loadingPb.setVisibility(View.INVISIBLE);

            });

        }
        else{
            validationTv.setVisibility(View.VISIBLE);
            btnSignup.setEnabled(true);
        }
    }


}