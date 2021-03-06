package com.lockon.xebird.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.lockon.xebird.other.XeBirdHandler.SETBITMAP;
import static com.lockon.xebird.other.XeBirdHandler.SETNULLBITMAP;


public class getImgAndSave implements Runnable {
    private final String Host = "https://xebird.proto.cf/";
    private final String Path;
    private final String DMP;
    private final int index;
    private final String input;
    private final String TAG = "WebRequest";
    private final Handler handler;
    private final File path2Save;
    private boolean isPic;

    public getImgAndSave(String path, int index, String nameLA, Handler handler, File path2Save) {
        String DMP1;
        Path = path;
        isPic = true;
        switch (Path) {
            case "Descriptive_graph":
                DMP1 = "-D-";
                break;
            case "Map":
                DMP1 = "-M-";
                break;
            case "Photos":
                DMP1 = "-P-";
                break;
            case "Voice":
                DMP1 = "-V-";
                isPic = false;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Path);
        }
        DMP = DMP1;
        this.index = index;
        this.input = nameLA.replace(" ", "_");
        this.handler = handler;
        this.path2Save = path2Save;
    }

    @Override
    public void run() {

        if (!path2Save.exists()) {
            boolean a = path2Save.mkdirs();
            Log.i(TAG, "run: create path " + a);
        }

        if (isPic) {
            String picName = input + DMP + index + ".jpg";
            File localImg = new File(path2Save, picName.replace("-", "_"));
            Bitmap bitmap = null;
            if (!localImg.exists()) {
                Log.i(TAG, "onClick: local file dont exist");
                String totalWeb = Host + Path + "/" + picName;
                Log.i(TAG, "getImgFromWeb: download from " + totalWeb);
                try {
                    //网络请求
                    URL url = new URL(totalWeb);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    Log.i(TAG, "getImgFromWeb: download begin");
                    conn.connect();
                    InputStream is = conn.getInputStream();//获得图片的数据流
                    bitmap = BitmapFactory.decodeStream(is);//读取图像数据
                    is.close();
                    Log.i(TAG, "getImgFromWeb: download end");

                    Log.i(TAG, "getImgFromWeb: save pic to local file " + localImg.toString());
                    FileOutputStream bos = new FileOutputStream(localImg);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bos.flush();
                    bos.close();
                    Log.i(TAG, "getImgFromWeb: save pic to local file success");
                } catch (IOException e) {
                    if (bitmap == null) {
                        Log.i(TAG, "getImgFromWeb: get pic from web failed");
                    } else {
                        Log.i(TAG, "getImgFromWeb: save pic to local file failed");
                    }
                    e.printStackTrace();
                }
            } else {
                bitmap = BitmapFactory.decodeFile(localImg.getAbsolutePath());
                Log.i(TAG, "getImgFromLocal: get success");
            }
            if (bitmap == null) {
                handler.sendEmptyMessage(SETNULLBITMAP);
                Log.i(TAG, "getImgFromWeb: send message");
            } else {
                Message msg = Message.obtain(handler);
                msg.what = SETBITMAP;
                msg.obj = bitmap;
                Log.i(TAG, "getImgFromWeb: send message");
                msg.sendToTarget();
            }
        } else {
            String soundName = input + DMP + index + ".mp3";
            File localSound = new File(path2Save, soundName.replace("-", "_"));
            if (!localSound.exists()) {
                Log.i(TAG, "onClick: local file dont exist");
                String totalWeb = Host + Path + "/" + soundName;
                Log.i(TAG, "getSoundFromWeb: download from " + totalWeb);
                try {
                    //网络请求
                    URL url = new URL(totalWeb);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    Log.i(TAG, "getSoundFromWeb: download begin");
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Log.i(TAG, "getSoundFromWeb: download end");

                    Log.i(TAG, "getSoundFromWeb: save sound to local file " + localSound.toString());
                    FileOutputStream bos = new FileOutputStream(localSound);
                    byte[] buffer = new byte[1024 * 1024];
                    while (is.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    bos.flush();
                    bos.close();
                    is.close();
                    Log.i(TAG, "getSoundFromWeb: save pic to local file success");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "getSoundFromLocal: get success");
            }
            Message msg = Message.obtain(handler);
            msg.what = XeBirdHandler.SETSOUND;
            msg.obj = localSound.getAbsolutePath();
            msg.sendToTarget();
        }

    }
}

