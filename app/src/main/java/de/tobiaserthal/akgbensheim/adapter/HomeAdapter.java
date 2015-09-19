package de.tobiaserthal.akgbensheim.adapter;

import android.content.Context;
import android.database.Cursor;
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

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.HomeCallbacks;
import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.provider.news.NewsCursor;
import de.tobiaserthal.akgbensheim.data.provider.event.EventCursor;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkCursor;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.tools.ColorUtil;
import de.tobiaserthal.akgbensheim.tools.FileUtils;

import static de.tobiaserthal.akgbensheim.MainNavigation.NavigationItem;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_EVENT;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_HOMEWORK;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_NEWS;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_SUBSTITUTION;


/**
 * Created by tobiaserthal on 06.09.15.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CursorViewHolder> {
    private static final String TAG = "HomeAdapter";

    private SparseArrayCompat<Cursor> cursorList;
    private HomeCallbacks callbacks;
    private SimpleDateFormat todoDateFormat;
    private SimpleDateFormat eventDateFormat;

    public HomeAdapter(Context context) {
        setHasStableIds(true);

        this.cursorList = new SparseArrayCompat<>();
        this.todoDateFormat = new SimpleDateFormat(
                context.getString(R.string.homework_todo_date_string), Locale.getDefault());
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
                holder.bindSubtitle(cursor.getCount() + " items to show");
                holder.bindCursor(EventCursor.wrap(cursor));
                break;

            case FRAGMENT_HOMEWORK:
                holder.bindTitle(R.string.fragment_title_homework);
                holder.bindCursor(HomeworkCursor.wrap(cursor));
                break;

            case FRAGMENT_NEWS:
                holder.bindTitle(R.string.fragment_title_news);
                holder.bindSubtitle(cursor.getCount() + " items to show");
                holder.bindCursor(NewsCursor.wrap(cursor));
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
        private LinearLayout layItems;

        public CursorViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.home_list_item_title);
            txtSubtitle = (TextView) itemView.findViewById(R.id.home_list_item_subtitle);
            layItems = (LinearLayout) itemView.findViewById(R.id.home_list_items_layout);

            // TODO
        }

        public void bindTitle(String title) {
            txtTitle.setText(title);
        }

        public void bindTitle(@StringRes int stringRes) {
            txtTitle.setText(stringRes);
        }

        public void bindSubtitle(String subtitle) {
            txtSubtitle.setText(subtitle);
        }

        public void bindCursor(EventCursor cursor) {
            layItems.removeAllViews();

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(R.layout.home_list_item_text3, layItems, false);

                TextView icon = (TextView) listItem.findViewById(android.R.id.icon);
                icon.setText(eventDateFormat.format(cursor.getEventDate()));
                icon.setBackgroundColor(ContextCompat.getColor(listItem.getContext(), R.color.primaryDark));

                ((TextView) listItem.findViewById(android.R.id.text1)).setText(cursor.getTitle());
                ((TextView) listItem.findViewById(android.R.id.text2)).setText(cursor.getDateString());

                listItem.setTag(cursor.getId());
                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        public void bindCursor(NewsCursor cursor) {
            layItems.removeAllViews();

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(R.layout.home_list_item_text2, layItems, false);

                ((TextView) listItem.findViewById(android.R.id.text1)).setText(cursor.getTitle());
                ((TextView) listItem.findViewById(android.R.id.text2)).setText(FileUtils.removeProtocol(cursor.getArticleUrl()));

                Picasso.with(listItem.getContext())
                        .load(cursor.getImageUrl())
                        .fit().centerCrop()
                        .into((ImageView) listItem.findViewById(android.R.id.icon));

                listItem.setTag(cursor.getId());
                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        public void bindCursor(HomeworkCursor cursor) {
            layItems.removeAllViews();

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(android.R.layout.simple_list_item_2, layItems, false);

                ((TextView) listItem.findViewById(android.R.id.text1)).setText(
                        cursor.getTitle());
                ((TextView) listItem.findViewById(android.R.id.text2)).setText(
                        todoDateFormat.format(cursor.getTodoDate()));

                listItem.setTag(cursor.getId());
                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        public void bindCursor(SubstitutionCursor cursor) {
            layItems.removeAllViews();

            while (cursor.moveToNext()) {
                View listItem = LayoutInflater.from(layItems.getContext())
                        .inflate(R.layout.home_list_item_text3, layItems, false);

                TextView icon = (TextView) listItem.findViewById(android.R.id.icon);
                TextView text1 = (TextView) listItem.findViewById(android.R.id.text1);
                TextView text2 = (TextView) listItem.findViewById(android.R.id.text2);

                icon.setText(cursor.getLesson());
                icon.setBackgroundColor(ColorUtil.getInstance(listItem.getContext())
                        .getColorFromSubstType(cursor.getType()));

                text1.setText(cursor.getType());
                text2.setText(listItem.getContext().getString(
                                R.string.home_summary_subst,
                                cursor.getPeriod(),
                                cursor.getLessonSubst(),
                                cursor.getRoomSubst())
                );

                listItem.setTag(cursor.getId());
                listItem.setOnClickListener(this);
                layItems.addView(listItem);
            }
        }

        @Override
        public void onClick(View v) {
            int type = getItemViewType();
            long id = (long) v.getTag();

            Log.d(TAG, "Sub item clicked for type: %d with id: %d", type, id);
            if(callbacks != null) {
                //noinspection ResourceType
                callbacks.onSubItemClicked(type, id);
            }
        }
    }
}
