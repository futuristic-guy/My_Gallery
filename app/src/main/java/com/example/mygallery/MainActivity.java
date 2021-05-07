package com.example.mygallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button galleryBtn,cameraBtn,allImagesBtn,storeImageBtn;
    private ImageView imageView;
    private static boolean permissionGranted=false;
    private static boolean enabled=true;
    private static long tempName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!permissionGranted){
                    checkPermission();
                }
                Context context = getApplicationContext();
                tempName = System.currentTimeMillis();
                File file= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),tempName+".jpg");
                Uri uri =  FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                   Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                   intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent,1);
            }
        });

        storeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storeImage();
            }
        });

        allImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Gallery.class);
                startActivity(intent);
            }
        });


    }


    public void initView() {
      galleryBtn = findViewById(R.id.gallery);
      cameraBtn = findViewById(R.id.camera);
      allImagesBtn = findViewById(R.id.allImages);
      storeImageBtn = findViewById(R.id.storeImage);
      imageView = findViewById(R.id.imageView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if(resultCode==RESULT_OK){
                if(enabled==false){
                    enabled =true;
                    storeImageBtn.setEnabled(enabled);
                }
                Uri uri = data.getData();
                imageView.setImageURI(uri);
            }
        }

        if(requestCode==1){
            if(resultCode==RESULT_OK){
                enabled = false;
                storeImageBtn.setEnabled(enabled);
             File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
             File file = new File(filePath,tempName+".jpg");
                FileInputStream fis = null;
                try {
                     fis = new FileInputStream(file);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                imageView.setImageBitmap(bitmap);

                Toast.makeText(MainActivity.this,"Image Saved to Documents Directory",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void storeImage(){
        try {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            if(!permissionGranted){
                checkPermission();
            }
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),System.currentTimeMillis()+".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,fos);
            Toast.makeText(MainActivity.this,"File Saved in Documents",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
  }

  public boolean isInternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) || Environment.MEDIA_MOUNTED.equals(state));
  }


  public boolean checkPermission(){
        if(!isExternalStorageWritable() || !isInternalStorageReadable()){
            Toast.makeText(MainActivity.this,"Storage not Available",Toast.LENGTH_SHORT).show();
            return false;
        }

        int checkPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);
            return false;
        }else{
            return true;
        }
  }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1001){

            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                permissionGranted=true;
                Toast.makeText(MainActivity.this,"Permission granted",Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(MainActivity.this,"Please Give Permission",Toast.LENGTH_SHORT).show();
            }
        }
    }
}