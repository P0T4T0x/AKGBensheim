package de.tobiaserthal.akgbensheim.adapter.tools;

import android.view.View;

/**
 * An interface to respond to click events on this adapter's items
 */
public abstract class AdapterClickHandler {
    public void onClick(View view, int position, long id) {}
    public void onLongClick(View view, int position, long id) {}
}
