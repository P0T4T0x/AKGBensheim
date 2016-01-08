package de.tobiaserthal.akgbensheim.teacher;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.backend.model.ModelUtils;
import de.tobiaserthal.akgbensheim.backend.provider.DataProvider;
import de.tobiaserthal.akgbensheim.backend.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.backend.sync.auth.AuthenticatorService;
import de.tobiaserthal.akgbensheim.backend.utils.Log;
import de.tobiaserthal.akgbensheim.backend.utils.NetworkManager;
import de.tobiaserthal.akgbensheim.base.tabs.TabbedHostFragment;

public class TeacherHostFragment extends TabbedHostFragment {
    private Object syncObserverHandle;
    private SwipeRefreshLayout refreshLayout;

    private final SyncStatusObserver syncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(final int which) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Account account = AuthenticatorService.getAccount(SyncUtils.ACCOUNT_TYPE);
                    boolean syncActive = ((which & ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE)
                            == ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE)
                            && ContentResolver.isSyncActive(account, DataProvider.AUTHORITY);
                    boolean syncPending = ((which & ContentResolver.SYNC_OBSERVER_TYPE_PENDING)
                            == ContentResolver.SYNC_OBSERVER_TYPE_PENDING)
                            && ContentResolver.isSyncPending(account, DataProvider.AUTHORITY);

                    boolean refresh = syncActive || syncPending;
                    Log.d(TAG, "Status change detected. Active: %b, pending: %b, refreshing: %b", syncActive, syncPending, refresh);

                    if (refreshLayout != null) {
                        refreshLayout.setRefreshing(refresh);
                    }
                }
            });
        }
    };

    private final SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.d(TAG, "Force refresh triggered!");

            boolean allowed = NetworkManager.getInstance().isAccessAllowed();
            if(allowed) {
                SyncUtils.forceRefresh(ModelUtils.TEACHERS);
            } else {
                refreshLayout.setRefreshing(false);
                Snackbar.make(getContentView(), R.string.action_prompt_body_networkUnavailable, Snackbar.LENGTH_SHORT)
                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.md_edittext_error))
                        .setAction(R.string.action_title_retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refreshListener.onRefresh();
                            }
                        }).show();
            }
        }
    };

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
        final Context context = container.getContext();
        final int tabHeight = getResources().getDimensionPixelOffset(R.dimen.tab_height);
        final int contentMargin = getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);

        SwipeRefreshLayout refreshLayout = new SwipeRefreshLayout(context);
        refreshLayout.setId(android.R.id.secondaryProgress);

        ViewPager pager = new ViewPager(container.getContext());
        pager.setId(INTERNAL_LIST_ID);
        pager.setOffscreenPageLimit(getAdapter().getCount());
        refreshLayout.addView(pager, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        params.topMargin = tabHeight;
        refreshLayout.setLayoutParams(params);
        refreshLayout.setProgressViewOffset(false, 0, getToolbarHeight() + contentMargin);
        refreshLayout.setColorSchemeResources(R.color.primary, R.color.accent, R.color.primaryDark);

        return refreshLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();

        syncStatusObserver.onStatusChanged(0);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING
                | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        syncObserverHandle = ContentResolver.addStatusChangeListener(mask, syncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(syncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(syncObserverHandle);
            syncObserverHandle = null;
        }
    }

    @Override
    public void onDestroyView() {
        refreshLayout.setOnRefreshListener(null);
        super.onDestroyView();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        ensureRefreshLayout();
        refreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
    }

    private void ensureRefreshLayout() {
        if(refreshLayout != null) {
            return;
        }

        View root = getContentView();
        if(root == null)
            throw new IllegalStateException("Header view not created!");

        if(root instanceof SwipeRefreshLayout) {
            refreshLayout = (SwipeRefreshLayout) root;
        } else {
            View raw = root.findViewById(android.R.id.secondaryProgress);
            if(!(raw instanceof SwipeRefreshLayout)) {
                if(raw == null)
                    throw new IllegalStateException("You must provide a tab layout to content view with id android.R.id.secondaryProgress");
                else
                    throw new IllegalStateException("View provided as tab layout is not a sliding tab layout");
            }

            refreshLayout = (SwipeRefreshLayout) raw;
        }

        refreshLayout.setOnRefreshListener(refreshListener);
    }

    public static class Builder extends TabbedHostFragment.Builder {
        private Builder() {
            super(TeacherFragment.class);
        }

        public static Builder withDefault() {
            return new Builder();
        }

        @Override
        public TeacherHostFragment build() {
            return build(TeacherHostFragment.class);
        }
    }

}
