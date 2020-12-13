package com.android.helpdesk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TechnicianAdapter extends ArrayAdapter<Technician> {

    public TechnicianAdapter(@NonNull Context context, ArrayList<Technician> arrayAdapter) {
        super(context, 0,arrayAdapter);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
            View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent)
    {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_custom, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.text_view);
        Technician currentItem = getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            textViewName.setText(currentItem.getFullname());
        }
        return convertView;
    }

    @Nullable
    @Override
    public Technician getItem(int position) {
        return super.getItem(position);
    }
}
