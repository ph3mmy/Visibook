package com.jcedar.visibook.lautech.io.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.FormatUtils;
import com.jcedar.visibook.lautech.provider.DataContract;

/**
 * Created by Afolayan on 21/8/2015.
 */
public class StudentCursorAdapter extends CursorAdapter {


    public static final String TAG = StudentCursorAdapter.class.getSimpleName();

    private int lastAnimatedPosition = -1;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    private int mLayoutResource;
    private String userId;
    boolean hasImage;

    Context context;

    public StudentCursorAdapter(Context context, Cursor c, int layoutResource) {
        super(context, c, 0);
        mLayoutResource = layoutResource;
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(mLayoutResource, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();
        runEnterAnimation(view, position);




        userId = cursor.getString(
                cursor.getColumnIndex(DataContract.Students._ID));

        TextView title = (TextView) view.findViewById(R.id.lblName);
        title.setTypeface(null, Typeface.BOLD);
        String titleText = cursor.getString(
                cursor.getColumnIndex(DataContract.Students.NAME));
        if(  context.getClass().getSimpleName().equals("CourseDetailsActivity" ))
        title.setText(FormatUtils.ellipsize(titleText));
        else title.setText(titleText);


        TextView mark = (TextView)view.findViewById(R.id.lblDoB);
        mark.setTypeface(null, Typeface.BOLD);
        String xx = "DoB: "+cursor.getString(
                cursor.getColumnIndex(DataContract.Students.DATE_OF_BIRTH));
         mark.setText(xx);

        TextView unit = (TextView)view.findViewById(R.id.lblCourse);
        unit.setTypeface(null, Typeface.BOLD);
        String puc = cursor.getString(
                cursor.getColumnIndex(DataContract.Students.COURSE));
       unit.setText("Course: " + puc);
        String imagePresent = cursor.getString(
                cursor.getColumnIndex(DataContract.Students.IMAGE));
        Log.e(TAG, "image from cursor ==" + imagePresent);

        final ImageView myImageView  = (ImageView) view.findViewById(R.id.user_image);



    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView myImageView;

        if( convertView == null ) {
            myImageView = (ImageView) parent.findViewById(R.id.user_image);
            //myImageView = (ImageView) convertView.findViewById(R.id.user_image);
        } else {
            myImageView = (ImageView) convertView;
        }
        if ( hasImage ){
            String url = String.format(AppSettings.SERVER_IMAGE_URL+"%s.png", userId);
            Log.e(TAG, "image url == 2 " + url);

            /*Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_user)
                    .crossFade()
                    .into(myImageView);*/
        }

        return myImageView;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }
}
