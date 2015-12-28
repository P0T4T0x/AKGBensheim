package de.tobiaserthal.akgbensheim.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Binds the view holder and its items to the specified
     * set of data passed to this method.
     * @param data The specified set of data
     */
    public void bind(T data) {
        // empty body
    }
}
