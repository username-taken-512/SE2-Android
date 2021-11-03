package com.example.se2_android.DevicesTab;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_android.MainActivity;
import com.example.se2_android.Models.Device;
import com.example.se2_android.R;

import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private ArrayList<Device> mItemList;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDeviceTextView;
        public ImageView mDeviceImageView;
        public SwitchCompat mDeviceSwitch;

        public ViewHolder(View itemView) {
            super(itemView);
            mDeviceTextView = itemView.findViewById(R.id.deviceTextView);
            mDeviceImageView = itemView.findViewById(R.id.deviceImageView);
            mDeviceSwitch = itemView.findViewById(R.id.deviceSwitch);
        }
    }

    public DevicesAdapter(ArrayList<Device> itemList, Context context) {
        mItemList = itemList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices_rv_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device currentItem = mItemList.get(position);

        holder.mDeviceTextView.setText(currentItem.getName());
        if (currentItem.getValue() == 0) {
            holder.mDeviceImageView.setColorFilter(Color.GRAY);
            holder.mDeviceSwitch.setChecked(false);
        } else {
            //TODO: Change to use theme colors
            holder.mDeviceImageView.setColorFilter(Color.GREEN);
            holder.mDeviceSwitch.setChecked(true);
        }

        holder.mDeviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DevicesAdapter", "onCheckedChanged");
                boolean checked = ((SwitchCompat)v).isChecked();
                if (checked) {
                    currentItem.setValue(1);
                } else {
                    currentItem.setValue(0);
                }
                ((MainActivity) mContext).changeDevice(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
