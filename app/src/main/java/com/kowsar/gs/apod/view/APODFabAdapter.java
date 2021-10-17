package com.kowsar.gs.apod.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kowsar.gs.apod.R;
import com.kowsar.gs.apod.model.db.FavouriteDB;

import java.util.ArrayList;

public class APODFabAdapter extends RecyclerView.Adapter<APODFabAdapter.ViewHolder> {
    private final String TAG= this.getClass().getSimpleName();
    private ArrayList<APODItem> apodItems;
    private Context mContext;
    private FavouriteDB db;
    private ICommunication listner;

    public APODFabAdapter(ArrayList<APODItem> apodItems, Context mContext, ICommunication listner) {
        this.apodItems = apodItems;
        this.mContext = mContext;
        this.listner= listner;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db= new FavouriteDB(mContext);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav_apod, parent, false);
        return new ViewHolder(view, listner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        APODItem item= apodItems.get(position);
        holder.fabTitle.setText(item.getTitle());
        holder.fabBtn.setBackgroundResource(R.drawable.ic_favourite_selected);
        Glide.with(mContext)
                .asDrawable()
                .load(item.getThumbURL())
                .placeholder(R.drawable.default_apod_image_gs)
                .error(R.drawable.default_apod_image_gs)
                .into(holder.fabImg);

    }

    @Override
    public int getItemCount() {
        return apodItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView fabImg;
        TextView fabTitle;
        ImageButton fabBtn;
        ICommunication mListner;
        public ViewHolder(@NonNull View itemView, ICommunication listner) {
            super(itemView);
            mListner = listner;
            fabImg = itemView.findViewById(R.id.fav_img);
            fabImg.setOnClickListener(this);
            fabTitle = itemView.findViewById(R.id.fav_title);
            fabTitle.setOnClickListener(this);
            fabBtn = itemView.findViewById(R.id.fav_btn);
            fabBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fav_btn:
                    int pos= getAdapterPosition();
                    Log.d(TAG, "ViewHolder:OnItemClicked-->pos="+pos);
                    fabBtn.setBackgroundResource(R.drawable.ic_favourite_de_select);
                    listner.deleteFromFAV(apodItems.get(getAdapterPosition()));
                    apodItems.remove(pos);
                    notifyDataSetChanged();
                    break;
                case R.id.fav_title:
                case R.id.fav_img:
                    listner.favdetail(apodItems.get(getAdapterPosition()));
                    break;
            }
        }
    }

    interface ICommunication{
        public void deleteFromFAV(APODItem item);
        public void favdetail(APODItem item);
    }
}
