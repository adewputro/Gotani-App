package com.ptkebonagung.gotani.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ptkebonagung.gotani.R;

import java.io.File;
import java.io.FileOutputStream;

public class DialogBuktiTimbangFragment extends DialogFragment {

    private View root_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.dialog_bukti_timbang, container, false);

        final Bundle bundleArgs = getArguments();

        root_view.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shareit(root_view, bundleArgs.getString("noSPTA"));
            }
        });

        root_view.findViewById(R.id.backgroud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                dismiss();
            }
        });

        TextView jenisTebang    = root_view.findViewById(R.id.jenisTebang);
        TextView waktuMasuk     = root_view.findViewById(R.id.waktuMasuk);
        TextView waktuKeluar    = root_view.findViewById(R.id.waktuKeluar);
        TextView nomorSPTA      = root_view.findViewById(R.id.noSPTA);
        TextView nomorAntrian   = root_view.findViewById(R.id.noAntrian);
        TextView register       = root_view.findViewById(R.id.register);
        TextView pemilik        = root_view.findViewById(R.id.pemilik);
        TextView bruto          = root_view.findViewById(R.id.beratBruto);
        TextView tara           = root_view.findViewById(R.id.beratTara);
        TextView nettoKG        = root_view.findViewById(R.id.beratNettoKg);
        TextView nettoKW        = root_view.findViewById(R.id.beratNettoKw);
        TextView rafaksiKW      = root_view.findViewById(R.id.rafaksiKW);
        TextView mutu           = root_view.findViewById(R.id.mutu);
        TextView nomorPolisi    = root_view.findViewById(R.id.nopol);
        TextView varietas       = root_view.findViewById(R.id.varietas);

        int bobot               = Integer.parseInt(bundleArgs.getString("berat_netto_kw")) - Integer.parseInt(bundleArgs.getString("rafaksi"));
        String jenis_tebang     = setJenisTebang(bundleArgs.getString("jenis_tebang"));
        String waktu_masuk      = setTimeStamp(bundleArgs.getString("waktu_masuk"));
        String waktu_keluar     = setTimeStamp(bundleArgs.getString("waktu_keluar"));
        String nomor_SPTA       = bundleArgs.getString("noSPTA").toUpperCase();
        String nomor_antrian    = bundleArgs.getString("noAntrian").toUpperCase();
        String register_        = bundleArgs.getString("register").toUpperCase();
        String pemilik_         = bundleArgs.getString("pemilik").toUpperCase();
        String bruto_           = bundleArgs.getString("berat_bruto").toUpperCase();
        String tara_            = bundleArgs.getString("berat_tara").toUpperCase();
        String nettoKG_         = bundleArgs.getString("berat_netto").toUpperCase();
        String nettoKW_         = bundleArgs.getString("berat_netto_kw").toUpperCase()+" => "+bobot;
        String rafaksiKW_       = setRafaksi(bundleArgs.getString("rafaksi"));
        String mutu_            = "MBS "+bundleArgs.getString("mutu").toUpperCase() + " (MEJA "+bundleArgs.getString("no_meja")+")";
        String nomor_polisi     = bundleArgs.getString("nomor_polisi").toUpperCase();
        String varietas_        = bundleArgs.getString("varietas").toUpperCase();

        jenisTebang.setText(jenis_tebang);
        waktuMasuk.setText(waktu_masuk);
        waktuKeluar.setText(waktu_keluar);
        nomorSPTA.setText(nomor_SPTA);
        nomorAntrian.setText(nomor_antrian);
        register.setText(register_);
        pemilik.setText(pemilik_);
        bruto.setText(bruto_);
        tara.setText(tara_);
        nettoKG.setText(nettoKG_);
        nettoKW.setText(nettoKW_);
        rafaksiKW.setText(rafaksiKW_);
        mutu.setText(mutu_);
        nomorPolisi.setText(nomor_polisi);
        varietas.setText(varietas_);

//        ImageView barcode = root_view.findViewById(R.id.barcode);
//
//        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//        try {
//            BitMatrix bitMatrix = multiFormatWriter.encode(spta, BarcodeFormat.CODE_128, 700, 120);
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//            barcode.setImageBitmap(bitmap);
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
        return root_view;
    }

    private String setTimeStamp(String waktu) {
        String result;

        if (waktu == null || waktu == ""){
            result = "-";
        } else {
            String tempRes[]    = waktu.split(" ");
            String time         = tempRes[1];
            String tmpDate[]    = tempRes[0].split("-");

            String tmpTime[]= time.split("\\.");
            time            = tmpTime[0];

            String dateStamp    = tmpDate[2].concat("-").concat(tmpDate[1]).concat("-").concat(tmpDate[0]);
            result              = time.concat(" ").concat(dateStamp);
        }

        return result;
    }

    private String setRafaksi(String rafaksi) {
        String result;

        if (rafaksi == null || rafaksi == " "){
            result = "0";
        } else {
            result = rafaksi;
        }

        return result;
    }

    private String setJenisTebang(String jenis_tebang) {
        String result;

        if (jenis_tebang == null || jenis_tebang == ""){
            result = "-";
        } else {
            if (jenis_tebang.contains("PG")){
                result = "TEBANG PG";
            } else {
                result = "TEBANG SENDIRI";
            }
        }

        return result;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void Shareit(View view, String spta) {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            sharePict(view, spta);
        } else {
            Toast.makeText(view.getContext(), "Permission not granted, pls restart or install again this app", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePict(View view, String spta) {

        LinearLayout im = view.findViewById(R.id.tombolShare);
        im.setVisibility(View.INVISIBLE);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File picDir = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Gotani");
            } else {
                picDir = new File(Environment.getExternalStorageDirectory() + "/Gotani");
            }
            if (!picDir.exists()) {
                picDir.mkdirs();
            }

            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache(true);
            Bitmap bitmap = view.getDrawingCache();
//          Date date = new Date();
            String fileName = "Bukti Timbang "+spta.toUpperCase()+".jpg";
            File picFile = new File(picDir + "/" + fileName);
            try {
                picFile.createNewFile();
                FileOutputStream picOut = new FileOutputStream(picFile);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picOut);
                if (saved) {
                    Toast.makeText(view.getContext(), "Image saved to your device Pictures " + "directory!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(), "Image not saved to your device Pictures " + "directory!", Toast.LENGTH_SHORT).show();
                }
                picOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            view.destroyDrawingCache();

            // share via intent
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/jpeg");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(picFile.getAbsolutePath()));
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else {
            //Error

        }
        im.setVisibility(View.VISIBLE);
    }

}