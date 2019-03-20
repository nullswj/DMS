package com.example.dms.activity.teacher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.dms.R;


import java.util.List;

public class StudentItemAdapter extends RecyclerView.Adapter<StudentItemAdapter.ViewHolder>
{
    private List<StudentItem> studentItemList;
    private OnItemClickListener mClickListener;

    public StudentItemAdapter(List<StudentItem> studentItemList1)
    {
        studentItemList = studentItemList1;
    }

    @Override
    public int getItemCount() {
        return studentItemList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        StudentItem studentItem = studentItemList.get(i);
        holder.collegeView.setText(studentItem.getM_college());
        holder.nameView.setText(studentItem.getM_name());
        holder.banjiView.setText(studentItem.getM_classname());
        holder.xuehaoView.setText(studentItem.getM_number());
        holder.m_image.setImageResource(studentItem.getM_image());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_layout_student,parent,false);
        ViewHolder holder = new ViewHolder(view,mClickListener);
        return holder;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener1)
    {
        mClickListener = itemClickListener1;
    }



    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView nameView;
        TextView xuehaoView;
        TextView banjiView;
        TextView collegeView;
        ImageView m_image;

        private OnItemClickListener ItemClickListener;


        public ViewHolder(View view,OnItemClickListener listener)
        {
            super(view);
            nameView = view.findViewById(R.id.item_student_sname);
            xuehaoView = view.findViewById(R.id.item_student_sno);
            banjiView =  view.findViewById(R.id.item_student_class);
            collegeView = view.findViewById(R.id.item_student_college);
            m_image = view.findViewById(R.id.item_modify_right_arrow);
            ItemClickListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ItemClickListener.onItemClick(v,getPosition());
        }
    }




    public void removeItem(int position)
    {
        studentItemList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }




}
