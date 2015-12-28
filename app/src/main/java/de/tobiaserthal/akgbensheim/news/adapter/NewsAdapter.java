package de.tobiaserthal.akgbensheim.news.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.adapter.BaseViewHolder;
import de.tobiaserthal.akgbensheim.base.adapter.CursorAdapter;

/**
 * Simple adapter to manage items in the recycler view
 */
public class NewsAdapter extends CursorAdapter<NewsCursor, NewsAdapter.NewsViewHolder> {
    public static final String TAG = "NewsAdapter";

    private AdapterClickHandler callbacks;

    public NewsAdapter(Context context, NewsCursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder viewHolder, int position) {
        moveCursorOrThrow(position);
        viewHolder.bind(getCursor());
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NewsViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.news_list_item, parent, false)
            );
    }

    public void setOnClickListener(AdapterClickHandler clickListener) {
        this.callbacks = clickListener;
    }

    class NewsViewHolder extends BaseViewHolder<NewsCursor>
            implements View.OnClickListener {

        TextView txtHeading;
        TextView txtSnippet;
        ImageView imageView;

        public NewsViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            txtHeading = (TextView) itemView.findViewById(R.id.news_list_item_title);
            txtSnippet = (TextView) itemView.findViewById(R.id.news_list_item_subtitle);
            imageView = (ImageView) itemView.findViewById(R.id.news_list_item_image);
        }

        @Override
        public void bind(NewsCursor cursor) {
            txtHeading.setText(cursor.getTitle());
            txtSnippet.setText(cursor.getSnippet());

            String url = cursor.getImageUrl();
            if(!TextUtils.isEmpty(url)) {
                imageView.setVisibility(View.VISIBLE);

                Picasso.with(getContext())
                        .load(url)
                        .fit().centerCrop()
                        .into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d(TAG, "News item at position: %d clicked!", position);

            if(callbacks != null) {
                callbacks.onClick(itemView, position, getItemId());
            }
        }
    }
}
