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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ptkebonagung.gotani.BuildConfig;
import com.ptkebonagung.gotani.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

public class DialogSPTAFragment extends DialogFragment {

    private View root_view;
    private String spta;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.dialog_spta, container, false);
        final Bundle args = getArguments();

        root_view.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spta = args.getString("no_spta");
                Shareit(root_view, spta);
            }
        });



        root_view.findViewById(R.id.backgroud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
                dismiss();
            }
        });

        ImageView barcode       = root_view.findViewById(R.id.barcode);
        TextView t_barcode       = root_view.findViewById(R.id.t_barcode);
        TextView txtCuttingType = root_view.findViewById(R.id.txtViewFragCuttingType);
        TextView txtStartDate   = root_view.findViewById(R.id.txtViewFragStartDate);
        TextView txtStartTime   = root_view.findViewById(R.id.txtViewFragStartTime);
        TextView txtEndDate     = root_view.findViewById(R.id.txtViewFragEndDate);
        TextView txtEndTime     = root_view.findViewById(R.id.txtViewFragEndTime);
        TextView txtRegister    = root_view.findViewById(R.id.txtViewFragRegister);
        TextView txtOwner       = root_view.findViewById(R.id.txtViewFragOwner);
        /*TextView txtVarietas    = root_view.findViewById(R.id.txtViewFragVarietas);
        TextView txtfieldArea   = root_view.findViewById(R.id.txtViewFragFieldArea);
        TextView txtpoliceNumber= root_view.findViewById(R.id.txtViewFragPoliceNumber);*/


        //txtView.setText(args.getString("SPTA"));

        String cuttingType = getCuttingType(args.getString("jenis_tebang"));
        String startDate   = getStartDate(args.getString("tanggal_berlaku_awal"));
        String startTime   = getStartTime(args.getString("tanggal_berlaku_awal"));
        String endDate     = getEndDate(args.getString("tanggal_berlaku_akhir"));
        String endTime     = getEndTime(args.getString("tanggal_berlaku_akhir"));
        String register    = args.getString("register");
        String owner       = args.getString("pemilik");
        /*String varietas    = "-";
        String fieldArea   = "-";
        String policeNumb  = "-";*/

        /*show argument from bundle to fragment detail SPTA property*/
        txtCuttingType.setText(cuttingType);
        txtStartDate.setText("dari "+startDate.toUpperCase());
        txtStartTime.setText(startTime.toUpperCase());
        txtEndDate.setText("s.d "+endDate.toUpperCase());
        txtEndTime.setText(endTime.toUpperCase());
        txtRegister.setText(register.toUpperCase());
        txtOwner.setText(owner.toUpperCase());
        /*txtVarietas.setText(varietas.toUpperCase());
        txtfieldArea.setText(fieldArea.toUpperCase());
        txtpoliceNumber.setText(policeNumb.toUpperCase());*/

        String spta = args.getString("no_spta");
        t_barcode.setText(spta);
        final String dari = txtStartDate.getText() + " " + txtStartTime.getText();
        final String batas = txtEndDate.getText() + " " + txtEndTime.getText();
        final String reg = txtRegister.getText().toString();
        final String pet = txtOwner.getText().toString();
        final String jt = txtCuttingType.getText().toString();

        root_view.findViewById(R.id.share_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spta = args.getString("no_spta");
                Intent message = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + "" ) );
                message.putExtra( "sms_body", "SPTA: "+spta+"\nMasa berlaku\n  " +dari+ "\n  "+batas + "\nRegister: "+reg + "\nPemilik: "+pet+"\nJenis: "+jt);
                startActivity(message);
            }
        });

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(spta, BarcodeFormat.CODE_128, 700, 120);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return root_view;
    }

    private String getCuttingType(String cuttingType){
        String cutting_type = "";

        if (cuttingType.equalsIgnoreCase("PG")){
            cutting_type = "TEBANG PG";
        } else {
            cutting_type = "TEBANG SENDIRI";
        }

        return cutting_type;
    }

    private String getStartDate(String startDate){
        String start_date[] = startDate.split(" ");
        String make_date[]  = start_date[0].split("-");
        String MakeDate     = make_date[2].concat("-").concat(make_date[1]).concat("-").concat(make_date[0]);
        return MakeDate;
    }


    private String getStartTime(String startDate){
        String start_date[] = startDate.split(" ");
        return start_date[1];
    }

    private String getEndDate(String endDate){
        String end_date[] = endDate.split(" ");
        String make_date[]= end_date[0].split("-");
        String MakeDate   = make_date[2].concat("-").concat(make_date[1]).concat("-").concat(make_date[0]);
        return MakeDate;
    }

    private String getEndTime(String endTime){
        String end_time[] = endTime.split(" ");
        return end_time[1];
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

    private void sharePict(View view, String spta){
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
            String fileName = "SPTA "+spta.toUpperCase()+".jpg";
            File picFile = new File(picDir + "/" + fileName);

            try {
                picFile.createNewFile();
                FileOutputStream picOut = new FileOutputStream(picFile);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picOut);
                if (saved) {
//                    Toast.makeText(view.getContext(), "Image " + spta.toUpperCase() + " saved to your device Pictures " + "directory!", Toast.LENGTH_SHORT).show();
                    Log.e("-->", "Image " + spta.toUpperCase() + " saved");
                } else {
//                    Toast.makeText(view.getContext(), "Image " + spta.toUpperCase() + " not saved to your device Pictures " + "directory!", Toast.LENGTH_SHORT).show();
                    Log.e("-->", "Image " + spta.toUpperCase() + " not saved");
                }
                picOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            view.destroyDrawingCache();

            // share via intent
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("image/jpeg");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(picFile.getAbsolutePath()));
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        } else {
            //Error
            Toast.makeText(view.getContext(), "External Storage Unmounted", Toast.LENGTH_SHORT).show();
        }
        im.setVisibility(View.VISIBLE);
    }
}