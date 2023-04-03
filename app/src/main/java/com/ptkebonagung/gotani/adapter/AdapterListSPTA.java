package com.ptkebonagung.gotani.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.model.SPTA;


import java.util.ArrayList;
import java.util.List;

public class AdapterListSPTA extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<SPTA> items=new ArrayList<>();
    public ArrayList<SPTA> selected_items=new ArrayList<>();

    private Context ctx;

    @LayoutRes
    private int layout_id;

    public AdapterListSPTA(Context context, ArrayList<SPTA> items, ArrayList<SPTA> selected_items, @LayoutRes int layout_id) {
        this.items = items;
        this.selected_items = selected_items;
        ctx = context;
        this.layout_id = layout_id;
    }

    public AdapterListSPTA(){

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public View lyt_parent;
        public TextView no_spta;
        public TextView jenis_tebang;
        public TextView register_id;
        public TextView tanggal_berlaku_awal;
        public TextView tanggal_berlaku_akhir;

        public OriginalViewHolder(View v) {
            super(v);
            no_spta                 = v.findViewById(R.id.no_spta);
            register_id             = v.findViewById(R.id.register);
            tanggal_berlaku_awal    = v.findViewById(R.id.tgl_mulai);
            tanggal_berlaku_akhir   = v.findViewById(R.id.tgl_sampai);
            lyt_parent              = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            SPTA n = items.get(position);
            view.no_spta.setText(n.no_spta.toUpperCase());
            view.register_id.setText(n.register_id.toUpperCase());
            view.tanggal_berlaku_awal.setText(setTanggal(n.tanggal_berlaku_awal.toUpperCase()));
            view.tanggal_berlaku_akhir.setText(setTanggal(n.tanggal_berlaku_akhir.toUpperCase()));

            if(selected_items.contains(items.get(position)))
                ((OriginalViewHolder) holder).lyt_parent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.list_item_selected_state));
            else
                ((OriginalViewHolder) holder).lyt_parent.setBackgroundColor(ContextCompat.getColor(ctx, R.color.list_item_normal_state));

        }
    }

    private String setTanggal(String tgl) {
        String result = "";

        if(tgl == null || tgl == ""){
            result = "-";
        } else {
            String splitDate[]      = tgl.split(" ");
            String time             = splitDate[1];
            String dateStampTmp[]   = splitDate[0].split("-");
            String dateStamp        = dateStampTmp[2].concat("-").concat(dateStampTmp[1]).concat("-").concat(dateStampTmp[0]);
            result             = time.concat(" ").concat(dateStamp);
        }

        return result;
    }

    @Override
    public int getItemCount() {
            return items.size();
    }

    public void addMoreSPTA(List<SPTA> spta){

        for(int i=0; i < spta.size(); i++){
            SPTA sptaData                   = new SPTA();
            sptaData.no_spta                = spta.get(i).no_spta;
            sptaData.register_id            = spta.get(i).register_id;
            sptaData.tanggal_berlaku_awal   = spta.get(i).tanggal_berlaku_awal;
            sptaData.tanggal_berlaku_akhir  = spta.get(i).tanggal_berlaku_akhir;
            sptaData.name                   = spta.get(i).name;
            sptaData.jenis_tebang           = spta.get(i).jenis_tebang;
            items.add(sptaData);
        }

        notifyDataSetChanged();
    }

    public void clearSPTA(){
        items.clear();
    }
}