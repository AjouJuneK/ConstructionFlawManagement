package xyz.cfm.constructionflawmanagement;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class AnalysisStatePageAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    public AnalysisStatePageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                AnalyzeByDongFragment analyzeByDongFragment = new AnalyzeByDongFragment();
                return analyzeByDongFragment;
            case 1:
                AnalyzeByCoopFragment analyzeByCoopFragment = new AnalyzeByCoopFragment();
                return analyzeByCoopFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
