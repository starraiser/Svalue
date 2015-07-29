package com.example.test;

import android.app.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
/**
 *
 * @author
 *
 */
public class User implements Runnable {

    /**
     * Name of the file to be uploaded
     */
    String filename;
    /**
     * 要上传的文件大小
     */
    int fileSize;
    /**
     * Size of the file which is uplpaded
     */
    int currentSize;
    /**
     * 网络输出流
     */
    OutputStream out = null;
    /**
     * 文件
     */
    File file = null;
    /**
     * 构造函数
     *
     * @param fname 初始化文件名
     */
    String tempdata;
    public User(String fname) {
        this.filename = fname;
    }
    /**
     * 构造函数
     * @param 初始化文件
     */
    public User(File file) {
        this.file = file;
    }
    @Override
    public void run() {
        try {
            if(!file.exists()){
                System.out.println("file not exist");
                return;
            }
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            //定义与服务器连接的socket
            Socket socket = new Socket("192.168.191.1",11015);
            socket.setSoTimeout(30);
            out = socket.getOutputStream();
            //将文件名和文件的md5值发给服务器
            out.write(("upload#"+file.getName() +"#"+ getFileMD5(file)).getBytes());
            //文件输入流
            InputStream in = socket.getInputStream();
            int len = 0;
            while((len = in.read(buf)) != -1){
                if(len == 0)
                    continue;
                String str = new String(buf, 0,len);
                System.out.println(str);
                if(str.startsWith("Existed")){
                    String sts[] = str.split("#");
                    tempdata = sts[2];
                    //获取服务器已上传的该文件的大小
                    currentSize = Integer.parseInt(sts[1]);
                    fis.skip(currentSize);
                    while(( len = fis.read(buf)) != -1){
                        out.write(buf,0,len);
                        currentSize += len;
                    }
                }
                System.out.println("上传成功");
                out.close();
                in.close();
                socket.close();
                return;
            }

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("upload fail");
        }
    }
    public String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[4096];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            System.out.println("获取文件的md5值失败");
            e.printStackTrace();

            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public String getString(){
        return tempdata;
    }
}
