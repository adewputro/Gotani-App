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
import com.ptkebonagung.gotani.model.EMPLASEMEN;

import java.util.ArrayList;
import java.util.List;

public class AdapterListEmplasemen extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EMPLASEMEN> emplasemenItems = new ArrayList<>();
    private Context context;

    @LayoutRes
    private int layoutID;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, EMPLASEMEN obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListEmplasemen(Context ctx, List<EMPLASEMEN> items, @LayoutRes int layoutID){
        context             = ctx;
        this.emplasemenItems= items;
        this.layoutID       = layoutID;
    }

    public AdapterListEmplasemen(){}

    public class OriginalViewHolder extends RecyclerView.ViewHolder{

        public View layout_parent;
        public TextView noAntrian;
        public TextView noSPTA;
        public TextView no_polisi;
        public TextView register_;
        public TextView masuk_emplasemen;
        public TextView tgl_estimasi;

        public OriginalViewHolder(@NonNull View v) {
            super(v);
            layout_parent   = v.findViewById(R.id.lyt_parent);
            noAntrian       = v.findViewById(R.id.title);
            noSPTA          = v.findViewById(R.id.no_spta);
            register_       = v.findViewById(R.id.register);
            no_polisi       = v.findViewById(R.id.no_polisi);
            masuk_emplasemen= v.findViewById(R.id.tgl_masuk_kajar);
            tgl_estimasi    = v.findViewById(R.id.tgl_est);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        viewHolder= new OriginalViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder view = (OriginalViewHolder) holder;

            EMPLASEMEN emplasemenObj = emplasemenItems.get(position);
            view.noAntrian.setText(setNoAntrian(emplasemenObj.kd_antrian));
            view.noSPTA.setText(emplasemenObj.nospta);
            view.register_.setText(emplasemenObj.no_register);
            view.no_polisi.setText(emplasemenObj.no_pol);
            view.masuk_emplasemen.setText(setTanggalAntrian(emplasemenObj.tgl_antrian));
            view.tgl_estimasi.setText(setTanggalEstimasi(emplasemenObj.tgl_estimasi));
            /*view.layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener == null) return;
                    mOnItemClickListener.onItemClick(view, emplasemenItems.get(position), position);
                }
            });*/
        }
    }

    private String setNoAntrian(String no_antrian) {
        String nomorAntrian = "";

        if (no_antrian == null){
            nomorAntrian = "Antrian -";
        } else {
            nomorAntrian = "Antrian "+no_antrian;
        }

        return nomorAntrian;
    }

    @Override
    public int getItemCount() {
        return emplasemenItems.size();
    }

    private String setTanggalAntrian(String tglAntrian) {
        String tgl_antrian = "";

        if(tglAntrian == null || tglAntrian == ""){
            tgl_antrian = "-";
        } else {
            String splitDate[]      = tglAntrian.split(" ");
            String time             = splitDate[1];
            String dateStampTmp[]   = splitDate[0].split("-");
            String dateStamp        = dateStampTmp[2].concat("-").concat(dateStampTmp[1]).concat("-").concat(dateStampTmp[0]);
            tgl_antrian             = time.concat(" ").concat(dateStamp);
        }

        return tgl_antrian;
    }

    private String setTanggalEstimasi(String tglEstimasi){
        String tgl_antrian = "";

        if(tglEstimasi == null || tglEstimasi == ""){
            tgl_antrian = "-";
        } else {
            String splitDate[]      = tglEstimasi.split(" ");
            String time             = splitDate[1];
            String dateStampTmp[]   = splitDate[0].split("-");
            String dateStamp        = dateStampTmp[2].concat("-").concat(dateStampTmp[1]).concat("-").concat(dateStampTmp[0]);
            tgl_antrian             = time.concat(" ").concat(dateStamp);
        }

        return tgl_antrian;
    }


    public void addMoreEmplasemen(List<EMPLASEMEN> emplasemenAdder){

        for (int i=0; i < emplasemenAdder.size(); i++){
            EMPLASEMEN emplasemen   = new EMPLASEMEN();
            emplasemen.kd_antrian   = emplasemenAdder.get(i).kd_antrian;
            emplasemen.nospta       = emplasemenAdder.get(i).nospta;
            emplasemen.no_register  = emplasemenAdder.get(i).no_register;
            emplasemen.no_pol       = emplasemenAdder.get(i).no_pol;
            emplasemen.tgl_antrian  = emplasemenAdder.get(i).tgl_antrian;
            emplasemen.tgl_estimasi = emplasemenAdder.get(i).tgl_estimasi;
            emplasemenItems.add(emplasemen);
        }

        notifyDataSetChanged();
    }

    public void clearEmplasemenData(){
        this.emplasemenItems.clear();
    }

}
