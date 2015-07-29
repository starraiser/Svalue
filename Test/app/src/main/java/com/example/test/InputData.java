package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class InputData extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局
        setContentView(R.layout.input_data_layout);

        //初始化按钮
        Button btnConfirm = (Button)findViewById(R.id.upload);
        Button btnClear = (Button)findViewById(R.id.clear);
        final EditText height = (EditText)findViewById(R.id.height);
        final EditText bust = (EditText)findViewById(R.id.Bvalue);
        final EditText waist = (EditText)findViewById(R.id.Wvalue);
        final EditText hips = (EditText)findViewById(R.id.Hvalue);

        //确认按钮
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //检测是否有未输入的项目
                if(TextUtils.isEmpty(height.getText()) || TextUtils.isEmpty(bust.getText())
                        || TextUtils.isEmpty(waist.getText())
                        || TextUtils.isEmpty(hips.getText())){

                    //
                    new AlertDialog.Builder(InputData.this).setTitle("警告")
                            .setMessage("你还有未填写项！")
                            .show();
                    return;
                }

                //获取各个值(String)
                String heistring = height.getText().toString();
                String bstring = bust.getText().toString();
                String wstring = waist.getText().toString();
                String hstring = hips.getText().toString();

                //转化为Int
                int heivalue = Integer.parseInt(heistring);
                int wvalue = Integer.parseInt(wstring);
                int bvalue = Integer.parseInt(bstring);
                int hvalue = Integer.parseInt(hstring);

                String heitemp = "身高:";
                String btemp = "\n胸围:";
                String wtemp = "\n腰围:";
                String htemp = "\n臀围:";
                String infomation = heitemp + heistring
                        + btemp + bstring + wtemp + wstring + htemp + hstring;
                new AlertDialog.Builder(InputData.this).setTitle("数据")
                        .setMessage(infomation)
                        .show();

                try{
                    String savePath = "/mnt/sdcard/rectPhoto/txt/";
                    File folder = new File(savePath);
                    long dataTake = System.currentTimeMillis();
                    String dataName = savePath + dataTake + ".txt";
                    if (!folder.exists()) // 如果文件夹不存在则创建
                    {
                        folder.mkdir();
                    }
                    File file = new File(dataName);
                    if (!file.exists()) // 如果文件夹不存在则创建
                    {
                        file.createNewFile();
                    }
                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(infomation);
                    bw.close();
                    RectPhoto temp = new RectPhoto();
                    temp.regular(file);
                }catch (IOException e){
                    System.out.println("fail to create");
                    e.printStackTrace();
                }
            }
        });

        //清空数据
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                height.setText("");
                bust.setText("");
                waist.setText("");
                hips.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_data, menu);
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
