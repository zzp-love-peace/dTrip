package com.zzp.dtrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zzp.dtrip.R;
import com.zzp.dtrip.data.DataMessage;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<DataMessage.DataDTO> data;
    private int sum;

    public DataAdapter(List<DataMessage.DataDTO> data) {
        this.data = data;
        for (int i = 0; i < data.size(); i++) sum += data.get(i).getMileage();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView transportation;
        TextView mileages;
        TextView location;
        TextView time;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            transportation = itemView.findViewById(R.id.transportation_imageview);
            mileages = itemView.findViewById(R.id.mileage);
            location = itemView.findViewById(R.id.location);
            time = itemView.findViewById(R.id.time);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DataMessage.DataDTO message = data.get(position);
        holder.mileages.setText(message.getMileage() + "公里");
        holder.location.setText(message.getLocation());
        holder.time.setText(message.getTime());
        int progress = (int) (1000.0 * message.getMileage() / sum);
        holder.progressBar.setProgress(progress);
        if (message.getType().equals("公交"))
            holder.transportation.setBackgroundResource(R.drawable.bus);
        if (message.getType().equals("步行"))
            holder.transportation.setBackgroundResource(R.drawable.walk);
        if (message.getType().equals("骑行"))
            holder.transportation.setBackgroundResource(R.drawable.bike);
        if (message.getType().equals("乘车"))
            holder.transportation.setBackgroundResource(R.drawable.car);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}