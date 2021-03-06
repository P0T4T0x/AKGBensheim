package de.tobiaserthal.akgbensheim.home.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.backend.provider.event.EventCursor;
import de.tobiaserthal.akgbensheim.backend.provider.homework.HomeworkCursor;
import de.tobiaserthal.akgbensheim.backend.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.backend.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.home.HomeCallbacks;
import de.tobiaserthal.akgbensheim.utils.FileHelper;

import static de.tobiaserthal.akgbensheim.base.MainNavigation.*;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CursorViewHolder> {
    private static final String TAG = "HomeAdapter";

    private Context context;
    private HomeCallbacks callbacks;
    private SimpleDateFormat todoDateFormat;
    private SimpleDateFormat eventDateFormat;
    private SparseArrayCompat<Cursor> cursorList;

    public HomeAdapter(Context context) {
        setHasStableIds(true);

        this.context = context;
        this.cursorList = new SparseArrayCompat<>();
        this.todoDateFormat = new SimpleDateFormat(
                context.getString(R.string.detail_homework_summary_todoDate), Locale.getDefault());
        this.eventDateFormat = new SimpleDateFormat(
                "EEE", Locale.getDefault());
    }

    public void setCallbacks(HomeCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public CursorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CursorViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_list_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(CursorViewHolder holder, int position) {
        Cursor cursor = getCursorAt(position);
        switch (holder.getItemViewType()) {
            case FRAGMENT_EVENT:
                holder.bindTitle(R.string.fragment_title_events);
                holder.bindCursor(EventCursor.wrap(cursor));
                holder.bindSubtitle(
                        MessageFormat.format(
                                context.getString(R.string.detail_subtitle_homeItem),
                                cursor.getCount()
                        )
                );
                break;

            case FRAGMENT_HOMEWORK:
                holder.bindTitle(R.string.fragment_title_homework);
                holder.bindCursor(HomeworkCursor.wrap(cursor));
                break;

            case FRAGMENT_NEWS:
                holder.bindTitle(R.string.fragment_title_news);
                holder.bindCursor(NewsCursor.wrap(cursor));
                holder.bindSubtitle(
                        MessageFormat.format(
                                context.getString(R.string.detail_subtitle_homeItem),
                                cursor.getCount()
                        )
                );
                break;

            case FRAGMENT_SUBSTITUTION:
                holder.bindTitle(R.string.fragment_title_subst);
                holder.bindCursor(SubstitutionCursor.wrap(cursor));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cursorList != null ?
                cursorList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return getItemViewType(position);
    }

    @Override
    @NavigationItem
    public int getItemViewType(int position) {
        //noinspection ResourceType
        return cursorList != null ?
                cursorList.keyAt(position) : 0;
    }

    public Cursor getCursor(int id) {
        return cursorList != null ?
                cursorList.get(id) : null;
    }

    public Cursor getCursorAt(int position) {
        return cursorList != null ?
                cursorList.valueAt(position) : null;
    }

    public Cursor swapCursor(@NavigationItem int id, Cursor cursor) {
        Cursor oldCursor = getCursor(id);
        if(cursor == oldCursor) {
            return null;
        }

        if(cursor != null && cursor.getCount() > 0) {
            if(cursorList.indexOfKey(id) >= 0) {
                cursorList.put(id, cursor);
                notifyItemChanged(cursorList.indexOfKey(id));
            } else {
                cursorList.put(id, cursor);
                notifyItemInserted(cursorList.indexOfKey(id));
            }
        } else {
            int pos = cursorList.indexOfKey(id);
            if(pos >= 0) {
                cursorList.remove(id);
                notifyItemRemoved(pos);
            } else {
                notifyDataSetChanged();
            }
        }

        return oldCursor;
    }

    public void changeCursor(int id, Cursor cursor) {
        Cursor oldCursor = swapCursor(id, cursor);
        if(oldCursor != null) {
            oldCursor.close();
        }
    }


    class CursorViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView txtTitle;
        private TextView txtSubtitle;

        private LinearLayout layHeader;
        private LinearLayout layItems;

        public CursorViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.home_list_item_title);
            txtSubtitle = (TextView) itemView.findViewById(R.id.home_list_item_subtitle);

            layHeader = (LinearLayout) itemView.findViewById(R.id.home_list_header_layout);
            layItems = (LinearLayout) itemView.findViewById(R.id.home_list_items_layout);
        }

        public void bindTitle(String title) {
            txtTitle.setText(title);

            Bundle extras = new Bundle();
            extras.putInt("_type", getItemViewType());
            layHeader.setTag(extras);
            layHeader.setOnClickListener(this);
        }

        public void bindTitle(@StringRes int stringRes) {
            txtTitle.setText(stringRes);

            Bundle extras = new Bundle();
            extras.putInt("_type", getItemViewType());
            layHeader.setTag(extras);
            layHeader.setOnClickListener(this);
        }

        public void bindSubtitle(String subtitle) {
            txtSubtitle.setText(subtitle);
        }

        public void bindCursor(EventCursor cursor) {
            layItems.removeAllViews();
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(R.layout.home_list_item_text3, layItems, false);

                TextView icon = (TextView) listItem.findViewById(android.R.id.icon);
                icon.setText(eventDateFormat.format(cursor.getEventDate()));
                icon.setBackgroundColor(ContextCompat.getColor(listItem.getContext(), R.color.primaryDark));

                ((TextView) listItem.findViewById(android.R.id.text1)).setText(cursor.getTitle());
                ((TextView) listItem.findViewById(android.R.id.text2)).setText(cursor.getDateString());

                Bundle extras = new Bundle();
                extras.putLong("_id", cursor.getId());
                extras.putInt("_type", getItemViewType());
                listItem.setTag(extras);

                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        public void bindCursor(NewsCursor cursor) {
            layItems.removeAllViews();
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(R.layout.home_list_item_text2, layItems, false);

                ((TextView) listItem.findViewById(android.R.id.text1)).setText(cursor.getTitle());
                ((TextView) listItem.findViewById(android.R.id.text2)).setText(FileHelper.removeProtocol(cursor.getArticleUrl()));

                Picasso.with(listItem.getContext())
                        .load(cursor.getImageUrl())
                        .fit().centerCrop()
                        .into((ImageView) listItem.findViewById(android.R.id.icon));

                Bundle extras = new Bundle();
                extras.putLong("_id", cursor.getId());
                extras.putInt("_type", getItemViewType());
                listItem.setTag(extras);

                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        public void bindCursor(HomeworkCursor cursor) {
            layItems.removeAllViews();
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(android.R.layout.simple_list_item_2, layItems, false);

                ((TextView) listItem.findViewById(android.R.id.text1)).setText(
                        cursor.getTitle());
                ((TextView) listItem.findViewById(android.R.id.text2)).setText(
                        todoDateFormat.format(cursor.getTodoDate()));

                Bundle extras = new Bundle();
                extras.putLong("_id", cursor.getId());
                extras.putInt("_type", getItemViewType());
                listItem.setTag(extras);

                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        public void bindCursor(SubstitutionCursor cursor) {
            layItems.removeAllViews();
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(R.layout.home_list_item_text3, layItems, false);

                TextView icon = (TextView) listItem.findViewById(android.R.id.icon);
                TextView text1 = (TextView) listItem.findViewById(android.R.id.text1);
                TextView text2 = (TextView) listItem.findViewById(android.R.id.text2);


                int color = PreferenceProvider.getInstance()
                        .getColorFromType(cursor.getType());

                icon.setBackgroundColor(color);
                icon.setText(cursor.getLesson());

                text1.setText(cursor.getType());
                text2.setText(listItem.getContext().getString(
                                R.string.detail_subst_summary,
                                cursor.getPeriod(),
                                cursor.getLessonSubst(),
                                cursor.getRoomSubst())
                );

                Bundle extras = new Bundle();
                extras.putLong("_id", cursor.getId());
                extras.putInt("_type", getItemViewType());
                extras.putInt("color", color);
                listItem.setTag(extras);

                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        @Override
        public void onClick(View v) {
            Bundle tag = (Bundle) v.getTag();
            int type = tag.getInt("_type", -1);
            long id = tag.getLong("_id", -1L);

            if(id < 1) {
                Log.d(TAG, "Item clicked for type: %d", type);
                if(callbacks != null) {
                    //noinspection ResourceType
                    callbacks.onItemClicked(type, tag);
                }
            } else {
                Log.d(TAG, "Sub item clicked for type: %d with id: %d", type, id);
                if (callbacks != null) {
                    //noinspection ResourceType
                    callbacks.onSubItemClicked(type, id, tag);
                }
            }
        }
    }
}
