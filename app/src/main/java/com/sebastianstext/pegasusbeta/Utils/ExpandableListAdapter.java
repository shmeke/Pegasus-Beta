package com.sebastianstext.pegasusbeta.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sebastianstext.pegasusbeta.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHandler;
    private HashMap<String,List<String>> listHashMap;

    public ExpandableListAdapter(Context context, List<String> listDataHandler, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.listDataHandler = listDataHandler;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listDataHandler.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHandler.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHandler.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataHandler.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String)getGroup(i);
        if(view == null){
            LayoutInflater lf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = lf.inflate(R.layout.list_group, null);
        }

        TextView listHeader = (TextView) view.findViewById(R.id.txtListHeader);
        listHeader.setTypeface(null, Typeface.BOLD);
        listHeader.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String) getChild(i, i1);
        if(view == null){
            LayoutInflater lf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = lf.inflate(R.layout.list_items, null);
        }

        TextView listItemOne = (TextView) view.findViewById(R.id.txtListItemLeft);
        TextView listItemTwo = (TextView) view.findViewById(R.id.txtListItemRight);
        listItemOne.setText(childText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
