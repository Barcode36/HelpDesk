package com.android.helpdesk;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FaqAdapter extends BaseAdapter {
    private ArrayList<FAQ> faqArrayList;

    public FaqAdapter(ArrayList<FAQ> faqArrayList) {
        this.faqArrayList = faqArrayList;
    }

    @Override
    public int getCount() {
        return faqArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return faqArrayList.get(position).getIdFAQ();
    }

    @Override
    public Object getItem(int position) {
        return faqArrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewFAQ;
        if (convertView == null) {
            viewFAQ = View.inflate(parent.getContext(), R.layout.faq_row, null);
        } else viewFAQ = convertView;


        FAQ faq = (FAQ) getItem(position);
        ((TextView) viewFAQ.findViewById(R.id.row_question)).setText(faq.getQuestion());

        return viewFAQ;
    }
}
