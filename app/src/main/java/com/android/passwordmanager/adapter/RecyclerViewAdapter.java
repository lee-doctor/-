package com.android.passwordmanager.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.passwordmanager.Password;
import com.android.passwordmanager.R;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.passwordmanager.RecyclerItemView;
import com.android.passwordmanager.RecyclerUtils;
import com.android.passwordmanager.secrettextview.SecretTextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SimpleHolder>
        implements RecyclerItemView.onSlidingButtonListener{

    private Context context;

    private List<Password> mPwdList;

    private onSlidingViewClickListener onSvcl;

    private RecyclerItemView recyclers;
    private boolean isTelescopic = false;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public RecyclerViewAdapter(Context context, List<Password> mPwdList) {

        this.context = context;
        this.mPwdList = mPwdList;
    }

    @Override
    public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new SimpleHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleHolder holder, int position) {

        holder.layout_left.getLayoutParams().width = RecyclerUtils.getScreenWidth(context);
        Password password = mPwdList.get(position);

        holder.account_name.setText(password.getAccount_name());
        holder.account.setText(password.getAccount());
        holder.password.setText(password.getPassword());
        holder.describe.setText(password.getDescribe());
        holder.time.setText(password.getTime());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否有删除菜单打开
                if (menuIsOpen()) {
                    closeMenu();//关闭菜单
                } else {
                    //获得布局下标（点的哪一个）
                    int subscript = holder.getLayoutPosition();
                    onSvcl.onItemClick(view, subscript);
                }
            }
        });

        // 编辑按钮监听事件
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 进入到密码编辑界面
                int subscript = holder.getAdapterPosition();
                onSvcl.onEditBtnClick(view, subscript);
            }
        });
        // 删除按钮监听事件
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // 提示是否删除
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("确认删除");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int subscript = holder.getAdapterPosition();
                        onSvcl.onDeleteBtnCilck(view,subscript);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }

        });

        //添加itemView的动画 这里是直接为整个itemView添加一个动画
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.scale_50_to_100);
        holder.itemView.startAnimation(animation);

        holder.describe.post(new Runnable() {
            @Override
            public void run() {
                boolean b = isTextView(holder.describe);
                if (b) {
                    holder.des_key.setVisibility(View.VISIBLE);
                } else {
                    holder.des_key.setVisibility(View.GONE);

                }
            }
        });

        holder.des_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTelescopic) {
                    //隐藏展开按钮
                    holder.des_key.setVisibility(View.GONE);
                    //TextView行数显示最大
                    holder.describe.setMaxLines(Integer.MAX_VALUE);
                    isTelescopic = true;
                } else {
                    //显示展开按钮
                    holder.des_key.setVisibility(View.VISIBLE);
                    //TextView行数显示3行
                    holder.describe.setMaxLines(1);
                    isTelescopic = false;
                }
            }
        });
    }

    private boolean isTextView(TextView textView){
        float m=textView.getPaint().measureText(textView.getText().toString());
        float n=3*(textView.getWidth()-textView.getPaddingRight()-textView.getPaddingLeft());
        return m>n;
    }

    @Override
    public int getItemCount() {
        return mPwdList.size();
    }

    @Override
    public void onMenuIsOpen(View view) {
        recyclers = (RecyclerItemView) view;
    }

    @Override
    public void onDownOrMove(RecyclerItemView recycler) {
        if(menuIsOpen()){
            if(recyclers != recycler){
                closeMenu();
            }
        }
    }

    class SimpleHolder extends  RecyclerView.ViewHolder {

        CardView delete;
        CardView cv;
        CardView edit;
        TextView acc_name;
        TextView account_name;
        TextView acc;
        TextView account;
        TextView pass;
        SecretTextView password;
        TextView des;
        TextView describe;
        TextView des_key;
        TextView tim;
        TextView time;
        LinearLayout layout_left;

        public SimpleHolder(View view) {
            super(view);

            edit = (CardView) view.findViewById(R.id.edit);
            delete = (CardView) view.findViewById(R.id.delete);
            layout_left = (LinearLayout) view.findViewById(R.id.layout_left);

            cv = (CardView)itemView.findViewById(R.id.cv);

            acc_name = (TextView)itemView.findViewById(R.id.acc_name);
            account_name = (TextView)itemView.findViewById(R.id.account_name);
            acc = (TextView)itemView.findViewById(R.id.acc);
            account = (TextView)itemView.findViewById(R.id.account);
            pass = (TextView)itemView.findViewById(R.id.pass);
            password = (SecretTextView)itemView.findViewById(R.id.password);
            des = (TextView)itemView.findViewById(R.id.des);
            describe = (TextView) itemView.findViewById(R.id.describe);
            des_key = (TextView) itemView.findViewById(R.id.des_key);
            tim = (TextView)itemView.findViewById(R.id.tim);
            time = (TextView)itemView.findViewById(R.id.time);

            ((RecyclerItemView)view).setSlidingButtonListener(RecyclerViewAdapter.this);
        }
    }

    // 添加数据
    public void addData(Password pwd) {
        mPwdList.add(pwd);
        notifyDataSetChanged();
    }

    //删除数据
    public void removeData(int position){
        if (position != -1) {
            mPwdList.remove(position);
            notifyItemRemoved(position); //刷新被删除的地方
            notifyItemRangeChanged(position, getItemCount() - position); //刷新被删除数据，以及其后面的数据
        }
    }

    //关闭菜单
    public void closeMenu() {
        recyclers.closeMenu();
        recyclers = null;

    }

    // 判断是否有菜单打开
    public Boolean menuIsOpen() {
        if(recyclers != null){
            return true;
        }
        return false;
    }



    //设置在滑动侦听器上
    public void setOnSlidListener(onSlidingViewClickListener listener) {
        onSvcl = listener;
    }

    // 在滑动视图上单击侦听器
    public interface onSlidingViewClickListener {
        void onItemClick(View view, int position);
        void onDeleteBtnCilck(View view, int position);
        void onEditBtnClick(View view, int position);
    }

}
