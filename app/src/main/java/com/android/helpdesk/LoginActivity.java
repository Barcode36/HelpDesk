package com.android.helpdesk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public static String id;
    public static boolean vi = false;
    public  static String name,role;
    public Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText txtUsername = (EditText) findViewById(R.id.txtUsername);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final Switch switchVi = (Switch) findViewById(R.id.switch_vi);
        switchVi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btnLogin.setText("Đăng nhập");
                    setAppLocale("vi");
                    vi = true;
                }else {
                    btnLogin.setText("Login");
                    setAppLocale("en");
                    vi = false;
                }
            }
        });
        alertDialog = new Dialog(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loading = builder.create();
                //loading.show();
                loadingDialog();
                logIn(txtUsername.getText().toString(), txtPassword.getText().toString());
            }
        });
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

        protected void logIn(String username, String password) {
        String postUrl = "https://helpdesk-v2-demo.herokuapp.com/v1/login";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                alertDialog.cancel();
                try {
                    id = response.getString("id");
                    name = response.getString("fullName");
                    role = response.getString("role");
                    if(role.equals("technician")){
                        Intent intent = new Intent(LoginActivity.this, TechnicianActivity.class);
                        startActivity(intent);
                    }else if(role.equals("user")){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);
//                        Toast.makeText(LoginActivity.this, "Vui lòng đăng nhập bằng Web cho Admin", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                openErrorDialog();
            }
        });
        requestQueue.add(jsonObjectRequest);
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

    public void loadingDialog() {
        alertDialog.setContentView(R.layout.loading_dialog);
        alertDialog.show();
    }
}