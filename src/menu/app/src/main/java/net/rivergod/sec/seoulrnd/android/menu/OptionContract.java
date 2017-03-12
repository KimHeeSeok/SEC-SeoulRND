package net.rivergod.sec.seoulrnd.android.menu;

import net.rivergod.sec.seoulrnd.android.menu.presenter.BasePresenter;
import net.rivergod.sec.seoulrnd.android.menu.view.BaseView;

public interface OptionContract {

    interface View extends BaseView<OptionContract.Presenter> {

        void setCampusCheckBox(int campusId);

        void setAlarmCheckBox(int selectAlarmIndex);

        void setCustomTimeText(int hour, int minute);

        void setCustomTimeFromPopup(String time);

        void showCustomTimeSettingPopup();
    }

    interface Presenter extends BasePresenter {

        void campusChanged(int campusId);

        void setCustomTime(int hour, int minute);

        void selectAlarm(int selectAlarmIndex, boolean fromUser);

    }
}
