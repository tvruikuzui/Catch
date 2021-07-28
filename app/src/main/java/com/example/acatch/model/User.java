package com.example.acatch.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.acatch.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;


@Entity
public class User{
    @PrimaryKey
    @NonNull
    public String id;
    public String fullName;
    public String email;
    public String password;
    public String phone;
    public String age;
    public String image;
    public Long lastUpdated;
    public boolean isDeleted;

    final static String ID = "id";
    final static String FULL_NAME = "fullName";
    final static String EMAIL = "email";
    final static String PASSWORD = "password";
    final static String PHONE = "phone";
    final static String AGE = "age";
    final static String IMAGE = "image";
    final static String LAST_UPDATED = "lastUpdated";
    final static String IS_DELETED = "isDeleted";


    //Setters:
    public void setId(String id) {
        this.id = id;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public void setDeleted(boolean isDeleted) {
        isDeleted = isDeleted;
    }
    public void setAge(String age) { this.age = age; }

    //Getters:
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPassword() { return password; }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getImage() {
        return image;
    }
    public String getAge() { return age; }
    public Long getLastUpdated() {
        return lastUpdated;
    }
    public boolean isDeleted() {
        return isDeleted;
    }


    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(ID, id);
        json.put(FULL_NAME, fullName);
        json.put(EMAIL, email);
        json.put(PASSWORD, password);
        json.put(PHONE, phone);
        json.put(IMAGE, id);
        json.put(AGE, age);
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        json.put(IS_DELETED,isDeleted);

        return json;
    }

    static public User create(Map<String,Object> json) {
        User user = new User();
        user.id = (String)json.get(ID);
        user.fullName = (String)json.get(FULL_NAME);
        user.email = (String)json.get(EMAIL);
        user.password = (String)json.get(PASSWORD);
        user.phone = (String)json.get(PHONE);
        user.image = (String)json.get(IMAGE);
        user.age = (String)json.get(AGE);
        user.isDeleted = (boolean)json.get(IS_DELETED);
        Timestamp ts = (Timestamp) json.get(LAST_UPDATED);

        if(ts!=null)
            user.lastUpdated = new Long(ts.getSeconds());
        else
            user.lastUpdated = new Long(0);


        return user;
    }

    private static final String USER_LAST_UPDATE = "UserLastUpdate";

    static public void setLocalLastUpdateTime(Long ts){
        //Shared preference, saving the ts on the disk (like the db):
        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(USER_LAST_UPDATE,ts);
        editor.commit();
    }

    static public Long getLocalLastUpdateTime(){
        //Shared preference, saving the ts in app:
         return MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                 .getLong(USER_LAST_UPDATE,0);
    }

}
