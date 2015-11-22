package de.tobiaserthal.akgbensheim.preferences;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.adapter.SyncPreferenceAdapter;
import de.tobiaserthal.akgbensheim.adapter.tools.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider.PeriodicSyncPreference;
import de.tobiaserthal.akgbensheim.ui.RecyclerFragment;
import de.tobiaserthal.akgbensheim.ui.widget.DividerItemDecoration;

public class SyncPreferenceFragment extends RecyclerFragment<SyncPreferenceAdapter> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the adapter
        ArrayList<PeriodicSyncPreference> dummies = PreferenceProvider.getInstance(getContext()).getAllSyncs();
        setAdapter(new SyncPreferenceAdapter(getContext(), dummies));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLayoutManager(new LinearLayoutManager(getActivity()));
        getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity(), null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);

        getAdapter().setClickHandler(new AdapterClickHandler() {
            @Override
            public void onClick(View view, final int position, long id) {
                final PeriodicSyncPreference dummy = getAdapter().getItem(position);
                new MaterialDialog.Builder(getContext())
                        .title(R.string.pref_prompt_sync_frequency)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .items(PreferenceProvider.getInstance(getContext()).getFrequencySummaries())
                        .itemsCallbackSingleChoice(PreferenceProvider.getInstance(getContext())
                                        .getFrequencyIndex(dummy.getFrequency()),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        dummy.setFrequency(PreferenceProvider.getInstance(getContext()).getFrequency(which));
                                        dummy.setSubtitle(PreferenceProvider.getInstance(getContext()).getFrequencySummary(which));

                                        getAdapter().notifyItemChanged(position);
                                        return true;
                                    }
                                })
                        .build()
                        .show();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceProvider.getInstance(getContext())
                .dispatchAllSyncs(getAdapter().getItems());
    }
}
