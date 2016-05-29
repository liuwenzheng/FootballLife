package com.blestep.footballlife.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blestep.footballlife.BTConstants;
import com.blestep.footballlife.R;
import com.blestep.footballlife.adapter.RefreshAdapter;
import com.blestep.footballlife.db.DBTools;
import com.blestep.footballlife.entity.SportItem;
import com.blestep.footballlife.entity.Step;
import com.blestep.footballlife.module.BTModule;
import com.blestep.footballlife.module.LogModule;
import com.blestep.footballlife.service.BTService;
import com.blestep.footballlife.service.BTService.LocalBinder;
import com.blestep.footballlife.utils.SPUtiles;
import com.blestep.footballlife.utils.SportDataUtils;
import com.blestep.footballlife.utils.ToastUtils;
import com.blestep.footballlife.utils.Utils;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends SlidingFragmentActivity implements
        OnClickListener {

    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private ProgressDialog mDialog;
    private BTService mBtService;

    public BTService getmBtService() {
        return mBtService;
    }

    public void setmBtService(BTService mBtService) {
        this.mBtService = mBtService;
    }

    private TextView tv_main_conn_tips, tv_main_tips, log;
    private Fragment leftMenuFragment, rightMenuFragment;
    private ScrollView sv_log;
    private PullToRefreshListView pull_refresh_list;
    private ListView lv_refresh;
    private RefreshAdapter mAdapter;
    private ArrayList<SportItem> items;
    private RadarChart radarChart;
    private boolean isConnDevice = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initListener();
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BTConstants.ACTION_CONN_STATUS_TIMEOUT);
        filter.addAction(BTConstants.ACTION_CONN_STATUS_DISCONNECTED);
        filter.addAction(BTConstants.ACTION_DISCOVER_SUCCESS);
        filter.addAction(BTConstants.ACTION_DISCOVER_FAILURE);
        filter.addAction(BTConstants.ACTION_REFRESH_DATA);
        filter.addAction(BTConstants.ACTION_ACK);
        filter.addAction(BTConstants.ACTION_REFRESH_DATA_BATTERY);
        // filter.addAction(BTConstants.ACTION_REFRESH_DATA_SLEEP_INDEX);
        // filter.addAction(BTConstants.ACTION_REFRESH_DATA_SLEEP_RECORD);
        // filter.addAction(BTConstants.ACTION_LOG);
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onStart() {
        bindService(new Intent(this, BTService.class), mServiceConnection,
                BIND_AUTO_CREATE);
        super.onStart();
    }

    private void initView() {
        pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        initRightMenu();
        initListView();
        initChart();
        lv_refresh.setAdapter(mAdapter);


        tv_main_conn_tips = (TextView) findViewById(R.id.tv_main_conn_tips);
        tv_main_conn_tips.setVisibility(View.GONE);
        tv_main_tips = (TextView) findViewById(R.id.tv_main_tips);
        tv_main_tips.setVisibility(View.GONE);

        log = (TextView) findViewById(R.id.log);
        sv_log = (ScrollView) findViewById(R.id.sv_log);
        // if (LogModule.debug) {
        // sv_log.setVisibility(View.VISIBLE);
        // log.setVisibility(View.VISIBLE);
        // } else {
        sv_log.setVisibility(View.GONE);
        log.setVisibility(View.GONE);
        // }
    }

    private void initListener() {
        tv_main_conn_tips.setOnClickListener(this);
        pull_refresh_list
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        if (mBtService.isConnDevice() && !isConnDevice) {
                            synData();
                        } else {
                            // pull_refresh_viewpager.onRefreshComplete();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        // stopService(new Intent(this, BTService.class));
        unbindService(mServiceConnection);
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        // 注销广播接收器
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (BTConstants.ACTION_CONN_STATUS_TIMEOUT.equals(intent
                        .getAction())
                        || BTConstants.ACTION_CONN_STATUS_DISCONNECTED
                        .equals(intent.getAction())
                        || BTConstants.ACTION_DISCOVER_FAILURE.equals(intent
                        .getAction())) {
                    if (leftMenuFragment != null
                            && leftMenuFragment.isVisible()) {
                        ((MenuLeftFragment) leftMenuFragment)
                                .updateView(mBtService);
                    }
                    isConnDevice = false;
                    LogModule.d("配对失败...");
                    pull_refresh_list.onRefreshComplete();
                    ToastUtils.showToast(MainActivity.this,
                            R.string.setting_device_conn_failure);
                    tv_main_conn_tips.setVisibility(View.VISIBLE);
                    tv_main_tips.setVisibility(View.GONE);
                    // if (mDialog != null) {
                    // mDialog.dismiss();
                    // }
                }
                if (BTConstants.ACTION_DISCOVER_SUCCESS.equals(intent
                        .getAction())) {
                    isConnDevice = false;
                    LogModule.d("配对成功...");
                    if (leftMenuFragment != null
                            && leftMenuFragment.isVisible()) {
                        ((MenuLeftFragment) leftMenuFragment)
                                .updateView(mBtService);
                    }
                    ToastUtils.showToast(MainActivity.this,
                            R.string.setting_device_conn_success);
                    pull_refresh_list.onRefreshComplete();
                    autoPullUpdate(getString(R.string.step_syncdata_waiting));
                    tv_main_conn_tips.setVisibility(View.GONE);
                    tv_main_tips.setVisibility(View.GONE);
                    // if (mDialog != null) {
                    // mDialog.dismiss();
                    // }
                    synData();
                    // tv_main_tips.setText(R.string.step_syncdata_waiting);
                    // tv_main_tips.setVisibility(View.VISIBLE);
                    // mDialog = ProgressDialog.show(MainActivity.this, null,
                    // getString(R.string.step_syncdata_waiting),
                    // false, false);
                }
                if (BTConstants.ACTION_REFRESH_DATA.equals(intent.getAction())) {
                    pull_refresh_list.onRefreshComplete();
                    updateView();
                    LogModule.d("同步成功...");
                    int battery = SPUtiles.getIntValue(
                            BTConstants.SP_KEY_BATTERY, 0);
                    LogModule.i("电量为" + battery + "%");
                    tv_main_tips.setVisibility(View.GONE);
                    if (leftMenuFragment != null
                            && leftMenuFragment.isVisible()) {
                        ((MenuLeftFragment) leftMenuFragment)
                                .updateView(mBtService);
                    }
                    ToastUtils.showToast(MainActivity.this,
                            R.string.syn_success);
                    // mBtService.getSleepIndex();
                    // if (mDialog != null) {
                    // mDialog.dismiss();
                    // }
                    // 每天初始化一次触摸按钮
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            BTConstants.PATTERN_YYYY_MM_DD);
                    String dateStr = sdf.format(calendar.getTime());
                    if (!TextUtils.isEmpty(SPUtiles.getStringValue(
                            BTConstants.SP_KEY_TOUCHBUTTON, ""))
                            && SPUtiles.getStringValue(
                            BTConstants.SP_KEY_TOUCHBUTTON, "").equals(
                            dateStr)) {
                        return;
                    } else {
                        mBtService.synTouchButton();
                        SPUtiles.setStringValue(BTConstants.SP_KEY_TOUCHBUTTON,
                                dateStr);
                    }
                }
                if (BTConstants.ACTION_LOG.equals(intent.getAction())) {
                    String strLog = intent.getStringExtra("log");
                    log.setText(log.getText().toString() + "\n" + strLog);

                }
                if (BTConstants.ACTION_REFRESH_DATA_BATTERY.equals(intent
                        .getAction())) {
                    int battery = intent.getIntExtra(
                            BTConstants.EXTRA_KEY_BATTERY_VALUE, 0);
                    if (battery == 0) {
                        return;
                    }
                    SPUtiles.setIntValue(BTConstants.SP_KEY_BATTERY, battery);
                    mBtService.getStepData();
                }
                // if (BTConstants.ACTION_REFRESH_DATA_SLEEP_INDEX.equals(intent
                // .getAction())) {
                // mBtService.getSleepRecord();
                // }
                // if
                // (BTConstants.ACTION_REFRESH_DATA_SLEEP_RECORD.equals(intent
                // .getAction())) {
                //
                // }
                if (BTConstants.ACTION_ACK.equals(intent.getAction())) {
                    int ack = intent.getIntExtra(
                            BTConstants.EXTRA_KEY_ACK_VALUE, 0);
                    if (ack == 0) {
                        return;
                    }
                    if (ack == BTConstants.HEADER_SYNTIMEDATA) {
                        mBtService.synUserInfoData();
                    } else if (ack == BTConstants.HEADER_SYNUSERINFO) {
                        mBtService.synAlarmData();
                    } else if (ack == BTConstants.HEADER_SYNALARM) {
                        // mBtService.synSleepTime();
                        // }
                        // else if (ack == BTConstants.HEADER_SYNSLEEP) {
                        mBtService.getBatteryData();
                    }
                }
            }

        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogModule.d("连接服务onServiceConnected...");
            mBtService = ((LocalBinder) service).getService();
            // 开启蓝牙
            if (!BTModule.isBluetoothOpen()) {
                BTModule.openBluetooth(MainActivity.this);
            } else {
                LogModule.d("连接手环or同步数据？");
                if (mBtService.isConnDevice()) {
                    autoPullUpdate(getString(R.string.step_syncdata_waiting));
                    synData();
                    tv_main_conn_tips.setVisibility(View.GONE);
                    // tv_main_tips.setText(R.string.step_syncdata_waiting);
                    // tv_main_tips.setVisibility(View.VISIBLE);
                    // mDialog = ProgressDialog.show(MainActivity.this, null,
                    // getString(R.string.step_syncdata_waiting),
                    // false, false);
                } else {
                    isConnDevice = true;
                    autoPullUpdate(getString(R.string.setting_device));
                    mBtService.connectBle(SPUtiles.getStringValue(
                            BTConstants.SP_KEY_DEVICE_ADDRESS, null));
                    // tv_main_tips.setText(R.string.setting_device);
                    // tv_main_tips.setVisibility(View.VISIBLE);
                    // mDialog = ProgressDialog.show(MainActivity.this, null,
                    // getString(R.string.setting_device), false,
                    // false);
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogModule.d("断开服务onServiceDisconnected...");
            mBtService.mBluetoothGatt = null;
            mBtService = null;
        }
    };

    /**
     * 同步数据
     */
    private void synData() {
        // 5.0偶尔会出现获取不到数据的情况，这时候延迟发送命令，解决问题
        BTService.mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                mBtService.synTimeData();

            }
        }, 200);
        // 10s后若未获取数据自动结束刷新
        // BTService.mHandler.postDelayed(new Runnable() {
        // @Override
        // public void run() {
        // runOnUiThread(new Runnable() {
        //
        // @Override
        // public void run() {
        // if (pull_refresh_viewpager.isRefreshing()) {
        // LogModule.e("10s后未获得手环数据！！！");
        // pull_refresh_viewpager.onRefreshComplete();
        // }
        //
        // }
        // });
        // }
        // }, 10000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BTModule.REQUEST_ENABLE_BT:
                    isConnDevice = true;
                    autoPullUpdate(getString(R.string.setting_device));
                    mBtService.connectBle(SPUtiles.getStringValue(
                            BTConstants.SP_KEY_DEVICE_ADDRESS, null));
                    // tv_main_tips.setText(R.string.setting_device);
                    // tv_main_tips.setVisibility(View.VISIBLE);
                    // mDialog = ProgressDialog
                    // .show(MainActivity.this, null,
                    // getString(R.string.setting_device), false, false);

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initListView() {
        lv_refresh = pull_refresh_list.getRefreshableView();
        items = new ArrayList<SportItem>();
        mAdapter = new RefreshAdapter(this, items);
        createStepItem(items, 0);
        createDistanceItem(items, 0);
        createCaloriesItem(items, 0);
        createBmiItem(items, 0);
        createBfrItem(items, 0);
        createBmrItem(items, 0);
        createSpeedItem(items, 0);
        createPowerItem(items, 0);
        createExplosiveItem(items, 0);
        createEnduranceItem(items, 0);
        createSpiritItem(items, 0);
        mAdapter.notifyDataSetChanged();
    }

    private void initRightMenu() {

        leftMenuFragment = new MenuLeftFragment();
        setBehindContentView(R.layout.left_menu_frame);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
        SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT_RIGHT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // menu.setBehindWidth(i);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        // menu.setBehindScrollScale(1.0f);
        menu.setSecondaryShadowDrawable(R.drawable.shadow_right);
        // 设置右边（二级）侧滑菜单
        menu.setSecondaryMenu(R.layout.right_menu_frame);
        rightMenuFragment = new MenuRightFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_right_menu_frame, rightMenuFragment).commit();
    }

    public void showLeftMenu(View view) {
        getSlidingMenu().showMenu();
    }

    public void showRightMenu(View view) {
        getSlidingMenu().showSecondaryMenu();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_conn_tips:
                tv_main_conn_tips.setVisibility(View.GONE);
                if (!BTModule.isBluetoothOpen()) {
                    BTModule.openBluetooth(MainActivity.this);
                    return;
                }
                mBtService.connectBle(SPUtiles.getStringValue(
                        BTConstants.SP_KEY_DEVICE_ADDRESS, null));
                isConnDevice = true;
                autoPullUpdate(getString(R.string.setting_device));
                // tv_main_tips.setText(R.string.setting_device);
                // tv_main_tips.setVisibility(View.VISIBLE);
                // mDialog = ProgressDialog.show(MainActivity.this, null,
                // getString(R.string.setting_device), false, false);
                break;

            default:
                break;
        }

    }

    /**
     * 自动下拉刷新
     */
    private void autoPullUpdate(String tips) {
        pull_refresh_list.getLoadingLayoutProxy().setRefreshingLabel(tips);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                pull_refresh_list.setRefreshing();
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage(R.string.main_exit_tips);
        builder.setPositiveButton(R.string.main_exit_tips_confirm,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                });
        builder.setNegativeButton(R.string.main_exit_tips_cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
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

    private void initChart() {
        View view = LayoutInflater.from(this).inflate(R.layout.radarchart, null);
        radarChart = ButterKnife.findById(view, R.id.radar_chart);
        radarChart.setDescription("");
        radarChart.setWebLineWidth(1f);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColor(getResources().getColor(R.color.blue_89ccf3));
        radarChart.setWebColorInner(getResources().getColor(R.color.blue_89ccf3));
        radarChart.setWebAlpha(255);
        radarChart.setTouchEnabled(false);
        radarChart.setLogEnabled(true);
        // 背景
        // radarChart.setBackgroundColor(getResources().getColor(R.color.blue_2a9fe4));

        setData();

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextSize(14f);
        xAxis.setTextColor(ColorTemplate.rgb("#ffffff"));
        xAxis.setEnabled(false);

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(2, true);
        yAxis.setAxisMinValue(0);
        yAxis.setAxisMaxValue(100);
        yAxis.setShowOnlyMinMax(true);
        yAxis.setEnabled(false);


        Legend legend = radarChart.getLegend();
        legend.setEnabled(false);
        radarChart.addView(LayoutInflater.from(this).inflate(R.layout.radar_speed, radarChart, false));
        radarChart.addView(LayoutInflater.from(this).inflate(R.layout.radar_explosive, radarChart, false));
        radarChart.addView(LayoutInflater.from(this).inflate(R.layout.radar_endurance, radarChart, false));
        radarChart.addView(LayoutInflater.from(this).inflate(R.layout.radar_spirit, radarChart, false));
        radarChart.addView(LayoutInflater.from(this).inflate(R.layout.radar_power, radarChart, false));
        TextView textView = new TextView(this);
        textView.setText("测试版");
        textView.setTextSize(Utils.dip2px(this, 15));
        textView.setTextColor(getResources().getColor(R.color.white_ffffff));
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        radarChart.addView(textView);
        lv_refresh.addHeaderView(view);
    }

    private String[] mParties;

    public void setData() {
        mParties = new String[]{getString(R.string.speed), getString(R.string.explosive), getString(R.string.endurance), getString(R.string.spirit), getString(R.string.power)};
        int cnt = mParties.length;

        ArrayList<Entry> entries = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < cnt; i++) {
            entries.add(new Entry(0, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < cnt; i++)
            xVals.add(mParties[i % mParties.length]);

        RadarDataSet dataSet = new RadarDataSet(entries, "Set 1");
        dataSet.setColor(getResources().getColor(R.color.grey_aee1ff));
        dataSet.setFillColor(getResources().getColor(R.color.grey_aee1ff));
        dataSet.setFillAlpha(65);
        dataSet.setDrawFilled(true);
        dataSet.setLineWidth(0f);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(dataSet);

        RadarData data = new RadarData(xVals, sets);
        data.setValueTextSize(8f);
        data.setValueTextColor(getResources().getColor(R.color.white_ffffff));
        data.setDrawValues(false);
        radarChart.setData(data);
        radarChart.invalidate();
    }

    public void updateView() {
        Step step = DBTools.getInstance(this).selectCurrentStep();
        if (step != null) {
            if (items != null)
                items.clear();
            int stepCount = Integer.valueOf(step.count);
            createStepItem(items, stepCount);
            float distance = Float.valueOf(step.distance) * 1000;
            createDistanceItem(items, distance);
            float calories = Float.valueOf(step.calories);
            createCaloriesItem(items, calories);
            float bmi = SportDataUtils.getBMI(this);
            createBmiItem(items, bmi);
            float bfr = SportDataUtils.getBFR(this, bmi);
            createBfrItem(items, bfr);
            float bmr = SportDataUtils.getBMR(this);
            createBmrItem(items, bmr);
            float speed = Float.valueOf(step.speed);
            createSpeedItem(items, speed);
            float power = Float.valueOf(step.power);
            createPowerItem(items, power);
            float explosive = SportDataUtils.getExplosive(power, speed);
            createExplosiveItem(items, explosive);
            float endurance = SportDataUtils.getEndurance(distance, Float.valueOf(step.duration));
            createEnduranceItem(items, endurance);
            float spirit = SportDataUtils.getSpirit(speed, power, explosive, endurance);
            createSpiritItem(items, spirit);
            mAdapter.notifyDataSetChanged();
            // change charts
            int speedRadar = (int) (speed * 100 / SportDataUtils.MAX_SPEED);
            int powerRadar = (int) (power * 100 / SportDataUtils.MAX_POWER);
            int explosiveRadar = (int) (explosive * 100 / SportDataUtils.MAX_EXPLOSIVE);
            int enduranceRadar = (int) (endurance * 100 / SportDataUtils.MAX_ENDURANCE);
            int spiritRadar = (int) (spirit * 100 / SportDataUtils.MAX_SPIRIT);
            ArrayList<IRadarDataSet> sets = (ArrayList<IRadarDataSet>) radarChart.getData()
                    .getDataSets();
            for (IRadarDataSet set : sets) {
                for (int i = 0; i < set.getEntryCount(); i++) {
                    Entry entry = set.getEntryForIndex(i);
                    if (i == 0) {
                        entry.setVal(speedRadar > 100 ? 100 : speedRadar);
                    }
                    if (i == 1) {
                        entry.setVal(explosiveRadar > 100 ? 100 : explosiveRadar);
                    }
                    if (i == 2) {
                        entry.setVal(enduranceRadar > 100 ? 100 : enduranceRadar);
                    }
                    if (i == 3) {
                        entry.setVal(spiritRadar > 100 ? 100 : spiritRadar);
                    }
                    if (i == 4) {
                        entry.setVal(powerRadar > 100 ? 100 : powerRadar);
                    }

                }
            }
            radarChart.invalidate();
        }

    }

    private void createStepItem(ArrayList<SportItem> items, int count) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_steps;
        item.name = getString(R.string.sport_steps);
        item.maxValue = SportDataUtils.MAX_STEP;
        item.showValue = count + "";
        item.value = count > SportDataUtils.MAX_STEP ? SportDataUtils.MAX_STEP : count;
        items.add(item);
    }

    private void createDistanceItem(ArrayList<SportItem> items, float distance) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_distance;
        item.name = getString(R.string.sport_distance);
        item.maxValue = SportDataUtils.MAX_DISTANCE;
        item.showValue = (int) distance + "";
        item.value = distance > SportDataUtils.MAX_DISTANCE ? SportDataUtils.MAX_DISTANCE : distance;
        items.add(item);
    }

    private void createCaloriesItem(ArrayList<SportItem> items, float calories) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_calorie;
        item.name = getString(R.string.sport_calorie);
        item.maxValue = SportDataUtils.MAX_CALORIE;
        item.showValue = (int) calories + "";
        item.value = calories > SportDataUtils.MAX_CALORIE ? SportDataUtils.MAX_CALORIE : calories;
        items.add(item);
    }

    private void createBmiItem(ArrayList<SportItem> items, float bmi) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_bmi;
        item.name = getString(R.string.sport_bmi);
        item.maxValue = SportDataUtils.MAX_BMI;
        item.showValue = (int) bmi + "";
        item.value = bmi > SportDataUtils.MAX_BMI ? SportDataUtils.MAX_BMI : bmi;
        items.add(item);
    }

    private void createBfrItem(ArrayList<SportItem> items, float bfr) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_bfr;
        item.name = getString(R.string.sport_bfr);
        item.maxValue = SportDataUtils.MAX_BFR;
        item.showValue = (int) bfr + "";
        item.value = bfr > SportDataUtils.MAX_BFR ? SportDataUtils.MAX_BFR : bfr;
        items.add(item);
    }

    private void createBmrItem(ArrayList<SportItem> items, float bmr) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_bmr;
        item.name = getString(R.string.sport_bmr);
        item.maxValue = SportDataUtils.MAX_BMR;
        item.showValue = (int) bmr + "";
        item.value = bmr > SportDataUtils.MAX_BMR ? SportDataUtils.MAX_BMR : bmr;
        items.add(item);
    }

    private void createSpeedItem(ArrayList<SportItem> items, float speed) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_speed;
        item.name = getString(R.string.speed);
        item.maxValue = SportDataUtils.MAX_SPEED;
        item.showValue = speed + "";
        item.value = speed > SportDataUtils.MAX_SPEED ? SportDataUtils.MAX_SPEED : speed;
        items.add(item);
    }

    private void createPowerItem(ArrayList<SportItem> items, float power) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_power;
        item.name = getString(R.string.power);
        item.maxValue = SportDataUtils.MAX_POWER;
        item.showValue = (int) power + "";
        item.value = power > SportDataUtils.MAX_POWER ? SportDataUtils.MAX_POWER : power;
        items.add(item);
    }

    private void createExplosiveItem(ArrayList<SportItem> items, float explosive) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_explosive;
        item.name = getString(R.string.explosive);
        item.maxValue = SportDataUtils.MAX_EXPLOSIVE;
        item.showValue = (int) explosive + "";
        item.value = explosive > SportDataUtils.MAX_EXPLOSIVE ? SportDataUtils.MAX_EXPLOSIVE : explosive;
        items.add(item);
    }

    private void createEnduranceItem(ArrayList<SportItem> items, float endurance) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_endurance;
        item.name = getString(R.string.endurance);
        item.maxValue = SportDataUtils.MAX_ENDURANCE;
        item.showValue = (int) endurance + "";
        item.value = endurance > SportDataUtils.MAX_ENDURANCE ? SportDataUtils.MAX_ENDURANCE : endurance;
        items.add(item);
    }

    private void createSpiritItem(ArrayList<SportItem> items, float spirit) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_spirit;
        item.name = getString(R.string.spirit);
        item.maxValue = SportDataUtils.MAX_SPIRIT;
        item.showValue = (int) spirit + "";
        item.value = spirit > SportDataUtils.MAX_SPIRIT ? SportDataUtils.MAX_SPIRIT : spirit;
        items.add(item);
    }

    @OnClick(R.id.btn_step_history)
    public void onClick() {
        startActivity(new Intent(this, HistoryActivity.class));
        this.overridePendingTransition(R.anim.page_down_in, R.anim.page_up_out);
    }
}
