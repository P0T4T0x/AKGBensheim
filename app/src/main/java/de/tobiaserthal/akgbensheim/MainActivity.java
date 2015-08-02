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

import de.tobiaserthal.akgbensheim.event.EventFragment;
import de.tobiaserthal.akgbensheim.foodplan.FoodPlanFragment;
import de.tobiaserthal.akgbensheim.homework.HomeworkFragment;
import de.tobiaserthal.akgbensheim.homework.HomeworkHostFragment;
import de.tobiaserthal.akgbensheim.news.NewsFragment;
import de.tobiaserthal.akgbensheim.preferences.SettingsActivity;
import de.tobiaserthal.akgbensheim.subst.SubstFragment;
import de.tobiaserthal.akgbensheim.teacher.TeacherFragment;
import de.tobiaserthal.akgbensheim.ui.drawer.DrawerCallbacks;
import de.tobiaserthal.akgbensheim.ui.drawer.DrawerFragment;
import de.tobiaserthal.akgbensheim.ui.base.ToolbarActivity;
import de.tobiaserthal.akgbensheim.ui.tabs.TabbedHostFragment;


public class MainActivity extends ToolbarActivity implements DrawerCallbacks {
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

    // TODO: reconsider working with back stack
    private void changeFragment(int index) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(index));

        if(fragment == null) {
            fragment = createFragment(index);
        }

        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, fragment, String.valueOf(index))
                    // .addToBackStack(String.valueOf(index))
                    .commit();
        }
    }

    private Fragment createFragment(int index) {
        switch (index) {
            case 0: {
                return HomeFragment.newInstance();
            }

            case 1: {
                return TabbedHostFragment.Builder
                        .withClass(SubstFragment.class)
                        .addPage(getString(R.string.subst_tab_form), SubstFragment.createArgs(SubstFragment.FORM))
                        .addPage(getString(R.string.subst_tab_phase), SubstFragment.createArgs(SubstFragment.PHASE))
                        .addPage(getString(R.string.subst_tab_all), SubstFragment.createArgs(SubstFragment.ALL))
                        .build();
            }

            case 2: {
                return FoodPlanFragment.newInstance();
            }

            case 3: {
                return HomeworkHostFragment.Builder
                        .withDefault()
                        .addPage(getString(R.string.homework_tab_todo), HomeworkFragment.createArgs(HomeworkFragment.TODO, true))
                        .addPage(getString(R.string.homework_tab_done), HomeworkFragment.createArgs(HomeworkFragment.DONE, false))
                        .build();
            }

            case 4: {
                return TabbedHostFragment.Builder
                        .withClass(EventFragment.class)
                        .addPage(getString(R.string.event_tab_coming), EventFragment.createArgs(EventFragment.COMING))
                        .addPage(getString(R.string.event_tab_over), EventFragment.createArgs(EventFragment.OVER))
                        .build();
            }

            case 5: {
                return TabbedHostFragment.Builder
                        .withClass(NewsFragment.class)
                        .addPage(getString(R.string.news_tab_all), NewsFragment.createArgs(NewsFragment.ALL))
                        .addPage(getString(R.string.news_tab_bookmarks), NewsFragment.createArgs(NewsFragment.BOOKMARKED))
                        .build();
            }

            case 6: {
                return TabbedHostFragment.Builder
                        .withClass(TeacherFragment.class)
                        .addPage(getString(R.string.teacher_tab_teachers), TeacherFragment.createArgs(TeacherFragment.TEACHER))
                        .addPage(getString(R.string.teacher_tab_student_teachers), TeacherFragment.createArgs(TeacherFragment.STUDENT_TEACHER))
                        .build();
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void onNavigationItemSelected(int index, int position, boolean reselect) {
        switch (index) {
            case 7: {
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
            }

            case 8: {
                Intent faqBrowser = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.akgbensheim.de/android/faq"));

                startActivity(faqBrowser);
                break;
            }

            default: {
                changeFragment(index);
                break;
            }
        }
    }

    @Override
    public void onHeaderItemSelected() {
        Toast.makeText(this, "Header item selected!", Toast.LENGTH_SHORT).show();

        // Todo: start contact activity
    }
}
