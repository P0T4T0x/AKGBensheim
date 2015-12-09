package de.tobiaserthal.akgbensheim.ui.drawer;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.MainNavigation;
import de.tobiaserthal.akgbensheim.data.Log;
import de.tobiaserthal.akgbensheim.data.preferences.PreferenceProvider;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkColumns;
import de.tobiaserthal.akgbensheim.data.provider.homework.HomeworkSelection;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionColumns;
import de.tobiaserthal.akgbensheim.data.provider.substitution.SubstitutionSelection;

import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_NEWS;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_HOME;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_EVENT;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_TEACHER;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_HOMEWORK;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_FOODPLAN;
import static de.tobiaserthal.akgbensheim.MainNavigation.FRAGMENT_SUBSTITUTION;

import static de.tobiaserthal.akgbensheim.MainNavigation.ACTIVITY_FAQ;
import static de.tobiaserthal.akgbensheim.MainNavigation.ACTIVITY_CONTACT;
import static de.tobiaserthal.akgbensheim.MainNavigation.ACTIVITY_SETTINGS;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class DrawerFragment extends Fragment implements DrawerCallbacks, LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String TAG = "DrawerFragment";

    /**
     * A pointer to the current navigation manager (parent activity).
     */
    private MainNavigation mainNavigation;

    /**
     * The adapter that manages the navigation items.
     */
    private DrawerAdapter drawerAdapter;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle drawerToggle;

    private DrawerLayout drawerLayout;
    private View containerView;

    private int currentSelectedPosition = 0;
    private boolean fromSavedInstanceState;
    private boolean userLearnedDrawer;

    private ChangeReceiver preferenceChangeReceiver;
    static class ChangeReceiver extends PreferenceProvider.PreferenceChangeReceiver {
        private WeakReference<DrawerFragment> reference;

        public ChangeReceiver(DrawerFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void onSubstPreferenceChange() {
            DrawerFragment fragment = reference.get();
            if(fragment != null) {
                fragment.getLoaderManager().restartLoader(FRAGMENT_SUBSTITUTION, Bundle.EMPTY, fragment);
            }
        }
    }

    public DrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainNavigation = (MainNavigation) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent context must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            fromSavedInstanceState = true;
        }

        String[] navMenuItemTitles = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navMenuItemIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        drawerAdapter = new DrawerAdapter(
                getResources().getString(R.string.app_name),
                getResources().getString(R.string.address),
                ContextCompat.getDrawable(getActivity(), R.drawable.akg)
        );

        drawerAdapter.addItem(navMenuItemTitles[0], navMenuItemIcons.getResourceId(0, -1));
        drawerAdapter.addSection();

        drawerAdapter.addItem(navMenuItemTitles[1], navMenuItemIcons.getResourceId(1, -1));
        drawerAdapter.addItem(navMenuItemTitles[2], navMenuItemIcons.getResourceId(2, -1));
        drawerAdapter.addItem(navMenuItemTitles[3], navMenuItemIcons.getResourceId(3, -1));
        drawerAdapter.addSection();

        drawerAdapter.addItem(navMenuItemTitles[4], navMenuItemIcons.getResourceId(4, -1));
        drawerAdapter.addItem(navMenuItemTitles[5], navMenuItemIcons.getResourceId(5, -1));
        drawerAdapter.addItem(navMenuItemTitles[6], navMenuItemIcons.getResourceId(6, -1));
        drawerAdapter.addSection();

        drawerAdapter.addSpecialItem(navMenuItemTitles[7], navMenuItemIcons.getResourceId(7, -1));
        drawerAdapter.addSpecialItem(navMenuItemTitles[8], navMenuItemIcons.getResourceId(8, -1));

        navMenuItemIcons.recycle();

        drawerAdapter.setOnItemClickListener(this);
        drawerAdapter.selectPosition(currentSelectedPosition);

        preferenceChangeReceiver = new ChangeReceiver(this);
        IntentFilter filter = new IntentFilter(PreferenceProvider.ACTION_SUBST);
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(preferenceChangeReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        RecyclerView recyclerView = (RecyclerView) inflater
                .inflate(R.layout.fragment_navigation_drawer, container, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(drawerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return recyclerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FRAGMENT_SUBSTITUTION, null, this);
        getLoaderManager().initLoader(FRAGMENT_HOMEWORK, null, this);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     * @param toolbar      The Toolbar of the activity.
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        containerView = (View) getActivity().findViewById(fragmentId).getParent();

        this.drawerLayout = drawerLayout;
        this.drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primaryDark));

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        } else {
            this.containerView.setElevation(getResources().getDimension(R.dimen.navigation_drawer_elevation));
        }

        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                DrawerFragment.this.drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!userLearnedDrawer) {
                    userLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!userLearnedDrawer && !fromSavedInstanceState) {
            this.drawerLayout.openDrawer(containerView);
        }

        // Defer code dependent on restoration of previous instance state.
        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        this.drawerLayout.setDrawerListener(drawerToggle);
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(containerView);
    }

    public void openDrawer() {
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(containerView);
            }
        });
    }

    public void closeDrawer() {
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(containerView);
            }
        });
    }

    public void openDrawer(int delay) {
        drawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(containerView);
            }
        }, delay);
    }

    public void closeDrawer(int delay) {
        drawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(containerView);
            }
        }, delay);
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(FRAGMENT_SUBSTITUTION);
        getLoaderManager().destroyLoader(FRAGMENT_HOMEWORK);

        // unregister listener
        LocalBroadcastManager
                .getInstance(getContext())
                .unregisterReceiver(preferenceChangeReceiver);

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainNavigation = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onNavigationItemSelected(int index, int position, boolean reselect) {
        currentSelectedPosition = index;
        if(drawerLayout != null) {
            closeDrawer(200);
        }

        if(mainNavigation != null) {
            switch (index) {
                case 0:
                    mainNavigation.switchToNavigationItem(FRAGMENT_HOME);
                    break;

                case 1:
                    mainNavigation.switchToNavigationItem(FRAGMENT_SUBSTITUTION);
                    break;

                case 2:
                    mainNavigation.switchToNavigationItem(FRAGMENT_FOODPLAN);
                    break;

                case 3:
                    mainNavigation.switchToNavigationItem(FRAGMENT_HOMEWORK);
                    break;

                case 4:
                    mainNavigation.switchToNavigationItem(FRAGMENT_EVENT);
                    break;

                case 5:
                    mainNavigation.switchToNavigationItem(FRAGMENT_NEWS);
                    break;

                case 6:
                    mainNavigation.switchToNavigationItem(FRAGMENT_TEACHER);
                    break;

                case 7:
                    mainNavigation.callNavigationExtra(ACTIVITY_SETTINGS);
                    break;

                case 8:
                    mainNavigation.callNavigationExtra(ACTIVITY_FAQ);
                    break;
            }
        }
    }

    @Override
    public void onHeaderItemSelected() {
        if(mainNavigation != null) {
            mainNavigation.callNavigationExtra(ACTIVITY_CONTACT);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Creating loader with id: %d", id);
        switch (id) {
            case FRAGMENT_SUBSTITUTION: {
                String[] projection = {"count(" + SubstitutionColumns._ID + ")"};
                SubstitutionSelection selection = SubstitutionSelection.getForm(
                        PreferenceProvider.getInstance().getSubstPhase(),
                        PreferenceProvider.getInstance().getSubstForm(),
                        PreferenceProvider.getInstance().getSubstSubjects());

                return selection.loader(getActivity(), projection);
            }

            case FRAGMENT_HOMEWORK: {
                String[] projection = {"count(" + HomeworkColumns._ID + ")"};
                return HomeworkSelection.getTodo().loader(getActivity(), projection);
            }

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Loader finished with id: %d", loader.getId());
        switch (loader.getId()) {
            case FRAGMENT_SUBSTITUTION:
                if(data.moveToFirst()) {
                    drawerAdapter.updateItemCount(1, data.getInt(0));
                }

                break;

            case FRAGMENT_HOMEWORK:
                if(data.moveToFirst()) {
                    drawerAdapter.updateItemCount(3, data.getInt(0));
                }

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FRAGMENT_SUBSTITUTION:
                drawerAdapter.updateItemCount(1, 0);
                break;
            case FRAGMENT_HOMEWORK:
                drawerAdapter.updateItemCount(3, 0);
                break;
        }
    }
}
