package de.tobiaserthal.akgbensheim;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tobiaserthal.akgbensheim.ui.base.ToolbarFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends ToolbarFragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

}
