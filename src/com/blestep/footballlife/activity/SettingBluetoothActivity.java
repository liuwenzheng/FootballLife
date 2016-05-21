package com.blestep.footballlife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.blestep.footballlife.R;
import com.blestep.footballlife.base.BaseActivity;
import com.blestep.footballlife.module.BTModule;
import com.umeng.analytics.MobclickAgent;

public class SettingBluetoothActivity extends BaseActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_bluetooth);
		initView();
		initListener();
		initData();
	}

	private void initListener() {
		findViewById(R.id.tv_setting_next).setOnClickListener(this);
	}

	private void initData() {
		// 开启蓝牙
		if (!BTModule.isBluetoothOpen()) {
			BTModule.openBluetooth(SettingBluetoothActivity.this);
		}
	}

	private void initView() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_setting_next:
			if (!BTModule.isBluetoothOpen()) {
				BTModule.openBluetooth(SettingBluetoothActivity.this);
			} else {
				startActivity(new Intent(this, SettingDeviceActivity.class));
				this.finish();
			}
			break;

		default:
			break;
		}
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
