package xyz.cfm.constructionflawmanagement;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CheckListStatePagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    public CheckListStatePagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                CheckListFragment checkListFragment = new CheckListFragment();
                return checkListFragment;
            case 1:
                CheckListPhotoFragment checkListPhotoFragment = new CheckListPhotoFragment();
                return checkListPhotoFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
