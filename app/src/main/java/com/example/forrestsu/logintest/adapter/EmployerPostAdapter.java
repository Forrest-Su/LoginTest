package com.example.forrestsu.logintest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.forrestsu.logintest.EmployerPost;
import com.example.forrestsu.logintest.R;

import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;


public class EmployerPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<EmployerPost> employerList;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOT = 1;
    private static final int NO_MORE = 0;
    private static final int LOADING_MORE = 1;
    private int footer_state = 0;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public EmployerPostAdapter(Context context, List<EmployerPost> employerList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.employerList = employerList;
    }

    /*
    NormalViewHolder
    */
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView workDateTV;
        TextView workDateToTV;
        TextView employerAddressTV;
        TextView employerPriceTV;

        NormalViewHolder(View view) {
            super(view);
            workDateTV = (TextView)view.findViewById(R.id.tv_show_work_date);
            employerAddressTV = (TextView)view.findViewById(R.id.tv_employer_address);
            employerPriceTV = (TextView)view.findViewById(R.id.tv_employer_price);
        }
    }

    /*
    FooterViewHolder
     */
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        View footerView;
        ProgressBar footerLoadPB;
        TextView footerLoadTV;

        FooterViewHolder(View view) {
            super(view);
            footerView = view.findViewById(R.id.footerload);
            footerLoadPB = (ProgressBar)view.findViewById(R.id.pb_footerload);
            footerLoadTV = (TextView)view.findViewById(R.id.tv_footerload);
        }
    }


    @Override
    //onCreateViewHolder根据不同的viewType返回不同的布局
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_NORMAL) {
            view = layoutInflater.inflate(R.layout.item_employerpost, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick((Integer) v.getTag());
                    }
                }
            });
            return new NormalViewHolder(view);
        } else {
            view = layoutInflater
                    .inflate(R.layout.recyclerview_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }


    @Override
    //绑定数据
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NormalViewHolder) {
            EmployerPost employerPost = employerList.get(position);
            String province = employerPost.getProvince();
            String city = employerPost.getCity();
            String area = employerPost.getArea();
            String address;
            if (province.equals("台湾") || province.equals("澳门")
                    || province.equals("香港") || province.equals("北京")
                    || province.equals("天津") || province.equals("上海")
                    || province.equals("重庆") || province.equals("钓鱼岛")) {
                address = city + "." + area;
            } else {
                address = province + "." + city + "." + area;
            }
            ((NormalViewHolder)holder).workDateTV.setText((employerPost.getWorkDateFrom() + " 至 "
                    + employerPost.getWorkDateTo()));
            ((NormalViewHolder)holder).employerAddressTV.setText(address);
            ((NormalViewHolder)holder).employerPriceTV.setText((employerPost.getEmployerPrice() + "元/小时"));
            ((NormalViewHolder)holder).itemView.setTag(position);

        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            if (position == 0) {
                footerViewHolder.footerLoadPB.setVisibility(GONE);
                footerViewHolder.footerLoadTV.setVisibility(GONE);
            }
            switch (footer_state) {
                case NO_MORE:
                    footerViewHolder.footerLoadPB.setVisibility(GONE);
                    footerViewHolder.footerLoadTV.setText("");
                    break;
                case LOADING_MORE:
                    footerViewHolder.footerLoadPB.setVisibility(View.VISIBLE);
                    footerViewHolder.footerLoadTV.setText(R.string.loading);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    //返回item数量时要多返回一个item，作为显示加载更多的item
    public int getItemCount() {
        if (employerList.size() > 0)
            return employerList.size() + 1;
        else
            return 0;
    }

    @Override
    //加载到最后一个数据时，这个item为加载更多的那个item，返回不同的viewtype给onCreateViewHolder
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount())
            return TYPE_FOOT;
        else
            return TYPE_NORMAL;
    }

    public void changeFooterState(int footer_state) {
        this.footer_state = footer_state;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
