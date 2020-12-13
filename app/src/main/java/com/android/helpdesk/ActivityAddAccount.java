package com.android.helpdesk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAddAccount extends AppCompatActivity {
    EditText txtFullName, txtEmail, txtUserName, txtPass, txtConfirm;
    Button btnAdd;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        alertDialog = new Dialog(this);
        txtFullName = (EditText) findViewById(R.id.text_fullname_acc);
        txtEmail = (EditText) findViewById(R.id.text_email_acc);
        txtPass = (EditText) findViewById(R.id.text_pass_acc);
        txtUserName = (EditText) findViewById(R.id.text_username_acc);
        txtConfirm = (EditText) findViewById(R.id.text_confirm_acc);
        btnAdd = (Button) findViewById(R.id.btnAddUser);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog();
               if (validate())
                    addRequest();
            }
        });
    }

    protected boolean validate() {
        if (
                txtFullName.getText().toString().equals("") ||
                        txtEmail.getText().toString().equals("") ||
                        txtUserName.getText().toString().equals("") ||
                        txtPass.getText().toString().equals("") ||
                        txtConfirm.getText().toString().equals("")
        ){  alertDialog.cancel();
            Toast.makeText(this, R.string.toast1, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(
                txtPass.getText().toString().equals(txtConfirm.getText().toString())
        ){

            return true;
        }else {
            alertDialog.cancel();
            Toast.makeText(this, R.string.toast2, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    protected void clearFields(){
        txtFullName.setText("");
        txtEmail.setText("");
        txtUserName.setText("");
        txtPass.setText("");
        txtConfirm.setText("");
    }

    protected void addRequest() {
        String postUrl = "https://helpdesk-v2-demo.herokuapp.com/v1/user";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("fullName", txtFullName.getText().toString());
            postData.put("email", txtEmail.getText().toString());
            postData.put("username", txtUserName.getText().toString());
            postData.put("password", txtPass.getText().toString());
            postData.put("role", "technician");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                alertDialog.cancel();
                openSuccessDialog();
                clearFields();
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

    public void openSuccessDialog() {
        alertDialog.setContentView(R.layout.success_dialog);
        Button button = alertDialog.findViewById(R.id.btn_success_ok);
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