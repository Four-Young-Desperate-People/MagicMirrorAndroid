package com.example.heartratealarm.alarm;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.heartratealarm.alarm.Alarm;

import java.util.List;

@Dao
public interface AlarmDao {
    // TODO: sort by enabled, then next_run
    @Query("SELECT * FROM alarms ORDER BY next_run")
    List<Alarm> getAll();

    @Insert
    void insert(Alarm alarm);

    @Update
    void update(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM alarms WHERE id LIKE :id")
    List<Alarm> getAlarmByID(int id);

    @Query("SELECT MAX(id) FROM alarms")
    int getMaxAlarm();
}
