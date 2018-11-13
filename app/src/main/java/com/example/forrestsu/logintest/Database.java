package com.example.forrestsu.logintest;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Database extends BmobObject {

    private String databaseUrl;
    private String databaseName;

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
