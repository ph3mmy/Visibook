package com.jcedar.visibook.lautech.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AppSettings;
import com.jcedar.visibook.lautech.helper.UIUtils;
import com.jcedar.visibook.lautech.provider.DataContract;

import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecyclerCursorAdapterAll extends RecyclerViewCursorAdapter<RecyclerCursorAdapterAll.ResultViewHolder>
        implements View.OnClickListener
{
    private final LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    public Context context;

    public RecyclerCursorAdapterAll(final Context context)
    {
        super();

        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        final View view = this.layoutInflater.inflate(R.layout.list_n_item_all_student, parent, false);
        view.setOnClickListener(this);

        return new ResultViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final ResultViewHolder holder, final Cursor cursor)
    {
        holder.bindData(cursor);
    }

     /*
     * View.OnClickListener
     */

    @Override
    public void onClick(final View view)
    {
        if (this.onItemClickListener != null)
        {
            final RecyclerView recyclerView = (RecyclerView) view.getParent();
            final int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION)
            {
                final Cursor cursor = this.getItem(position);
                this.onItemClickListener.onItemClicked(cursor);
            }
        }
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.lblName)
        TextView textViewName;

        @Bind(R.id.lblCourse)
        TextView textViewCourse;

        @Bind(R.id.lblDoB)
        TextView textViewDOB;

        @Bind(R.id.lblSchl)
        TextView textViewSchl;

        @Bind(R.id.user_image)
        ImageView myImageView;

        Context context;

        Bitmap bitmap;
        boolean imageIsLoaded = false;

        public ResultViewHolder(final View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = context;
        }

        public void bindData(final Cursor cursor)
        {
            final String context = this.context.getClass().getSimpleName();

            String userId = cursor.getString(
                    cursor.getColumnIndex(DataContract.Students._ID));

            final String name = cursor.getString(cursor.getColumnIndex(DataContract.Students.NAME));
            this.textViewName.setText(name);

            if ( context.equals("AllStudentDetailsActivity")){
                this.textViewCourse.setVisibility( View.GONE );
            }
            final String course = cursor.getString(cursor.getColumnIndex(DataContract.Students.COURSE));
            this.textViewCourse.setText(course);

            final String dob = cursor.getString(cursor.getColumnIndex(DataContract.Students.DATE_OF_BIRTH));
            this.textViewDOB.setText(dob);

            final String schl = cursor.getString(cursor.getColumnIndex(DataContract.Students.CHAPTER));
            this.textViewSchl.setText("");

            String imagePresent = cursor.getString(
                    cursor.getColumnIndex(DataContract.Students.IMAGE));



            if(imagePresent != null){
                if( imagePresent.equals("1")) {
                    final String url = String.format(AppSettings.SERVER_IMAGE_URL + "%s.png", userId);

                    /*Glide.with(this.context)
                            .load(url)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_user)
                            .crossFade()
                            .into(myImageView);*/

                    final Context context1 = this.context;

                    new AsyncTask<Void, Void, Bitmap>() {

                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            try {
                                return  Glide.with(context1)
                                        .load(url)
                                        .asBitmap()
                                        .into(-1, -1)
                                        .get();


                            }

                            catch(InterruptedException| ExecutionException e ) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            if( null != bitmap){
                                setBitmap(bitmap);
                                setImageIsLoaded(true);
                                Bitmap bitmap1 = UIUtils.getCircleBitmap(bitmap);
                                myImageView.setImageBitmap(bitmap1);
                                Log.e("TAG", "Image loaded");
                            }
                        }
                    }.execute();

                    myImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           if( isImageLoaded() ) {
                               ImageView mImageView = new ImageView(context1);
                               mImageView.setImageBitmap(getBitmap());

                               AlertDialog.Builder builder = new AlertDialog.Builder(context1);
                               builder.create().setCancelable(true);
                               builder.setView(mImageView);
                               builder.show();

                           }
                        }
                    });
                }

            }

        }

        public boolean isImageLoaded() {
            return imageIsLoaded;
        }

        public void setImageIsLoaded(boolean imageIsLoaded) {
            this.imageIsLoaded = imageIsLoaded;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    public interface OnItemClickListener
    {
        void onItemClicked(Cursor cursor);
    }
}