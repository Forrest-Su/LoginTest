package com.example.forrestsu.logintest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.SubMenu;

import java.util.List;

public class SubMenuAdapter extends RecyclerView.Adapter<SubMenuAdapter.ViewHolder> {

    private List<SubMenu> subMenuList;
    private OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView contentTV;

        public ViewHolder(View view) {
            super(view);
            contentTV = (TextView) view.findViewById(R.id.tv_submenu_content);
        }
    }

    public SubMenuAdapter(List<SubMenu> subMenuList) {
       this.subMenuList = subMenuList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_submenu, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SubMenu subMenu = subMenuList.get(position);
        holder.contentTV.setText(subMenu.getContent());

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return subMenuList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
