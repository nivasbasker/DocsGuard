package com.zio.docsguard.DataBase;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DOCS")
public class DocsEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public String Name;

    @ColumnInfo(name = "uri")
    public String Uri;

    @NonNull
    public String getName() {
        return Name;
    }

    public void setName(@NonNull String name) {
        Name = name;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public DocsEntity() {
        //Name = name;
        //Uri = uri;
    }
}