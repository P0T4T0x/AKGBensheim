package de.tobiaserthal.akgbensheim.ui.drawer;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.data.Log;


/**
 * An adapter for the recycler view in the navigation drawer
 */
public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "DrawerAdapter";

    private static final int VIEW_TYPE_HEADER_VIEW = 0x0;
    private static final int VIEW_TYPE_SECTION_VIEW = 0x1;
    private static final int VIEW_TYPE_ITEM_VIEW = 0x2;
    private List<Item> navigationDrawerItems;

    private int headerId;
    private String heading;
    private String description;
    private Drawable backgroundResource;

    private DrawerCallbacks callbacks;
    private SparseBooleanArray selectionMap;

    /**
     * Creates a new NavDrawerAdapter
     * @param heading The string to display as the
     */
    public DrawerAdapter(int headerId, String heading, String description, Drawable backgroundResource) {
        this.selectionMap = new SparseBooleanArray();
        this.navigationDrawerItems = new ArrayList<>();

        this.headerId = headerId;
        this.heading = heading;
        this.description = description;
        this.backgroundResource = backgroundResource;
    }

    /**
     * Add a new drawer item to the navigation stack
     * @param id the item's id
     * @param title The title of the drawer object
     * @param count The counter string to display or {@code null}
     * @param icon The icon resource
     * @return The index this item was inserted
     */
    public int addItem(int id, String title, int icon, String count) {
        this.navigationDrawerItems.add(
                new DrawerItem(id, title, icon, count != null, count)
        );

        return this.navigationDrawerItems.size() - 1;
    }

    /**
     * Add a new drawer item to the navigation stack
     * @param title The title of the drawer object
     * @param icon The icon resource
     * @return The index this item was inserted
     */
    public int addItem(int id, String title, int icon) {
        return this.addItem(id, title, icon, null);
    }

    /**
     * Add a new special drawer item to the navigation stack
     * @param id the item's id
     * @param title The title of the drawer object
     * @param count The counter string to display or {@code null}
     * @param icon The icon resource
     * @return The index this item was inserted
     */
    public int addSpecialItem(int id, String title, int icon, String count) {
        this.navigationDrawerItems.add(
                new DrawerItem(id, title, icon, count != null, count, false)
        );

        return this.navigationDrawerItems.size() - 1;
    }

    /**
     * Add a new special drawer item to the navigation stack
     * @param id The item's id
     * @param title The title of the drawer object
     * @param icon The icon resource
     * @return The index this item was inserted
     */
    public int addSpecialItem(int id, String title, int icon) {
        return this.addSpecialItem(id, title, icon, null);
    }

    /**
     * Add a new section item to the navigation stack
     * @param title The title of the drawer object or {@code null}
     * @return The index this item was inserted
     */
    public int addSection(String title) {
        this.navigationDrawerItems.add(new DrawerSection(title));
        return this.navigationDrawerItems.size() - 1;
    }

    /**
     * Add a new section item without a title to the navigation stack
     * @return The index this item was inserted
     */
    public int addSection() {
        return this.addSection(null);
    }

    /**
     * Set the navigation drawer callbacks
     * @param clickListener The callback which implements the callback interface
     * @see DrawerCallbacks
     */
    public void setOnItemClickListener(DrawerCallbacks clickListener) {
        this.callbacks = clickListener;
    }

    /**
     * Set the item's selected state at the specific index to true
     * @param index The index of the item in the array committed to this adapter
     */
    public void selectPosition(int index) {
        int oldPos = getFirstSelectedPosition();
        int newPos = itemIndexToAdapterIndex(index, 1);

        if(oldPos > -1) {
            deselect(oldPos);
        }

        select(newPos);

        if(callbacks != null) {
            callbacks.onNavigationItemSelected(index, newPos, newPos == oldPos);
        }
    }

    /**
     * Get the position of the first selected item
     * @return The position of the item. Use {@link #adapterIndexToItemIndex(int)} to get the item index
     */
    public int getFirstSelectedPosition() {
        int index = selectionMap.indexOfValue(true);
        if(index < 0)
            return index;

        return selectionMap.keyAt(index);
    }

    public void selectId(int id) {
        int newPos = -1;

        for(int i = 1; i < navigationDrawerItems.size() + 1; i++) {
            if(getItem(i).isCheckable()
                    && getItem(i).getId() == id) {
                newPos = i;
                break;
            }
        }

        if(newPos < 1) {
            return;
        }

        int index = adapterIndexToItemIndex(newPos);
        int oldPos = getFirstSelectedPosition();
        if(oldPos > -1) {
            deselect(oldPos);
        }

        select(newPos);

        if(callbacks != null) {
            callbacks.onNavigationItemSelected(index, newPos, newPos == oldPos);
        }
    }

    /**
     * Check whether the item at the specified position is selected
     * @param position The position of the item. Use {@link #itemIndexToAdapterIndex(int, int)}
     *                 to get the absolute adapter position from the item index
     * @return Whether the item is selected or not.
     */
    public boolean isSelected(int position) {
        return selectionMap.get(position, false);
    }

    /**
     * Select the item at the absolute adapter position
     * @param position The position of the item. Use {@link #itemIndexToAdapterIndex(int, int)}
     *                 to get the absolute adapter position from the item index
     */
    public void select(int position) {
        selectionMap.put(position, true);
        notifyItemChanged(position);
    }

    /**
     * Deselect the item at the absolute adapter position
     * @param position The position of the item. Use {@link #itemIndexToAdapterIndex(int, int)}
     *                 to get the absolute adapter position from the item index
     */
    public void deselect(int position) {
        selectionMap.delete(position);
        notifyItemChanged(position);
    }

    /**
     * Converts the absolute index of the current non-section item to an nth-child-of-type index
     * @param index The absolute item index of the item
     * @return The nth-of-type position of the item at this index or of the last item of this type
     */
    public int adapterIndexToItemIndex(int index) {
        int ret = 0;

        for (int i = 1; i < index; i++) {
            if(getItem(i).isSection())
                continue;
            ret ++;
        }

        return ret;
    }

    /**
     * Converts a nth-of-type index back to an absolute index
     * @param index The nth-of-type index of the item
     * @param def The default value to return
     * @return The absolute item index of the item or {@code def} if not found.
     */
    public int itemIndexToAdapterIndex(int index, int def) {
        int counter = 0;
        for(int i = 1; i < getItemCount(); i ++) {
            if(getItem(i).isSection())
                continue;

            if(counter >= index)
                return i;

            counter ++;
        }

        return def;
    }

    public void updateItemCount(int index, int count) {
        int pos = itemIndexToAdapterIndex(index, 1);

        DrawerItem item = (DrawerItem) getItem(pos);
        item.setCounterVisibility(count > 0);
        item.setCount(String.valueOf(count));

        notifyItemChanged(pos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case VIEW_TYPE_HEADER_VIEW:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.drawer_list_header, parent, false);
                viewHolder = new HeaderViewHolder(itemView);
                break;
            case VIEW_TYPE_SECTION_VIEW:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.drawer_list_section, parent, false);
                viewHolder = new SectionViewHolder(itemView);
                break;
            case VIEW_TYPE_ITEM_VIEW:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.drawer_list_item, parent, false);
                viewHolder = new ItemViewHolder(itemView);
                break;
            default:
                return null;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER_VIEW:
                ((HeaderViewHolder) viewHolder).txtHeading.setText(heading);
                ((HeaderViewHolder) viewHolder).txtDescription.setText(description);
                ((HeaderViewHolder) viewHolder).imgBackground.setImageDrawable(backgroundResource);
                break;
            case VIEW_TYPE_ITEM_VIEW:
                DrawerItem drawerItem = (DrawerItem) getItem(position);
                ((ItemViewHolder) viewHolder).txtTitle.setText(drawerItem.getTitle());
                ((ItemViewHolder) viewHolder).imgIcon.setImageResource(drawerItem.getIcon());

                if(drawerItem.getCounterVisibility()) {
                    ((ItemViewHolder) viewHolder).txtCounter.setVisibility(View.VISIBLE);
                    ((ItemViewHolder) viewHolder).txtCounter.setText(drawerItem.getCount());
                } else {
                    ((ItemViewHolder) viewHolder).txtCounter.setVisibility(View.GONE);
                }

                /* Set the selection state */
                ((ItemViewHolder) viewHolder).itemView.setSelected(isSelected(position));
                break;
            case VIEW_TYPE_SECTION_VIEW:
                DrawerSection sectionItem = (DrawerSection) getItem(position);

                if(sectionItem.getTitle() != null) {
                    ((SectionViewHolder) viewHolder).txtTitle.setVisibility(View.VISIBLE);
                    ((SectionViewHolder) viewHolder).txtTitle.setText(sectionItem.getTitle());
                } else {
                    ((SectionViewHolder) viewHolder).txtTitle.setVisibility(View.GONE);
                }
                break;
        }
    }

    public Item getItem(int index) {
        return navigationDrawerItems.get(index - 1);
    }

    public Item getItemWithIndex(int index) {
        return navigationDrawerItems.get(index);
    }

    public boolean isCheckable(int index) {
        return getItem(index).isCheckable();
    }

    @Override
    public int getItemCount() {
        /* return length + 1 for header view */
        return navigationDrawerItems.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        if(position == 0) {
            return headerId;
        }

        return navigationDrawerItems.get(position - 1).getId();
    }

    @Override
    public int getItemViewType(int index) {
        if(index == 0)
            return VIEW_TYPE_HEADER_VIEW;
        else if (getItem(index).isSection())
            return VIEW_TYPE_SECTION_VIEW;
        else
            return VIEW_TYPE_ITEM_VIEW;
    }

    private class HeaderViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtHeading;
        TextView txtDescription;
        ImageView imgBackground;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            txtHeading = (TextView) itemView.findViewById(R.id.drawer_header_item_heading);
            txtDescription = (TextView) itemView.findViewById(R.id.drawer_header_item_description);
            imgBackground = (ImageView) itemView.findViewById(R.id.drawer_header_item_image);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Header item in recycler view clicked!");

            if(callbacks != null) {
                callbacks.onNavigationItemSelected(-1, 0, false);
            }
        }
    }

    private class ItemViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtTitle;
        TextView txtCounter;
        ImageView imgIcon;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            txtTitle = (TextView) itemView.findViewById(R.id.drawer_list_item_title);
            txtCounter = (TextView) itemView.findViewById(R.id.drawer_list_item_counter);
            imgIcon = (ImageView) itemView.findViewById(R.id.drawer_list_item_icon);
        }

        @Override
        public void onClick(View v) {
            int oldPos = getFirstSelectedPosition();
            int newPos = getAdapterPosition();
            int index = adapterIndexToItemIndex(newPos);

            Log.i(TAG, "Navigation item in recycler view clicked at position: %d with index: %d", newPos, index);
            boolean special = !isCheckable(newPos);
            if(!special) {
                deselect(oldPos);
                select(newPos);
            }

            if(callbacks != null) {
                callbacks.onNavigationItemSelected(index, newPos, oldPos == newPos);
            }
        }
    }

    private class SectionViewHolder
            extends RecyclerView.ViewHolder{

        TextView txtTitle;

        public SectionViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.drawer_section_item_title);
        }
    }
}
