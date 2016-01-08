package de.tobiaserthal.akgbensheim.base;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import de.tobiaserthal.akgbensheim.R;
import de.tobiaserthal.akgbensheim.drawer.DrawerFragment;
import de.tobiaserthal.akgbensheim.base.toolbar.ToolbarActivity;

public class MainActivity extends ToolbarActivity implements MainNavigation {
    private static final String TAG = "MainActivity";
    private DrawerFragment navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar((Toolbar) findViewById(R.id.toolbar));

        navigationDrawer = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.drawerFragment);

        navigationDrawer.setup(
                R.id.drawerFragment, (DrawerLayout) findViewById(R.id.drawerLayout), getToolbar());
    }

    @Override
    public void onDestroy() {
        navigationDrawer = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawer.isDrawerOpen()) {
            navigationDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void callNavigationItem(@NavigationItem int item) {
        navigationDrawer.selectItemId(item);
    }
}
