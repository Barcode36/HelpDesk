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

public class AdminTicketFragment extends Fragment {
    ArrayList<Ticket> ticketArrayList;
    TicketAdapter ticketAdapter;
    ListView ticketListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_ticket, container, false);
        ticketListView = view.findViewById(R.id.ticket_listview);
        ticketArrayList = new ArrayList<>();
        ticketAdapter = new TicketAdapter(ticketArrayList);
        ticketListView.setAdapter(ticketAdapter);
        if(LoginActivity.vi){
            setAppLocale("vi");
        }
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                getTicket(inflater.getContext());
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
    public void getTicket(final Context context) {
        if(ticketArrayList.isEmpty()){
            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/ticket";///user/" + LoginActivity.id;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            String id = object.getString("id");
                            String title = object.getString("title");
                            String startDate = object.getString("startDate");
                            String endDate = object.getString("endDate");
                            String description = object.getString("description");
                            String place =  object.getString("place");
                            String userId = object.getString("userId");
                            String fullName = object.getString("fullName");
                            String technicianId = object.getString("technicianId");
                            String technicianName = object.getString("technicianName");
                            String modifiedBy = object.getString("modifiedBy");;
                            ArrayList<JSONObject> arrStatus = new ArrayList<>();
                            ArrayList<String> arrImage = new ArrayList<>();
                            ArrayList<String> arrComment = new ArrayList<>();
                            JSONArray jsonArrStatus = object.getJSONArray("status");
                            JSONArray jsonArrComment = object.getJSONArray("comment");
                            JSONArray jsonArrImage = object.getJSONArray("images");
                            for (int j = 0;j<jsonArrStatus.length();j++){
                                JSONObject objStatus = jsonArrStatus.getJSONObject(j);
                                arrStatus.add(objStatus);
                            };
                            for (int j = 0;j<jsonArrImage.length();j++){
                                String strImage = jsonArrImage.getString(j) ;
                                arrImage.add(strImage);
                            };
                            for (int j = 0;j<jsonArrComment.length();j++){
                                String strComment = jsonArrComment.getString(j) ;
                                arrComment.add(strComment);
                            }
                            ticketArrayList.add(new Ticket(i,id,title,startDate,endDate,description,place,userId,fullName,technicianId,technicianName,modifiedBy,arrStatus,arrImage,arrComment));
                        }
                        ticketAdapter.notifyDataSetChanged();

                        ticketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), AdminDetail.class);
                                AdminDetail.ticket = (Ticket) parent.getItemAtPosition(position);
                                startActivity(intent);
                            }
                        });

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
        }else {
            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/ticket";///user/ + LoginActivity.id;
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        ticketArrayList.clear();
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            String id = object.getString("id");
                            String title = object.getString("title");
                            String startDate = object.getString("startDate");
                            String endDate = object.getString("endDate");
                            String description = object.getString("description");
                            String place =  object.getString("place");
                            String userId = object.getString("userId");
                            String fullName = object.getString("fullName");
                            String technicianId = object.getString("technicianId");
                            String technicianName = object.getString("technicianName");
                            String modifiedBy = object.getString("modifiedBy");;
                            ArrayList<JSONObject> arrStatus = new ArrayList<>();
                            ArrayList<String> arrImage = new ArrayList<>();
                            ArrayList<String> arrComment = new ArrayList<>();
                            JSONArray jsonArrStatus = object.getJSONArray("status");
                            JSONArray jsonArrComment = object.getJSONArray("comment");
                            JSONArray jsonArrImage = object.getJSONArray("images");
                            for (int j = 0;j<jsonArrStatus.length();j++){
                                JSONObject objStatus = jsonArrStatus.getJSONObject(j);
                                arrStatus.add(objStatus);
                            };
                            for (int j = 0;j<jsonArrImage.length();j++){
                                String strImage = jsonArrImage.getString(j) ;
                                arrImage.add(strImage);
                            };
                            for (int j = 0;j<jsonArrComment.length();j++){
                                String strComment = jsonArrComment.getString(j) ;
                                arrComment.add(strComment);
                            }
                            ticketArrayList.add(new Ticket(i,id,title,startDate,endDate,description,place,userId,fullName,technicianId,technicianName,modifiedBy,arrStatus,arrImage,arrComment));
                        }
                        ticketAdapter.notifyDataSetChanged();

                        ticketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), AdminDetail.class);
                                AdminDetail.ticket = (Ticket) parent.getItemAtPosition(position);
                                startActivity(intent);
                            }
                        });

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
}
