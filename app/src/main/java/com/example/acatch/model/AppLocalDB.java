package com.example.acatch.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.acatch.MyApplication;


@Database(entities = {User.class,Ride.class}, version = 2)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract RideDao rideDao();

}

public class AppLocalDB{
    public final static  AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.context,
                    AppLocalDbRepository.class,
                    "dbCatch.db")
                    .fallbackToDestructiveMigration()
                    .build();
}

