package de.tobiaserthal.akgbensheim.homework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.provider.homework.HomeworkCursor;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.adapter.BaseViewHolder;
import de.tobiaserthal.akgbensheim.base.adapter.CursorAdapter;

public class HomeworkAdapter extends CursorAdapter<HomeworkCursor, HomeworkAdapter.ItemViewHolder> {

    private AdapterClickHandler callbacks;

    public HomeworkAdapter(Context context, HomeworkCursor cursor) {
        super(context, cursor, 0);
    }

    public void setOnClickListener(AdapterClickHandler clickListener) {
        this.callbacks = clickListener;
    }

    @Override
    public HomeworkAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.homework_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(HomeworkAdapter.ItemViewHolder holder, int position) {
        moveCursorOrThrow(position);
        holder.bind(getCursor());
    }

    class ItemViewHolder extends BaseViewHolder<HomeworkCursor>
            implements View.OnClickListener {

        TextView txtDate;
        TextView txtTitle;
        TextView txtNotes;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtNotes = (TextView) itemView.findViewById(R.id.txtNotes);
        }

        @Override
        public void bind(HomeworkCursor cursor) {
            txtDate.setText(SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
                            .format(cursor.getTodoDate()));

            txtTitle.setText(cursor.getTitle());
            txtNotes.setText(cursor.getNotes());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d(TAG, "News item at position: %d clicked!", position);

            if(callbacks != null) {
                callbacks.onClick(itemView, position, getItemId());
            }
        }
    }
}
