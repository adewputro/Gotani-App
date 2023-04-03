package com.ptkebonagung.gotani.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ptkebonagung.gotani.R;
import com.ptkebonagung.gotani.model.TIMBANGAN;

import java.util.ArrayList;
import java.util.List;

public class AdapterListTimbangan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TIMBANGAN> itemTimbangan = new ArrayList<>();
    private Context ctx;

    @LayoutRes
    private int layout_id;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, TIMBANGAN obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListTimbangan(Context context, List<TIMBANGAN> items, @LayoutRes int layout_id) {
        this.itemTimbangan = items;
        ctx = context;
        this.layout_id = layout_id;
    }

    public AdapterListTimbangan(){}

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public View lyt_parent;
        public TextView no_antrian;
        public TextView no_spta;
        public TextView no_polisi;
        public TextView bobot_bruto;
        public TextView masuk_timbangan;

        public OriginalViewHolder(View v) {
            super(v);
            no_antrian      = v.findViewById(R.id.title);
            no_spta         = v.findViewById(R.id.no_spta);
            no_polisi       = v.findViewById(R.id.no_polisi);
            bobot_bruto     = v.findViewById(R.id.bobot_bruto);
            masuk_timbangan = v.findViewById(R.id.tgl_masuk_timb);
            lyt_parent      = v.findViewById(R.id.lyt_parent);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view   = LayoutInflater.from(parent.getContext()).inflate(layout_id, parent, false);
        viewHolder  = new OriginalViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof  OriginalViewHolder){
            OriginalViewHolder view = (OriginalViewHolder) holder;

            TIMBANGAN timbangan = itemTimbangan.get(position);
            view.no_antrian.setText("Antrian "+timbangan.kode_antrian.toUpperCase());
            view.no_spta.setText(timbangan.noSPTA.toUpperCase());
            view.no_polisi.setText(timbangan.nopol.toUpperCase());
            view.bobot_bruto.setText(timbangan.weightIn.toUpperCase());
            view.masuk_timbangan.setText(setTanggal(timbangan.dateIn));

            /*view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener == null) return;
                        mOnItemClickListener.onItemClick(view, itemTimbangan.get(position), position);
                }
            });*/
        }
    }

    private String setTanggal(String dateIn) {
        String result;

        if (dateIn == null || dateIn == ""){
            result = "-";
        } else {
            String resultTemp[] = dateIn.split(" ");
            String time         = resultTemp[1];
            String dateTemp[]   = resultTemp[0].split("-");
            String date         = dateTemp[2].concat("-").concat(dateTemp[1]).concat("-").concat(dateTemp[0]);
            result              = time.concat(" ").concat(date);
        }

        return result.toUpperCase();
    }

    @Override
    public int getItemCount() {
        return itemTimbangan.size();
    }

    public void addMoreDataTimbangan(List<TIMBANGAN> timbanganAdder){

        for (int i=0; i < timbanganAdder.size(); i++){
            TIMBANGAN moreTimbangan     = new TIMBANGAN();
            moreTimbangan.kode_antrian  = timbanganAdder.get(i).kode_antrian;
            moreTimbangan.noSPTA        = timbanganAdder.get(i).noSPTA;
            moreTimbangan.nopol         = timbanganAdder.get(i).nopol;
            moreTimbangan.weightIn      = timbanganAdder.get(i).weightIn;
            moreTimbangan.dateIn        = timbanganAdder.get(i).dateIn;
            itemTimbangan.add(moreTimbangan);
        }

        notifyDataSetChanged();
    }

    public void clearDataTimbangan(){
        itemTimbangan.clear();
    }
}
