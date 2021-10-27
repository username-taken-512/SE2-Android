package com.example.se2_android.DevicesTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_android.Models.Device;
import com.example.se2_android.R;

import java.util.ArrayList;

public class EditDeviceAdapter extends RecyclerView.Adapter<EditDeviceAdapter.MyViewHolder> {
    private final ArrayList<Device> devices;

    public EditDeviceAdapter(ArrayList<Device> deviceArrayList){
        devices = deviceArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_devices_rv_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.deviceName.setText(devices.get(position).getDeviceName());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("devName", devices.get(position).getDeviceName());
                bundle.putString("devType" , devices.get(position).getDeviceType());
                bundle.putInt("devID", devices.get(position).getDeviceId());
                Navigation.findNavController(v).navigate(R.id.action_editDeviceFragment_to_changeDeviceInfoFragment, bundle);



            }
        });

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        ImageButton editButton;
        ImageView deviceImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.textViewEditDevice);
            editButton = itemView.findViewById(R.id.imageButtonRecycler);
            deviceImage = itemView.findViewById(R.id.editDeviceImage);
        }
    }
}
