package com.lockon.xebird;

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

import static com.lockon.xebird.FirstFragment.SETBITMAP;

public class getImgAndSave implements Runnable {
    private final String Host = "https://xebird.proto.cf/";
    private final String Path;
    private final String DMP;
    private final int index;
    private final String input;
    private final String TAG = "WebRequest";
    private final Handler handler;
    private final File path2Img;

    public getImgAndSave(String path, int index, String nameLA, Handler handler, File path2Img) {
        String DMP1;
        Path = path;
        switch (Path) {
            case "Descriptive_graph":
                DMP1 = "-D-";
                break;
            case "Map":
                DMP1 = "-M-";
                break;
            case "Photo":
                DMP1 = "-P-";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Path);
        }
        DMP = DMP1;
        this.index = index;
        this.input = nameLA.replace(" ", "_");
        this.handler = handler;
        this.path2Img = path2Img;
    }

    @Override
    public void run() {

        if (!path2Img.exists()) {
            boolean a = path2Img.mkdirs();
            Log.i(TAG, "run: create path " + a);
        }

        String picName = input + DMP + index + ".jpg";
        File localImg = new File(path2Img, picName.replace("-", "_"));
        if (!localImg.exists()) {

            Log.i(TAG, "onClick: local file dont exist");
            String totalWeb = Host + Path + "/" + picName;
            Log.i(TAG, "getImgFromWeb: download from " + totalWeb);
            Bitmap bitmap = null;
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
            Message msg = Message.obtain(handler);
            msg.what = SETBITMAP;
            msg.obj = bitmap;
            Log.i(TAG, "getImgFromWeb: send message");
            msg.sendToTarget();
        } else {
            Message msg = Message.obtain(handler);
            msg.what = SETBITMAP;
            msg.obj = BitmapFactory.decodeFile(localImg.getPath());
            Log.i(TAG, "getImgFromWeb: send message from local");
            msg.sendToTarget();
        }
    }
}

