package com.ptkebonagung.gotani.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.model.SPTA;
import com.ptkebonagung.gotani.model.TEBANGAN;

import java.util.ArrayList;
import java.util.List;

public class AdapterListTebangan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<TEBANGAN> itemTebangan = new ArrayList<>();
    public ArrayList<TEBANGAN> selected_items=new ArrayList<>();
    private Context ctx;

    @LayoutRes
    private int layoutID;


    public AdapterListTebangan(Context context, ArrayList<TEBANGAN> itemTebangan, ArrayList<TEBANGAN> selected_items, @LayoutRes int layout_id) {
        this.itemTebangan   = itemTebangan;
        this.selected_items = selected_items;
        this.ctx            = context;
        this.layoutID       = layout_id;
    }

    public AdapterListTebangan(){}

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public View lyt_parent;
        public TextView title;
        public TextView no_spta;
        public TextView no_polisi;
        public TextView nilai_mbs;
        public TextView bobot_netto;
        public TextView tgl_keluar_timb;

        public OriginalViewHolder(View v) {
            super(v);
            lyt_parent      = v.findViewById(R.id.lyt_parent);
            title           = v.findViewById(R.id.title);
            no_spta         = v.findViewById(R.id.no_spta);
            no_polisi       = v.findViewById(R.id.no_polisi);
            nilai_mbs       = v.findViewById(R.id.nilai_mbs);
            bobot_netto     = v.findViewById(R.id.bobot_netto);
            tgl_keluar_timb = v.findViewById(R.id.tgl_keluar_timb);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view   = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        viewHolder  = new OriginalViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder view = (OriginalViewHolder) holder;

            TEBANGAN tebangan = itemTebangan.get(position);
            view.title.setText("Antrian "+tebangan.kdAntrian.toUpperCase());
            view.no_spta.setText(tebangan.noSpta.toUpperCase());
            view.no_polisi.setText(tebangan.nopol.toUpperCase());
            view.nilai_mbs.setText(tebangan.mbs.toUpperCase());
            int bobot = Integer.parseInt(tebangan.weightKw) - Integer.parseInt(tebangan.rafaksiKw);
            view.bobot_netto.setText(tebangan.weightKw.toUpperCase()+" => "+bobot);
            view.tgl_keluar_timb.setText(setTanggalKeluar(tebangan.dateOut.toUpperCase()));

            if(selected_items.contains(itemTebangan.get(position)))
                ((OriginalViewHolder) holder).lyt_parent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.list_item_selected_state));
            else
                ((OriginalViewHolder) holder).lyt_parent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.list_item_normal_state));
        }
    }

    private String setTanggalKeluar(String dateOut) {
        String result;

        if (dateOut == "" || dateOut == null){
            result = "-";
        } else {
            String resultTemp[] = dateOut.split(" ");
            String timeStamp    = resultTemp[1];
            String dateTemp[]   = resultTemp[0].split("-");
            String date         = dateTemp[2].concat("-").concat(dateTemp[1]).concat("-").concat(dateTemp[0]);
            result              = timeStamp.concat(" ").concat(date);
        }

        return result;
    }


    @Override
    public int getItemCount() {
        return itemTebangan.size();
    }

    public void addMoreTebangan(List<TEBANGAN> otherTebangan){
        int sizeData = otherTebangan.size();

        for (int i=0; i < sizeData; i++){
            TEBANGAN tebangan   = new TEBANGAN();
            tebangan.jenisTebang= otherTebangan.get(i).jenisTebang;

            tebangan.tglMasuk   = otherTebangan.get(i).tglMasuk;
            tebangan.dateOut    = otherTebangan.get(i).dateOut;

            tebangan.noSpta     = otherTebangan.get(i).noSpta;
            tebangan.kdAntrian  = otherTebangan.get(i).kdAntrian;

            tebangan.noRegister = otherTebangan.get(i).noRegister;
            tebangan.petani     = otherTebangan.get(i).petani;

            tebangan.weightIn   = otherTebangan.get(i).weightIn;
            tebangan.weightOut  = otherTebangan.get(i).weightOut;

            tebangan.weightNet  = otherTebangan.get(i).weightNet;
            tebangan.weightKw   = otherTebangan.get(i).weightKw;

            tebangan.rafaksiKw  = otherTebangan.get(i).rafaksiKw;
            tebangan.mbs        = otherTebangan.get(i).mbs;

            tebangan.nopol      = otherTebangan.get(i).nopol;
            tebangan.varietas   = otherTebangan.get(i).varietas;
            tebangan.no_meja   = otherTebangan.get(i).no_meja;

            itemTebangan.add(tebangan);
        }

        notifyDataSetChanged();
    }

    public void clearData(){
        itemTebangan.clear();
    }

}
