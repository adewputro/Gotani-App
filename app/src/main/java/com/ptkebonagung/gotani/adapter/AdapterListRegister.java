package com.ptkebonagung.gotani.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.model.GILINGAN;
import com.ptkebonagung.gotani.model.REGISTER;

import java.util.ArrayList;
import java.util.List;

public class AdapterListRegister extends ArrayAdapter {
    private List<REGISTER> reg = new ArrayList<>();
    private Context context;

    public AdapterListRegister(@NonNull Context context, List<REGISTER> register){
        super(context, R.layout.listview_register, register);
        this.reg = register;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.listview_register, null, true);
        TextView register = view.findViewById(R.id.daftarRegister);
        TextView petani = view.findViewById(R.id.petani);
        TextView lokasi = view.findViewById(R.id.lokasi);
        TextView jenis = view.findViewById(R.id.jenis);

        REGISTER register_id = reg.get(position);

        register.setText(register_id.no_register);
        petani.setText(register_id.petani);
        lokasi.setText(register_id.lokasi);
        jenis.setText(register_id.jenis);
        if (register_id.jenis.equals("SPT")){
            register.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark));
            petani.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark));
            lokasi.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark));
            jenis.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark));
        }
        return view;
    }
}
