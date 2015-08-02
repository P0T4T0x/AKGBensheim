package de.tobiaserthal.akgbensheim.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.adapter.tools.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.adapter.tools.BaseViewHolder;
import de.tobiaserthal.akgbensheim.adapter.tools.SectionCursorAdapter;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionCursor;
import de.tobiaserthal.akgbensheim.subst.SubstFragment;
import de.tobiaserthal.akgbensheim.tools.ColorUtil;

public class SubstAdapter extends SectionCursorAdapter<SubstitutionCursor, Date,
        SubstAdapter.ItemViewHolder, SubstAdapter.SectionViewHolder> {

    public static final String TAG = "SubstAdapter";

    private final int displayMode;
    private AdapterClickHandler callbacks;

    private final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("EEE", Locale.getDefault());
    private final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("dd. MMM", Locale.getDefault());

    public SubstAdapter(Context context, SubstitutionCursor cursor) {
        super(context, cursor, 0);
        this.displayMode = SubstFragment.ALL;
    }

    public SubstAdapter(Context context, SubstitutionCursor cursor, int displayMode) {
        super(context, cursor, 0);
        this.displayMode = displayMode;
    }

    public void setOnClickListener(AdapterClickHandler clickListener) {
        this.callbacks = clickListener;
    }

    @Override
    protected Date getSectionFromCursor(SubstitutionCursor cursor) throws IllegalStateException {
        return cursor.getSubstDate();
    }

    @Override
    protected SectionViewHolder onCreateSectionViewHolder(ViewGroup parent) {
        return new SectionViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.subst_list_section, parent, false)
        );
    }

    @Override
    protected ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.subst_list_item, parent, false)
        );
    }

    class SectionViewHolder extends BaseViewHolder<Date> {
        TextView txtDay;
        TextView txtDate;

        public SectionViewHolder(View itemView) {
            super(itemView);

            txtDay = (TextView) itemView.findViewById(android.R.id.text1);
            txtDate = (TextView) itemView.findViewById(android.R.id.text2);
        }

        @Override
        public void bind(Date item) {
            txtDay.setText(FORMAT_DAY.format(item));
            txtDate.setText(FORMAT_DATE.format(item));
        }
    }

    class ItemViewHolder extends BaseViewHolder<SubstitutionCursor> implements View.OnClickListener{

        View txtDivider;
        TextView txtFormKey;
        TextView txtLesson;
        TextView txtPeriod;
        TextView txtType;
        TextView txtRoomSubst;
        TextView txtLessonSubst;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            txtDivider = itemView.findViewById(R.id.txtDivider);
            txtFormKey = (TextView) itemView.findViewById(android.R.id.text1);
            txtLesson = (TextView) itemView.findViewById(android.R.id.text2);
            txtPeriod = (TextView) itemView.findViewById(R.id.txtPeriod);
            txtType = (TextView) itemView.findViewById(R.id.txtType);
            txtRoomSubst = (TextView) itemView.findViewById(R.id.txtRoomSubst);
            txtLessonSubst = (TextView) itemView.findViewById(R.id.txtLessonSubst);
        }

        @Override
        public void bind(SubstitutionCursor cursor) {
            if(displayMode != SubstFragment.FORM) {
                txtDivider.setVisibility(View.VISIBLE);
                txtFormKey.setVisibility(View.VISIBLE);
                txtFormKey.setText(cursor.getFormKey());
            } else {
                txtDivider.setVisibility(View.GONE);
                txtFormKey.setVisibility(View.GONE);
            }

            txtLesson.setText(cursor.getLesson());
            txtPeriod.setText(
                    getContext().getResources().getString(R.string.subst_list_period, cursor.getPeriod())
            );

            txtRoomSubst.setText(cursor.getRoomSubst());
            txtLessonSubst.setText(cursor.getLessonSubst());

            String type = cursor.getType();
            txtType.setText(type);

            itemView.setTag(ColorUtil.getInstance(getContext()).getColorFromSubstType(type));
            ((CardView) itemView).setCardBackgroundColor((Integer) itemView.getTag());
        }

        @Override
        public void onClick(View v) {
            if(callbacks != null)
                callbacks.onClick(v, getCursorPositionWithoutSections(getAdapterPosition()), getItemId());
        }
    }
}
