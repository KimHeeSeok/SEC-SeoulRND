package net.rivergod.sec.seoulrnd.android.menu.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.rivergod.sec.seoulrnd.android.menu.MenuContract;
import net.rivergod.sec.seoulrnd.android.menu.OptionContract;
import net.rivergod.sec.seoulrnd.android.menu.R;
import net.rivergod.sec.seoulrnd.android.menu.RegisterAlarm;
import net.rivergod.sec.seoulrnd.android.menu.presenter.OptionPresenter;

import java.util.Timer;
import java.util.TimerTask;

public class OptionView implements OptionContract.View {

    private Context mContext;

    private LinearLayout lyMenu;

    private float maxWidth;
    private float closeViewWidth;

    private Timer timer;
    private float positionX;

    private final int FRAME = 10;
    private final int MOVE_PIXEL = 40;

    private ImageView campus1Check;
    private ImageView campus2Check;
    private ViewGroup campus1Layout;
    private ViewGroup campus2Layout;

    private TextView tvAlarmUserSet;

    private ImageView ivAlarmCheck0;
    private ImageView ivAlarmCheck1;
    private ImageView ivAlarmCheck2;
    private ImageView ivAlarmCheck3;
    private ImageView ivAlarmCheck4;

    private View mAboutLicense;

    private View prevAlarm;

    private CustomTimeSettingDialog mPopUp;

    private OptionContract.Presenter mPresenter;

    public OptionView(Activity activity, MenuContract.Presenter menuPresenter) {
        this.mContext = activity;

        maxWidth = activity.getResources().getDimension(R.dimen.screen_max);
        closeViewWidth = activity.getResources().getDimension(R.dimen.close_menu);

        lyMenu = (LinearLayout) activity.findViewById(R.id.common_menu_layout);
        lyMenu.setX(maxWidth);

        activity.findViewById(R.id.menu_tab_setting).setOnClickListener(mOptionShowHideClickListener);
        activity.findViewById(R.id.common_menu_close).setOnClickListener(mOptionShowHideClickListener);

        activity.findViewById(R.id.menu_alarm_select_0_layout).setOnClickListener(mAlarmCheckboxClickListener);
        activity.findViewById(R.id.menu_alarm_select_1_layout).setOnClickListener(mAlarmCheckboxClickListener);
        activity.findViewById(R.id.menu_alarm_select_2_layout).setOnClickListener(mAlarmCheckboxClickListener);
        activity.findViewById(R.id.menu_alarm_select_3_layout).setOnClickListener(mAlarmCheckboxClickListener);
        activity.findViewById(R.id.menu_alarm_select_4_layout).setOnClickListener(mAlarmCheckboxClickListener);

        campus1Layout = (ViewGroup) activity.findViewById(R.id.orderCampus1_layout);
        campus2Layout = (ViewGroup) activity.findViewById(R.id.orderCampus2_layout);
        campus1Layout.setOnClickListener(mCampusOrderClickListener);
        campus2Layout.setOnClickListener(mCampusOrderClickListener);

        campus1Check = (ImageView) activity.findViewById(R.id.orderCampus1);
        campus2Check = (ImageView) activity.findViewById(R.id.orderCampus2);

        tvAlarmUserSet = (TextView) activity.findViewById(R.id.option_alarm_user_set_time);
        tvAlarmUserSet.setOnClickListener(mCustomTimeSettingButtonClickListener);

        ivAlarmCheck0 = (ImageView) activity.findViewById(R.id.menu_alarm_select_0_check);
        ivAlarmCheck1 = (ImageView) activity.findViewById(R.id.menu_alarm_select_1_check);
        ivAlarmCheck2 = (ImageView) activity.findViewById(R.id.menu_alarm_select_2_check);
        ivAlarmCheck3 = (ImageView) activity.findViewById(R.id.menu_alarm_select_3_check);
        ivAlarmCheck4 = (ImageView) activity.findViewById(R.id.menu_alarm_select_4_check);

        mAboutLicense = activity.findViewById(R.id.option_license);
        mAboutLicense.setOnClickListener(mAboutLicenseButtonListener);

        mPopUp = new CustomTimeSettingDialog(activity, this);

        new OptionPresenter(activity, menuPresenter, this);
    }

    @Override
    public void setPresenter(OptionContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private void showOption() {
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                lyMenu.post(new Runnable() {
                    @Override
                    public void run() {
                        lyMenu.setX(positionX);

                        positionX -= MOVE_PIXEL;
                        if (positionX < MOVE_PIXEL) {
                            lyMenu.setX(0);
                            cancel();
                        }
                    }
                });
            }
        };

        positionX = maxWidth - closeViewWidth;

        timer.schedule(task, 0, FRAME);
    }

    private void hideOption() {
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                lyMenu.post(new Runnable() {
                    @Override
                    public void run() {
                        lyMenu.setX(positionX);

                        positionX += MOVE_PIXEL;
                        if (positionX > maxWidth - closeViewWidth) {
                            lyMenu.setX(maxWidth);
                            cancel();
                        }
                    }
                });
            }
        };

        positionX = 0;

        timer.schedule(task, 0, FRAME);
    }

    @Override
    public void setCustomTimeText(int hour, int minute) {
        String timeValue = "";
        if (RegisterAlarm.isValid(hour, minute)) {
            timeValue = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
        }
        tvAlarmUserSet.setText(timeValue);
    }

    @Override
    public void setCustomTimeFromPopup(String time) {

        if (time.length() == 3 || time.length() == 4) {
            int[] convertTimeValues = convertTime(time);

            int hour = convertTimeValues[0];
            int minute = convertTimeValues[1];

            if (RegisterAlarm.isValid(hour, minute)) {
                mPresenter.setCustomTime(hour, minute);
                mPopUp.dismiss();
            }
        }

    }

    private int[] convertTime(String time) {

        int[] returnValue = new int[2];

        int hour = -1;
        int minute = -1;
        if (time != null) {
            time = time.replaceAll(":", "");
            if (time.length() == 3) {
                hour = Integer.parseInt(time.substring(0, 1));
                minute = Integer.parseInt(time.substring(1, 3));
            } else if (time.length() == 4) {
                hour = Integer.parseInt(time.substring(0, 2));
                minute = Integer.parseInt(time.substring(2, 4));
            }
        }
        returnValue[0] = hour;
        returnValue[1] = minute;

        return returnValue;

    }

    @Override
    public void setCampusCheckBox(int campusId) {

        if (campusId == R.id.orderCampus1) {
            campus1Check.setVisibility(View.VISIBLE);
            campus2Check.setVisibility(View.INVISIBLE);
        } else {
            campus1Check.setVisibility(View.INVISIBLE);
            campus2Check.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setAlarmCheckBox(int selectAlarmIndex) {

        if (selectAlarmIndex != -1) {

            View view = null;
            switch (selectAlarmIndex) {
                case OptionPresenter.ALARM_INDEX_0_DESIGN:
                    view = ivAlarmCheck0;
                    break;
                case OptionPresenter.ALARM_INDEX_1_DMC:
                    view = ivAlarmCheck1;
                    break;
                case OptionPresenter.ALARM_INDEX_2_SW:
                    view = ivAlarmCheck2;
                    break;
                case OptionPresenter.ALARM_INDEX_3_ETC:
                    view = ivAlarmCheck3;
                    break;
                case OptionPresenter.ALARM_INDEX_4_CUSTOM:
                    view = ivAlarmCheck4;
                    break;
            }

            if (prevAlarm != null) {
                prevAlarm.setVisibility(View.GONE);
            }
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            prevAlarm = view;
        }
    }

    @Override
    public void showCustomTimeSettingPopup() {

        String time = tvAlarmUserSet.getText().toString();
        if (time.length() != 0) {
            time = time.replaceAll(":", "");
        }
        mPopUp.setTimeText(time);
        mPopUp.show();
    }

    private View.OnClickListener mAboutLicenseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LicenseDialog licenseDialog = new LicenseDialog(mContext);
            licenseDialog.show();
        }
    };

    private View.OnClickListener mCampusOrderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int campusId;
            if (v.getId() == R.id.orderCampus1_layout) {
                campusId = R.id.orderCampus1;
            } else {
                campusId = R.id.orderCampus2;
            }

            mPresenter.campusChanged(campusId);
        }
    };

    private View.OnClickListener mCustomTimeSettingButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showCustomTimeSettingPopup();
        }
    };

    private View.OnClickListener mAlarmCheckboxClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selectAlarmIndex = Integer.parseInt((String) v.getTag());
            mPresenter.selectAlarm(selectAlarmIndex, true);
        }
    };

    private View.OnClickListener mOptionShowHideClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_tab_setting:
                    showOption();
                    break;

                case R.id.common_menu_close:
                    hideOption();
                    break;
            }
        }
    };
}
