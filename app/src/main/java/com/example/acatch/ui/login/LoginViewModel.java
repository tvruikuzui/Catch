package com.example.acatch.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.acatch.model.Model;
import com.example.acatch.model.User;

import java.util.List;

public class LoginViewModel extends ViewModel {


    private LiveData<List<User>> usersList;

    public LoginViewModel() {
        usersList = Model.instance.getAllUsers();
    }

    public LiveData<List<User>> getData() {
        return usersList;
    }

}