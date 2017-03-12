package net.rivergod.sec.seoulrnd.android.menu.view;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.rivergod.sec.seoulrnd.android.menu.OptionContract;
import net.rivergod.sec.seoulrnd.android.menu.R;

public class CustomTimeSettingDialog extends Dialog implements View.OnClickListener {

    private LinearLayout lyMsg;
    private TextView tvMsg;

    private LinearLayout lyTime;
    private EditText etTime;

    private OptionContract.View mView;

    public CustomTimeSettingDialog(Context context, OptionContract.View view) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        mView = view;

        setContentView(R.layout.custom_popup);

        lyMsg = (LinearLayout) findViewById(R.id.custom_popup_msg_layout);
        tvMsg = (TextView) findViewById(R.id.custom_popup_msg);
        tvMsg.setMovementMethod(new ScrollingMovementMethod());

        lyTime = (LinearLayout) findViewById(R.id.custom_popup_time_layout);
        etTime = (EditText) findViewById(R.id.custom_popup_set_time);
        findViewById(R.id.custom_popup_set_time_ok).setOnClickListener(this);
        findViewById(R.id.custom_popup_set_time_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.custom_popup_set_time_cancel:
                dismiss();
                break;
            case R.id.custom_popup_set_time_ok:
                mView.setCustomTimeFromPopup(etTime.getText().toString());
                break;
        }
    }

    public void setTimeText(String time) {
        if (time != null && time.length() > 0) {
            etTime.setText(time);
        }

        lyTime.setVisibility(View.VISIBLE);
        lyMsg.setVisibility(View.GONE);
    }

}
