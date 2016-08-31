package com.fitticket.viewmodel.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fitticket.R;

/**
 * Created by InFinItY on 11/25/2015.
 */
public class NavigationDrawerAdapter extends BaseAdapter {
    Context context;
    String[] listId;
    String [] iconId;
    TextView iconTextview;
    TextView listTextview;
    public NavigationDrawerAdapter(Context context, String[] pgmIcon, String[] pgmText) {
        listId=pgmText;
        iconId=pgmIcon;
        this.context=context;

    }
    @Override
    public int getCount() {
        return listId.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.navigation_item,null);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        iconTextview = (TextView)rowView.findViewById( R.id.navigation_icon_item);
        iconTextview.setTypeface(font);
        listTextview=(TextView)rowView.findViewById(R.id.navigation_text_item);
        iconTextview.setText(iconId[position]);
        listTextview.setText(listId[position]);
        return rowView;
    }
}

