package com.example.tomatoclock.report;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MainFragmentAdapter extends FragmentPagerAdapter
{
    private List<Fragment> fragmentList;
    private String[] titles = new String[]{"按日", "按周"};

    public MainFragmentAdapter(FragmentManager fm, List<Fragment> pFragmentList)
    {
        super(fm);
        fragmentList = pFragmentList;
    }

    @Override
    public int getCount()
    {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return titles[position];
    }
}