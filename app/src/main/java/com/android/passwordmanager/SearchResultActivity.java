package com.android.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.passwordmanager.adapter.PwdResultAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private List<Password> pwdResultList = new ArrayList<Password>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        // 初始化Toorbar
        initToorbar();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        PwdResultAdapter adapter = new PwdResultAdapter(pwdResultList);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String SearchContent = intent.getStringExtra("search_key");
        assert SearchContent != null;
        searchForResult(SearchContent);
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

    private void searchForResult(String keyword) {
        if (!keyword.equals("null")) {
            List<Study> studies = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Study.class);
            List<Social> socials = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Social.class);
            List<Work> works = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Work.class);
            List<Message> messages = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Message.class);
            List<Money> monies = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Money.class);
            List<Play> plays = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Play.class);
            List<Shopping> shoppings = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Shopping.class);
            List<Food> foods = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Food.class);
            List<Home> homes = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Home.class);
            List<Travel> travels = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Travel.class);
            List<Health> health = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Health.class);
            List<Art> arts = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Art.class);
            List<Other> others = LitePal.where("account_name like ? or describe like ?" +
                            "or time like ?", "%" + keyword + "%", "%" + keyword + "%",
                    "%" + keyword + "%").find(Other.class);

            if (studies.size()==0 && socials.size()==0 && works.size()==0 && messages.size()==0
                    && monies.size()==0 && plays.size()==0 && shoppings.size()==0 && foods.size()==0
                    && homes.size()==0 && travels.size()==0 && health.size()==0 && arts.size()==0
                    && others.size()==0) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SearchResultActivity.this);
                dialog.setMessage("没有找到");
                dialog.setCancelable(false);
                dialog.setPositiveButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                if (studies.size() != 0) {
                    for(Study data:studies) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (socials.size() != 0) {
                    for(Social data:socials) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (works.size() != 0) {
                    for(Work data:works) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (messages.size() != 0) {
                    for(Message data:messages) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (monies.size() != 0) {
                    for(Money data:monies) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (plays.size() != 0) {
                    for(Play data:plays) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (shoppings.size() != 0) {
                    for(Shopping data:shoppings) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (foods.size() != 0) {
                    for(Food data:foods) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (homes.size() != 0) {
                    for(Home data:homes) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (travels.size() != 0) {
                    for(Travel data:travels) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (arts.size() != 0) {
                    for(Art data:arts) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
                if (others.size() != 0) {
                    for(Other data:others) {
                        String account_name = data.getAccount_name();
                        String account = data.getAccount();
                        String password = data.getPassword();
                        String describe = data.getDescribe();
                        String time = data.getTime();
                        Password pwd = new Password(account_name, account, password, describe, time);
                        pwdResultList.add(pwd);
                    }
                }
            }
        }
    }
}
