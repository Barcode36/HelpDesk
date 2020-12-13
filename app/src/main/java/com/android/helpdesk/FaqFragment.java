package com.android.helpdesk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FaqFragment extends Fragment {
    ArrayList<FAQ> faqArrayList;
    FaqAdapter faqAdapter;
    ListView faqListView;
    EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        faqArrayList = new ArrayList<>();
        faqListView = view.findViewById(R.id.faq_listview);
        getFAQ();
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

    public void getFAQ() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://helpdesk-v2-demo.herokuapp.com/v1/faq";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String res = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id = object.getInt("id");
                        JSONObject getquestion = object.getJSONObject("question");
                        JSONObject getanswer = object.getJSONObject("answer");
                        if(LoginActivity.vi){
                            String question = getquestion.getString("vi");
                            String answer = getanswer.getString("vi");
                            faqArrayList.add(new FAQ(id, question, answer));
                        }else {
                            String question = getquestion.getString("en");
                            String answer = getanswer.getString("en");
                            faqArrayList.add(new FAQ(id, question, answer));
                        }
                    }
                    faqAdapter = new FaqAdapter(faqArrayList);
                    faqListView.setAdapter(faqAdapter);
                    faqListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            FAQ faq = (FAQ) parent.getItemAtPosition(position);
                            showFaqDialog(getView(), faq.getAnswer());
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    @SuppressLint("WrongConstant")
    public void showFaqDialog(View view, final String answer) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Answer");

        // set the custom layout
        final View customDialog = getLayoutInflater().inflate(R.layout.faq_dialog, null);
        builder.setView(customDialog);

        TextView txtAnswer = customDialog.findViewById(R.id.faq_answer);
        txtAnswer.setText(answer);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
