package com.android.helpdesk;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TicketAdapter extends BaseAdapter {
    ArrayList<Ticket> ticketArrayList;

    public TicketAdapter (ArrayList<Ticket> ticketArrayList){
        this.ticketArrayList = ticketArrayList;
    }
    @Override
    public int getCount() {
        return ticketArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return ticketArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ticketArrayList.get(position).idTicket;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewTicket;
        if (convertView == null) {
            viewTicket = View.inflate(parent.getContext(), R.layout.ticket_row, null);
        } else viewTicket = convertView;

        Ticket ticket = (Ticket) getItem(position);
        ((TextView) viewTicket.findViewById(R.id.ticket_id)).setText(ticket.getId());
        ((TextView) viewTicket.findViewById(R.id.ticket_title)).setText(ticket.getTitle());
        JSONObject obj = null;
        try {
            obj = ticket.getStatus().get(ticket.getStatus().size()-1).getJSONObject("name");
            if(LoginActivity.vi){
                ((TextView) viewTicket.findViewById(R.id.ticket_status)).setText(obj.getString("vi"));
            }else {
                ((TextView) viewTicket.findViewById(R.id.ticket_status)).setText(obj.getString("en"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return viewTicket;
    }
}
