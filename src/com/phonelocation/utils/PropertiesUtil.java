package com.phonelocation.utils;

import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.phonelocation.model.Token;

/**
 * 读取或存储配置信息
 * 
 * @author sumy
 *
 */
public class PropertiesUtil {
    private static Properties urlProps;

    /**
     * 读取properties的信息
     */
    public static Properties getProperties(Context c) {
        Properties props = new Properties();
        try {
            InputStream inputStream = c.getAssets()
                    .open("appConfig.properties");
            props.load(inputStream);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        urlProps = props;
        return urlProps;
    }

    /**
     * 存储Token信息
     */
    public static Token savaToken(Context context, Token token) {
        // 获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("LocationToken",
                Context.MODE_PRIVATE);
        // 存入数据
        Editor editor = sp.edit();
        editor.putString("TOKEN_OWNER", token.getOwner());
        editor.putString("TOKEN_ID", token.getTokenid());
        editor.putLong("TOKEN_DEADLINE", token.getDeadline());
        editor.commit();
        return token;
    }

    /**
     * 去除Token信息
     */
    public static Token getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("LocationToken",
                Context.MODE_PRIVATE);

        String owner = sp.getString("TOKEN_OWNER", "");
        String id = sp.getString("TOKEN_ID", "");
        long deadline = sp.getLong("TOKEN_DEADLINE", -1);
        if (deadline > 0) {
            return new Token(owner, id, deadline);
        }
        return null;
    }

    /**
     * 获取TokenID
     */
    public static String getTokenId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("LocationToken",
                Context.MODE_PRIVATE);

        return sp.getString("TOKEN_ID", "");
    }
}