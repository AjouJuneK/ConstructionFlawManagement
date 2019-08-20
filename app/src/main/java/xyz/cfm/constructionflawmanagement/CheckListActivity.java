package xyz.cfm.constructionflawmanagement;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CheckListActivity extends AppCompatActivity implements CheckListFragment.OnFragmentInteractionListener, CheckListPhotoFragment.OnFragmentInteractionListener {

    private TabLayout checkListTabLayout;
    private ViewPager checkListViewPager;
    private PagerAdapter checkListPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        checkListTabLayout = (TabLayout)findViewById(R.id.checkListTabLayout);
        checkListTabLayout.addTab(checkListTabLayout.newTab().setText("하자 리스트"));
        checkListTabLayout.addTab(checkListTabLayout.newTab().setText("사진첨부 하자"));
        checkListTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        checkListViewPager = (ViewPager)findViewById(R.id.check_list_view_pager);
        checkListPagerAdapter = new CheckListStatePagerAdapter(getSupportFragmentManager(), checkListTabLayout.getTabCount());
        checkListViewPager.setAdapter(checkListPagerAdapter);

        checkListViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(checkListTabLayout));
        checkListTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                checkListViewPager.setCurrentItem(tab.getPosition());
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
