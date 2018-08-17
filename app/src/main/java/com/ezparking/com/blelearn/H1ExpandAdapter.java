package com.ezparking.com.blelearn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by zyh
 */

public class H1ExpandAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private String[][] subdatas;
    private String[] groupDatas;

    public H1ExpandAdapter(Context mContext, String[][] subdatas, String[] groupDatas) {
        this.mContext = mContext;
        this.subdatas = subdatas;
        this.groupDatas = groupDatas;
    }

    @Override
    public int getGroupCount() {
        return groupDatas.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return subdatas[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupDatas[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return subdatas[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder grViewholer = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expand_group,parent,false);
            grViewholer = new GroupViewHolder();
            grViewholer.tvTitle = convertView.findViewById(R.id.tv_group_title);
            grViewholer.ivIndicator = convertView.findViewById(R.id.iv_groupIndirtor);
            convertView.setTag(grViewholer);
        }else {
            grViewholer = (GroupViewHolder) convertView.getTag();
        }
       if(isExpanded){
            grViewholer.ivIndicator.setImageResource(R.drawable.arrow_down);
       }else {
           grViewholer.ivIndicator.setImageResource(R.drawable.arrow_right);
       }
        grViewholer.tvTitle.setText(groupDatas[groupPosition]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder viewHolder = null;
        if (convertView == null){
           convertView=  LayoutInflater.from(mContext).inflate(R.layout.item_expand_child,parent,false);
            viewHolder = new ChildViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_child_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }
         viewHolder.tvTitle.setText(subdatas[groupPosition][childPosition]);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView tvTitle;
        ImageView ivIndicator;
    }
    static class ChildViewHolder {
        TextView tvTitle;
    }

}
