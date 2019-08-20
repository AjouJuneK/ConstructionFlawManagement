package xyz.cfm.constructionflawmanagement;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AnalysisActivity extends AppCompatActivity implements  AnalyzeByCoopFragment.OnFragmentInteractionListener, AnalyzeByDongFragment.OnFragmentInteractionListener{

    private TabLayout analysisTabLayout;
    private ViewPager analysisViewPager;
    private PagerAdapter analysisPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        analysisTabLayout = (TabLayout)findViewById(R.id.analysisTabLayout);
        analysisTabLayout.addTab(analysisTabLayout.newTab().setText("동별 현황"));
        analysisTabLayout.addTab(analysisTabLayout.newTab().setText("업체별 현황"));
        analysisTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        analysisViewPager = (ViewPager)findViewById(R.id.analysis_view_pager);
        analysisPagerAdapter = new AnalysisStatePageAdapter(getSupportFragmentManager(), analysisTabLayout.getTabCount());
        analysisViewPager.setAdapter(analysisPagerAdapter);

        analysisViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(analysisTabLayout));
        analysisTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                analysisViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
