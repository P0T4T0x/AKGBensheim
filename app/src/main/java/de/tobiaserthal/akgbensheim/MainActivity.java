package de.tobiaserthal.akgbensheim;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import de.tobiaserthal.akgbensheim.contact.ContactActivity;
import de.tobiaserthal.akgbensheim.data.sync.SyncUtils;
import de.tobiaserthal.akgbensheim.event.EventFragment;
import de.tobiaserthal.akgbensheim.event.EventHostFragment;
import de.tobiaserthal.akgbensheim.foodplan.FoodPlanFragment;
import de.tobiaserthal.akgbensheim.homework.HomeworkFragment;
import de.tobiaserthal.akgbensheim.homework.HomeworkHostFragment;
import de.tobiaserthal.akgbensheim.news.NewsFragment;
import de.tobiaserthal.akgbensheim.news.NewsHostFragment;
import de.tobiaserthal.akgbensheim.preferences.SettingsActivity;
import de.tobiaserthal.akgbensheim.subst.SubstFragment;
import de.tobiaserthal.akgbensheim.subst.SubstHostFragment;
import de.tobiaserthal.akgbensheim.teacher.TeacherFragment;
import de.tobiaserthal.akgbensheim.teacher.TeacherHostFragment;
import de.tobiaserthal.akgbensheim.ui.drawer.DrawerCallbacks;
import de.tobiaserthal.akgbensheim.ui.drawer.DrawerFragment;
import de.tobiaserthal.akgbensheim.ui.base.ToolbarActivity;
import de.tobiaserthal.akgbensheim.ui.tabs.TabbedHostFragment;


public class MainActivity extends ToolbarActivity implements MainNavigation {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawer.isDrawerOpen())
            navigationDrawer.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callNavigationItem(@NavigationItem int item) {
        navigationDrawer.selectItemId(item);
    }
}
