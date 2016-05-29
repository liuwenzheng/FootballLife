package com.blestep.footballlife.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blestep.footballlife.R;
import com.blestep.footballlife.base.BaseActivity;
import com.blestep.footballlife.utils.SportDataUtils;
import com.blestep.footballlife.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lwz on 2016/5/19 0019.
 */
public class ParamActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_confirm)
    TextView tvConfirm;
    @Bind(R.id.et_param)
    EditText etParam;
    @Bind(R.id.et_max_step)
    EditText etMaxStep;
    @Bind(R.id.et_max_distance)
    EditText etMaxDistance;
    @Bind(R.id.et_max_calorie)
    EditText etMaxCalorie;
    @Bind(R.id.et_max_bmi)
    EditText etMaxBmi;
    @Bind(R.id.et_max_bfr)
    EditText etMaxBfr;
    @Bind(R.id.et_max_bmr)
    EditText etMaxBmr;
    @Bind(R.id.et_max_speed)
    EditText etMaxSpeed;
    @Bind(R.id.et_max_power)
    EditText etMaxPower;
    @Bind(R.id.et_max_explosive)
    EditText etMaxExplosive;
    @Bind(R.id.et_max_endurance)
    EditText etMaxEndurance;
    @Bind(R.id.et_max_spirit)
    EditText etMaxSpirit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.param_layout);
        ButterKnife.bind(this);
        etParam.setText(SportDataUtils.speed_factor + "");
        etMaxStep.setText(SportDataUtils.MAX_STEP + "");
        etMaxDistance.setText(SportDataUtils.MAX_DISTANCE + "");
        etMaxCalorie.setText(SportDataUtils.MAX_CALORIE + "");
        etMaxBmi.setText(SportDataUtils.MAX_BMI + "");
        etMaxBfr.setText(SportDataUtils.MAX_BFR + "");
        etMaxBmr.setText(SportDataUtils.MAX_BMR + "");
        etMaxSpeed.setText(SportDataUtils.MAX_SPEED + "");
        etMaxPower.setText(SportDataUtils.MAX_POWER + "");
        etMaxExplosive.setText(SportDataUtils.MAX_EXPLOSIVE + "");
        etMaxEndurance.setText(SportDataUtils.MAX_ENDURANCE + "");
        etMaxSpirit.setText(SportDataUtils.MAX_SPIRIT + "");
    }

    @OnClick({R.id.iv_back, R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.tv_confirm:
                if (!TextUtils.isEmpty(etParam.getText().toString())
                        || !TextUtils.isEmpty(etMaxStep.getText().toString())
                        || !TextUtils.isEmpty(etMaxDistance.getText().toString())
                        || !TextUtils.isEmpty(etMaxCalorie.getText().toString())
                        || !TextUtils.isEmpty(etMaxBmi.getText().toString())
                        || !TextUtils.isEmpty(etMaxBfr.getText().toString())
                        || !TextUtils.isEmpty(etMaxBmr.getText().toString())
                        || !TextUtils.isEmpty(etMaxSpeed.getText().toString())
                        || !TextUtils.isEmpty(etMaxPower.getText().toString())
                        || !TextUtils.isEmpty(etMaxExplosive.getText().toString())
                        || !TextUtils.isEmpty(etMaxEndurance.getText().toString())
                        || !TextUtils.isEmpty(etMaxSpirit.getText().toString())) {
                    try {
                        float factor = Float.valueOf(etParam.getText().toString());
                        SportDataUtils.speed_factor = factor;
                        int step = Integer.valueOf(etMaxStep.getText().toString());
                        SportDataUtils.MAX_STEP = step;
                        int distance = Integer.valueOf(etMaxDistance.getText().toString());
                        SportDataUtils.MAX_DISTANCE = distance;
                        int calorie = Integer.valueOf(etMaxCalorie.getText().toString());
                        SportDataUtils.MAX_CALORIE = calorie;
                        float bmi = Float.valueOf(etMaxBmi.getText().toString());
                        SportDataUtils.MAX_BMI = bmi;
                        float bfr = Float.valueOf(etMaxBfr.getText().toString());
                        SportDataUtils.MAX_BFR = bfr;
                        float bmr = Float.valueOf(etMaxBmr.getText().toString());
                        SportDataUtils.MAX_BMR = bmr;
                        float speed = Float.valueOf(etMaxSpeed.getText().toString());
                        SportDataUtils.MAX_SPEED = speed;
                        float power = Float.valueOf(etMaxPower.getText().toString());
                        SportDataUtils.MAX_POWER = power;
                        float explosive = Float.valueOf(etMaxExplosive.getText().toString());
                        SportDataUtils.MAX_EXPLOSIVE = explosive;
                        float endurance = Float.valueOf(etMaxEndurance.getText().toString());
                        SportDataUtils.MAX_ENDURANCE = endurance;
                        float spirit = Float.valueOf(etMaxSpirit.getText().toString());
                        SportDataUtils.MAX_SPIRIT = spirit;
                        ToastUtils.showToast(this, "设置成功！！！");
                        this.finish();
                    } catch (Exception e) {
                        ToastUtils.showToast(this, "请输入正确格式！！！");
                        return;
                    }
                } else {
                    ToastUtils.showToast(this, "不能为空！！！");
                    return;
                }
                break;
        }
    }
}
