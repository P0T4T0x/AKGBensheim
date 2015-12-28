package de.tobiaserthal.akgbensheim.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.provider.teacher.TeacherCursor;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.base.adapter.BaseViewHolder;
import de.tobiaserthal.akgbensheim.base.adapter.SectionCursorAdapter;

public class TeacherAdapter extends SectionCursorAdapter<TeacherCursor, Character,
        TeacherAdapter.ItemViewHolder, TeacherAdapter.SectionViewHolder> {

    public static final String TAG = "TeacherAdapter";
    private AdapterClickHandler callbacks;

    public TeacherAdapter(Context context, TeacherCursor cursor) {
        super(context, cursor, 0);
    }

    public void setOnClickListener(AdapterClickHandler clickListener) {
        this.callbacks = clickListener;
    }

    @Override
    protected Character getSectionFromCursor(TeacherCursor cursor) throws IllegalStateException {
        return Character.toUpperCase(
                cursor.getLastName().charAt(0));
    }

    @Override
    protected SectionViewHolder onCreateSectionViewHolder(ViewGroup parent) {
        return new SectionViewHolder(
                LayoutInflater.from(getContext())
                        .inflate(R.layout.teacher_list_section, parent, false)
        );
    }

    @Override
    protected ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(
                LayoutInflater.from(getContext())
                        .inflate(R.layout.teacher_list_item, parent, false)
        );
    }

    class ItemViewHolder extends BaseViewHolder<TeacherCursor>
            implements View.OnClickListener {

        TextView txtShorthand;
        TextView txtName;
        TextView txtSubjects;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtSubjects = (TextView) itemView.findViewById(R.id.txtSubjects);
            txtShorthand = (TextView) itemView.findViewById(R.id.icnShorthand);
        }

        @Override
        public void bind(TeacherCursor data) {
            txtName.setText(getContext().getString(
                    R.string.detail_teacher_name,
                    data.getFirstName(),
                    data.getLastName())
            );

            txtSubjects.setText(data.getSubjects());
            txtShorthand.setText(data.getShorthand());
        }

        @Override
        public void onClick(View v) {
            if(callbacks != null)
                callbacks.onClick(v, getCursorPositionWithoutSections(getAdapterPosition()), getItemId());
        }
    }

    class SectionViewHolder extends BaseViewHolder<Character> {
        TextView txtChar;

        public SectionViewHolder(View itemView) {
            super(itemView);
            txtChar = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void bind(Character data) {
            txtChar.setText(data.toString());
        }
    }
}
