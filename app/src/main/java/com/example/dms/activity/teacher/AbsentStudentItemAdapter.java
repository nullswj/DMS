package com.example.dms.activity.teacher;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dms.R;

import java.util.List;

public class AbsentStudentItemAdapter extends RecyclerView.Adapter<AbsentStudentItemAdapter.ViewHolder> implements View.OnClickListener
{

    private List<AbsentStudentItem> mabsenteeismList;

    private OnItemClickListener aItemClickListener;

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView pictureView;
        TextView nameView;
        TextView xuehaoView;
        TextView banjiView;


        public ViewHolder(View view)
        {
            super(view);
            pictureView = (ImageView) view.findViewById(R.id.item_modify_image);
            nameView = (TextView) view.findViewById(R.id.item_modify_sname);
            xuehaoView = (TextView) view.findViewById(R.id.item_modify_sno);
            banjiView = (TextView) view.findViewById(R.id.item_modify_sclass);
        }
    }

    public AbsentStudentItemAdapter(List<AbsentStudentItem> absentStudentItemList)
    {
        mabsenteeismList = absentStudentItemList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_layout_modify,parent,false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener((View.OnClickListener) this);
        return holder;
    }

    @Override
    public void onClick(View v)
    {
        if(aItemClickListener != null)
        {
            aItemClickListener.onItemClick((Integer)v.getTag());
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener)
    {
        aItemClickListener = itemClickListener;
    }
    public void removeItem(int position)
    {
        mabsenteeismList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


    public void onBindViewHolder(ViewHolder holder, int position)
    {
        AbsentStudentItem absentStudentItem = mabsenteeismList.get(position);
        holder.pictureView.setImageBitmap(absentStudentItem.getImageId());
        holder.nameView.setText(absentStudentItem.getName());
        holder.xuehaoView.setText(absentStudentItem.getNumber());
        holder.banjiView.setText(absentStudentItem.getClassname());
        holder.itemView.setTag(position);
    }

    public int getItemCount()
    {
        return mabsenteeismList.size();
    }
}
