package de.tobiaserthal.akgbensheim.preferences;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.base.toolbar.ToolbarActivity;
import de.tobiaserthal.akgbensheim.preferences.AppPreferenceFragment.NavigationCallback;

public class SettingsActivity extends ToolbarActivity
        implements NavigationCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(
                    ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primaryDark));
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_main, new AppPreferenceFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeFragment(Class<? extends Fragment> fragmentClass) {
        final String nameTag = fragmentClass.getName();

        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(nameTag);

        if(fragment == null) {
            fragment = Fragment.instantiate(this, nameTag);
        }

        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, fragment, nameTag)
                    .addToBackStack(nameTag)
                    .commit();
        }
    }

    @Override
    public void loadSubjectSettings() {
        changeFragment(SubstPreferenceFragment.class);
    }

    @Override
    public void loadColorSettings() {
        changeFragment(ColorPreferenceFragment.class);
    }

    @Override
    public void loadSyncSettings() {
        changeFragment(SyncPreferenceFragment.class);
    }
}
