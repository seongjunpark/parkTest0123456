package www.funsumer.net;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog implements View.OnClickListener {
	static final float DIMENSIONS_DIFF_PORTRAIT = 40;
	private CustomDialogListener mListener;
	private Context mContext;
	private int mType;

	private String mStr;
	private int IMAGE_MAX_SIZE = 1500;

	public static final int TYPE_BASIC_CENCEL_OK = 10;
	public static final int TYPE_BASIC_OK = 20;
	public static final int TYPE_SINGO = 30;
	public static final int TYPE_REPLY_SINGO = 40;
	public static final int TYPE_LOGIN_SUCCESS = 50;
	public static final int TYPE_WRITE_SUCCESS = 60;
	public static final int TYPE_BUY_COMPLETE = 70;
	public static final int TYPE_BUY_COMPLETE2 = 80;
	public static final int TYPE_INPUT_YOUTUBE = 90;
	public static final int TYPE_INPUT_COPY_MUSIC = 100;
	public static final int TYPE_LOGOUT = 110;

	private String mResult;
	private LinearLayout layout_right1;
	private LinearLayout layout_right2;
	private LinearLayout layout_right3;
	private LinearLayout layout_right4;

	private EditText et_name;

//	public CustomDialog(Context context, int type, CustomDialogListener listener) {
//		super(context);
//		mContext = context;
//		mListener = listener;
//		mType = type;
//	}

	public CustomDialog(Context context, int type,
			CustomDialogListener listener, String str) {
		super(context);
		mContext = context;
		mListener = listener;
		mType = type;
		mStr = str;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater vi = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = null;
		if (mType == TYPE_BASIC_CENCEL_OK || mType == TYPE_LOGOUT) {
			v = vi.inflate(R.layout.custom_dialog_basic, null);

			Button btn_ok = (Button) v.findViewById(R.id.btn_ok);
			btn_ok.setOnClickListener(this);

			TextView tv_desc = (TextView) v.findViewById(R.id.tv_desc);
			tv_desc.setText(mStr);

			Button btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
			btn_cancel.setOnClickListener(this);
		} else if (mType == TYPE_BUY_COMPLETE2) {
			v = vi.inflate(R.layout.custom_dialog_basic, null);
			Button btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
			btn_cancel.setVisibility(View.GONE);

			TextView tv_desc = (TextView) v.findViewById(R.id.tv_desc);
			String str = "<font color='#BE7A7C'>My Page/악장관리/찜(구매)/내가 구매한 아이템</font>에 복사해 두었습니다.";
			tv_desc.setText(Html.fromHtml(str));

			Button btn_ok = (Button) v.findViewById(R.id.btn_ok);
			btn_ok.setOnClickListener(this);
		} else if (mType == TYPE_BUY_COMPLETE) {
			v = vi.inflate(R.layout.custom_dialog_basic, null);

			Button btn_ok = (Button) v.findViewById(R.id.btn_ok);
			btn_ok.setOnClickListener(this);

			TextView tv_desc = (TextView) v.findViewById(R.id.tv_desc);
			String str = "향후 A/S등 판매자와 연락을 취하거나, 올바른 거래문화 정착을 위한 목적입니다.<font color='#BE7A7C'>구매 완료는 회원님이 구매한 아이템에 대해서만 확인하시기 바랍니다.</font>";
			tv_desc.setText(Html.fromHtml(str));

			Button btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
			btn_cancel.setOnClickListener(this);
		} else if (mType == TYPE_BASIC_OK || mType == TYPE_LOGIN_SUCCESS
				|| mType == TYPE_WRITE_SUCCESS) {
			v = vi.inflate(R.layout.custom_dialog_basic, null);
			Button btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
			btn_cancel.setVisibility(View.GONE);

			TextView tv_desc = (TextView) v.findViewById(R.id.tv_desc);
			tv_desc.setText(mStr);

			Button btn_ok = (Button) v.findViewById(R.id.btn_ok);
			btn_ok.setOnClickListener(this);
		} else if (mType == TYPE_SINGO || mType == TYPE_REPLY_SINGO) {

			Button btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
			btn_cancel.setOnClickListener(this);

			Button btn_ok = (Button) v.findViewById(R.id.btn_ok);
			btn_ok.setOnClickListener(this);

			mResult = "1";
		}

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		float dimensions = DIMENSIONS_DIFF_PORTRAIT;

		addContentView(v, new LinearLayout.LayoutParams(display.getWidth()
				- ((int) (dimensions * scale + 0.5f)),
				LinearLayout.LayoutParams.WRAP_CONTENT));

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mListener != null)
			mListener.onClose(mType);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		//
		case R.id.btn_cancel:
			if (mListener != null)
				mListener.onCancel(mType);
			dismiss();
			break;
		case R.id.btn_ok: {
			if (mType == TYPE_SINGO || mType == TYPE_REPLY_SINGO) {
				if (mListener != null)
					mListener.onOK(mType, mResult);
			} else if (mType == TYPE_INPUT_YOUTUBE
					|| mType == TYPE_INPUT_COPY_MUSIC) {
				String result = et_name.getText().toString();
				if (mListener != null)
					mListener.onOK(mType, result);
			} else {
				if (mListener != null)
					mListener.onOK(mType);
			}
			dismiss();
		}
			break;
		}

	}

	private void setSingo(int type) {
		if (type == 1) {
			mResult = "1";
			layout_right1.setVisibility(View.VISIBLE);
			layout_right2.setVisibility(View.GONE);
			layout_right3.setVisibility(View.GONE);
			layout_right4.setVisibility(View.GONE);
		} else if (type == 2) {
			mResult = "2";
			layout_right1.setVisibility(View.GONE);
			layout_right2.setVisibility(View.VISIBLE);
			layout_right3.setVisibility(View.GONE);
			layout_right4.setVisibility(View.GONE);
		} else if (type == 3) {

			mResult = "3";
			layout_right1.setVisibility(View.GONE);
			layout_right2.setVisibility(View.GONE);
			layout_right3.setVisibility(View.VISIBLE);
			layout_right4.setVisibility(View.GONE);
		} else if (type == 4) {

			mResult = "4";
			layout_right1.setVisibility(View.GONE);
			layout_right2.setVisibility(View.GONE);
			layout_right3.setVisibility(View.GONE);
			layout_right4.setVisibility(View.VISIBLE);
		}
	}
}