package com.example.titinho.synchronisedcameras;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PHOTO = 101;
    private ImageView ivPhoto;
    private TextView tvPhoto;
    private final String IMAGEPATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()+
                    //"/Synchronised Camera/Images
                    "/tmpImage.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPhoto = (TextView) findViewById(R.id.tvPhoto);
        tvPhoto.append("\n"+IMAGEPATH+"\n");
        checkCameraParameters();

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        Button btnTakePhoto = (Button) findViewById(R.id.btnPhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTakePhoto = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );
                startActivityForResult(intentTakePhoto,REQUEST_CODE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && requestCode==REQUEST_CODE_PHOTO){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivPhoto.setImageBitmap(imageBitmap);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,10,out);
            File imageFile = new File(IMAGEPATH);
            if(imageFile.exists()) imageFile.delete();
            try {
                imageFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                fileOutputStream.write(out.toByteArray());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean checkCameraHardware(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return true;
        else return false;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){

        }
        return  c;
    }

    private void checkCameraParameters(){
        Camera c = null;
        try {
            c = Camera.open();
            Camera.Parameters p = c.getParameters();
            tvPhoto.append("\n");
            for(Camera.Size s : p.getSupportedPictureSizes())
            {
                tvPhoto.append(s.width+"*"+s.height+"\n");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(c != null) c.release();
        }
    }
}
