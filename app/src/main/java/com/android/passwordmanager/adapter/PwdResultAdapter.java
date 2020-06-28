package com.android.passwordmanager.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.passwordmanager.R;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.passwordmanager.Password;

import java.util.List;

public class PwdResultAdapter extends RecyclerView.Adapter<PwdResultAdapter.ViewHolder> {

    private List<Password> mPwdResultList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView acc_name;
        TextView account_name;
        TextView acc;
        TextView user;
        TextView pass;
        TextView password;
        TextView des;
        TextView describe;
        TextView tim;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);

            acc_name = (TextView)itemView.findViewById(R.id.acc_name);
            account_name = (TextView)itemView.findViewById(R.id.account_name);
            acc = (TextView)itemView.findViewById(R.id.acc);
            user = (TextView)itemView.findViewById(R.id.user);
            pass = (TextView)itemView.findViewById(R.id.pass);
            password = (TextView)itemView.findViewById(R.id.password);
            des = (TextView)itemView.findViewById(R.id.des);
            describe = (TextView)itemView.findViewById(R.id.describe);
            tim = (TextView)itemView.findViewById(R.id.tim);
            time = (TextView)itemView.findViewById(R.id.time);
        }
    }
    public PwdResultAdapter(List<Password> mPwdResultList) {
        this.mPwdResultList = mPwdResultList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_result, parent, false);
        PwdResultAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Password pwdResult = mPwdResultList.get(position);
        holder.account_name.setText(pwdResult.getAccount_name());
        holder.user.setText(pwdResult.getAccount());
        holder.password.setText(pwdResult.getPassword());
        holder.describe.setText(pwdResult.getDescribe());
        holder.time.setText(pwdResult.getTime());
    }

    @Override
    public int getItemCount() {
        return mPwdResultList.size();
    }
}
