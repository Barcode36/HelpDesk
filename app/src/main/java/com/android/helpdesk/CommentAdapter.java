package com.android.helpdesk;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends BaseAdapter {
    ArrayList<Comment> comments;

    public CommentAdapter(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comments.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewComment;
        if (convertView == null) {
            viewComment = View.inflate(parent.getContext(), R.layout.row_comment, null);
        } else viewComment = convertView;
        Comment comment = (Comment) getItem(position);
        String strDate = comment.getTime();
        ((TextView) viewComment.findViewById(R.id.comment_time)).setText(strDate);
        ((TextView) viewComment.findViewById(R.id.comment_name)).setText(comment.getFullName());
        ((TextView) viewComment.findViewById(R.id.comment_content)).setText(comment.getContent());
        return viewComment;
    }
}
