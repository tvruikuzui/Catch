package com.example.acatch.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RideDao {
    @Query("select * from Ride")
    LiveData<List<Ride>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Ride... rides);

    @Delete
    void delete(Ride ride);
}
