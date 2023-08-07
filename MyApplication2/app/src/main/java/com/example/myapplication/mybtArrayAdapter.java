package com.example.myapplication;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;


public class mybtArrayAdapter extends ArrayAdapter {
    private final ArrayList<BluetoothDevice> marraylist;

    public mybtArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BluetoothDevice> objects) {
        super(context, resource, objects);
        marraylist = objects;
    }

    @SuppressLint("MissingPermission")
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //       return super.getView(position, convertView, parent);
        View rowview = convertView;

        if (rowview  == null) {
            rowview =LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }

        TextView loc_tvaddress = rowview.findViewById(R.id.textAddress);
        TextView loc_tvname = rowview.findViewById(R.id.textName);
        TextView loc_tvalias = rowview.findViewById(R.id.textAlias);

        loc_tvaddress.setText(marraylist.get(position).getAddress());
        loc_tvname.setText(marraylist.get(position).getName());
        loc_tvalias.setText("Alias");

        return  rowview ;

    }
}
