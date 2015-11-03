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

    private Fragment createFragment(@NavigationItem int item) {
        switch (item) {
            case FRAGMENT_HOME: {
                return HomeFragment.newInstance();
            }

            case FRAGMENT_SUBSTITUTION: {
                return SubstHostFragment.Builder
                        .withDefault()
                        .addPage(getString(R.string.subst_tab_form), SubstFragment.createArgs(SubstFragment.FORM))
                        .addPage(getString(R.string.subst_tab_phase), SubstFragment.createArgs(SubstFragment.PHASE))
                        .addPage(getString(R.string.subst_tab_all), SubstFragment.createArgs(SubstFragment.ALL))
                        .build();
            }

            case FRAGMENT_FOODPLAN: {
                return FoodPlanFragment.newInstance();
            }

            case FRAGMENT_HOMEWORK: {
                return HomeworkHostFragment.Builder
                        .withDefault()
                        .addPage(getString(R.string.homework_tab_todo), HomeworkFragment.createArgs(HomeworkFragment.TODO, true))
                        .addPage(getString(R.string.homework_tab_done), HomeworkFragment.createArgs(HomeworkFragment.DONE, false))
                        .build();
            }

            case FRAGMENT_EVENT: {
                return EventHostFragment.Builder
                        .withDefault()
                        .addPage(getString(R.string.event_tab_coming), EventFragment.createArgs(EventFragment.COMING))
                        .addPage(getString(R.string.event_tab_over), EventFragment.createArgs(EventFragment.OVER))
                        .build();
            }

            case FRAGMENT_NEWS: {
                return NewsHostFragment.Builder
                        .withDefault()
                        .addPage(getString(R.string.news_tab_all), NewsFragment.createArgs(NewsFragment.ALL))
                        .addPage(getString(R.string.news_tab_bookmarks), NewsFragment.createArgs(NewsFragment.BOOKMARKED))
                        .build();
            }

            case FRAGMENT_TEACHER: {
                return TeacherHostFragment.Builder
                        .withDefault()
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
    public void switchToNavigationItem(@NavigationItem int item) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(String.valueOf(item));

        if(fragment == null) {
            fragment = createFragment(item);
        }

        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, fragment, String.valueOf(item))
                            // .addToBackStack(String.valueOf(index))
                    .commit();
        }
    }

    @Override
    public void callNavigationExtra(@NavigationExtra int extra) {
        switch (extra) {
            case ACTIVITY_FAQ:
                Intent faqBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.akgbensheim.de/android/faq"));
                startActivity(faqBrowser);
                break;

            case ACTIVITY_CONTACT:
                ContactActivity.startDetail(this);
                break;

            case ACTIVITY_SETTINGS:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
        }
    }
}
