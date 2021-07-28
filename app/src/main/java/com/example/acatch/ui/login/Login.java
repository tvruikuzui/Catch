package com.example.acatch.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.acatch.R;
import com.example.acatch.model.Model;


public class Login extends Fragment {


    View view;
    Button loginBtn;
    EditText EmailEt, PasswordEt;
    TextView signUpTv;
    LoginViewModel loginViewModel;
    TextView validationTv;
    ProgressBar loadingPb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn = view.findViewById(R.id.login_login_btn);
        EmailEt = view.findViewById(R.id.login_email_et);
        PasswordEt = view.findViewById(R.id.login_password_et);
        signUpTv = view.findViewById(R.id.login_signup_tv);
        validationTv = view.findViewById(R.id.login_valitaion_tv);
        loadingPb =  view.findViewById(R.id.login_progressBar);


        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getData().observe(getViewLifecycleOwner(), (data)->{});


        signUpTv.setOnClickListener(v-> Navigation.findNavController(v).navigate(R.id.nav_signup));
        loginBtn.setOnClickListener(v->login());

        return view;
    }



    private void login() {
        if (!EmailEt.getText().toString().isEmpty() && !PasswordEt.getText().toString().isEmpty()) {
            loadingPb.setVisibility(View.VISIBLE);
            validationTv.setVisibility(View.INVISIBLE);
            loginBtn.setEnabled(false);
            Model.instance.login(EmailEt.getText().toString(), PasswordEt.getText().toString(), (isSuccess) -> {
                if(isSuccess){
                    while(Navigation.findNavController(view).popBackStack()){}
                    Navigation.findNavController(view).navigate(R.id.nav_catchOrGiveRideFragment);
                }
                else{
                    validationTv.setVisibility(View.VISIBLE);
                    loginBtn.setEnabled(true);

                }
                loadingPb.setVisibility(View.INVISIBLE);
            });
        }
    }
}