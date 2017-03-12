package net.rivergod.sec.seoulrnd.android.menu.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tonicartos.superslim.LayoutManager;

import net.rivergod.sec.seoulrnd.android.menu.MenuContract;
import net.rivergod.sec.seoulrnd.android.menu.R;
import net.rivergod.sec.seoulrnd.android.menu.model.dto.CuisineDTO;
import net.rivergod.sec.seoulrnd.android.menu.presenter.MenuPresenter;
import net.rivergod.sec.seoulrnd.android.menu.presenter.MenuPresenter.TimeType;

import java.util.Calendar;
import java.util.List;

public class MenuActivity extends Activity implements MenuContract.View {

    private static final String TAG = "MenuActivity";

    public static final String CAMPUS_TAG = "Campus";
    public static final String ALARM_TAG = "Alarm";

    private MenuItemAdapter adapter;

    private RelativeLayout tabBreakfast;
    private RelativeLayout tabLunch;
    private RelativeLayout tabDinner;

    private TextView tabBreakfastText;
    private TextView tabLunchText;
    private TextView tabDinnerText;

    private ProgressDialog progress;

    private MenuContract.Presenter mPresenter;

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        Log.i(TAG, "Setting screen name: MenuActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MenuPresenter menuPresenter = new MenuPresenter(this, this);

        new OptionView(MenuActivity.this, menuPresenter);

        RecyclerView gridMenuItems = (RecyclerView) findViewById(R.id.grid_menu_items);
        gridMenuItems.setLayoutManager(new LayoutManager(this));

        adapter = new MenuItemAdapter(this);
        gridMenuItems.setAdapter(adapter);

        tabBreakfast = (RelativeLayout) findViewById(R.id.menu_tab_breakfast);
        tabLunch = (RelativeLayout) findViewById(R.id.menu_tab_lunch);
        tabDinner = (RelativeLayout) findViewById(R.id.menu_tab_dinner);

        tabBreakfast.setOnClickListener(TabClickListener);
        tabLunch.setOnClickListener(TabClickListener);
        tabDinner.setOnClickListener(TabClickListener);

        tabBreakfastText = (TextView) findViewById(R.id.menu_tab_breakfast_select);
        tabLunchText = (TextView) findViewById(R.id.menu_tab_lunch_select);
        tabDinnerText = (TextView) findViewById(R.id.menu_tab_dinner_select);

    }

    @Override
    public void setToday() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        String Week = "";
        if (week == 1) Week = "일";
        else if (week == 2) Week = "월";
        else if (week == 3) Week = "화";
        else if (week == 4) Week = "수";
        else if (week == 5) Week = "목";
        else if (week == 6) Week = "금";
        else if (week == 7) Week = "토";

        ((TextView) findViewById(R.id.menu_top_title)).setText(month + "월 " + day + "일 (" + Week + ")");
    }

    private View.OnClickListener TabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_tab_breakfast:
                    mPresenter.timeTypeChanged(TimeType.TIME_TYPE_BREAKFAST);
                    break;
                case R.id.menu_tab_lunch:
                    mPresenter.timeTypeChanged(TimeType.TIME_TYPE_LUNCH);
                    break;
                case R.id.menu_tab_dinner:
                    mPresenter.timeTypeChanged(TimeType.TIME_TYPE_DINNER);
                    break;
            }
        }
    };

    @Override
    public void setPresenter(MenuContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoadingDialog() {
        progress = ProgressDialog.show(MenuActivity.this, "", "Menu data loading....", true);
    }

    @Override
    public void hideLoadingDialog() {
        if (progress != null)
            progress.dismiss();
    }

    @Override
    public void setMenuType(MenuPresenter.TimeType timeType, List<CuisineDTO> adapterItems) {

        tabBreakfast.setBackground(null);
        tabLunch.setBackground(null);
        tabDinner.setBackground(null);

        tabBreakfastText.setBackground(null);
        tabLunchText.setBackground(null);
        tabDinnerText.setBackground(null);

        tabBreakfastText.setTextColor(Color.rgb(2, 4, 101));
        tabLunchText.setTextColor(Color.rgb(2, 4, 101));
        tabDinnerText.setTextColor(Color.rgb(2, 4, 101));

        switch (timeType) {
            case TIME_TYPE_BREAKFAST:
                tabBreakfastText.setTextColor(Color.WHITE);
                tabBreakfast.setBackgroundColor(Color.rgb(166, 229, 255));
                break;

            case TIME_TYPE_LUNCH:
                tabLunchText.setTextColor(Color.WHITE);
                tabLunch.setBackgroundColor(Color.rgb(166, 229, 255));
                break;

            case TIME_TYPE_DINNER:
                tabDinnerText.setTextColor(Color.WHITE);
                tabDinner.setBackgroundColor(Color.rgb(166, 229, 255));
                break;
        }

        adapter.setItems(adapterItems);
    }


    @Override
    public void showToastNotResponseFromServer() {
        Toast.makeText(getApplicationContext(), "Network 연결을 확인 하세요.\n Server 응답이 없습니다.", Toast.LENGTH_SHORT).show();
    }
}
