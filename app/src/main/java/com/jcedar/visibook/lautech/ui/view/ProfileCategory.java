package com.jcedar.visibook.lautech.ui.view;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.jcedar.visibook.lautech.R;
import com.jcedar.visibook.lautech.helper.AccountUtils;

/**
 * Created by user1 on 18/08/2014.
 */
public class ProfileCategory extends PreferenceCategory {
    TextView fullname;
    TextView username;
    TextView organisation;
    private Context mContext;
    public ProfileCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setLayoutResource(R.layout.fragment_profile_view);
    }



    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        fullname = (TextView) view.findViewById(R.id.fullName);
        fullname.setText(AccountUtils.getFullName(mContext));
        username = (TextView) view.findViewById(R.id.username);
        username.setText(AccountUtils.getChosenAccountName(mContext));
        organisation = (TextView) view.findViewById(R.id.organization);
        if(AccountUtils.getRole(mContext).equalsIgnoreCase("ServiceProvider")){
            organisation.setText("Service Provider");
        }else{
            organisation.setText(AccountUtils.getRole(mContext));
        }



    }
}
