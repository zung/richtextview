package com.example.richtextview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.czg.richtextview.MyHtml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    ImageView ivImage;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
        ivImage = findViewById(R.id.iv_img);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
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
        MyHtml.init(mTextView);
        Spanned spanned = MyHtml.fromHtml(src, MyHtml.FROM_HTML_MODE_COMPACT, new MyHtml.ImageGetter() {
            @Override
            public Drawable getDrawable(String source, int start) {
                getImage(source, start);
                return null;
            }
        }, null);
        mTextView.setText(spanned);
    }

    public void getImage(String source, int start) {
        if (TextUtils.isEmpty(source)) return;
        Handler handler = new Handler();
        Glide.with(this)
                .asGif()
                .load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201611%2F04%2F20161104110413_XzVAk.thumb.700_0.gif&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1614922677&t=90bd10eb2a248d80084eec919f8ab23a")
                .into(new CustomTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                        try {
                            Object GifState = resource.getConstantState();
                            Field frameLoader = GifState.getClass().getDeclaredField("frameLoader");
                            frameLoader.setAccessible(true);
                            Object gifFrameLoader = frameLoader.get(GifState);

                            Field gifDecoder = gifFrameLoader.getClass().getDeclaredField("gifDecoder");
                            gifDecoder.setAccessible(true);
                            StandardGifDecoder standardGifDecoder = (StandardGifDecoder) gifDecoder.get(gifFrameLoader);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mTextView.getText().length() > start) {
                                        standardGifDecoder.advance();
                                        SpannableString spannableString = (SpannableString) mTextView.getText();
                                        spannableString.setSpan(new ImageSpan(MyHtml.sContext, standardGifDecoder.getNextFrame()), start, start + 1,
                                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        handler.postDelayed(this, standardGifDecoder.getNextDelay());
                                    }
                                }
                            });

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
//    new Thread(new Runnable() {
//            @Override
//            public void run() {
//                request(source, start);
//            }
//        }).start();

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