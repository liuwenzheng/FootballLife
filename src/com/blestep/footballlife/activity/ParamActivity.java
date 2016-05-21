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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.param_layout);
        ButterKnife.bind(this);
        etParam.setText(SportDataUtils.speed_factor + "");
    }

    @OnClick({R.id.iv_back, R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.tv_confirm:
                if (!TextUtils.isEmpty(etParam.getText().toString())) {
                    try {
                        float factor = Float.valueOf(etParam.getText().toString());
                        SportDataUtils.speed_factor = factor;
                        ToastUtils.showToast(this, "设置成功！！！");
                        this.finish();
                    } catch (Exception e) {
                        ToastUtils.showToast(this, "请输入正确格式！！！");
                        return;
                    }
                }
                break;
        }
    }
}
