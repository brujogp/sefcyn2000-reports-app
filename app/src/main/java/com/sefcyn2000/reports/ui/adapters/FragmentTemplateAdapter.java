package com.sefcyn2000.reports.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.ViewGroup;

import com.sefcyn2000.reports.ui.activities.templates.TemplatesActivity;
import com.sefcyn2000.reports.ui.fragments.templates.listtemplates.ExpandableListViewListTemplatesFragment;

public class FragmentTemplateAdapter extends FragmentStateAdapter {

    public FragmentTemplateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ExpandableListViewListTemplatesFragment();
        } else if (position == 1) {
            fragment = new ExpandableListViewListTemplatesFragment();
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return TemplatesActivity.NUMBER_TABS;
    }
}
