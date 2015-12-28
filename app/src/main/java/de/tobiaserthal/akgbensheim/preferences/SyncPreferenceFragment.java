package de.tobiaserthal.akgbensheim.preferences;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.base.RecyclerFragment;
import de.tobiaserthal.akgbensheim.base.adapter.AdapterClickHandler;
import de.tobiaserthal.akgbensheim.preferences.adapter.SyncPreferenceAdapter;
import de.tobiaserthal.akgbensheim.utils.widget.DividerItemDecoration;

public class SyncPreferenceFragment extends RecyclerFragment<SyncPreferenceAdapter> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the adapter
        ArrayList<PreferenceProvider.PeriodicSyncPreference> dummies = PreferenceProvider.getInstance().getAllSyncs();
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
                final PreferenceProvider.PeriodicSyncPreference dummy = getAdapter().getItem(position);
                new MaterialDialog.Builder(getContext())
                        .title(R.string.action_prompt_title_settingsSelectFrequency)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .items(PreferenceProvider.getInstance().getFrequencySummaries())
                        .itemsCallbackSingleChoice(PreferenceProvider.getInstance()
                                        .getFrequencyIndex(dummy.getFrequency()),
                                new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        dummy.setFrequency(PreferenceProvider.getInstance().getFrequency(which));
                                        dummy.setSubtitle(PreferenceProvider.getInstance().getFrequencySummary(which));

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
        PreferenceProvider.getInstance()
                .dispatchAllSyncs(getAdapter().getItems());
    }
}
