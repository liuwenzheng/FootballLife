package com.blestep.footballlife.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blestep.footballlife.R;
import com.blestep.footballlife.activity.HistoryActivity;
import com.blestep.footballlife.activity.MainActivity;
import com.blestep.footballlife.db.DBTools;
import com.blestep.footballlife.entity.SportItem;
import com.blestep.footballlife.entity.Step;
import com.blestep.footballlife.module.LogModule;
import com.blestep.footballlife.utils.SportDataUtils;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainTab01 extends Fragment {
    @Bind(R.id.radar_chart)
    RadarChart radarChart;
    @Bind(R.id.ll_sport_parent)
    LinearLayout llSportParent;
    @Bind(R.id.btn_step_history)
    Button btnStepHistory;
    private View mView;

    private MainActivity mainActivity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogModule.i("onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        LogModule.i("onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        LogModule.i("onPause");
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_tab_01, container, false);
        mainActivity = (MainActivity) getActivity();
        ButterKnife.bind(this, mView);
        initView();
        return mView;
    }

    private void initView() {
        radarChart.setDescription("");
        radarChart.setWebLineWidth(1f);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebColor(getResources().getColor(R.color.blue_89ccf3));
        radarChart.setWebColorInner(getResources().getColor(R.color.blue_89ccf3));
        radarChart.setWebAlpha(255);
        radarChart.setTouchEnabled(false);
        radarChart.setLogEnabled(true);
        // 背景
        radarChart.setBackgroundColor(getResources().getColor(R.color.blue_2a9fe4));

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
        radarChart.addView(getActivity().getLayoutInflater().inflate(R.layout.radar_speed, radarChart, false));
        radarChart.addView(getActivity().getLayoutInflater().inflate(R.layout.radar_explosive, radarChart, false));
        radarChart.addView(getActivity().getLayoutInflater().inflate(R.layout.radar_endurance, radarChart, false));
        radarChart.addView(getActivity().getLayoutInflater().inflate(R.layout.radar_spirit, radarChart, false));
        radarChart.addView(getActivity().getLayoutInflater().inflate(R.layout.radar_power, radarChart, false));
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
        ArrayList<SportItem> items = new ArrayList<SportItem>();
        Step step = DBTools.getInstance(mainActivity).selectCurrentStep();
        if (step != null) {
            createStepItem(step, items);
            float distance = Float.valueOf(step.distance) * 1000;
            createDistanceItem(items, distance);
            float calories = Float.valueOf(step.calories);
            createCaloriesItem(items, calories);
            float bmi = SportDataUtils.getBMI(mainActivity);
            createBmiItem(items, bmi);
            float bfr = SportDataUtils.getBFR(mainActivity, bmi);
            createBfrItem(items, bfr);
            float bmr = SportDataUtils.getBMR(mainActivity);
            createBmrItem(items, bmr);
            float speed = Float.valueOf(step.speed);
            createSpeedItem(items, speed);
            float power = Float.valueOf(step.power);
            createPowerItem(items, power);
            float explosive = SportDataUtils.getExplosive(power, speed);
            createExplosiveItem(items, explosive);
            float endurance = SportDataUtils.getEndurance(distance , Float.valueOf(step.duration));
            createEnduranceItem(items, endurance);
            float spirit = SportDataUtils.getSpirit(speed, power, explosive, endurance);
            createSpiritItem(items, spirit);
            for (int i = 0; i < items.size(); i++) {
                SportItem item = items.get(i);
                View view = LayoutInflater.from(mainActivity).inflate(R.layout.sports_params_item, null);
                ImageView icon = ButterKnife.findById(view, R.id.iv_sport_item_icon);
                TextView name = ButterKnife.findById(view, R.id.tv_sport_item_name);
                TextView value = ButterKnife.findById(view, R.id.tv_sport_item_value);
                ProgressBar progress = ButterKnife.findById(view, R.id.pb_sport_item_progress);
                if (i % 3 == 0) {
                    icon.setBackgroundResource(R.drawable.sports_item_bg_yellow);
                } else if (i % 3 == 1) {
                    icon.setBackgroundResource(R.drawable.sports_item_bg_red);
                } else {
                    icon.setBackgroundResource(R.drawable.sports_item_bg_blue);
                }
                icon.setImageResource(item.iconId);
                name.setText(item.name);
                value.setText(item.showValue);
                progress.setProgress((int) item.value);
                progress.setMax((int) item.maxValue);
                llSportParent.addView(view);
            }

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
                        entry.setVal(speedRadar);
                    }
                    if (i == 1) {
                        entry.setVal(explosiveRadar);
                    }
                    if (i == 2) {
                        entry.setVal(enduranceRadar);
                    }
                    if (i == 3) {
                        entry.setVal(spiritRadar);
                    }
                    if (i == 4) {
                        entry.setVal(powerRadar);
                    }

                }
            }
            radarChart.invalidate();
        }

    }


    private void createStepItem(Step step, ArrayList<SportItem> items) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_steps;
        item.name = getString(R.string.sport_steps);
        item.maxValue = SportDataUtils.MAX_STEP;
        item.showValue = step.count;
        item.value = Integer.valueOf(step.count) > SportDataUtils.MAX_STEP ? SportDataUtils.MAX_STEP : Integer.valueOf(step.count);
        items.add(item);
    }

    private void createDistanceItem(ArrayList<SportItem> items, float distance) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_distance;
        item.name = getString(R.string.sport_distance);
        item.maxValue = SportDataUtils.MAX_DISTANCE;
        item.showValue = distance + "";
        item.value = distance > SportDataUtils.MAX_DISTANCE ? SportDataUtils.MAX_DISTANCE : distance;
        items.add(item);
    }

    private void createCaloriesItem(ArrayList<SportItem> items, float calories) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_calorie;
        item.name = getString(R.string.sport_calorie);
        item.maxValue = SportDataUtils.MAX_CALORIE;
        item.showValue = calories + "";
        item.value = calories > SportDataUtils.MAX_CALORIE ? SportDataUtils.MAX_CALORIE : calories;
        items.add(item);
    }

    private void createBmiItem(ArrayList<SportItem> items, float bmi) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_bmi;
        item.name = getString(R.string.sport_bmi);
        item.maxValue = SportDataUtils.MAX_BMI;
        item.showValue = bmi + "";
        item.value = bmi > SportDataUtils.MAX_BMI ? SportDataUtils.MAX_BMI : bmi;
        items.add(item);
    }

    private void createBfrItem(ArrayList<SportItem> items, float bfr) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_bfr;
        item.name = getString(R.string.sport_bfr);
        item.maxValue = SportDataUtils.MAX_BFR;
        item.showValue = bfr + "";
        item.value = bfr > SportDataUtils.MAX_BFR ? SportDataUtils.MAX_BFR : bfr;
        items.add(item);
    }

    private void createBmrItem(ArrayList<SportItem> items, float bmr) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_bmr;
        item.name = getString(R.string.sport_bmr);
        item.maxValue = SportDataUtils.MAX_BMR;
        item.showValue = bmr + "";
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
        item.showValue = power + "";
        item.value = power > SportDataUtils.MAX_POWER ? SportDataUtils.MAX_POWER : power;
        items.add(item);
    }

    private void createExplosiveItem(ArrayList<SportItem> items, float explosive) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_explosive;
        item.name = getString(R.string.explosive);
        item.maxValue = SportDataUtils.MAX_EXPLOSIVE;
        item.showValue = explosive + "";
        item.value = explosive > SportDataUtils.MAX_EXPLOSIVE ? SportDataUtils.MAX_EXPLOSIVE : explosive;
        items.add(item);
    }

    private void createEnduranceItem(ArrayList<SportItem> items, float endurance) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_endurance;
        item.name = getString(R.string.endurance);
        item.maxValue = SportDataUtils.MAX_ENDURANCE;
        item.showValue = endurance + "";
        item.value = endurance > SportDataUtils.MAX_ENDURANCE ? SportDataUtils.MAX_ENDURANCE : endurance;
        items.add(item);
    }

    private void createSpiritItem(ArrayList<SportItem> items, float spirit) {
        SportItem item = new SportItem();
        item.iconId = R.drawable.ic_sport_spirit;
        item.name = getString(R.string.spirit);
        item.maxValue = SportDataUtils.MAX_SPIRIT;
        item.showValue = spirit + "";
        item.value = spirit > SportDataUtils.MAX_SPIRIT ? SportDataUtils.MAX_SPIRIT : spirit;
        items.add(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_step_history)
    public void onClick() {
        startActivity(new Intent(mainActivity, HistoryActivity.class));
        mainActivity.overridePendingTransition(R.anim.page_down_in,
                R.anim.page_up_out);
    }
}
