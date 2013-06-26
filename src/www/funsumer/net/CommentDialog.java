package www.funsumer.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

import www.funsumer.net.constants.CommentInfo;
import www.funsumer.net.constants.TweetInfo;
import www.funsumer.net.widget.CommentAdapter;
import www.funsumer.net.widget.CustomAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommentDialog extends Dialog implements View.OnClickListener {
	// here upload start
	private String toid, mynoteid;
	static InputStream is = null;
	static String json = "";
	private String GCMname, GCMfrom;
	private List<String> registrationIds = new ArrayList<String>();
	private String comment;

	private Sender gcmSender;
	private Message gcmMessage;
	private MulticastResult gcmMultiResult;
	private static String API_KEY = "AIzaSyAulyO4EdZWoOvTb3-G_Fwv4c0wQLHcoyo";
	private static boolean DELAY_WHILE_IDLE = true;
	private static int TIME_TO_LIVE = 1800;
	private static int RETRY = 3;
	private static final String TAG = "GCM";
	// here upload end

	static final float DIMENSIONS_DIFF_PORTRAIT = 40;
	private CustomDialogListener mListener;
	private Context mContext;
	private int mType;

	// private String mStr;
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

	private EditText et_name;

	private CommentAdapter mList;


	public CommentDialog(Context context, int type,
			CustomDialogListener listener, CommentAdapter cAdapter) {
		super(context);
		mContext = context;
		mListener = listener;
		mType = type;
		mList = cAdapter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toid = CustomAdapter.articleid;
		mynoteid = MainActivity.mynoteid;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		LayoutInflater vi = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = null;

		v = vi.inflate(R.layout.comment_dialog, null);

		ListView com_ls = (ListView) v.findViewById(R.id.comment_list);
		com_ls.addFooterView(my_footer());

		com_ls.setAdapter(mList);
		com_ls.setDividerHeight(0);

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

		case R.id.btn_cancel:
			if (mListener != null)
				mListener.onCancel(mType);
			dismiss();
			break;
		case R.id.btn_okk: {
			onlyText();
			
			dismiss();
		}
		}

	}
	private LayoutInflater mInflater;
	public static LinearLayout my_footer = null;
	private LinearLayout my_footer() {
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		my_footer = (LinearLayout) mInflater.inflate(R.layout.comment_footer,
				null);

		Button btn_ok = (Button)my_footer.findViewById(R.id.btn_okk);
		btn_ok.setOnClickListener(this);
		return my_footer;
	}

	public void onlyText() {

		EditText editcomment = (EditText) findViewById(R.id.editcomment);
		comment = editcomment.getText().toString();

		if (comment.equals("") || comment.equals(null)) {
			dismiss();
		} else {

			try {

				HttpClient client = new DefaultHttpClient();
				String postURL = "http://funsumer.net/json/";
				HttpPost post = new HttpPost(postURL);


				List<BasicNameValuePair> params1 = new ArrayList<BasicNameValuePair>();
				params1.add(new BasicNameValuePair("oopt", "3"));
				params1.add(new BasicNameValuePair("mynoteid", mynoteid));
				params1.add(new BasicNameValuePair("toid", toid));
				params1.add(new BasicNameValuePair("content", comment));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1,
						HTTP.UTF_8);
				post.setEntity(ent);

				HttpResponse responsePOST = client.execute(post);

				HttpEntity resEntity = responsePOST.getEntity();
				is = resEntity.getContent();

				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "euc-kr"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					json = sb.toString();
				} catch (Exception e) {
					Log.e("Buffer Error",
							"Error converting result " + e.toString());
				}

				if (resEntity != null) {
					JSONArray obj = new JSONArray(json);

					for (int i = 0; i < obj.length(); i++) {

						JSONObject tjo = obj.getJSONObject(i);
						String regId = tjo.getString("ID");
						GCMname = tjo.getString("Name");
						GCMfrom = tjo.getString("From");

						registrationIds.add(0, regId);

					}
					setMessage();
					sendMessage();
				}

			} catch (Exception e) {

				// TODO: handle exception
			}
		}

	}

	public void setMessage() {
		gcmSender = new Sender(API_KEY);
		gcmMessage = new Message.Builder()
				// .collapseKey(COLLAPSE_KEY)
				.delayWhileIdle(DELAY_WHILE_IDLE).timeToLive(TIME_TO_LIVE)
				.addData("ticker", GCMname + "님이 글을 남겼습니다.")
				.addData("title", "[Funsumer]" + GCMfrom)
				.addData("msg", comment).build();
	}

	public void sendMessage() {
		// 일괄전송시에 사용
		try {
			gcmMultiResult = gcmSender.send(gcmMessage, registrationIds, RETRY);
			System.out.println("testV01" + gcmMessage);
			System.out.println("testV02" + registrationIds);
			System.out.println("textV03" + RETRY);
		} catch (IOException e) {
			Log.w(TAG, "IOException " + e.getMessage());
		}
		Log.w(TAG, "getCanonicalIds : " + gcmMultiResult.getCanonicalIds()
				+ "\n" + "getSuccess : " + gcmMultiResult.getSuccess() + "\n"
				+ "getTotal : " + gcmMultiResult.getTotal() + "\n"
				+ "getMulticastId : " + gcmMultiResult.getMulticastId());

	}
}