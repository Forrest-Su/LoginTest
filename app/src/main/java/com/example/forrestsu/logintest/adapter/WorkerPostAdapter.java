package com.example.forrestsu.logintest.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.forrestsu.logintest.R;
import com.example.forrestsu.logintest.WorkerPost;
import com.example.forrestsu.logintest.utils.BmobDownload;
import com.example.forrestsu.logintest.utils.ImageViewUtil;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class WorkerPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "WorkerPostAdapter";

    private LayoutInflater layoutInflater;
    private Context context;
    private List<WorkerPost> workerList;
    static final int TYPE_NORMAL = 0;
    static final int TYPE_FOOT = 1;
    static final int NO_MORE = 0;
    static final int LOADING_MORE = 1;
    private int footer_state = 0;

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public WorkerPostAdapter(Context context, List<WorkerPost> workerList) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.workerList = workerList;
    }

    /*
    NormalViewHolder
    */
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        View normalView;
        CircleImageView headCIV;
        TextView workerNameTV;
        ImageView workerLevelIV;
        TextView workerAddressTV;

        NormalViewHolder(View view) {
            super(view);
            normalView = view;
            workerNameTV = (TextView)view.findViewById(R.id.tv_workerName);
            workerLevelIV = (ImageView) view.findViewById(R.id.iv_workerLevel);
            workerAddressTV = (TextView)view.findViewById(R.id.tv_workerAddress);
            headCIV = (CircleImageView) view.findViewById(R.id.civ_head);
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
            view = layoutInflater.inflate(R.layout.item_workerpost, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick((Integer) v.getTag());
                    }
                }
            });
            /*
            final NormalViewHolder holder = new NormalViewHolder(view);
            holder.normalView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    WorkerPost workerPost = workerList.get(position);
                    Log.d(TAG, "onClick: position = " + position);
                    String id = workerPost.getObjectId();
                    String name = workerPost.getWorkerName();
                    Log.d(TAG, "onCreate: id:" + id + " 姓名:" + name);
                    Intent intent = new Intent(v.getContext(), WorkerPageActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    v.getContext().startActivity(intent);
                }
            });
            */
            return new NormalViewHolder(view);
        } else {
            view = layoutInflater
                    .inflate(R.layout.recyclerview_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }


    @Override
    //绑定数据
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NormalViewHolder) {
            WorkerPost workerPost = workerList.get(position);
            String url = workerPost.getWorkerPhotoUrl();
            String province = workerPost.getProvince();
            String city = workerPost.getCity();
            String area = workerPost.getArea();
            String address;
            if (province.equals("台湾") || province.equals("澳门")
                    || province.equals("香港") || province.equals("北京")
                    || province.equals("天津") || province.equals("上海")
                    || province.equals("重庆") || province.equals("钓鱼岛")) {
                address = city + "." + area;
            } else {
                address = province + "." + city + "." + area;
            }
            String level = workerPost.getWorkerLevel();

            if (level.equals("普通护工")) {
                ((NormalViewHolder)holder).workerLevelIV.setImageResource(R.drawable.ic_level_simple);
            } else if (level.equals("高级护工")) {
                ((NormalViewHolder)holder).workerLevelIV.setImageResource(R.drawable.ic_level_senior);
            } else if (level.equals("护士")) {
                ((NormalViewHolder)holder).workerLevelIV.setImageResource(R.drawable.ic_level_nurse);
            }

            ((NormalViewHolder)holder).workerNameTV.setText(workerPost.getWorkerName());
            ((NormalViewHolder)holder).workerAddressTV.setText(address);
            ((NormalViewHolder)holder).itemView.setTag(position);

            //文本内容加载完成后再去处理网络图片（头像）
            //检查远程文件在是否在本地已存在，如果存在，直接显示，否则调用下载
            //存储路径
            final String photoSavePath = context.getExternalCacheDir() + "/img_head/";
            //获取远程文件名
            final String photoName = url.substring(url.lastIndexOf("/") + 1);
            Log.d(TAG, "onSuccess: 远程文件名：" + photoName);
            if (new File(photoSavePath + photoName).exists()) {
                Log.d(TAG, "文件不变，直接显示 ");
                ((NormalViewHolder)holder).headCIV.setImageURI(Uri.fromFile(
                        new File(photoSavePath + photoName)));
            } else {
                Log.d(TAG, "文件有变，调用下载");

                final BmobFile bmobFile = new BmobFile(photoName, "", url);
                final BmobDownload bmobDownload = new BmobDownload();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bmobDownload.downloadBmobFile(bmobFile, photoSavePath, photoName);
                    }
                }).start();

                bmobDownload.setBmobDownloadListener(new BmobDownload.BmobDownloadListener() {
                    @Override
                    public void onSuccess() {
                        ((NormalViewHolder)holder).headCIV.setImageURI(Uri.fromFile(
                                new File(photoSavePath + photoName)));
                    }

                    @Override
                    public void onFailed() {
                        //
                    }
                });
            }

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
        if (workerList.size() > 0)
            return workerList.size() + 1;
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
