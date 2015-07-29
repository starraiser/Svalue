package com.example.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public class MainActivity extends ActionBarActivity {

    private static final String tag = "test";
    static String tempdata;
    private Uri imageUri;
    String temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Three buttons
        ImageButton image1=(ImageButton)findViewById(R.id.image1);
        ImageButton image2=(ImageButton)findViewById(R.id.image2);
        ImageButton image3=(ImageButton)findViewById(R.id.image3);

        //Take photos
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent();

                intent.setClassName("com.example.test","com.example.test.RectPhoto");
                startActivity(intent);
            }
        });

        //Choose a photo
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("1");
                Intent intent = new Intent();
                System.out.println("2");
                intent.setType("image/*");
                System.out.println("3");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                System.out.println("4");
                startActivityForResult(intent, 2);
            }
        });

        //Input data
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.example.test","com.example.test.InputData");
                startActivity(intent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK && requestCode == 2)
        {
            System.out.println("5");
            Uri uri = data.getData();
            System.out.println("6");
            ContentResolver cr = this.getContentResolver();
            try{
                System.out.println("7");
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 4;
                Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null , bitmapOptions);
                //Bitmap mBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                //saveJpeg(mBitmap);
                saveJpeg(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveJpeg(Bitmap bm) {
        String savePath = "/mnt/sdcard/rectPhoto/";
        File folder = new File(savePath);
        if (!folder.exists()) // create the folder if it does not exist
        {
            folder.mkdir();
        }
        long dataTake = System.currentTimeMillis();
        String jpegName = savePath + dataTake + ".jpg";
        Log.i(tag, "saveJpeg:jpegName--" + jpegName);
        // File jpegFile = new File(jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);

            // //如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800
            // Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);

            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            File file = new File(jpegName);

            regular(file);

            new AlertDialog.Builder(MainActivity.this).setTitle(tempdata)
                    .setMessage("test")
                    .show();
            Log.i(tag, "saveJpeg：存储完毕！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(tag, "saveJpeg:存储失败！");
            Log.e("WTF", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
    }

    public void regular(File file){
        if(file.isDirectory()){
            File files[] = file.listFiles();
            for(int i = 0; i < files.length; i++){
                regular(files[i]);
            }
        }
        else{
            //new Thread(new User(file)).start();
            User user_t = new User(file);
            Thread thr = new Thread(user_t);
            thr.start();
            try {
                thr.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            tempdata = user_t.getString();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
