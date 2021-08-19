package com.sefcyn2000.reports.ui.adapters.templates;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sefcyn2000.reports.ui.activities.templates.TemplatesActivity;
import com.sefcyn2000.reports.ui.fragments.templates.listtemplates.ExpandableListViewListTemplatesFragment;
import com.sefcyn2000.reports.ui.fragments.templates.listtemplates.InProcessTemplatesListFragment;

/**
 * Adapter para el muestreo de los fragments en el listado de plantillas
 */
public class FragmentTemplatesAdapter extends FragmentStateAdapter {


    public FragmentTemplatesAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ExpandableListViewListTemplatesFragment();
        } else if (position == 1) {
            fragment = new InProcessTemplatesListFragment();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return TemplatesActivity.NUMBER_TABS;
    }
}
