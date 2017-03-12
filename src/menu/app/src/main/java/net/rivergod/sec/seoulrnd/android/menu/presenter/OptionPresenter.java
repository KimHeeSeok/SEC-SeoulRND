package net.rivergod.sec.seoulrnd.android.menu.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import net.rivergod.sec.seoulrnd.android.menu.MenuContract;
import net.rivergod.sec.seoulrnd.android.menu.OptionContract;
import net.rivergod.sec.seoulrnd.android.menu.R;
import net.rivergod.sec.seoulrnd.android.menu.RegisterAlarm;
import net.rivergod.sec.seoulrnd.android.menu.view.MenuActivity;

public class OptionPresenter implements OptionContract.Presenter {

    public static final String HOUR = "_hour";
    public static final String MINUTE = "_minute";
    public static final String SELECT = "_select";

    public static final int ALARM_INDEX_0_DESIGN = 0;
    public static final int ALARM_INDEX_1_DMC = 1;
    public static final int ALARM_INDEX_2_SW = 2;
    public static final int ALARM_INDEX_3_ETC = 3;
    public static final int ALARM_INDEX_4_CUSTOM = 4;

    private Context mContext;
    private MenuContract.Presenter mMenuPresenter;
    private OptionContract.View mView;

    private SharedPreferences.Editor mPrefsEditor;

    public OptionPresenter(Context context, MenuContract.Presenter menuPresenter, OptionContract.View view) {
        this.mContext = context;
        this.mMenuPresenter = menuPresenter;
        this.mView = view;
        view.setPresenter(this);

        SharedPreferences prefs = mContext.getSharedPreferences(MenuActivity.ALARM_TAG, Context.MODE_PRIVATE);
        mPrefsEditor = prefs.edit();

        mView.setCampusCheckBox(prefs.getInt(MenuActivity.CAMPUS_TAG, R.id.orderCampus2));

        int selectAlarmIndex = prefs.getInt(MenuActivity.ALARM_TAG + OptionPresenter.SELECT, -1);
        selectAlarm(selectAlarmIndex, false);

        int alarmHour = prefs.getInt(MenuActivity.ALARM_TAG + OptionPresenter.HOUR, -1);
        int alarmMinute = prefs.getInt(MenuActivity.ALARM_TAG + OptionPresenter.MINUTE, -1);
        mView.setCustomTimeText(alarmHour, alarmMinute);
    }

    @Override
    public void campusChanged(int campusId) {
        mPrefsEditor.putInt(MenuActivity.CAMPUS_TAG, campusId);
        mPrefsEditor.commit();
        mMenuPresenter.campusChanged();
        mView.setCampusCheckBox(campusId);
    }

    @Override
    public void setCustomTime(int hour, int minute) {

        mPrefsEditor.putInt(MenuActivity.ALARM_TAG + HOUR, hour);
        mPrefsEditor.putInt(MenuActivity.ALARM_TAG + MINUTE, minute);
        mPrefsEditor.commit();

        selectAlarm(ALARM_INDEX_4_CUSTOM, true);

        mView.setCustomTimeText(hour, minute);
    }

    @Override
    public void selectAlarm(int selectAlarmIndex, boolean fromUser) {
        int hour = -1;
        int minute = -1;

        switch (selectAlarmIndex) {
            case ALARM_INDEX_0_DESIGN:
                hour = 11;
                minute = 30;
                break;
            case ALARM_INDEX_1_DMC:
                hour = 12;
                minute = 0;
                break;
            case ALARM_INDEX_2_SW:
                hour = 12;
                minute = 20;
                break;
            case ALARM_INDEX_3_ETC:
                hour = 12;
                minute = 20;
                break;
            case ALARM_INDEX_4_CUSTOM:
                SharedPreferences prefs = mContext.getSharedPreferences(MenuActivity.ALARM_TAG, Context.MODE_PRIVATE);
                hour = prefs.getInt(MenuActivity.ALARM_TAG + OptionPresenter.HOUR, -1);
                minute = prefs.getInt(MenuActivity.ALARM_TAG + OptionPresenter.MINUTE, -1);

                if (!RegisterAlarm.isValid(hour, minute)) {
                    if (fromUser) {
                        mView.showCustomTimeSettingPopup();
                    }
                    return;
                }
                break;
        }

        // validation
        if (RegisterAlarm.isValid(hour, minute)) {

            RegisterAlarm.unregister(mContext);

            if (fromUser) {
                mPrefsEditor.putInt(MenuActivity.ALARM_TAG + OptionPresenter.SELECT, selectAlarmIndex);
                mPrefsEditor.commit();
            }

            RegisterAlarm.register(mContext, hour, minute);
        }

        mView.setAlarmCheckBox(selectAlarmIndex);
    }
}
