package com.phonelocation;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.phonelocation.model.Token;
import com.phonelocation.utils.MD5;
import com.phonelocation.utils.PhoneStateUtil;
import com.phonelocation.utils.PropertiesUtil;

public class AuthActivity extends Activity implements OnClickListener {

    private EditText et_username;
    private EditText et_password;
    private Button btn_ok;

    private ProgressDialog dialog;

    private String authUrl;

    // 接收线程传回的消息响应认证结果
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            dialog.dismiss();
            if (msg.what == 0) {
                Toast.makeText(AuthActivity.this, "认证成功", Toast.LENGTH_LONG)
                        .show();
                AuthActivity.this.finish();
            } else {
                Toast.makeText(AuthActivity.this, "认证失败", Toast.LENGTH_LONG)
                        .show();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // 获取配置文件中的认证目录
        authUrl = PropertiesUtil.getProperties(this).getProperty("authUrl");

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        btn_ok = (Button) findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dialog = ProgressDialog.show(this, "请稍后", "正在认证，请稍后...");

        // 在线程中发送HTTP请求
        new Thread() {
            public void run() {
                HttpPost httpPost = new HttpPost(authUrl);
                // 设置HTTP POST请求参数必须用NameValuePair对象
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", et_username
                        .getText().toString()));
                params.add(new BasicNameValuePair("password", et_password
                        .getText().toString()));
                params.add(new BasicNameValuePair("name", MD5
                        .string2MD5(PhoneStateUtil
                                .getPhoneID(AuthActivity.this))));
                HttpResponse httpResponse = null;

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params,
                            HTTP.UTF_8));
                    httpResponse = new DefaultHttpClient().execute(httpPost);

                    int statusCode = httpResponse.getStatusLine()
                            .getStatusCode();
                    if (statusCode == 200) {
                        // 认证成功，解析服务器响应的JSON信息
                        String result = EntityUtils.toString(httpResponse
                                .getEntity());
                        JSONObject jsontoken = new JSONObject(result);
                        Token token = new Token();
                        token.setOwner(jsontoken.getString("owner"));
                        token.setTokenid(jsontoken.getString("tokenid"));
                        token.setDeadline(jsontoken.getLong("deadline"));
                        PropertiesUtil.savaToken(AuthActivity.this, token);
                        handler.sendEmptyMessage(0); // 认证成功消息
                    } else {
                        handler.sendEmptyMessage(1); // 认证失败消息
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            };
        }.start();
    }
}
