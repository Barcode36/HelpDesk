package com.android.helpdesk;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LogAdapter extends BaseAdapter {
    ArrayList<Log> logsArrayList;

    public LogAdapter (ArrayList<Log> logsArrayList){
        this.logsArrayList = logsArrayList;
    }
    @Override
    public int getCount() {
        return logsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return logsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return logsArrayList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewLog;
        if (convertView == null) {
            viewLog = View.inflate(parent.getContext(), R.layout.log_row, null);
        } else viewLog = convertView;

        Log log = (Log) getItem(position);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String strTime = log.getTime();
        try {
            Date dateTime = formatter.parse(strTime);
            dateTime.setHours(dateTime.getHours()+7);
            String content = log.getContent()+" "+dateTime;
            ((TextView) viewLog.findViewById(R.id.text_view_log_row)).setText(content);
//            System.out.println(dateTime);
//            commentArrayList.add(new Comment(i,ticketId,userId,content,fullName,dateTime.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return viewLog;
    }

}
