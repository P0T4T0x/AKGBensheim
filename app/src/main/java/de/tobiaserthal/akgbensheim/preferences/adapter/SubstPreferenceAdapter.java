package de.tobiaserthal.akgbensheim.preferences.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.base.adapter.BaseViewHolder;

public class SubstPreferenceAdapter extends RecyclerView.Adapter<SubstPreferenceAdapter.ItemViewHolder> {

    private ArrayList<String> data;

    public SubstPreferenceAdapter(Collection<String> data) {
        this.data = new ArrayList<>();
        if(data != null) {
            this.data.addAll(data);
        }
    }

    public SubstPreferenceAdapter(String[] data) {
        this.data = new ArrayList<>();

        if(data != null) {
            this.data.addAll(Arrays.asList(data));
        }
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            builder.append(data.get(i));

            if(i < data.size() - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getItem(int position) {
        return data.get(position);
    }

    public void addItem(String item) {
        data.add(0, item);
        notifyItemInserted(0);
    }

    public void removeItem(String item) {
        int index = data.indexOf(item);
        removeItem(index);
    }

    public void removeItem(int index) {
        if(index >= 0) {
            data.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void moveItem(int fromIndex, int toIndex) {
        if(fromIndex >= 0) {
            String item = data.remove(fromIndex);
            data.add(toIndex, item);

            notifyItemMoved(fromIndex, toIndex);
        }
    }

    public void moveItem(String item, int toIndex) {
        int index = data.indexOf(item);
        moveItem(index, toIndex);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.basic_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ItemViewHolder extends BaseViewHolder<String> {
        TextView txt1;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txt1 = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void bind(String text) {
            txt1.setText(text);
        }
    }
}
