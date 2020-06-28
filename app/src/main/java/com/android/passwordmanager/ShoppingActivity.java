package com.android.passwordmanager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.passwordmanager.adapter.RecyclerViewAdapter;
import com.android.passwordmanager.secrettextview.SecretTextView;
import com.android.passwordmanager.table.Shopping;
import com.cjj.PerseiLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.yalantis.jellytoolbar.listener.JellyListener;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShoppingActivity extends AppCompatActivity implements RecyclerViewAdapter.onSlidingViewClickListener{

    private RecyclerView recycler;              //在xml 中 RecyclerView 布局
    private RecyclerViewAdapter rvAdapter;      //item_recycler 布局的 适配器

    private FloatingActionButton fab;

    //设置数据
    private List<Password> mPwdList = new ArrayList<Password>();

    private JellyToolbar toolbar;
    private AppCompatEditText editText;
    private static final String TEXT_KEY = "text";

    private InputMethodManager manager;//输入法管理器
    private DrawerLayout mDrawerLayout;
    private PerseiLayout perseiLayout;
    private NavigationView navigationView;
    private int id;
    private int showTime = 10; //默认密码显示时间为10s
    private int pwdSize = 8; //默认密码生成器生成的密码长度为8
    private int WNC = 0; //密码包含类型

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    String account_name;
    String account;
    String password;
    String describe;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        ActivityManager.getInstance().addActivity(this);
        initView();
        initToolbar();
        initSettings();
        initPerseiView();
        // 设置数据库加密密钥
        LitePal.aesKey("ZjHuTdOfDdXfVdLewQug");
        //将 RecyclerView 布局设置为线性布局
        recycler.setLayoutManager(new LinearLayoutManager(this));
        datas();//初始化列表数据
        //更新界面
        updateInterface();
        search();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShoppingActivity.this, "I'm here", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShoppingActivity.this, AddPwdActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_about:

                        break;
                    case R.id.nav_pwd_create:
                        final String p = getRandomNum(pwdSize, WNC);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingActivity.this);
                        dialog.setTitle("密码生成器");
                        dialog.setMessage(p);
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager copy = (ClipboardManager) ShoppingActivity.this
                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                assert copy != null;
                                copy.setText(p); //复制随机生成的密码
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();
                        break;
                    case R.id.nav_exit:
                        ActivityManager.getInstance().exit();
                        break;
                    case R.id.nav_copy:

                        break;
                    case R.id.nav_settings:
                        Intent intent = new Intent(ShoppingActivity.this, SettingsActivity.class);
                        intent.putExtra("init_show_time", showTime);
                        intent.putExtra("init_pwd_size", pwdSize);
                        intent.putExtra("word_num_char", WNC);
                        startActivityForResult(intent, 3);
                        break;
                }
                return true;
            }
        });
    }

   /*
     * android 动态权限申请
     * */
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
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(ShoppingActivity.this);
                        dialog1.setMessage("备份成功");
                        dialog1.setCancelable(false);
                        dialog1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog1.show();
                    }
                } else {
                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(ShoppingActivity.this);
                    dialog1.setMessage("备份失败");
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

    // 将数据库备份到sd卡中
    public static boolean dataSave() {
        String dbpath = "/data/data/com.android.passwordmanager/databases/"
                +"Passwords.db";
        File destDir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/data", "databases");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        boolean success = copyFile(dbpath, destDir + "/" + "Passwords.db");
        /*
        boolean success = copyFile(dbpath, Environment.getExternalStorageDirectory().getPath() +
                "/databases/"
                + "Passwords.db");

         */

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
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
        }
        return true;
    }

    private void initView() {
        toolbar = (JellyToolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        recycler = (RecyclerView)findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initToolbar() {
        toolbar.getToolbar().setNavigationIcon(R.drawable.ic_menu);
        toolbar.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        toolbar.setJellyListener(jellyListener);
        toolbar.getToolbar().setPadding(0, getStatusBarHeight(), 0, 0);

        editText = (AppCompatEditText) LayoutInflater.from(this).inflate(R.layout.edit_text, null);
        editText.setBackgroundResource(R.color.colorTransparent);
        toolbar.setContentView(editText);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void initSettings() {
        Intent intent = getIntent();
        showTime = intent.getIntExtra("init_show_time", 10);
        pwdSize = intent.getIntExtra("init_pwd_size", 8);
        WNC = intent.getIntExtra("word_num_char", 0);
    }

    // 搜索功能实现
    private void search() {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //先隐藏键盘
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                    }
                    //自己需要的操作
                    Intent intent = new Intent(ShoppingActivity.this, SearchResultActivity.class);
                    intent.putExtra("search_key", editText.getText().toString());
                    startActivity(intent);
                }
                //记得返回false
                return false;
            }
        });
    }

    private JellyListener jellyListener = new JellyListener() {
        @Override
        public void onCancelIconClicked() {
            if (TextUtils.isEmpty(editText.getText())) {
                // 还原状态
                toolbar.collapse();
                // 关闭软键盘
                if (manager.isActive()) {
                    manager.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                }
            } else {
                editText.getText().clear();
                getFocus();
            }
        }

        @Override
        public void onToolbarExpandingStarted() {
            getFocus();
        }
    };

    /**
     * EditText获取焦点并显示软键盘
     */
    private void getFocus() {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        manager.showSoftInput(editText, 0);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TEXT_KEY, editText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editText.setText(savedInstanceState.getString(TEXT_KEY));
        editText.setSelection(editText.getText().length());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // 获取 AddPwdActivity 返回的数据
                    String account_name = data.getStringExtra("data_account_name");
                    String user = data.getStringExtra("data_user");
                    String password = data.getStringExtra("data_password");
                    String describe = data.getStringExtra("data_describe");
                    String time = data.getStringExtra("data_time");

                    // 添加到数据库
                    Shopping user1 = new Shopping();
                    user1.setAccount_name(account_name);
                    user1.setAccount(user);
                    user1.setPassword(password);
                    user1.setDescribe(describe);
                    user1.setTime(time);
                    user1.save();

                    // 动态添加Item
                    Password pwd = new Password(account_name, user, password, describe, time);
                    rvAdapter.addData(pwd);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingActivity.this);
                    dialog.setMessage("添加成功");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    // 获取编辑后返回的数据
                    String Account_name = data.getStringExtra("data_account_name");
                    String Account = data.getStringExtra("data_user");
                    String Password = data.getStringExtra("data_password");
                    String Describe = data.getStringExtra("data_describe");


                    // 修改数据库中的数据
                    List<Shopping> datas = LitePal.where("account_name = ?", account_name)
                            .find(Shopping.class);
                    for(Shopping Data:datas) {
                        id = Data.getId();
                    }

                    String DateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    Shopping user = new Shopping();
                    user.setAccount_name(Account_name);
                    user.setAccount(Account);
                    user.setPassword(Password);
                    user.setDescribe(Describe);
                    user.setTime(DateNow);
                    user.update(id);

                    // 清空当前item并返回数据库所有数据（刷新itemview）
                    mPwdList.clear();
                    datas();
                    rvAdapter.notifyDataSetChanged();
                    if (rvAdapter.menuIsOpen()) {
                        rvAdapter.closeMenu();//关闭菜单
                    }
                    //Toast.makeText(ListActivity.this, "修改成功", Toast.LENGTH_SHORT).show();

                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    showTime = data.getIntExtra("pwd_show_time", 10);
                    pwdSize = data.getIntExtra("pwd_size", 8);
                    WNC = data.getIntExtra("signal", 7);
                    //String p = data.getStringExtra("pwd_size");
                    //Toast.makeText(ListActivity.this, pwdSize, Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    public void init(){
        recycler = (RecyclerView)findViewById(R.id.recyclerView);
    }

    public void updateInterface(){
        if (rvAdapter == null) {
            //实例化 RecyclerViewAdapter 并设置数据
            rvAdapter = new RecyclerViewAdapter(this, mPwdList);
            //将适配的内容放入 mRecyclerView
            recycler.setAdapter(rvAdapter);
            //控制Item增删的动画，需要通过ItemAnimator  DefaultItemAnimator -- 实现自定义动画
            recycler.setItemAnimator(new DefaultItemAnimator());
        }else {
            //强调通过 getView() 刷新每个Item的内容
            rvAdapter.notifyDataSetChanged();
        }
        //设置滑动监听器 （侧滑）
        rvAdapter.setOnSlidListener(this);
    }

    //通过 position 区分点击了哪个 item
    @Override
    public void onItemClick(View view, int position) {
        // 实现文本（密码）隐藏效果
        SecretTextView secretTextView = view.findViewById(R.id.password);
        secretTextView.setDuration(showTime * (int) Math.pow(10, 3)); //设置淡入淡出时间
        secretTextView.setIsVisible(true); //设置可见性而不会淡入或淡出
        secretTextView.toggle(); //根据当前状态淡入或淡出

        // 关闭顶部菜单栏
        perseiLayout.closeMenu();
    }

    //点击删除按钮时，根据传入的 position 调用 RecyclerAdapter 中的 removeData() 方法
    @Override
    public void onDeleteBtnCilck(View view, int position) {
        // 删除数据库中的数据
        if (mPwdList != null && rvAdapter.getItemCount() > 0) {
            String account_name = mPwdList.get(position).getAccount_name();
            LitePal.deleteAll(Shopping.class, "account_name = ?", account_name);
            // 最后删除 itemview 否则会出现删除错乱，边界溢出
            rvAdapter.removeData(position);

            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingActivity.this);
            dialog.setMessage("删除成功");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onEditBtnClick(View view, int position) {
        // 初始化要传过去的数据

        account_name = mPwdList.get(position).getAccount_name();
        account = mPwdList.get(position).getAccount();
        password = mPwdList.get(position).getPassword();
        describe = mPwdList.get(position).getDescribe();

        Intent intent = new Intent(ShoppingActivity.this, EditDataActivity.class);
        intent.putExtra("data_Account_name", account_name);
        intent.putExtra("data_User", account);
        intent.putExtra("data_Password", password);
        intent.putExtra("data_Describe", describe);
        startActivityForResult(intent, 2);
    }

    public void datas(){
        // 查询数据库中的所有数据，返回到 recyclerview 显示
        List<Shopping> users = LitePal.findAll(Shopping.class);
        for (Shopping user: users) {
            String account_name = user.getAccount_name();
            String account = user.getAccount();
            String password = user.getPassword();
            String describe = user.getDescribe();
            String time = user.getTime();
            Password pwd = new Password(account_name, account, password, describe, time);
            mPwdList.add(pwd);
        }
    }


    // 生成密码
    public static String getRandomNum(int pwd_len, int signal) {
        final int maxNum = 82;
        int i;
        int count = 0;
        char[] str = {'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h',
                'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'X', 'y', 'Y', 'z', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                '!', '#', '$', '%', '&', '(', ')', '*', '+', ',', '-', '.', '/', '\'', '"',
                ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'};
        StringBuilder pwd = new StringBuilder("");
        Random r = new Random();
        switch (signal) {
            case 0: //只含小写字母
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 97 && str[i] <= 122) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            case 1: //小写字母加字符
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 97 && str[i] <= 122 || str[i] <= 47 || str[i] >= 58
                                && str[i] <= 64 || str[i] >= 91 || str[i] <= 96 && str[i] >= 123) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            case 2: // 小写字母加数字
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 97 && str[i] <= 122 || str[i] >= 48 && str[i] <= 57) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            case 3: //小写字母、数字、字符
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 97 && str[i] <= 122 || str[i] >= 48 && str[i] <= 57
                                || str[i] <= 47 || str[i] >= 58 && str[i] <= 64 || str[i] >= 91
                                && str[i] <= 96 || str[i] >= 123) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            case 4: //大小写字母
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 65 && str[i] <= 90 || str[i] >= 97 && str[i] <= 122) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            case 5: //大小写字母加字符
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 65 && str[i] <= 90 || str[i] >= 97 && str[i] <= 122
                                || str[i] <= 47 || str[i] >= 58 && str[i] <= 64 || str[i] >= 91
                                && str[i] <= 96 || str[i] >= 123) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            case 6: //大小写字母加数字
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 65 && str[i] <= 90 || str[i] >= 97 && str[i] <= 122
                                || str[i] >= 48 && str[i] <= 57) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            case 7: //大小写字母、数字、字符
                while (count < pwd_len) {
                    i = Math.abs(r.nextInt(maxNum));

                    if (i >= 0 && i < str.length) {
                        if (str[i] >= 65 && str[i] <= 90 || str[i] >= 97 && str[i] <= 122
                                || str[i] >= 48 && str[i] <= 57 || str[i] <= 47 || str[i] >= 58
                                && str[i] <= 64 || str[i] >= 91 && str[i] <= 96 || str[i] >= 123) {
                            pwd.append(str[i]);
                            count++;
                        }
                    }
                }
                break;
            default:
        }
        return pwd.toString();
    }
    /*
    public static String getRandomNum(int pwd_len) {
        final int maxNum = 50;
        int i;
        int count = 0;
        char[] str = { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h',
                'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'X', 'y', 'Y', 'z', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };
        StringBuilder pwd = new StringBuilder("");
        Random r = new Random();
        while (count < pwd_len) {
            i = Math.abs(r.nextInt(maxNum));

            if (i >= 0 && i < str.length) {
                pwd.append(str[i]);
                count++;
            }
        }
        return pwd.toString();
    }

     */

    private void initPerseiView() {
        perseiLayout = (PerseiLayout) this.findViewById(R.id.persei);
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new IconAdapter(this));
        perseiLayout.setHeaderView(recyclerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

        public int[] icons =
                {
                        R.drawable.ic_study, R.drawable.ic_social, R.drawable.ic_work,
                        R.drawable.ic_message, R.drawable.ic_money, R.drawable.ic_play,
                        R.drawable.ic_shopping, R.drawable.ic_food, R.drawable.ic_home,
                        R.drawable.ic_travel, R.drawable.ic_health, R.drawable.ic_art, R.drawable.ic_else
                };

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView mImageView;
            public TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(R.id.itdu);
            }
        }

        public IconAdapter(Context context) {
            super();
        }

        @Override
        public IconAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final IconAdapter.ViewHolder holder, final int position) {
            holder.mImageView.setImageResource(icons[position]);
            switch (position) {
                case 0:
                    holder.mTextView.setText("学习");
                    break;
                case 1:
                    holder.mTextView.setText("社交");
                    break;
                case 2:
                    holder.mTextView.setText("工作");
                    break;
                case 3:
                    holder.mTextView.setText("信息");
                    break;
                case 4:
                    holder.mTextView.setText("理财");
                    break;
                case 5:
                    holder.mTextView.setText("娱乐");
                    break;
                case 6:
                    holder.mTextView.setText("购物");
                    break;
                case 7:
                    holder.mTextView.setText("餐饮");
                    break;
                case 8:
                    holder.mTextView.setText("家居");
                    break;
                case 9:
                    holder.mTextView.setText("出行");
                    break;
                case 10:
                    holder.mTextView.setText("医疗健康");
                    break;
                case 11:
                    holder.mTextView.setText("艺术设计");
                    break;
                case 12:
                    holder.mTextView.setText("其他");
                    break;
            }

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            Intent intent = new Intent(ShoppingActivity.this, StudyActivity.class);
                            intent.putExtra("init_show_time", showTime);
                            intent.putExtra("init_pwd_size", pwdSize);
                            intent.putExtra("word_num_char", WNC);
                            startActivity(intent);
                            finish();
                            break;
                        case 1:
                            Intent intent1 = new Intent(ShoppingActivity.this, SocialActivity.class);
                            intent1.putExtra("init_show_time", showTime);
                            intent1.putExtra("init_pwd_size", pwdSize);
                            intent1.putExtra("word_num_char", WNC);
                            startActivity(intent1);
                            finish();
                            break;
                        case 2:
                            Intent intent2 = new Intent(ShoppingActivity.this, WorkActivity.class);
                            intent2.putExtra("init_show_time", showTime);
                            intent2.putExtra("init_pwd_size", pwdSize);
                            intent2.putExtra("word_num_char", WNC);
                            startActivity(intent2);
                            finish();
                            break;
                        case 3:
                            Intent intent3 = new Intent(ShoppingActivity.this, MessageActivity.class);
                            intent3.putExtra("init_show_time", showTime);
                            intent3.putExtra("init_pwd_size", pwdSize);
                            intent3.putExtra("word_num_char", WNC);
                            startActivity(intent3);
                            finish();
                            break;
                        case 4:
                            Intent intent4 = new Intent(ShoppingActivity.this, MoneyActivity.class);
                            intent4.putExtra("init_show_time", showTime);
                            intent4.putExtra("init_pwd_size", pwdSize);
                            intent4.putExtra("word_num_char", WNC);
                            startActivity(intent4);
                            finish();
                            break;
                        case 5:
                            Intent intent5 = new Intent(ShoppingActivity.this, PlayActivity.class);
                            intent5.putExtra("init_show_time", showTime);
                            intent5.putExtra("init_pwd_size", pwdSize);
                            intent5.putExtra("word_num_char", WNC);
                            startActivity(intent5);
                            finish();
                            break;
                        case 6:
                            perseiLayout.closeMenu();
                            break;
                        case 7:
                            Intent intent7 = new Intent(ShoppingActivity.this, FoodActivity.class);
                            intent7.putExtra("init_show_time", showTime);
                            intent7.putExtra("init_pwd_size", pwdSize);
                            intent7.putExtra("word_num_char", WNC);
                            startActivity(intent7);
                            finish();
                            break;
                        case 8:
                            Intent intent8 = new Intent(ShoppingActivity.this, HomeActivity.class);
                            intent8.putExtra("init_show_time", showTime);
                            intent8.putExtra("init_pwd_size", pwdSize);
                            intent8.putExtra("word_num_char", WNC);
                            startActivity(intent8);
                            finish();
                            break;
                        case 9:
                            Intent intent9 = new Intent(ShoppingActivity.this, TravelActivity.class);
                            intent9.putExtra("init_show_time", showTime);
                            intent9.putExtra("init_pwd_size", pwdSize);
                            intent9.putExtra("word_num_char", WNC);
                            startActivity(intent9);
                            finish();
                            break;
                        case 10:
                            Intent intent10 = new Intent(ShoppingActivity.this, HealthActivity.class);
                            intent10.putExtra("init_show_time", showTime);
                            intent10.putExtra("init_pwd_size", pwdSize);
                            intent10.putExtra("word_num_char", WNC);
                            startActivity(intent10);
                            finish();
                            break;
                        case 11:
                            Intent intent11 = new Intent(ShoppingActivity.this, ArtActivity.class);
                            intent11.putExtra("init_show_time", showTime);
                            intent11.putExtra("init_pwd_size", pwdSize);
                            intent11.putExtra("word_num_char", WNC);
                            startActivity(intent11);
                            finish();
                            break;
                        case 12:
                            Intent intent12 = new Intent(ShoppingActivity.this, OtherActivity.class);
                            intent12.putExtra("init_show_time", showTime);
                            intent12.putExtra("init_pwd_size", pwdSize);
                            intent12.putExtra("word_num_char", WNC);
                            startActivity(intent12);
                            finish();
                            break;
                        default:
                    }

                    //Toast.makeText(ListActivity.this, "you have select pos--->" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return icons.length;
        }
    }
}
