package com.android.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.passwordmanager.adapter.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.yalantis.jellytoolbar.listener.JellyListener;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private JellyToolbar toolbar;
    private AppCompatEditText editText;
    private DrawerLayout mDrawerLayout;
    private InputMethodManager manager;//输入法管理器
    private RecyclerView recycler;              //在xml 中 RecyclerView 布局
    private RecyclerViewAdapter rvAdapter;      //item_recycler 布局的 适配器
    private FloatingActionButton fab;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initView();
        initToolbar();

        // 设置数据库加密密钥
        LitePal.aesKey("ZjHuTdOfDdXfVdLewQug");
    }

    private void initView() {
        toolbar = (JellyToolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
}
