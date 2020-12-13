package com.android.helpdesk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


public class AdminDetail extends AppCompatActivity {
    public static Ticket ticket;
    private TextView txtTitle, txtStartDate, txtEndDate, txtAssigned, txtStatus, txtPlace, txtInput, txtId;
    private Button btnComment, btnClose,btnAssign;
    ArrayList<Comment> commentArrayList;
    CommentAdapter commentAdapter;
    ListView commentListView;
    public Dialog alertDialog;
    ArrayList<Technician> technicianArrayList;
    TechnicianAdapter technicianAdapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail);
        spinner = (Spinner) findViewById(R.id.spinner);
        txtId = (TextView) findViewById(R.id.detail_id);
        txtTitle = (TextView) findViewById(R.id.detail_title);
        txtStartDate = (TextView) findViewById(R.id.detail_start_date);
        txtEndDate = (TextView) findViewById(R.id.detail_end_date);
        txtAssigned = (TextView) findViewById(R.id.detail_assigned);
        txtStatus = (TextView) findViewById(R.id.detail_status);
        txtPlace = (TextView) findViewById(R.id.detail_place);
        txtInput = (TextView) findViewById(R.id.txtInput);
        btnComment = (Button) findViewById(R.id.btnComment);
        btnClose = (Button) findViewById(R.id.btnCloseTicket);
        commentListView = (ListView) findViewById(R.id.list_comment);
        commentArrayList = new ArrayList<>();
        technicianArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentArrayList);
        commentListView.setAdapter(commentAdapter);
        alertDialog = new Dialog(this);
        btnAssign =(Button) findViewById(R.id.btnAssign);
        btnAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openAssignWarningDialog();
            }
        });
        loadSpinner();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openCloseWarningDialog();
            }
        });
        try {
            loadTicketDetail(ticket);
            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    if (!txtInput.getText().toString().trim().equals("")) {
                        putRequest(new Comment(0, ticket.getId(), LoginActivity.id, txtInput.getText().toString(), LoginActivity.name, dtf.format(now)));
                        txtInput.setText("");
                    } else {
                        Toast.makeText(AdminDetail.this, R.string.ticket_comment_toast, Toast.LENGTH_SHORT).show();
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

    void loadSpinner(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://helpdesk-v2-demo.herokuapp.com/v1/user/role/technician";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String idTech = object.getString("id");
                        String username = object.getString("username");
                        String password = object.getString("password");
                        String role = object.getString("role");
                        String fullname = object.getString("fullName");
                        String email = object.getString("email");
                        technicianArrayList.add(new Technician(i,idTech,username,password,fullname,role,email));
                    }
                    technicianAdapter = new TechnicianAdapter(getApplicationContext(),technicianArrayList);
                    spinner.setAdapter(technicianAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Technician technician = (Technician) parent.getItemAtPosition(position);
                            System.out.println(technician.fullname);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void openCloseWarningDialog() {
        alertDialog.setContentView(R.layout.warning_dialog);
        Button button = alertDialog.findViewById(R.id.btn_warning_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(ticket.getId());
                alertDialog.cancel();
                loadingDialog();
            }
        });
        alertDialog.show();
    }
    public void openAssignWarningDialog() {
        alertDialog.setContentView(R.layout.warning_dialog);
        Button button = alertDialog.findViewById(R.id.btn_warning_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTicketById(ticket.getId());
                alertDialog.cancel();
                loadingDialog();
            }
        });
        alertDialog.show();
    }

    public void loadingDialog() {
        alertDialog.setContentView(R.layout.loading_dialog);
        alertDialog.show();
    }

    public void openErrorDialog() {
        alertDialog.setContentView(R.layout.error_dialog);
        Button button = alertDialog.findViewById(R.id.btn_error_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void openSuccessDialog() {
        alertDialog.setContentView(R.layout.success_dialog);
        Button button = alertDialog.findViewById(R.id.btn_success_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                Intent intent = new Intent(AdminDetail.this,AdminActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.show();
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
//        txtStartDate.setText(ticket.getStartDate());
//        txtEndDate.setText(ticket.getEndDate());
        txtAssigned.setText(ticket.getTechnicianName());
        if (LoginActivity.vi) {
            txtStatus.setText(ticket.getStatus().get(ticket.getStatus().size() - 1).getJSONObject("name").getString("vi"));
        } else {
            txtStatus.setText(ticket.getStatus().get(ticket.getStatus().size() - 1).getJSONObject("name").getString("en"));
        }
        txtPlace.setText(ticket.getPlace());
    }

    public void putRequest(Comment comment) {
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

    public void putRequest(JSONObject object) {
        String postUrl = "https://helpdesk-v2-demo.herokuapp.com/v1/ticket/" + ticket.getId();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, postUrl, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                openSuccessDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                openErrorDialog();
                error.printStackTrace();
            }
        });
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void getTicketById(String id) {
        String url = "https://helpdesk-v2-demo.herokuapp.com/v1/ticket/" + id;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    JSONObject ticket = new JSONObject(res);
                    JSONArray getStatus = new JSONArray();
                    JSONObject name = new JSONObject();
                    JSONObject object = new JSONObject();
                    name.put("vi", "Đã phân công");
                    name.put("en", "Assigned");
                    object.put("name", name);
                    object.put("time", "2020-10-26T14:27:51.817+00:00");
                    getStatus.put(object);
                    Object technician = spinner.getSelectedItem();
                    Technician tech = (Technician) technician;
                    String technicianId = tech.getIdTech();
                    ticket.put("status", getStatus);
                    ticket.put("modifiedBy",technicianId);
                    ticket.put("technicianId",technicianId);
                    ticket.put("technicianName",tech.getFullname());
                    //ticket.put("endDate","2020-10-27T00:46:27.635Z");
                    ticket.remove("id");
                    putRequest(ticket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                openErrorDialog();
            }
        });
        queue.add(stringRequest);
    }

    public void close(String id) {
        String url = "https://helpdesk-v2-demo.herokuapp.com/v1/ticket/" + id;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    JSONObject ticket = new JSONObject(res);
                    JSONArray getStatus = new JSONArray();
                    JSONObject name = new JSONObject();
                    JSONObject object = new JSONObject();
                    name.put("vi", "Đã đóng");
                    name.put("en", "Closed");
                    object.put("name", name);
                    object.put("time", "2020-10-26T14:27:51.817+00:00");
                    getStatus.put(object);
                    Object technician = spinner.getSelectedItem();
                    Technician tech = (Technician) technician;
                    String technicianId = tech.getIdTech();
                    ticket.put("status", getStatus);
                    ticket.put("modifiedBy",LoginActivity.id);
                    //ticket.put("technicianId",technicianId);
                    //ticket.put("technicianName",tech.getFullname());
                    ticket.put("endDate","2020-10-27T00:46:27.635Z");
                    ticket.remove("id");
                    putRequest(ticket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                openErrorDialog();
            }
        });
        queue.add(stringRequest);
    }

    public void getRequest(final Context context) {
        if (commentArrayList.isEmpty()) {
            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/comment/ticket/" + ticket.getId();
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        for (int i = 0; i < array.length(); i++) {
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
                                commentArrayList.add(new Comment(i, ticketId, userId, content, fullName, dateTime.toString()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                            String strTime = time;
//                            try {
//                                Date dateTime = formatter.parse(strTime);
//                                dateTime.setHours(dateTime.getHours()+7);
//                                commentArrayList.add(new Comment(i,ticketId,userId,content,fullName,dateTime.toString()));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
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
        } else {
            String url = "https://helpdesk-v2-demo.herokuapp.com/v1/comment/ticket/" + ticket.getId();
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                        JSONArray array = new JSONArray(res);
                        commentArrayList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String s = object.getString("content");
                            String content =  Html.fromHtml(s).toString();
                            String fullName = object.getString("fullName");
                            String time = object.getString("time");
                            String ticketId = object.getString("ticketId");
                            String userId = object.getString("userId");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                            String strTime = time;
//                            try {
//                                Date dateTime = formatter.parse(strTime);
//                                commentArrayList.add(new Comment(i, ticketId, userId, content, fullName, dateTime.toString()));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
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
                    } catch (Exception ex) {
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