package com.example.se2_android.DevicesTab;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_android.MainActivity;
import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Utils.Constant;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Calendar;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private static final int DEVICE_VIEWTYPE_DEFAULT = 1;
    private static final int DEVICE_VIEWTYPE_FAN = 2;
    private static final int DEVICE_VIEWTYPE_THERMOMETER = 3;
    private static final int DEVICE_VIEWTYPE_POWERSENSOR = 4;

    private ArrayList<Device> mItemList;
    private Context mContext;

    // Main ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDeviceTextView;
        public ImageView mDeviceImageView;
        public SwitchCompat mDeviceSwitch;
        public ImageView mImageViewTimer;

        public ViewHolder(View itemView) {
            super(itemView);
            mDeviceTextView = itemView.findViewById(R.id.deviceTextView);
            mDeviceImageView = itemView.findViewById(R.id.deviceImageView);
            mDeviceSwitch = itemView.findViewById(R.id.deviceSwitch);
            mImageViewTimer = itemView.findViewById(R.id.imageViewTimer);
        }
    }

    // --- Different ViewHolder Views ---------------


    // Fan ViewHolder
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

    // Thermometer ViewHolder
    public static class ViewHolderThermometer extends DevicesAdapter.ViewHolder {
        public TextView mDeviceTextView;
        public ImageView mDeviceImageView;
        public TextView mDeviceTextViewValue;

        public ViewHolderThermometer(View itemView) {
            super(itemView);
            mDeviceTextView = itemView.findViewById(R.id.deviceTextView);
            mDeviceImageView = itemView.findViewById(R.id.deviceImageView);
            mDeviceTextViewValue = itemView.findViewById(R.id.deviceTextViewValue);
        }
    }
    // --------------------------------------------

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
            case DEVICE_VIEWTYPE_POWERSENSOR:   // Use same as thermometer
            case DEVICE_VIEWTYPE_THERMOMETER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.devices_rv_item_thermometer, parent, false);
                return new ViewHolderThermometer(v);
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
            case "alarm":
                setSwitchListeners(holder, currentItem);
                holder.mImageViewTimer.setVisibility(View.INVISIBLE);

                if (currentItem.getValue() == 0) {          // Disarmed, no alarm
                    holder.mDeviceImageView.setColorFilter(Color.GRAY);
                    holder.mDeviceSwitch.setChecked(false);
                } else if (currentItem.getValue() == 1) {   // Armed, No alarm
                    holder.mDeviceImageView.setColorFilter(Color.GREEN);
                    holder.mDeviceSwitch.setChecked(true);
                } else if (currentItem.getValue() == 2) {   // Armed, Alarming
                    holder.mDeviceImageView.setColorFilter(Color.RED);
                    holder.mDeviceSwitch.setChecked(true);
                }

                // If all alarms are disarmed, disable switch
//                for (Device d : mItemList) {
//                    if (d.getType().equals("masteralarm")) {
//                        if(d.getValue() == 1) {
//                            Log.i("DevicesAdapter", "hmm");
//                            holder.mDeviceSwitch.setVisibility(View.INVISIBLE);
//                        } else {
//                            holder.mDeviceSwitch.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
                break;
//            case "masteralarm":
//                    // All alarms disarmed
//                if (currentItem.getValue() == 0) {
//                    holder.mDeviceImageView.setColorFilter(Color.GRAY);
//                    holder.mDeviceSwitch.setChecked(false);
//                } else if (currentItem.getValue() == 1) {
//                    holder.mDeviceImageView.setColorFilter(Color.GREEN);
//                    // If all alarms are armed AND If any alarm is alarming, display red
//                    for (Device d : mItemList) {
//                        if (d.getType().equals("alarm")) {
//                            if(d.getValue() == 2) {
//                                holder.mDeviceImageView.setColorFilter(Color.RED);
//                                break;
//                            }
//                        }
//                    }
//                    holder.mDeviceSwitch.setChecked(true);
//                }
//                setSwitchListeners(holder, currentItem);
//                break;
            case "thermometer":
                DevicesAdapter.ViewHolderThermometer thermometerHolder = (DevicesAdapter.ViewHolderThermometer) holder;
                thermometerHolder.mDeviceTextViewValue.findViewById(R.id.deviceTextViewValue);
                thermometerHolder.mDeviceTextViewValue.setText(String.valueOf(currentItem.getValue() +" C"));
                break;
            case "powersensor":
                DevicesAdapter.ViewHolderThermometer powerHolder = (DevicesAdapter.ViewHolderThermometer) holder;
                powerHolder.mDeviceTextViewValue.findViewById(R.id.deviceTextViewValue);
                double power = ((double) currentItem.getValue()) / 10;
                powerHolder.mDeviceTextViewValue.setText(String.valueOf(power +" W"));
                break;
            default:
                setSwitchListeners(holder, currentItem);
                break;
        }
    }

    private void setSwitchListeners(DevicesAdapter.ViewHolder holder, Device currentItem) {
        if (currentItem.getValue() == 0) {
            holder.mDeviceImageView.setColorFilter(Color.GRAY);
            holder.mDeviceSwitch.setChecked(false);
        } else {
            //TODO: Change to use theme colors
            holder.mDeviceImageView.setColorFilter(Color.GREEN);
            holder.mDeviceSwitch.setChecked(true);
        }

        if (!(currentItem.getType().equals("alarm"))) {
            holder.mImageViewTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, currentItem.getName() + " will turn off after 30 min", Toast.LENGTH_SHORT).show();
                    Calendar cal = Calendar.getInstance();
                    currentItem.setTimer(cal.getTimeInMillis() + Constant.TIMER_MILLIS);
                    ((MainActivity) mContext).changeDevice(currentItem, Constant.OPCODE_CHANGE_DEVICE_STATUS);
                }
            });
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
    }

    @Override
    public int getItemViewType(int position) {
        String type = mItemList.get(position).getType();

        switch (type) {
            case "fan":
                return DEVICE_VIEWTYPE_FAN;
            case "thermometer":
                return DEVICE_VIEWTYPE_THERMOMETER;
            case "powersensor":
                return DEVICE_VIEWTYPE_POWERSENSOR;
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
            case "fan":
                return R.drawable.fan;
            case "timer":
                return R.drawable.ic_baseline_timer_24;
            case "thermometer":
                return R.drawable.thermometer_lines;
            case "powersensor":
                return R.drawable.home_lightning_bolt;
            default:
                return R.drawable.ic_baseline_device_unknown_24;
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
