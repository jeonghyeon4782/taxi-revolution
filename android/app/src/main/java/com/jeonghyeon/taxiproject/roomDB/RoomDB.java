package com.jeonghyeon.taxiproject.roomDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jeonghyeon.taxiproject.roomDB.dao.FrequencyDao;
import com.jeonghyeon.taxiproject.roomDB.dao.GuardianDao;
import com.jeonghyeon.taxiproject.roomDB.dao.RecordDao;
import com.jeonghyeon.taxiproject.domain.Frequency;
import com.jeonghyeon.taxiproject.domain.Guardian;
import com.jeonghyeon.taxiproject.domain.Record;

// 만약 엔티티가 두개라면?
// ex) @Database(entities = {Order.class, Product.class}, version = 1, exportSchema = false)
@Database(entities = {Guardian.class, Frequency.class, Record.class}, version = 5, exportSchema = false)
public abstract class RoomDB extends RoomDatabase
{
    private static RoomDB database;

    private static String DATABASE_NAME = "my_taxi";

    public synchronized static RoomDB getInstance(Context context)
    {
        if (database == null)
        {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME) // 객체 생성 빌더 반환
                    .allowMainThreadQueries() // Main 쓰레드에서 DB에 IO 가능하게 함
                    .fallbackToDestructiveMigration() // 스키마 버전 변경 가능
                    .build(); // 객체 생성 후 반환
        }
        return database;
    }

    public abstract FrequencyDao frequencyDao();
    public abstract GuardianDao guardianDao();
    public abstract RecordDao recordDao();

}
