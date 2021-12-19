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
import com.example.se2_android.Utils.Constant;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Locale;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private static final int DEVICE_VIEWTYPE_DEFAULT = 1;
    private static final int DEVICE_VIEWTYPE_FAN = 2;

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

    public static class ViewHolderFan extends DevicesAdapter.ViewHolder {
        public TextView mDeviceTextView;
        public ImageView mDeviceImageView;
        public Slider mDeviceSlider;

        public ViewHolderFan(View itemView) {
            super(itemView);
            mDeviceTextView = itemView.findViewById(R.id.deviceTextView);
            mDeviceImageView = itemView.findViewById(R.id.deviceImageView);
            mDeviceSlider = itemView.findViewById(R.id.deviceSlider);
        }
    }

    public DevicesAdapter(ArrayList<Device> itemList, Context context) {
        mItemList = itemList;
        mContext = context;
    }

    @NonNull
    @Override
    public DevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            case DEVICE_VIEWTYPE_FAN:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.devices_rv_item_fan, parent, false);
                return new ViewHolderFan(v);
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.devices_rv_item, parent, false);
                return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesAdapter.ViewHolder holder, int position) {
        Device currentItem = mItemList.get(position);
        holder.mDeviceImageView.setImageResource(deviceImagePicker(currentItem.getType()));
        holder.mDeviceTextView.setText(currentItem.getName());
        switch (currentItem.getType()) {
            case "fan":
                DevicesAdapter.ViewHolderFan fanHolder = (DevicesAdapter.ViewHolderFan) holder;
                fanHolder.mDeviceSlider.findViewById(R.id.deviceSlider);
                fanHolder.mDeviceSlider.setLabelFormatter(value -> "" + (int) value);
                if (currentItem.getValue() >= 0 && currentItem.getValue() <= 255) {
                    fanHolder.mDeviceSlider.setValue((float) currentItem.getValue());
                }

                fanHolder.mDeviceSlider.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                        if (currentItem.getValue() != (int) value) {
                            currentItem.setValue((int) value);
                            ((MainActivity) mContext).changeDevice(currentItem, Constant.OPCODE_CHANGE_DEVICE_STATUS);
                        }

                    }
                });
                break;
            default:
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
                        boolean checked = ((SwitchCompat) v).isChecked();
                        if (checked) {
                            currentItem.setValue(1);
                        } else {
                            currentItem.setValue(0);
                        }
                        ((MainActivity) mContext).changeDevice(currentItem, Constant.OPCODE_CHANGE_DEVICE_STATUS);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        String type = mItemList.get(position).getType();

        switch (type) {
            case "fan":
                return DEVICE_VIEWTYPE_FAN;
            default:
                return DEVICE_VIEWTYPE_DEFAULT;
        }
    }

    private int deviceImagePicker(String type) {
        switch (type) {
            case "lamp":
                return R.drawable.ic_baseline_lightbulb_48;
            case "element":
                return R.drawable.ic_radiator;
            case "alarm":
                return R.drawable.ic_alarm_light;
            case "timer":
                return R.drawable.ic_baseline_timer_24;
            default:
                return R.drawable.ic_baseline_device_unknown_24;
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
