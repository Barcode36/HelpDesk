package com.android.helpdesk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class AdminAccountFragment extends Fragment {
    ArrayList<Account> accountArrayList;
    AccountAdapter accountAdapter;
    ListView accountListView;
    Button btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_account, container, false);
        accountListView = view.findViewById(R.id.account_listview);
        accountArrayList = new ArrayList<>();
        accountAdapter = new AccountAdapter(accountArrayList);
        accountListView.setAdapter(accountAdapter);
        btnAdd = (Button) view.findViewById(R.id.btnAddUser);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(),ActivityAddAccount.class);
                startActivity(intent);
            }
        });
        if(LoginActivity.vi){
            setAppLocale("vi");
        }
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                getAccount(inflater.getContext());
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(r, 2000);
        return view;
    }
    public void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }
    public void getAccount(final Context context) {
            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/user/role/technician";///user/ + LoginActivity.id;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        accountArrayList.clear();
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            String id = object.getString("id");
                            String fullname = object.getString("fullName");
                            String pass = object.getString("password");
                            String email = object.getString("email");
                            String role = object.getString("role");
                            String username = object.getString("username");
                            accountArrayList.add(new Account(i,id,fullname,email,role,pass,username));
                        }
                        accountAdapter.notifyDataSetChanged();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(stringRequest);

    }
}
