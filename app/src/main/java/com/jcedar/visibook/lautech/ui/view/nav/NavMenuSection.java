package com.jcedar.visibook.lautech.ui.view.nav;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcedar.visibook.lautech.R;


public class NavMenuSection implements NavDrawerItem {

	public static final int SECTION_TYPE = 0;

	private int id;
	
	private int label;

	@Override
	public int getType() {
		return SECTION_TYPE;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean updateActionBarTitle() {
		return false;
	}

    @Override
    public boolean isCheckable() {
        return false;
    }

    public View getView( View convertView, ViewGroup parentView, NavDrawerItem navDrawerItem, LayoutInflater inflater ) {
        NavMenuSection menuSection = (NavMenuSection) navDrawerItem ;
        NavMenuSectionHolder navMenuItemHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.navdrawer_section, parentView, false);
           /* TextView labelView = (TextView) convertView
                    .findViewById( R.id.navmenusection_label );*/

            navMenuItemHolder = new NavMenuSectionHolder();
            /*navMenuItemHolder.labelView = labelView ;*/
            convertView.setTag(navMenuItemHolder);
        }

        return convertView ;
    }

    private class NavMenuSectionHolder {
        //private TextView labelView;
        private View view;
    }
}
