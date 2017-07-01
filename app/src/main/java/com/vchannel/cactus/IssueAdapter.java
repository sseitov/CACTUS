package com.vchannel.cactus;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by sseitov on 01.07.17.
 */

public class IssueAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<Issue> objects;

    IssueAdapter(Context context, ArrayList<Issue> issues) {
        ctx = context;
        objects = issues;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    Issue getIssue(int position) {
        return ((Issue) getItem(position));
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.issue, parent, false);
        }

        Issue p = getIssue(position);

        ((TextView) view.findViewById(R.id.titleView)).setText(p.Title);
        ((TextView) view.findViewById(R.id.metaView)).setText(p.Meta);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        Picasso.with(ctx).load(p.Thumb).into(iv);

        return view;
    }
}
