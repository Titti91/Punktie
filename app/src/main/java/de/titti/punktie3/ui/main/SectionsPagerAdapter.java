package de.titti.punktie3.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import de.titti.punktie3.ListDetail;
import de.titti.punktie3.MainActivity;
import de.titti.punktie3.R;
import de.titti.punktie3.ui.main.foodItem.FoodItemContent;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_Calc, R.string.tab_text_List, R.string.tab_text_Overview};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
//                MainActivity.hideFab();
                return CalcFragment.newInstance("eins", "zwei");
            case 1:
//                MainActivity.showFab();
                return ListFragment.newInstance(1);
            case 2:
//                MainActivity.hideFab();
                return OverviewFragment.newInstance("test1", "test2");
        }
        return CalcFragment.newInstance("vier", "fuenf");
//        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}