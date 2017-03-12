package net.rivergod.sec.seoulrnd.android.menu.presenter;

import android.app.Activity;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.rivergod.sec.seoulrnd.android.menu.MenuApplication;
import net.rivergod.sec.seoulrnd.android.menu.MenuContract;
import net.rivergod.sec.seoulrnd.android.menu.model.Communicator;
import net.rivergod.sec.seoulrnd.android.menu.model.dto.CuisineDTO;
import net.rivergod.sec.seoulrnd.android.menu.model.dto.DayCuisionsDTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MenuPresenter implements MenuContract.Presenter {

    public enum TimeType {
        TIME_TYPE_BREAKFAST,
        TIME_TYPE_LUNCH,
        TIME_TYPE_DINNER
    }

    ;

    private MenuContract.View mView;
    private TimeType mSelectedTime;

    private Tracker mTracker;

    private List<CuisineDTO> menuBreakfast;
    private List<CuisineDTO> menuLunch;
    private List<CuisineDTO> menuDinner;

    public MenuPresenter(MenuContract.View view, Activity activity) {
        this.mView = view;
        view.setPresenter(this);

        MenuApplication application = (MenuApplication) activity.getApplication();
        mTracker = application.getDefaultTracker();

        menuBreakfast = new ArrayList<>();
        menuLunch = new ArrayList<>();
        menuDinner = new ArrayList<>();

        setToday();

        Communicator.init(activity);
        Communicator.getEventBus().register(this);

        mView.showLoadingDialog();
        Communicator.getMenu(null);
    }

    @Override
    public void onResume() {
        mTracker.setScreenName("MenuActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToday() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            mSelectedTime = TimeType.TIME_TYPE_BREAKFAST;
        } else if (hour < 14) {
            mSelectedTime = TimeType.TIME_TYPE_LUNCH;
        } else {
            mSelectedTime = TimeType.TIME_TYPE_DINNER;
        }

        mView.setToday();
    }

    @Override
    public void timeTypeChanged(TimeType timeType) {
        mSelectedTime = timeType;

        switch (timeType) {
            case TIME_TYPE_BREAKFAST:
                mView.setMenuType(timeType, menuBreakfast);
                break;

            case TIME_TYPE_LUNCH:
                mView.setMenuType(timeType, menuLunch);
                break;

            case TIME_TYPE_DINNER:
                mView.setMenuType(timeType, menuDinner);
                break;
        }
    }

    @Override
    public void campusChanged() {
        timeTypeChanged(mSelectedTime);
    }

    @SuppressWarnings("unused")
    public void onEvent(DayCuisionsDTO e) {
        mView.hideLoadingDialog();

        menuBreakfast.clear();
        menuLunch.clear();
        menuDinner.clear();

        for (CuisineDTO item : e.getCuisines()) {
            int mealCode = item.getMealCode();

            switch (TimeType.values()[mealCode]) {
                case TIME_TYPE_BREAKFAST:
                    menuBreakfast.add(item);
                    break;
                case TIME_TYPE_LUNCH:
                    menuLunch.add(item);
                    break;
                case TIME_TYPE_DINNER:
                    menuDinner.add(item);
                    break;
            }
        }

        timeTypeChanged(mSelectedTime);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("MenuActivity")
                .setAction("onEvent(e)")
                .build());
    }

    @SuppressWarnings("unused")
    public void onEvent(VolleyError error) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("MenuActivity")
                .setAction("onEvent(error)")
                .build());

        mView.hideLoadingDialog();
        mView.showToastNotResponseFromServer();
    }
}
