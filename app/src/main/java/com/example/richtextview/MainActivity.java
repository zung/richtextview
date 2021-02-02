package com.example.richtextview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.czg.richtextview.MyHtml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView  textView = findViewById(R.id.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String src = "<div id=\"se-knowledge\"><p><big>H</big>ello <i>world!</i></p>"
                + "<img src=\"https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF\"/>"
                + "<ul>" +
                "<li><a href=\"http://aa\">1.java</a></li>" +
                "<li><a>2.c++</a></li>" +
                "<li><a>3.python</a></li>" +
                "<li><a href=\"http://aa\">4.kotlin</a></li>" +
                "</ul>"
                + "successful!"
                + "</div>";
        MyHtml.init(textView);
        Spanned spanned = MyHtml.fromHtml(src, MyHtml.FROM_HTML_MODE_COMPACT, new MyHtml.ImageGetter() {
            @Override
            public Drawable getDrawable(String source, int start) {
                getImage(source, start);
                return null;
            }
        }, null);
        textView.setText(spanned);
    }

    public void getImage(String source, int start) {
        if (TextUtils.isEmpty(source)) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                request(source, start);
            }
        }).start();

    }

    public void request(String source, int start) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(source).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(3000);
            urlConnection.setUseCaches(true);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len;
                while ((len = inputStream.read(buff)) != -1) {
                    bos.write(buff, 0, len);
                }
                bos.close();
                inputStream.close();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.size());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyHtml.onImageReady(bitmap, start);
                        urlConnection.disconnect();
                    }
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}