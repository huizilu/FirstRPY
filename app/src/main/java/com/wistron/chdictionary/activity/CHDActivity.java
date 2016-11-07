package com.wistron.chdictionary.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import java.io.IOException;
import java.io.InputStream;


public class CHDActivity extends Activity {
    //这是我的注释

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
         //获取资源图片
        //111
        try {
            InputStream is = this.getResources().openRawResource(R.raw.bg);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
            Bitmap bitmap = BitmapFactory.decodeStream(is,null, opt);
            linearLayout.setBackground(new BitmapDrawable(this.getResources(), bitmap));
            is.close();
            InputStream is2 = this.getResources().openRawResource(R.raw.bg_tree);
            LinearLayout linearLayout1= (LinearLayout) findViewById(R.id.linear2);
            Bitmap bitmap2 = BitmapFactory.decodeStream(is2,null, opt);
            linearLayout1.setBackground(new BitmapDrawable(this.getResources(),bitmap2));
            is2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
