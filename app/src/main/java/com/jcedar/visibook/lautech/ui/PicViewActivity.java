package com.jcedar.visibook.lautech.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.facebook.appevents.AppEventsLogger;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;

import static com.jcedar.visibook.lautech.helper.PrefUtils.getPhoto;

public class PicViewActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_view);


        imageView = (ImageView) findViewById(R.id.profile_image);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PicViewActivity.this.finish();
                }
            });
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    toolbar.setTitle(AccountUtils.getUserName(PicViewActivity.this) != null
                            ? AccountUtils.getUserName(PicViewActivity.this) : "Profile Picture");
                }
            });
        }
            imageView.setImageBitmap(getPhoto(this));
        }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.deactivateApp(this);
    }
}
