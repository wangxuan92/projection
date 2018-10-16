package io.kuban.projection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.R;
import io.kuban.projection.base.Constants;
import io.kuban.projection.event.BindingResultsEvent;
import io.kuban.projection.model.PadsModel;
import io.kuban.projection.util.AnimatorUtils;

/**
 * Created by wangxuan on 17/11/21.
 */

public class BindingSuccessfulActivity extends BaseCompatActivity {
    @BindView(R.id.company_name)
    TextView mCompanyName;
    @BindView(R.id.location_name)
    TextView mLocationName;
    @BindView(R.id.again_match)
    TextView againMatch;
    @BindView(R.id.animation_view_click)
    LottieAnimationView mAnimationView;
    private PadsModel padsModel;
    private boolean isCanReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binding_successful_activity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (null != intent && null != intent.getExtras()) {
            padsModel = (PadsModel) intent.getExtras().getSerializable(Constants.PADS_MODEL);
            isCanReturn = intent.getExtras().getBoolean(Constants.IS_CAN_RETURN);
        }
        if (null != padsModel) {
            if (null != padsModel.space) {
                mCompanyName.setText(padsModel.space.name);
            }
            if (null != padsModel.location) {
                mLocationName.setText(padsModel.location.name);
            }
        }
        if (isCanReturn) {
            againMatch.setVisibility(View.VISIBLE);
        }
        AnimatorUtils.loading(this, mAnimationView, "data.json", false);
    }

    @OnClick({R.id.open_visitors_system, R.id.again_match})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_visitors_system:
                EventBus.getDefault().post(new BindingResultsEvent(true));
                break;
            case R.id.again_match:
//                EventBus.getDefault().post(new BindingResultsEvent(false));
                break;
        }
        finish();
    }
}
