package de.tobiaserthal.akgbensheim.utils;

import android.support.v4.view.ViewPager;

public class PageChangeAdapter implements ViewPager.OnPageChangeListener {
    public static final String TAG = "PageChangeAdapter";

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Log.i(TAG, "Page scrolled. Position: %d, offset: %f, pixelOffset: %d", position, positionOffset, positionOffsetPixels);

        if(positionOffset != 0f && positionOffset != 1f)
            onPageTransition(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        // Log.i(TAG, "Page at position: %d selected.", position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // Log.i(TAG, "Page scroll state changed to state: %d", state);
    }

    public void onPageTransition(int position, float offset) {

    }
}