package com.zio.docsguard.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DocsEntity.class}, version = 1)
public abstract class DocsDB extends RoomDatabase {
    public abstract DocsDAO docsdao();
}
