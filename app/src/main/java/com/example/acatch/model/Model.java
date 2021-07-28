package com.example.acatch.model;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model {

    final public static Model instance = new Model();
    ExecutorService executorService = Executors.newCachedThreadPool();

    public interface OnCompleteListener {
        void onComplete(boolean isSuccess);
    }

    public enum LoadingState {loaded, loading,}
    public MutableLiveData<LoadingState> loadingState = new MutableLiveData<LoadingState>(LoadingState.loaded);

    User user;
    private Model() {}
    public User getUser() {
        return user;
    }
    public void setUser(User user  ) { this.user = user;}

    //---------------------------------------User---------------------------------------------

    LiveData<List<User>> allUsers = AppLocalDB.db.userDao().getAll();

    public LiveData<List<User>> getAllUsers() {
        loadingState.setValue(LoadingState.loading);

        Long localLastUpdate = User.getLocalLastUpdateTime();
        ModelFirebase.getAllUsers(localLastUpdate,(users)->{
            executorService.execute(()->
            {
                Long lastUpdate = new Long(0);

                for (User user: users)
                {
                    if(user.isDeleted())
                        AppLocalDB.db.userDao().delete(user);

                    else
                        AppLocalDB.db.userDao().insertAll(user);

                    if(lastUpdate < user.lastUpdated)
                        lastUpdate = user.lastUpdated;
                }
                User.setLocalLastUpdateTime(lastUpdate);

                loadingState.postValue(LoadingState.loaded);
            });
        });

        return allUsers;
    }

    public void saveUser(User user, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveUser(user, (isSuccess) -> {
            getAllUsers();
            listener.onComplete(isSuccess);
        });
    }

    public void login(String email, String password, OnCompleteListener listener) {
        ModelFirebase.login(email, password, (isSuccess) -> listener.onComplete(isSuccess));
    }

    public void isLoggedIn(OnCompleteListener listener) {
        ModelFirebase.isLoggedIn((isSuccess) ->{
            loadingState.setValue(LoadingState.loaded);
            listener.onComplete(isSuccess);
        });
    }

    public static void signOut() {
        ModelFirebase.signOut();
    }



    //---------------------------------------User---------------------------------------------

    LiveData<List<Ride>> allRides = AppLocalDB.db.rideDao().getAll();

    public LiveData<List<Ride>> getAllRides() {
        loadingState.setValue(LoadingState.loading);

        Long localLastUpdate = Ride.getLocalLastUpdateTime();
        ModelFirebase.getAllRides(localLastUpdate,(rides)->{
            executorService.execute(()->
            {
                Long lastUpdate = new Long(0);

                for (Ride ride: rides)
                {
                    if(ride.isDeleted)
                        AppLocalDB.db.rideDao().delete(ride);
                    else
                        AppLocalDB.db.rideDao().insertAll(ride);
                    if(lastUpdate < ride.lastUpdated)
                        lastUpdate = ride.lastUpdated;
                }
                Ride.setLocalLastUpdateTime(lastUpdate);

                loadingState.postValue(LoadingState.loaded);
            });
        });

        return allRides;
    }

    public void updateRide(Ride ride, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.updateRide(ride, (isSuccess) -> {
            getAllRides();
            listener.onComplete(isSuccess);
        });
    }

    public void saveRide(Ride ride, OnCompleteListener listener) {
        loadingState.setValue(LoadingState.loading);
        ModelFirebase.saveRide(ride, (isSuccess) -> {
            getAllRides();
            listener.onComplete(isSuccess);
        });
    }
    //----------------------------Save Images In Firebase----------------------------------
    public interface  UpLoadImageListener{
        void onComplete(String url);
    }

    public static void uploadImage(Bitmap imageBmp, String name, final UpLoadImageListener listener) {
        ModelFirebase.uploadImage(imageBmp,name,listener);
    }

}
