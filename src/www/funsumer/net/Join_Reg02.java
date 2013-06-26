package www.funsumer.net;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import www.funsumer.net.login.AlertDialogManager;
import www.funsumer.net.login.KakaoLink;
import www.funsumer.net.login.ListArrayItem;
import www.funsumer.net.login.SessionManager;
import www.funsumer.net.widget.gsHttpConnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Copyright 2012 Kakao Crop. All rights reserved.
 * 
 * @author kakaolink@kakao.com
 */
public class Join_Reg02 extends Activity {
	String dial1 = "";
	private List<ListArrayItem> items = new ArrayList<ListArrayItem>();
	private static String loginurl = "http://funsumer.net/json/?opt=1";
	String url = "";
	ProgressDialog dialog;
	// Recommened Charset UTF-8
	private String encoding = "UTF-8";
	private String username, password;
	JSONArray LoginAPI = null;
	SessionManager session;
	AlertDialogManager alert = new AlertDialogManager();
	static Cursor c;
	ContentResolver cr;
	int idd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.api_kakao);

		final JSONParser jParser = new JSONParser();

		Intent intent = getIntent();
		username = intent.getStringExtra("email");
		password = intent.getStringExtra("password");

		session = new SessionManager(getApplicationContext());

		Button bt_nexter = (Button) findViewById(R.id.bt_nexter);

		bt_nexter.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				idd = 0;
				// 肄섑뀗���꾨줈諛붿씠�붿� 而ㅼ꽌瑜�由ы꽩諛�뒗��(二쇱냼 �뺣낫��痍⑤뱷)---------�ш린遺�꽣
				// sqlite��醫�
				// �대젮�곗떎�섎룄
				cr = getContentResolver();
				c = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
				gsHttpConnect com = new gsHttpConnect();
				GetContact contact = new GetContact();
				contact.execute(c);

				url = loginurl + "&LoginID=" + username + "&LoginPass="
						+ password;
				try {
					JSONObject json = jParser.getJSONFromUrl(url);

					LoginAPI = json.getJSONArray("LoginAPI");

					JSONObject login_obj = LoginAPI.getJSONObject(0);

					String Result = login_obj.getString("Result");

					if (Result.equals("0")) {
						String Result_data = login_obj.getString("Result_data");
						alert.showAlertDialog(Join_Reg02.this,
								"Welcome to FUNSUMER!! ", "Success", false);

						session.createLoginSession(Result_data);

						Intent i = new Intent(getApplicationContext(),
								MainActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(i);

						finish();

					} else {
						alert.showAlertDialog(Join_Reg02.this,
								"Data Submit Failed", "", false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent intent1 = new Intent(Join_Reg02.this, MainActivity.class);
				startActivity(intent1);
			}
		});

	}

	public void readContacts() {
		gsHttpConnect com = new gsHttpConnect();
		while (c.moveToNext()) {
			// 그룹
			int group = c
					.getInt(c
							.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP));
			if (group != 1)
				continue;

			// ID
			String id = c.getString(c
					.getColumnIndex(ContactsContract.Contacts._ID));

			// 표지명
			String Name = c.getString(c
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

			// 전화

			if (Integer
					.parseInt(c.getString(c
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				Cursor cp = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = ? ", new String[] { id }, null);
				while (cp.moveToNext()) {
					dial1 = cp
							.getString(cp
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1));
					if(dial1.length()>11){
						dial1 = dial1.substring(3, 12);
						dial1 = "0"+dial1;
						
					}

				}
				cp.close();
			}
			idd = idd + 1;
			// 추가
			items.add(new ListArrayItem(Name, dial1, idd));

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("a", dial1);
			params.put("b", Name);
			try {

				String rec = com.uploadAndRequest(new URL(
						"http://funsumer.net/test2.php"), params);
				Log.d("=====", rec);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public class GetContact extends AsyncTask<Cursor, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Cursor... params) {

			// TODO Auto-generated method stub
			System.out.println("value of param==========" + params.length);
			readContacts();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	/**
	 * Send URL
	 * 
	 * @throws NameNotFoundException
	 */
	public void sendUrlLink(View v) throws NameNotFoundException {
		// Recommended: Use application context for parameter.
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent())
			return;

		/**
		 * @param activity
		 * @param url
		 * @param message
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 */
		kakaoLink
				.openKakaoLink(
						this,
						"http://link.kakao.com/?test-android-app",
						"First KakaoLink Message for send url.",
						getPackageName(),
						getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
						"KakaoLink Test App", encoding);
	}

	/**
	 * Send App data
	 */
	public void sendAppData(View v) throws NameNotFoundException {
		ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();

		// If application is support Android platform.
		Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
		metaInfoAndroid.put("os", "android");
		metaInfoAndroid.put("devicetype", "phone");
		metaInfoAndroid.put("installurl", "market://details?id=com.kakao.talk");
		metaInfoAndroid.put("executeurl", "kakaoLinkTest://starActivity");

		// add to array
		metaInfoArray.add(metaInfoAndroid);

		// Recommended: Use application context for parameter.
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());

		// check, intent is available.
		if (!kakaoLink.isAvailableIntent())
			return;

		/**
		 * @param activity
		 * @param url
		 * @param message
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 * @param metaInfoArray
		 */
		kakaoLink
				.openKakaoAppLink(
						this,
						"http://link.kakao.com/?test-android-app",
						"�뱀떊��媛쒕뀗李�sns!!",
						getPackageName(),
						getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
						"wowBOW", encoding, metaInfoArray);
	}

}
