package com.android.passwordmanager.login;

import androidx.appcompat.app.AppCompatActivity;

import com.android.passwordmanager.KeyBoardPatch;
import com.android.passwordmanager.R;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

public class ChangeMPwdActivity extends AppCompatActivity {

    private TextView old_pwd;
    private TextView new_pwd;
    private TextView sure;
    private String pwd;
    private KeyBoardPatch keyBoardPatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_m_pwd);
        this.keyBoardPatch = new KeyBoardPatch(this, ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
        this.keyBoardPatch.enable();
        initToorbar();
        old_pwd = (TextView) findViewById(R.id.password);
        new_pwd = (TextView) findViewById(R.id.new_pwd);
        sure = (TextView) findViewById(R.id.sure);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Old = old_pwd.getText().toString();
                String New = new_pwd.getText().toString();
                String Sure = sure.getText().toString();

                User user = LitePal.findFirst(User.class);
                String password = user.getPassword(); // 原始登录密码

                if (!TextUtils.isEmpty(Old) && !TextUtils.isEmpty(New) && !TextUtils.isEmpty(Sure)) {

                    if (MD5Util.encrypt(Old).equals(password) && Sure.equals(New)) {
                        // 修改第一条数据
                        ContentValues values = new ContentValues();
                        values.put("password", MD5Util.encrypt(New));
                        LitePal.update(User.class, values, 1);
                        Toast toast = Toast.makeText(ChangeMPwdActivity.this,
                                "修改成功", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ChangeMPwdActivity.this);
                        dialog.setMessage("修改成功");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        dialog.show();
                    } else if (!MD5Util.encrypt(Old).equals(password)) {
                        Toast.makeText(ChangeMPwdActivity.this,
                                password, Toast.LENGTH_SHORT);
                        Toast toast = Toast.makeText(ChangeMPwdActivity.this,
                                "原密码输入错误，请重新输入", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(ChangeMPwdActivity.this,
                                "密码输入不一致，请重新输入", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                } else {
                    Toast toast = Toast.makeText(ChangeMPwdActivity.this,
                            "信息未完善，无法修改", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.keyBoardPatch != null) {
            this.keyBoardPatch.disable();
        }
    }

    private void initToorbar() {
        JellyToolbar toolbar = (JellyToolbar) findViewById(R.id.toolbar);
        toolbar.getToolbar().setNavigationIcon(R.drawable.back);
        toolbar.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.getToolbar().setPadding(0, getStatusBarHeight(), 0, 0);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
