package com.android.helpdesk;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccountAdapter extends BaseAdapter {
    ArrayList<Account> accountArrayList;

    public AccountAdapter(ArrayList<Account> accountArrayList){
        this.accountArrayList = accountArrayList;
    }
    @Override
    public int getCount() {
        return accountArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return accountArrayList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewAccount;
        if (convertView == null) {
            viewAccount = View.inflate(parent.getContext(), R.layout.account_row, null);
        } else viewAccount = convertView;

        Account account = (Account) getItem(position);
        ((TextView) viewAccount.findViewById(R.id.account_id)).setText(account.getIdAcc());
        ((TextView) viewAccount.findViewById(R.id.accountfullname)).setText(account.getFullname());
        ((TextView) viewAccount.findViewById(R.id.accountusername)).setText(account.getUsername());
        ((TextView) viewAccount.findViewById(R.id.accountemail)).setText(account.getEmail());
        return viewAccount;
    }
}
