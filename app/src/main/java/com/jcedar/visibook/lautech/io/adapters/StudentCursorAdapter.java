package com.jcedar.visibook.lautech.io.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.FormatUtils;
import com.jcedar.visibook.lautech.helper.PrefUtils;
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
    public StudentCursorAdapter(Context context, Cursor c, int layoutResource) {
        super(context, c, 0);
        mLayoutResource = layoutResource;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(mLayoutResource, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();
        runEnterAnimation(view, position);


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
       unit.setText("Course: "+puc);

        final ImageView imageView = (ImageView) view.findViewById(R.id.user_image);
        String imageBas64 = cursor.getString(
                cursor.getColumnIndex(DataContract.Students.IMAGE));
        if( imageBas64 != null ) {
            final Bitmap bitmap = PrefUtils.decodeBase64(imageBas64);
             new Handler().post(new Runnable() {
                 @Override
                 public void run() {
                    imageView.setImageBitmap(bitmap);
                 }
             });
        }
//
//        TextView sortCode = (TextView)view.findViewById(R.id.sort_code);
//        sortCode.setText(cursor.getString(
//                cursor.getColumnIndex(DataContract.Account.SORT_CODE)));

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
