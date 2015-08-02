package de.tobiaserthal.akgbensheim.adapter.tools;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import com.tonicartos.superslim.GridSLM;

import java.util.ArrayList;
import java.util.List;

import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.provider.base.AbstractCursor;

/**
 * @param <T> section type.
 * @param <VH> the view holder extending {@code BaseViewHolder<Cursor>} that is bound to the cursor data.
 * @param <SH> the view holder extending {@code BaseViewHolder<<T>>} that is bound to the section data.
 */
public abstract class SectionCursorAdapter<CS extends AbstractCursor, T, VH extends BaseViewHolder<CS>, SH extends BaseViewHolder<T>>
        extends CursorAdapter<CS, BaseViewHolder<?>> implements SectionIndexer {

    public static final String TAG = "SectionCursorAdapter";

    protected static final int VIEW_TYPE_ITEM = 0x0;
    protected static final int VIEW_TYPE_SECTION = 0x1;

    protected SparseArrayCompat<T> sectionMap = new SparseArrayCompat<>();
    protected ArrayList<Integer> sectionIndexList = new ArrayList<>();
    protected Comparator<T> sectionComparator;
    protected Object[] fastScrollItems;

    public SectionCursorAdapter(Context context, CS cursor, int flags) {
        this(context, cursor, flags, new Comparator<T>() {
            @Override
            public boolean equal(T obj1, T obj2) {
                    return (obj1 == null) ?
                        obj2 == null : obj1.equals(obj2);
            }
        });
    }

    public SectionCursorAdapter(Context context, CS cursor, int flags, Comparator<T> comparator) {
        super(context, cursor, flags);
        registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                if (hasValidData()) {
                    buildSections();
                } else {
                    sectionMap.clear();
                    sectionIndexList.clear();
                    fastScrollItems = null;
                }
            }
        });

        setSectionComparator(comparator);
    }

    /**
     * Assign a comparator which will be used to check whether
     * a section is contained in the list of sections. The default implementation
     * will check for null pointers and compare sections using the {@link #equals(Object)} method.
     * @param comparator The comparator to compare section objects.
     */
    public void setSectionComparator(Comparator<T> comparator) {
        this.sectionComparator = comparator;
        buildSections();
    }

    /**
     * If the adapter's cursor is not null then this method will call buildSections(Cursor cursor).
     */
    private void buildSections() {
        if (hasValidData()) {
            moveCursor(-1);
            try {
                sectionMap.clear();
                sectionIndexList.clear();
                fastScrollItems = null;

                appendSections(getCursor());
            } catch (IllegalStateException e) {
                Log.e(TAG, e, "Couldn't build sections. Perhaps you're moving the cursor" +
                        "in #getSectionFromCursor(Cursor)?");
                swapCursor(null);

                sectionMap.clear();
                sectionIndexList.clear();
                fastScrollItems = null;
            }
        }
    }

    protected void appendSections(CS cursor) throws IllegalStateException {
        int cursorPosition = 0;
        while(hasValidData() && cursor.moveToNext()) {
            T section = getSectionFromCursor(cursor);
            if (cursor.getPosition() != cursorPosition)
                throw new IllegalStateException("Do not move the cursor's position in getSectionFromCursor.");
            if (!hasSection(section))
                sectionMap.append(cursorPosition + sectionMap.size(), section);
            cursorPosition++;
        }
    }

    public boolean hasSection(T section) {
        for(int i = 0; i < sectionMap.size(); i++) {
            T obj = sectionMap.valueAt(i);
            if(sectionComparator.equal(obj, section))
                return true;
        }

        return false;
    }

    /**
     * The object which is return will determine what section this cursor position will be in.
     * @return the section from the cursor at its current position.
     * This object will be passed to newSectionView and bindSectionView.
     */
    protected abstract T getSectionFromCursor(CS cursor) throws IllegalStateException;
    protected String getTitleFromSection(T section) {
        return section != null ? section.toString() : "";
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + sectionMap.size();
    }

    @Override
    public long getItemId(int listPosition) {
        if (isSection(listPosition))
            return listPosition;
        else {
            int cursorPosition = getCursorPositionWithoutSections(listPosition);
            return super.getItemId(cursorPosition);
        }
    }

    /**
     * @param listPosition  the position of the current item in the list with sectionMap included
     * @return Whether or not the listPosition points to a section.
     */
    public boolean isSection(int listPosition) {
        return sectionMap.indexOfKey(listPosition) >= 0;
    }

    /**
     * This will map a position in the list adapter (which includes sectionMap) to a position in
     * the cursor (which does not contain sectionMap).
     *
     * @param listPosition the position of the current item in the list with sectionMap included
     * @return the correct position to use with the cursor
     */
    public int getCursorPositionWithoutSections(int listPosition) {
        if (sectionMap.size() == 0) {
            return listPosition;
        } else if (!isSection(listPosition)) {
            int sectionIndex = getSectionForPosition(listPosition);
            if (isListPositionBeforeFirstSection(listPosition, sectionIndex)) {
                return listPosition;
            } else {
                return listPosition - (sectionIndex + 1);
            }
        } else {
            return -1;
        }
    }

    /**
     * Get the section object for the index within the array of sections.
     * @param sectionPosition The section index.
     * @return The specified section object for this position.
     */
    public T getSection(int sectionPosition) {
        if (sectionIndexList.contains(sectionPosition)) {
            return sectionMap.get(sectionIndexList.get(sectionPosition));
        }

        return null;
    }

    /**
     * Returns all indices at which the first item of a section is placed.
     * @return The first index of each section.
     */
    public List<Integer> getSectionListPositions() {
        return sectionIndexList;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndexList.isEmpty()) {
            for (int i = 0; i < sectionMap.size(); i++) {
                sectionIndexList.add(sectionMap.keyAt(i));
            }
        }

        return sectionIndex < sectionIndexList.size() ?
                sectionIndexList.get(sectionIndex) : getItemCount();
    }

    /**
     * Given the list position of an item in the adapter, returns the
     * adapter position of the first item of the section the given item belongs to.
     * @param listPosition The absolute list position.
     * @return The position of the first item of the section.
     */
    public int getFirstSectionPosition(int listPosition) {
        int start = 0;
        for(int i = 0; i <= listPosition; i++) {
            if(isSection(i))
                start = i;
        }

        return start;
    }

    @Override
    public int getSectionForPosition(int listPosition) {
        boolean isSection = false;
        int numPrecedingSections = 0;
        for (int i = 0; i < sectionMap.size(); i++) {
            int sectionPosition = sectionMap.keyAt(i);

            if (listPosition > sectionPosition) {
                numPrecedingSections++;
            } else if (listPosition == sectionPosition) {
                isSection = true;
            } else {
                break;
            }
        }

        return isSection ? numPrecedingSections : Math.max(numPrecedingSections - 1, 0);
    }

    @Override
    public Object[] getSections() {
        if(fastScrollItems == null) {
            fastScrollItems = getSectionLabels();
        }

        return fastScrollItems;
    }

    private Object[] getSectionLabels() {
        if(sectionMap == null)
            return new Object[0];

        String[] ret = new String[sectionMap.size()];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = getTitleFromSection(sectionMap.valueAt(i));
        }

        return ret;
    }

    private boolean isListPositionBeforeFirstSection(int listPosition, int sectionIndex) {
        boolean hasSections = sectionMap != null && sectionMap.size() > 0;
        return sectionIndex == 0 && hasSections && listPosition < sectionMap.keyAt(0);
    }

    @Override
    public final int getItemViewType(int listPosition) {
        return isSection(listPosition) ? VIEW_TYPE_SECTION : VIEW_TYPE_ITEM;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(BaseViewHolder holder, int position) {
        GridSLM.LayoutParams layoutParams = GridSLM.LayoutParams.from(holder.itemView.getLayoutParams());
        layoutParams.setSlm(GridSLM.ID);
        layoutParams.setNumColumns(1);
        layoutParams.setFirstPosition(getFirstSectionPosition(position));
        holder.itemView.setLayoutParams(layoutParams);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ITEM :
                moveCursorOrThrow(getCursorPositionWithoutSections(position));
                onBindItemViewHolder((VH) holder, getCursor());
                break;

            case VIEW_TYPE_SECTION:
                T section = sectionMap.get(position);
                onBindSectionViewHolder((SH) holder, section);
                break;
        }
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SECTION:
                return onCreateSectionViewHolder(parent);

            case VIEW_TYPE_ITEM:
                return onCreateItemViewHolder(parent, viewType);

            default:
                return null;
        }
    }

    protected abstract SH onCreateSectionViewHolder(ViewGroup parent);
    protected abstract VH onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected void onBindSectionViewHolder(SH holder, T section) {
        holder.bind(section);
    }
    protected void onBindItemViewHolder(VH holder, CS cursor) {
        holder.bind(cursor);
    }

    public interface Comparator<T> {
        boolean equal(T obj1, T obj2);
    }
}
