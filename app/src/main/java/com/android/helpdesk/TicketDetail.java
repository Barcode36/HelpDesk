package com.android.helpdesk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TicketDetail extends AppCompatActivity {
    public static Ticket ticket;
    private TextView txtTitle, txtStartDate, txtEndDate, txtAssigned, txtStatus, txtPlace,txtInput,txtId;
    private Button btnComment;
    ArrayList<Comment> commentArrayList;
    CommentAdapter commentAdapter;
    ListView commentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        txtId = (TextView) findViewById(R.id.detail_id);
        txtTitle = (TextView) findViewById(R.id.detail_title);
        txtStartDate = (TextView) findViewById(R.id.detail_start_date);
        txtEndDate = (TextView) findViewById(R.id.detail_end_date);
        txtAssigned = (TextView) findViewById(R.id.detail_assigned);
        txtStatus = (TextView) findViewById(R.id.detail_status);
        txtPlace = (TextView) findViewById(R.id.detail_place);
        txtInput = (TextView) findViewById(R.id.txtInput);
        btnComment = (Button)findViewById(R.id.btnComment);
        commentListView = (ListView) findViewById(R.id.list_comment);
        commentArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentArrayList);
        commentListView.setAdapter(commentAdapter);
        try {
            loadTicketDetail(ticket);
            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    if(!txtInput.getText().toString().trim().equals("")){
                        putRequest(new Comment(0,ticket.getId(),LoginActivity.id,txtInput.getText().toString(),LoginActivity.name,dtf.format(now)));
                        txtInput.setText("");
                    }else {
                        Toast.makeText(TicketDetail.this, R.string.ticket_comment_toast, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    getRequest(getApplicationContext());
                    handler.postDelayed(this, 2000);
                }
            };
            handler.postDelayed(r, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void loadTicketDetail(Ticket ticket) throws JSONException {
        txtId.setText(ticket.getId());
        txtTitle.setText(ticket.getTitle());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String startTime = ticket.getStartDate();
        String endTime = ticket.getEndDate();
        try {
            Date dateTime1 = formatter.parse(startTime);
            dateTime1.setHours(dateTime1.getHours()+7);
            txtStartDate.setText(dateTime1.toString());
            Date dateTime2 = formatter.parse(endTime);
            dateTime2.setHours(dateTime2.getHours()+7);
            txtEndDate.setText(dateTime2.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //txtStartDate.setText(ticket.getStartDate());
        //txtEndDate.setText(ticket.getEndDate());
        txtAssigned.setText(ticket.getTechnicianName());
        if(LoginActivity.vi){
            txtStatus.setText(ticket.getStatus().get(ticket.getStatus().size() - 1).getJSONObject("name").getString("vi"));
        }else {
            txtStatus.setText(ticket.getStatus().get(ticket.getStatus().size() - 1).getJSONObject("name").getString("en"));
        }
        txtPlace.setText(ticket.getPlace());
    }

    public void putRequest(Comment comment){
        String postUrl = "https://helpdesk-v2-demo.herokuapp.com/v1/comment/ticket/" + comment.getTicketId();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("content", comment.getContent());
            postData.put("fullName", comment.getFullName());
            postData.put("ticketId", comment.getTicketId());
            postData.put("userId", comment.getUserId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void getRequest(final Context context){
        if(commentArrayList.isEmpty()){
            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/comment/ticket/" + ticket.getId();
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            String s = object.getString("content");
                            String content =  Html.fromHtml(s).toString();
                            String fullName = object.getString("fullName");
                            String time = object.getString("time");
                            String ticketId = object.getString("ticketId");
                            String userId = object.getString("userId");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            String strTime = time;
                            try {
                                Date dateTime = formatter.parse(strTime);
                                dateTime.setHours(dateTime.getHours()+7);
                                commentArrayList.add(new Comment(i,ticketId,userId,content,fullName,dateTime.toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
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
            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/comment/ticket/" + ticket.getId();
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        commentArrayList.clear();
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            String s = object.getString("content");
                            String content =  Html.fromHtml(s).toString();
                            String fullName = object.getString("fullName");
                            String time = object.getString("time");
                            String ticketId = object.getString("ticketId");
                            String userId = object.getString("userId");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            String strTime = time;
                            try {
                                Date dateTime = formatter.parse(strTime);
                                dateTime.setHours(dateTime.getHours()+7);
                               // System.out.println(dateTime);
                                commentArrayList.add(new Comment(i,ticketId,userId,content,fullName,dateTime.toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
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