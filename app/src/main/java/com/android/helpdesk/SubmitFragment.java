package com.android.helpdesk;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class SubmitFragment extends Fragment {
    String idEmployee;
    EditText txtTitle;
    EditText txtPlace;
    ImageView imageView;
    EditText txtDescription;
    Dialog alertDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_submit, container, false);
        txtTitle = view.findViewById(R.id.txtTitle);
        idEmployee = LoginActivity.id;
        txtPlace = view.findViewById(R.id.txtPlace);
        imageView = view.findViewById(R.id.imageView);
        Button btnSend = view.findViewById(R.id.btnSend);
        Button btnOpenFile = view.findViewById(R.id.btnChooseFile);
        txtDescription = view.findViewById(R.id.txtDescription);
        alertDialog = new Dialog(getContext());
        if(LoginActivity.vi){
            setAppLocale("vi");
        }
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BitmapDrawable bit = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = bit.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    String encodeImageString = Base64.encodeToString(b, Base64.NO_WRAP);
                    if(checkInput() == true){
                        loadingDialog();
                        postRequest(idEmployee, txtTitle.getText().toString(), txtPlace.getText().toString(), txtDescription.getText().toString(), encodeImageString);
                    }else Toast.makeText(getContext(), "Nhập tất cả các thông tin", Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Toast.makeText(getContext(), "Nhập tất cả các thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);

            }
        });
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

    public void postRequest(String idEmployee, String title, String place, String description, String image) {
        String postUrl = "https://helpdesk-v2-demo.herokuapp.com/v1/ticket";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject postData = new JSONObject();
        try {
            JSONArray jsonObject = new JSONArray();
            String arr[] = new String[1];
            for (int i = 0; i < 1; i++) {
                arr[i] = image;
                jsonObject.put(arr[i]);
            }

            postData.put("description", description);
            postData.put("fullName", LoginActivity.name );
            postData.put("images", jsonObject);
            postData.put("place", place);
            postData.put("title", title);
            postData.put("userId", idEmployee);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(getContext(), "Your ticket was sent!!!", Toast.LENGTH_LONG).show();
                alertDialog.cancel();
                openSuccessDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                openErrorDialog();
//                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStream inputStream, int reqWidth, int reqHeight) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            int n;
            byte[] buffer = new byte[1024];
            while ((n = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, n);
            }
            return decodeSampledBitmapFromByteArray(outputStream.toByteArray(), reqWidth, reqHeight);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    try {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = decodeSampledBitmapFromStream(inputStream, 100, 100);
                        imageView.setImageBitmap(bitmap);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the bitmap object
//                        byte[] b = baos.toByteArray();
//                        String encodeString = Base64.encodeToString(b,Base64.DEFAULT);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
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

    public boolean checkInput(){
        if(txtTitle.getText().length() < 1|| txtPlace.getText().length() < 1 || txtDescription.getText().length() < 1){
            return false;
        }else return true;
    }
}
