package com.android.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.passwordmanager.login.ChangeMPwdActivity;
import com.yalantis.jellytoolbar.widget.JellyToolbar;

import org.jetbrains.annotations.NotNull;

public class SettingsActivity extends AppCompatActivity {

    private TextView textView;
    private SeekBar seekBar;
    private TextView pwdSize;
    private int size = 8;
    private Switch isWord;
    private Switch isNum;
    private Switch isChar;
    private Intent intent = new Intent();
    private int w;
    private int n;
    private int c;
    private int signal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initToorbar();

        textView = (TextView) findViewById(R.id.seek_time);
        seekBar = (SeekBar) findViewById(R.id.appear_time);
        pwdSize = (TextView) findViewById(R.id.pwdSize);
        isWord = (Switch) findViewById(R.id.switch1);
        isNum = (Switch) findViewById(R.id.switch2);
        isChar = (Switch) findViewById(R.id.switch3);


        // 大小写
        isWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    w = 1;
                } else {
                    w = 0;
                }
            }
        });

        // 包含数字
        isNum.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    n = 1;
                } else {
                    n = 0;
                }
            }
        });

        // 包含字符
        isChar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    c = 1;
                } else {
                    c = 0;
                }
            }
        });

        initViews();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*拖动条停止拖动时调用 */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "拖动停止");
            }
            /*拖动条开始拖动时调用*/
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("SeekBarActivity", "开始拖动");
            }
            /* 拖动条进度改变时调用*/
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(progress + "s");
            }
        });

        pwdSize.setText(size + "");
        findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size > 8) {
                    size--;
                    pwdSize.setText(size + "");
                } else {
                    Toast.makeText(SettingsActivity.this, "密码最小长度为8", Toast.LENGTH_SHORT).show();
                    pwdSize.setText("8");
                }

            }
        });

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (size < 16) {
                    size++;
                    pwdSize.setText(size + "");
                } else {
                    Toast.makeText(SettingsActivity.this, "密码最大长度为16", Toast.LENGTH_SHORT).show();
                    pwdSize.setText("16");
                }

            }
        });

        Button changePwd = (Button) findViewById(R.id.change_btn);
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangeMPwdActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initToorbar() {
        JellyToolbar toolbar = (JellyToolbar) findViewById(R.id.toolbar);
        toolbar.getToolbar().setNavigationIcon(R.drawable.ic_back);
        toolbar.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnDatas();
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

    private void initViews() {
        Intent intent = getIntent();
        // 初始化密码显示时间
        int time = intent.getIntExtra("init_show_time", 10);
        seekBar.setProgress(time);
        textView.setText(time + "s");
        // 初始化生成的密码长度
        size = intent.getIntExtra("init_pwd_size", 8);
        pwdSize.setText(size + "");
        //
        int n = intent.getIntExtra("word_num_char", 0);
        switch (n) {
            case 0:

                break;
            case 1:

                isChar.setChecked(true);
                break;
            case 2:

                isNum.setChecked(true);
                break;
            case 3:

                isNum.setChecked(true);
                isChar.setChecked(true);
                break;
            case 4:

                isWord.setChecked(true);
                break;
            case 5:

                isWord.setChecked(true);
                isChar.setChecked(true);
                break;
            case 6:

                isWord.setChecked(true);
                isNum.setChecked(true);
                break;
            case 7:

                isWord.setChecked(true);
                isNum.setChecked(true);
                isChar.setChecked(true);
                break;

        }
    }

    private void returnDatas() {
        try {
            //Intent intent = new Intent();
            String a = textView.getText().toString();
            int Time = Integer.parseInt(a.substring(0, a.length() - 1));
            intent.putExtra("pwd_show_time", Time); //回传密码显示时间
            int Size = Integer.parseInt(pwdSize.getText().toString());
            intent.putExtra("pwd_size", Size); //回传密码生成长度
            isWNC(w, n, c);
            setResult(RESULT_OK, intent);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void isWNC(int w, int n, int c) {
        switch (w*100 + n*10 + c) {
            case 0:
                intent.putExtra("signal", 0);
                break;
            case 1:
                intent.putExtra("signal", 1);
                break;
            case 10:
                intent.putExtra("signal", 2);
                break;
            case 11:
                intent.putExtra("signal", 3);
                break;
            case 100:
                intent.putExtra("signal", 4);
                break;
            case 101:
                intent.putExtra("signal", 5);
                break;
            case 110:
                intent.putExtra("signal", 6);
                break;
            case 111:
                intent.putExtra("signal", 7);
                break;
            default:
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("size", size);
    }

    //重写返回键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            returnDatas();
            finish();
        }
        return true;
    }
}
