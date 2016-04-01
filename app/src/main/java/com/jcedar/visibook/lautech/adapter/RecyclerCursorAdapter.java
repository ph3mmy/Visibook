package com.jcedar.visibook.lautech.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.provider.DataContract;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecyclerCursorAdapter extends RecyclerViewCursorAdapter<RecyclerCursorAdapter.SearchResultViewHolder>
        implements View.OnClickListener
{
    private final LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;

    public RecyclerCursorAdapter(final Context context)
    {
        super();

        this.layoutInflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        final View view = this.layoutInflater.inflate(R.layout.list_n_item_student, parent, false);
        view.setOnClickListener(this);

        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchResultViewHolder holder, final Cursor cursor)
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

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(R.id.lblName)
        TextView textViewName;

        @Bind(R.id.lblCourse)
        TextView textViewCourse;

        @Bind(R.id.lblDoB)
        TextView textViewDOB;

        public SearchResultViewHolder(final View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(final Cursor cursor)
        {
            final String name = cursor.getString(cursor.getColumnIndex(DataContract.Students.NAME));
            this.textViewName.setText(name);

            final String course = cursor.getString(cursor.getColumnIndex(DataContract.Students.COURSE));
            this.textViewCourse.setText(course);

            final String dob = cursor.getString(cursor.getColumnIndex(DataContract.Students.DATE_OF_BIRTH));
            this.textViewDOB.setText(dob);

        }
    }

    public interface OnItemClickListener
    {
        void onItemClicked(Cursor cursor);
    }
}