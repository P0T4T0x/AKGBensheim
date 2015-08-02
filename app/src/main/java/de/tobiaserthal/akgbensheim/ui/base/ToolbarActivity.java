package de.tobiaserthal.akgbensheim.ui.base;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public abstract class ToolbarActivity extends AppCompatActivity {
    private Toolbar toolbar;

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        this.toolbar = toolbar;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
