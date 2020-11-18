package com.example1.markattendance;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeAdapter extends FragmentPagerAdapter {

    private int pageCount = 3;
    //private String tabTiles[] = new String[] {"Batches","Attendance","Statistics"};

    public HomeAdapter(@NonNull FragmentManager fm, int behavior, int pageCount) {
        super(fm, behavior);
        this.pageCount = pageCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new Class_Fragment();
            case 1:
                return new Attendance_Fragment();
            case 2:
                return new Statistics_Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return pageCount;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTiles[position];
//    }
}
