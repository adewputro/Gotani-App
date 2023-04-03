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
import com.ptkebonagung.gotani.model.GILINGAN;

import java.util.ArrayList;
import java.util.List;

public class AdapterListGilingan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GILINGAN> itemGilingan = new ArrayList<>();
    private Context ctx;

    @LayoutRes
    private int layout_id;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, GILINGAN obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListGilingan(Context context, List<GILINGAN> gilinganItems, @LayoutRes int layout_id){
        this.ctx            = context;
        this.itemGilingan   = gilinganItems;
        this.layout_id      = layout_id;
    }

    public AdapterListGilingan(){}

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public View lyt_parent;
        public TextView titleItem;
        public TextView noSPTA;
        public TextView noPolisi;
        public TextView mbs;
        public TextView masukGilingan;

        public OriginalViewHolder(View v) {
            super(v);
            titleItem       = v.findViewById(R.id.title);
            noSPTA          = v.findViewById(R.id.no_spta);
            noPolisi        = v.findViewById(R.id.no_polisi);
            mbs             = v.findViewById(R.id.nilai_mbs);
            masukGilingan   = v.findViewById(R.id.tgl_masuk_gil);
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
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

            GILINGAN gilingan = itemGilingan.get(position);
            viewHolder.titleItem.setText("Antrian "+gilingan.kd_antrian.toUpperCase());
            viewHolder.noSPTA.setText(gilingan.no_spta.toUpperCase());
            viewHolder.noPolisi.setText(gilingan.nopol.toUpperCase());
            viewHolder.mbs.setText(gilingan.mbs.toUpperCase());
            viewHolder.masukGilingan.setText(setTanggalMasuk(gilingan.tgl_nilai));
        }
    }

    private String setTanggalMasuk(String tgl_nilai) {
        String result;

        if (tgl_nilai == null || tgl_nilai == ""){

            result = "-";

        } else {

            String resultTemp[] = tgl_nilai.split(" ");
            String time         = resultTemp[1];
            String dateTemp[]   = resultTemp[0].split("-");
            String date         = dateTemp[2].concat("-").concat(dateTemp[1]).concat("-").concat(dateTemp[0]);
            result              = time.concat(" ").concat(date);

        }

        return result.toUpperCase();
    }


    @Override
    public int getItemCount() {
        return itemGilingan.size();
    }

    public void addMoreDataGilingan(List<GILINGAN> listTimbanganAdder){
        for (int i=0; i < listTimbanganAdder.size(); i++){
            GILINGAN gilinganMore   = new GILINGAN();
            gilinganMore.kd_antrian = listTimbanganAdder.get(i).kd_antrian;
            gilinganMore.no_spta    = listTimbanganAdder.get(i).no_spta;
            gilinganMore.nopol      = listTimbanganAdder.get(i).nopol;
            gilinganMore.mbs        = listTimbanganAdder.get(i).mbs;
            gilinganMore.tgl_nilai  = listTimbanganAdder.get(i).tgl_nilai;
            itemGilingan.add(gilinganMore);
        }
        notifyDataSetChanged();
    }

    public void clearDataGilingan(){
        itemGilingan.clear();
    }
}
