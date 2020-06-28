package com.android.passwordmanager.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.passwordmanager.R;
import androidx.appcompat.app.AppCompatActivity;


import org.litepal.LitePal;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText userName,password,passwords ,code;
    Button up_bt,code_bt;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //设置透明状态栏和导航栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        userName =findViewById( R.id.up_uesr);
        password =findViewById(R.id.up_pw);
        passwords =findViewById(R.id.up_pw2);

        up_bt =findViewById(R.id.up_login);
        up_bt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.up_login:{
                String username = userName.getText().toString().trim();
                String pw = password.getText().toString().trim();
                String pws = passwords.getText().toString().trim();

                User user1 = LitePal.findFirst(User.class);
                if (user1 != null) {
                    Toast toast = Toast.makeText(RegisterActivity.this,
                            "已注册过一次账号，无法再进行注册", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if (!username.equals("") && !pw.equals("")) {
                    if (pw.equals(pws)) {
                        //LitePal.aesKey("dkKJdhihgaLLd154534?.!@d");
                        User user = new User();
                        user.setUser(username);
                        user.setPassword(MD5Util.encrypt(pw));
                        user.save();
                        Toast toast = Toast.makeText(RegisterActivity.this,
                                "注册成功", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        finish();
                    } else {
                        Toast toast = Toast.makeText(RegisterActivity.this,
                                "密码输入不一致，请重新输入", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(RegisterActivity.this,
                            "注册信息未完善，无法注册", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            break;
        }
    }
}
