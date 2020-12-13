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

public class AdminHomeFragment extends Fragment {
    ArrayList<Log> logsArrayList;
    LogAdapter logAdapter;
    ListView logListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_home, container, false);
        logListView = view.findViewById(R.id.log_listview);
        logsArrayList = new ArrayList<>();
        logAdapter = new LogAdapter(logsArrayList);
        logListView.setAdapter(logAdapter);
        if(LoginActivity.vi){
            setAppLocale("vi");
        }
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                getLog(inflater.getContext());
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
    public void getLog(final Context context) {

            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/log";///user/ + LoginActivity.id;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        logsArrayList.clear();
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            String id = object.getString("id");
                            String time = object.getString("time");
                            String content = object.getString("content");
                            logsArrayList.add(new Log(i,id,content,time));
                        }
                        logAdapter.notifyDataSetChanged();
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

