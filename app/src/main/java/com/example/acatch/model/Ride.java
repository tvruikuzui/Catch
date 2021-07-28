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
public class Ride {
    @PrimaryKey
    @NonNull
    public String id;
    public String owner;
    public String driverName;
    public String customer;
    public String date;
    public String time;
    public String numSits;
    public String description;
    public String image;
    public double srcLatitude;
    public double srcLongitude;
    public double desLatitude;
    public double desLongitude;
    public Long lastUpdated;
    public boolean isDeleted;

    final static String ID = "id";
    final static String OWNER = "owner";
    final static String DRIVER_NAME = "driverName";
    final static String CUSTOMER = "customer";
    final static String DATE = "date";
    final static String TIME = "time";
    final static String NUM_SITS = "numSits";
    final static String DESCRIPTION = "description";
    final static String IMAGE = "image";
    final static String SRC_LATITUDE = "srcLatitude";
    final static String SRC_LONGITUDE = "srcLongitude";
    final static String DES_LATITUDE = "desLatitude";
    final static String DES_LONGITUDE = "desLongitude";
    final static String LAST_UPDATED = "lastUpdated";
    final static String IS_DELETED = "isDeleted";


    //Setters:
    public void setId(@NonNull String id) { this.id = id; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public void setCustomer(String customer) { this.customer = customer; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setNumSits(String numSits) { this.numSits = numSits; }
    public void setDescription(String description) { this.description = description; }
    public void setSrcLatitude(double srcLatitude) { this.srcLatitude = srcLatitude; }
    public void setSrcLongitude(double srcLongitude) { this.srcLongitude = srcLongitude; }
    public void setDesLatitude(double desLatitude) { this.desLatitude = desLatitude; }
    public void setDesLongitude(double desLongitude) { this.desLongitude = desLongitude; }
    public void setLastUpdated(Long lastUpdated) { this.lastUpdated = lastUpdated; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
    public void setImage(String image) { this.image = image; }



    //Getters:
    @NonNull
    public String getId() { return id; }
    public String getOwner() { return owner; }
    public String getDriverName() { return driverName; }
    public String getCustomer() { return customer; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getNumSits() { return numSits; }
    public String getDescription() { return description; }
    public double getSrcLatitude() { return srcLatitude; }
    public double getSrcLongitude() { return srcLongitude; }
    public double getDesLatitude() { return desLatitude; }
    public double getDesLongitude() { return desLongitude; }
    public Long getLastUpdated() { return lastUpdated; }
    public boolean isDeleted() { return isDeleted; }
    public String getImage() { return image; }



    public Map<String,Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(ID, id);
        json.put(OWNER, owner);
        json.put(DRIVER_NAME, driverName);
        json.put(CUSTOMER, customer);
        json.put(DATE, date);
        json.put(TIME, time);
        json.put(NUM_SITS, numSits);
        json.put(DESCRIPTION, description);
        json.put(IMAGE, image);
        json.put(SRC_LATITUDE, srcLatitude);
        json.put(SRC_LONGITUDE, srcLongitude);
        json.put(DES_LATITUDE, desLatitude);
        json.put(DES_LONGITUDE, desLongitude);
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        json.put(IS_DELETED,isDeleted);

        return json;
    }

    static public Ride create(Map<String,Object> json) {
        Ride user = new Ride();
        user.id = (String)json.get(ID);
        user.owner = (String)json.get(OWNER);
        user.driverName = (String)json.get(DRIVER_NAME);
        user.customer = (String)json.get(CUSTOMER);
        user.date = (String)json.get(DATE);
        user.time = (String)json.get(TIME);
        user.numSits = (String)json.get(NUM_SITS);
        user.description = (String)json.get(DESCRIPTION);
        user.image = (String)json.get(IMAGE);
        user.srcLatitude = (double)json.get(SRC_LATITUDE);
        user.srcLongitude = (double)json.get(SRC_LONGITUDE);
        user.desLatitude = (double)json.get(DES_LATITUDE);
        user.desLongitude = (double)json.get(DES_LONGITUDE);

        Timestamp ts = (Timestamp) json.get(LAST_UPDATED);
        user.isDeleted = (boolean)json.get(IS_DELETED);

        if(ts!=null)
            user.lastUpdated = new Long(ts.getSeconds());
        else
            user.lastUpdated = new Long(0);


        return user;
    }

    private static final String RIDE_LAST_UPDATE = "RideLastUpdate";

    static public void setLocalLastUpdateTime(Long ts){
        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(RIDE_LAST_UPDATE,ts);
        editor.commit();
    }

    static public Long getLocalLastUpdateTime(){
         return MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                 .getLong(RIDE_LAST_UPDATE,0);
    }

}
