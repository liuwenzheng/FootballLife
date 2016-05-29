package com.blestep.footballlife.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.blestep.footballlife.BTConstants;
import com.blestep.footballlife.R;
import com.blestep.footballlife.db.DBTools;
import com.blestep.footballlife.entity.Step;
import com.blestep.footballlife.fragment.HistoryTabCalorie;
import com.blestep.footballlife.fragment.HistoryTabDistance;
import com.blestep.footballlife.fragment.HistoryTabEndurance;
import com.blestep.footballlife.fragment.HistoryTabExplosive;
import com.blestep.footballlife.fragment.HistoryTabPower;
import com.blestep.footballlife.fragment.HistoryTabSpirit;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends FragmentActivity implements
        OnClickListener, OnPageChangeListener, OnCheckedChangeListener {
    private ViewPager vp_history;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private HistoryTabPower tabPower;
    private HistoryTabExplosive tabExplosive;
    private HistoryTabEndurance tabEndurance;
    private HistoryTabSpirit tabSpirit;
    private HistoryTabCalorie tabCalorie;
    private HistoryTabDistance tabDistance;

    private RadioGroup rg_history_tab;

    private ArrayList<Step> mSteps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_page);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        vp_history = (ViewPager) findViewById(R.id.vp_history);
        vp_history.setOffscreenPageLimit(6);
        rg_history_tab = (RadioGroup) findViewById(R.id.rg_history_tab);
        initViewPager();
    }

    private void initListener() {
        vp_history.setOnPageChangeListener(this);
        rg_history_tab.setOnCheckedChangeListener(this);
        findViewById(R.id.tv_history_back).setOnClickListener(this);
    }

    private void initData() {
    }

    private void initViewPager() {
        mSteps = DBTools.getInstance(this).selectAllStep();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BTConstants.EXTRA_KEY_HISTORY, mSteps);
        tabPower = new HistoryTabPower();
        tabExplosive = new HistoryTabExplosive();
        tabEndurance = new HistoryTabEndurance();
        tabSpirit = new HistoryTabSpirit();
        tabDistance = new HistoryTabDistance();
        tabCalorie = new HistoryTabCalorie();

        tabPower.setArguments(bundle);
        tabExplosive.setArguments(bundle);
        tabEndurance.setArguments(bundle);
        tabSpirit.setArguments(bundle);
        tabDistance.setArguments(bundle);
        tabCalorie.setArguments(bundle);

        mFragments.add(tabPower);
        mFragments.add(tabExplosive);
        mFragments.add(tabEndurance);
        mFragments.add(tabSpirit);
        mFragments.add(tabDistance);
        mFragments.add(tabCalorie);
        /**
         * 初始化Adapter
         */
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        vp_history.setAdapter(mAdapter);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.tv_history_back:
                finish();
                overridePendingTransition(R.anim.page_up_in, R.anim.page_down_out);
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_history_tab_power:
                vp_history.setCurrentItem(0);
                break;
            case R.id.rb_history_tab_explosive:
                vp_history.setCurrentItem(1);
                break;
            case R.id.rb_history_tab_endurance:
                vp_history.setCurrentItem(2);
                break;
            case R.id.rb_history_tab_spirit:
                vp_history.setCurrentItem(3);
                break;
            case R.id.rb_history_tab_distance:
                vp_history.setCurrentItem(4);
                break;
            case R.id.rb_history_tab_calorie:
                vp_history.setCurrentItem(5);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.page_up_in, R.anim.page_down_out);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton) rg_history_tab.getChildAt(position * 2))
                .setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
