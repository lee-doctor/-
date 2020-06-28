package com.android.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.List;

public class EditDataActivity extends AppCompatActivity {

    private TextView account_name;
    private TextView user;
    private TextView password;
    private TextView describe;
    private String back_up;
    private KeyBoardPatch keyBoardPatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
        this.keyBoardPatch = new KeyBoardPatch(this, ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
        this.keyBoardPatch.enable();

        initToorbar();

        initView();

        initData(); //初始化数据

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditDataActivity.this, account_name.getText().toString(), Toast.LENGTH_SHORT).show();
                // 账号名称不为空
                if (!account_name.getText().toString().equals("")) {
                    // 若账号名称更改
                    if (!account_name.getText().toString().equals(back_up)) {
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

                            AlertDialog.Builder dialog = new AlertDialog.Builder(EditDataActivity.this);
                            dialog.setMessage("该账号名称已存在，请重新输入");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            dialog.show();
                        } else { //返回数据
                            returnDatas();
                        }
                    } else {
                        returnDatas();
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EditDataActivity.this);
                    dialog.setMessage("账号名称不能为空，请重新输入");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
        account_name = (TextView) findViewById(R.id.account_name);
        user = (TextView) findViewById(R.id.user);
        password = (TextView) findViewById(R.id.password);
        describe = (TextView) findViewById(R.id.describe);
    }

    private void initData() {

        Intent intent = getIntent();
        String Account_name = intent.getStringExtra("data_Account_name");
        String Account = intent.getStringExtra("data_User");
        String Password = intent.getStringExtra("data_Password");
        String Describe = intent.getStringExtra("data_Describe");

        account_name.setText(Account_name);
        user.setText(Account);
        password.setText(Password);
        describe.setText(Describe);
        back_up = Account_name; //备份传过来的账号名称
    }

    private void returnDatas() {
        Intent intent = new Intent();
        intent.putExtra("data_account_name", account_name.getText().toString());
        intent.putExtra("data_user", user.getText().toString());
        intent.putExtra("data_password", password.getText().toString());
        intent.putExtra("data_describe", describe.getText().toString());
        setResult(RESULT_OK, intent);

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditDataActivity.this);
        dialog.setMessage("确认修改");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "修改成功", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout toastView = (LinearLayout) toast.getView();
                ImageView imageCodeProject = new ImageView(getApplicationContext());
                imageCodeProject.setImageResource(R.drawable.ic_success);
                toastView.addView(imageCodeProject, 0);
                toast.show();

                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }
}
