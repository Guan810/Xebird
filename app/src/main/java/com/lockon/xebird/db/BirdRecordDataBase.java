package com.lockon.xebird.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lockon.xebird.db.Daos.BirdRecordDao;
import com.lockon.xebird.db.Entities.BirdRecord;
import com.lockon.xebird.db.Entities.Checklist;

@Database(entities = {BirdRecord.class, Checklist.class}, version = 1)
public abstract class BirdRecordDataBase extends RoomDatabase {

    public abstract BirdRecordDao myDao();

    private final static String DBNAME = "Record.db";
    private volatile static BirdRecordDataBase BirdRecordDataBase;

    public static synchronized BirdRecordDataBase getInstance(Context context) {
        if (BirdRecordDataBase == null) {
            return create(context);
        } else {
            return BirdRecordDataBase;
        }
    }

    private static BirdRecordDataBase create(Context context) {
        return Room.databaseBuilder(context, BirdRecordDataBase.class, DBNAME)
                .allowMainThreadQueries()
                .build();
    }
}
