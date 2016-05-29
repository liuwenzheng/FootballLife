package com.blestep.footballlife.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.blestep.footballlife.BTConstants;
import com.blestep.footballlife.R;
import com.blestep.footballlife.activity.HistoryActivity;
import com.blestep.footballlife.entity.Step;
import com.blestep.footballlife.module.LogModule;
import com.blestep.footballlife.utils.DataRetriever;
import com.blestep.footballlife.utils.Utils;
import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class HistoryTabSpirit extends Fragment implements OnEntryClickListener {
	private String mLabels[];
	private String mValues[];
	private int BAR_SPIRIT_MAX = 1;
	// private int BAR_STEP_AIM = 0;
	private ArrayList<Step> mSteps;
	private ArrayList<Step> mStepsSort;
	private SimpleDateFormat mSdf;
	private Calendar mCalendar;
	private TextView tv_history_spirit_daily, tv_history_spirit_sum;

	private static Runnable mEndAction = new Runnable() {
		@Override
		public void run() {

		}
	};

	private View mView;
	private HistoryActivity mActivity;
	private BarChartView bcv_spirit;
	private TextView mBarTooltip;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		LogModule.i("onActivityCreated");
		mActivity = (HistoryActivity) getActivity();
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
		mView = inflater.inflate(R.layout.history_tab_spirit, container, false);
		initView();
		initData();
		return mView;
	}

	private void initData() {
		mLabels = new String[7];
		mValues = new String[7];
		mSteps = (ArrayList<Step>) getArguments().getSerializable(
				BTConstants.EXTRA_KEY_HISTORY);
		// BAR_STEP_AIM = SPUtiles.getIntValue(BTConstants.SP_KEY_STEP_AIM,
		// 100);
		// 找到最大的，与目标值对比
		mStepsSort = new ArrayList<Step>();
		mStepsSort.addAll(mSteps);
		Collections.sort(mStepsSort, new StepCompare());
		if (Float.valueOf(mStepsSort.get(0).spirit) >= BAR_SPIRIT_MAX) {
			BAR_SPIRIT_MAX = Float.valueOf(mStepsSort.get(0).spirit).intValue() + 1;
		}
		// else {
		// BAR_STEP_MAX = BAR_STEP_AIM;
		// }
		mSdf = new SimpleDateFormat(BTConstants.PATTERN_MM_DD);
		mCalendar = Calendar.getInstance();

		for (int i = mLabels.length - 1; i >= 0; i--) {
			if (i == mLabels.length - 1) {
				mLabels[i] = getString(R.string.history_today);
				continue;
			}
			mCalendar.add(Calendar.DAY_OF_MONTH, -1);
			// if (i == mLabels.length - 2) {
			// mLabels[i] = getString(R.string.history_yesterday);
			// continue;
			// }
			mLabels[i] = mSdf.format(mCalendar.getTime());
		}
		updateBarChart(mLabels.length);
		float stepSum = 0;
		for (int i = 0; i < mValues.length; i++) {
			if (Utils.isEmpty(mValues[i])) {
				stepSum += 0;
			} else {
				stepSum += Float.valueOf(mValues[i]);
			}
		}
		tv_history_spirit_sum.setText(new BigDecimal(stepSum).setScale(2,
				BigDecimal.ROUND_HALF_UP).floatValue()
				+ "");
		tv_history_spirit_daily.setText(new BigDecimal(stepSum
				/ mValues.length).setScale(2, BigDecimal.ROUND_HALF_UP)
				.floatValue() + "");

	}

	private void initView() {
		bcv_spirit = (BarChartView) mView.findViewById(R.id.bcv_spirit);
		tv_history_spirit_daily = (TextView) mView
				.findViewById(R.id.tv_history_spirit_daily);
		tv_history_spirit_sum = (TextView) mView
				.findViewById(R.id.tv_history_spirit_sum);
		bcv_spirit.setOnEntryClickListener(this);
	}

	@Override
	public void onClick(int setIndex, int entryIndex, Rect rect) {
		if (mBarTooltip == null)
			showBarTooltip(entryIndex, rect);
		else
			dismissBarTooltip(entryIndex, rect);

	}

	public void updateBarChart(int nPoints) {
		bcv_spirit.reset();
		BarSet data = new BarSet();
		int index = mSteps.size();
		int start = 0;
		if (index < nPoints) {
			start = nPoints - index;
		} else {
			start = index - nPoints;
		}
		// TODO
		for (int j = 0; j < nPoints; j++) {
			Bar bar;
			if (index < nPoints && j < start) {
				bar = new Bar(mLabels[j], 0f);
				mValues[j] = 0 + "";
			} else {
				if (index < nPoints) {
					bar = new Bar(mLabels[j], Float.valueOf(mSteps.get(j
							- start).spirit));
					mValues[j] = mSteps.get(j - start).spirit;
				} else {
					bar = new Bar(mLabels[j], Float.valueOf(mSteps.get(j
							+ start).spirit));
					mValues[j] = mSteps.get(j + start).spirit;
				}
			}
			data.addBar(bar);
		}
		// TEST
		// int stepValue[] = { 10000, 5000, 2500, 1250, 2000, 4000, 8000 };
		// for (int i = 0; i < nPoints; i++) {
		// Bar bar = new Bar(mLabels[i], stepValue[i]);
		// mValues[i] = stepValue[i] + "";
		// data.addBar(bar);
		// }
		data.setColor(getResources().getColor(R.color.blue_b4efff));
		bcv_spirit.addData(data);

		bcv_spirit.setBarSpacing((int) Tools.fromDpToPx(50));
		bcv_spirit.setSetSpacing(0);
		bcv_spirit.setBarBackground(false);
		bcv_spirit.setRoundCorners(0);
		// 运动目标值
		// bcv_spirit.setmThresholdText(BAR_STEP_AIM + "");
		bcv_spirit.setBorderSpacing(0).setGrid(null).setHorizontalGrid(null)
				.setVerticalGrid(null)
				.setYLabels(YController.LabelPosition.NONE).setYAxis(false)
				.setXLabels(XController.LabelPosition.OUTSIDE).setXAxis(true)
				.setMaxAxisValue(BAR_SPIRIT_MAX, 1)
				// .setThresholdLine(BAR_STEP_AIM, DataRetriever.randPaint())
				.animate(DataRetriever.randAnimation(mEndAction, nPoints));
	}

	private void showBarTooltip(int index, Rect rect) {

		mBarTooltip = (TextView) mActivity.getLayoutInflater().inflate(
				R.layout.tooltip, null);
		mBarTooltip.setText("" + mValues[index]);

		LayoutParams layoutParams = new LayoutParams(rect.width()
				+ (int) Tools.fromDpToPx(40), LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = rect.left - (int) Tools.fromDpToPx(20);
		layoutParams.topMargin = rect.top - (int) Tools.fromDpToPx(20);
		mBarTooltip.setLayoutParams(layoutParams);

		bcv_spirit.showTooltip(mBarTooltip);
		bcv_spirit.invalidate();
	}

	private void dismissBarTooltip(final int index, final Rect rect) {

		bcv_spirit.dismissTooltip(mBarTooltip);

		mBarTooltip = null;
		if (index != -1)
			showBarTooltip(index, rect);
	}

	private class StepCompare implements Comparator<Step> {

		@Override
		public int compare(Step lhs, Step rhs) {
			if (Float.valueOf(lhs.spirit) > Float.valueOf(rhs.spirit)) {
				return -1;
			} else if (Float.valueOf(lhs.spirit) < Float
					.valueOf(rhs.spirit)) {
				return 1;
			}
			return 0;
		}

	}
}
