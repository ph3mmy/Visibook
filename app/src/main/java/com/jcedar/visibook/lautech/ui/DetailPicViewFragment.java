package com.jcedar.visibook.lautech.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AppSettings;

/**
 * Created by Seyi.Afolayan on 4/15/2016.
 */
public class DetailPicViewFragment extends Fragment {

    private static final String TAG = DetailPicViewFragment.class.getSimpleName();
    String id;

    public static DetailPicViewFragment newInstance(String id) {
        DetailPicViewFragment fragment = new DetailPicViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);

        fragment.setArguments(bundle);
        return  fragment;

    }

    public DetailPicViewFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( getArguments() != null){
            id = getArguments().getString("ID");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details_pic_view, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.detail_image);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.places_ic_clear));
        toolbar.setNavigationContentDescription("");

        toolbar.setBackgroundColor(getActivity().getResources().getColor(R.color.theme_primary));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        String url = String.format(AppSettings.SERVER_IMAGE_URL + "%s.png", id);
        Log.e(TAG, "image url == " + url);


        Glide.with(getActivity())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.person_image_empty)
                .crossFade()
                .into(imageView);

        return view;
    }


}
