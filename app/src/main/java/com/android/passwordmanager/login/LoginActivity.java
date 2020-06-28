package com.android.passwordmanager.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.passwordmanager.MainActivity;
import com.android.passwordmanager.R;
import com.android.passwordmanager.StudyActivity;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName,password;
    private CheckBox rememberpass;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //verifyStoragePermissions(this);

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
        rememberpass = (CheckBox)findViewById(R.id.remember_pass);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        userName = findViewById(R.id.up_uesr);
        password =findViewById(R.id.up_pw);
        Button data_recover = findViewById(R.id.recover);
        Button login = findViewById(R.id.up_codebt);
        Button res = findViewById(R.id.res);

        data_recover.setOnClickListener(this);
        login.setOnClickListener(this);
        res.setOnClickListener(this);

        //是否记住账号
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String account = pref.getString("user", "");
            userName.setText(account);
            rememberpass.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // 数据恢复
            case R.id.recover:
                File destDir = new File("/data/data/com.android.passwordmanager", "databases");
                File sdDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/data/databases", "Passwords.db");
                if (!destDir.exists() && sdDir.exists()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle("数据恢复");
                    dialog.setMessage("确定恢复之前已备份的数据");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            verifyStoragePermissions(LoginActivity.this);
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                } else if (!sdDir.exists()) {
                    Toast toast = Toast.makeText(LoginActivity.this,
                            "数据未备份， 无法恢复", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (destDir.exists()) {
                    Toast toast = Toast.makeText(LoginActivity.this,
                            "已存在数据，无需恢复", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;
            // 登录
            case R.id.up_codebt:
                // 获取用户输入
                String name = userName.getText().toString();
                String pass = password.getText().toString();

                User user1 = LitePal.findFirst(User.class);
                if (user1 != null) {
                    String Us = user1.getUser();
                    String Pwd = user1.getPassword();

                    if (!name.equals("") && !pass.equals("")) {
                        if (name.equals(Us) && MD5Util.encrypt(pass).equals(Pwd)) {
                            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, StudyActivity.class);
                            intent.putExtra("flag", 1);
                            startActivity(intent);

                            editor = pref.edit();
                            if (rememberpass.isChecked()) {
                                editor.putBoolean("remember_password", true);
                                editor.putString("user", name);
                            } else {
                                editor.clear();
                            }
                            editor.apply();
                            editor.commit();
                            finish();//销毁此Activity
                        } else {
                            Toast toast = Toast.makeText(LoginActivity.this,
                                    "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(LoginActivity.this,
                                "请输入你的用户名或密码", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(LoginActivity.this,
                            "请先注册", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            break;
            // 注册
            case R.id.res:
                Intent res =new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(res);
            break;
            default:
        }
    }

    public static boolean dataSave() {
        File destDir = new File("/data/data/com.android.passwordmanager", "databases");
        String dbpath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/data/databases/" + "Passwords.db";
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        boolean success = copyFile(dbpath, destDir + "/" + "Passwords.db");

        if (success) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean copyFile(String source, String dest) {
        try {
            File f1 = new File(source);
            File f2 = new File(dest);
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);

            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            Log.d("LoginActivity(F)", String.valueOf(ex));
            return false;
        } catch (IOException e) {
            Log.d("LoginActivity(I)", String.valueOf(e));
            return false;
        }
        return true;
    }

    public boolean verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,1);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    if (dataSave()) {
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(LoginActivity.this);
                        dialog1.setMessage("恢复成功");
                        dialog1.setCancelable(false);
                        dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog1.show();
                        User user = LitePal.findFirst(User.class);
                        if (user != null) {
                            userName.setText(user.getUser());
                            userName.setSelection(userName.getText().length());
                        }
                    }
                } else {
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(LoginActivity.this);
                    dialog1.setMessage("恢复失败");
                    dialog1.setCancelable(false);
                    dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog1.show();
                }
        }
    }
}
