package com.example.heartratealarm;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    // TODO: These may need to be async-ed
    @Query("SELECT * FROM alarms")
    List<Alarm> getAll();

    @Insert
    void insert(Alarm alarm);

    @Update
    void updateUser(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM alarms WHERE id like :id")
    List<Alarm> getAlarmByID(String id);
}
