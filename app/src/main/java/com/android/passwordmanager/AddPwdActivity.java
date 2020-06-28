package com.android.passwordmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.passwordmanager.table.Art;
import com.android.passwordmanager.table.Food;
import com.android.passwordmanager.table.Health;
import com.android.passwordmanager.table.Home;
import com.android.passwordmanager.table.Message;
import com.android.passwordmanager.table.Money;
import com.android.passwordmanager.table.Other;
import com.android.passwordmanager.table.Play;
import com.android.passwordmanager.table.Shopping;
import com.android.passwordmanager.table.Social;
import com.android.passwordmanager.table.Study;
import com.android.passwordmanager.table.Travel;
import com.android.passwordmanager.table.Work;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import org.litepal.LitePal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;




public class AddPwdActivity extends AppCompatActivity {

    private EditText account_name;
    private EditText user;
    private EditText password;
    private EditText describe;
    private TextView time;
    private InputMethodManager manager;//输入法管理器
    private KeyBoardPatch keyBoardPatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pwd);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.keyBoardPatch = new KeyBoardPatch(this, ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
        this.keyBoardPatch.enable();
        initToorbar();

        initView();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                List<Study> studies = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Study.class);
                List<Social> socials = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Social.class);
                List<Work> works = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Work.class);
                List<Message> messages = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Message.class);
                List<Money> monies = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Money.class);
                List<Play> plays = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Play.class);
                List<Shopping> shoppings = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Shopping.class);
                List<Food> foods = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Food.class);
                List<Home> homes = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Home.class);
                List<Travel> travels = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Travel.class);
                List<Health> health = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Health.class);
                List<Art> arts = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Art.class);
                List<Other> others = LitePal.where("account_name = ?",
                        account_name.getText().toString()).find(Other.class);
                if (studies.size() != 0 || socials.size() != 0 || works.size() != 0
                        || messages.size() != 0 || monies.size() != 0 || plays.size() != 0
                        || shoppings.size() != 0 || foods.size() != 0 || homes.size() != 0
                        || travels.size() != 0 || health.size() != 0 || arts.size() != 0
                        || others.size() != 0) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddPwdActivity.this);
                    dialog.setMessage("该账号名称已存在，请重新输入");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                } else if (!returnDatas()) {
                    // 返回数据并结束
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddPwdActivity.this);
                    dialog.setMessage("账号名称不能为空，请重新输入");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddPwdActivity.this);
                    dialog.setMessage("添加成功");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.show();
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

    private void initView() {
        account_name = (EditText) findViewById(R.id.account_name);
        user = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);
        describe = (EditText) findViewById(R.id.describe);
        time = (TextView) findViewById(R.id.time);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean returnDatas() {
        if (!account_name.getText().toString().equals("")) {
            Intent intent = new Intent();
            intent.putExtra("data_account_name", account_name.getText().toString());
            intent.putExtra("data_user", user.getText().toString());
            intent.putExtra("data_password", password.getText().toString());
            intent.putExtra("data_describe", describe.getText().toString());
            String DateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); //当前日期和时间
            intent.putExtra("data_time", DateNow);
            setResult(RESULT_OK, intent);
            return true;
        } else {
            return false;
        }
    }
}
