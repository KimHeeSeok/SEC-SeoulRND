package net.rivergod.sec.seoulrnd.android.menu;

import net.rivergod.sec.seoulrnd.android.menu.model.dto.CuisineDTO;
import net.rivergod.sec.seoulrnd.android.menu.presenter.BasePresenter;
import net.rivergod.sec.seoulrnd.android.menu.presenter.MenuPresenter;
import net.rivergod.sec.seoulrnd.android.menu.view.BaseView;

import java.util.List;

public interface MenuContract {

    interface View extends BaseView<Presenter> {

        void showToastNotResponseFromServer();

        void showLoadingDialog();

        void hideLoadingDialog();

        void setMenuType(MenuPresenter.TimeType timeType, List<CuisineDTO> adapterItems);

        void setToday();
    }

    interface Presenter extends BasePresenter {

        void onResume();

        void campusChanged();

        void timeTypeChanged(MenuPresenter.TimeType timeType);

    }
}
