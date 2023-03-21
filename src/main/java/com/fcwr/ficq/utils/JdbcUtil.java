package com.fcwr.ficq.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class JdbcUtil {
    private static DataSource ds;
    static {
        try {
            InputStream in = JdbcUtil.class.getClassLoader().getResourceAsStream("druid.properties");
            Properties prop=new Properties();
            prop.load(in);
            assert in != null;
            in.close();
            ds= DruidDataSourceFactory.createDataSource(prop);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static DataSource getDs(){
        return ds;
    }
}
