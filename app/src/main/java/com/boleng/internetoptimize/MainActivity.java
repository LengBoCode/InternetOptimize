package com.boleng.internetoptimize;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.HttpResponseCache;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity implements View.OnClickListener {

    private String TAG=MainActivity.class.getSimpleName();
    private ImageView imgContainer;
    private Button btnRequest;
    private MyHandler myHandler = new MyHandler(this);
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgContainer = (ImageView) findViewById(R.id.imgContainer);
        btnRequest = (Button) findViewById(R.id.btnRequest);

        btnRequest.setOnClickListener(this);
//        init();
    }

    private void init() {
        File file = new File(this.getExternalCacheDir(), "http");
        long maxSize = 1024 * 1024 * 10;
        try {
            HttpResponseCache.install(file, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        file=new File(this.getExternalFilesDir("file"),"cache.txt");
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            fileOutputStream.write("hello world".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRequest:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long start=System.currentTimeMillis();
                        try {
                            URL url = new URL("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504152863170&di=14547c8e38df2cee170c7c56938c14c6&imgtype=0&src=http%3A%2F%2Fp.3761.com%2Fpic%2F43701399945993.png");
                            URLConnection connection = url.openConnection();
                            image = BitmapFactory.decodeStream(connection.getInputStream());
                            long end=System.currentTimeMillis();
                            Log.i(TAG,(end-start)+"");
                            myHandler.sendEmptyMessage(0);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache httpResponseCache = HttpResponseCache.getInstalled();
        if (httpResponseCache != null) {
            httpResponseCache.flush();
        }
    }

    public class MyHandler extends Handler {

        private WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity activity) {
            weakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = weakReference.get();
            if (activity != null) {
                imgContainer.setImageBitmap(image);
            }
        }
    }
}
