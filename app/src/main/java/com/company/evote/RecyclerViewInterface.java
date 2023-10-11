package com.company.evote;

import android.view.MenuItem;

public interface RecyclerViewInterface {
    boolean onNavigationItemSelected(MenuItem item);

    void onItemClick (int position);
    void onButtonClick(int position);
}
