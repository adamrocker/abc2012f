package com.adamrocker.abc2012f.fragment;

import com.adamrocker.abc2012f.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabOneFragment extends TabFragment {
 
    public static final String ARG_PAGE = "arg_page";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_one_f, null);
        //int index = getArguments().getInt(ARG_PAGE);
        //int index = 0;
        //TextView tv = (TextView) view.findViewById(R.id.text);
        //tv.setText("PAGE: " + index);
        return view;
    }

    @Override
    public int getTitleId() {
        return R.string.tab_label0;
    }

    @Override
    public int getIconId() {
        return R.drawable.icon_tab_recent;
    }
 
}

