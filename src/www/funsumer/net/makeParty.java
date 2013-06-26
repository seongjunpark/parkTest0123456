package www.funsumer.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class makeParty extends Activity {

	EditText writeName;
	private String mynoteid;
	static InputStream is = null;
	static String json = "";
	String userid, array;
	int num;
	String writeName_re;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.makeparty);

		num = inviteFR.count;
		Intent intent = getIntent();
		userid = intent.getStringExtra("userid");
		array = intent.getStringExtra("array");
		writeName_re = intent.getStringExtra("writeName_re");

		mynoteid = MainActivity.mynoteid;

		writeName = (EditText) findViewById(R.id.writeName);
		if (writeName_re != null) {

			writeName.setText(writeName_re);

		}

		InputFilter[] filters = new InputFilter[] { new ByteLengthFilter(16,
				"KSC5601") };
		writeName.setFilters(filters);

		Button btn_friend = (Button) findViewById(R.id.btn_friend);
		btn_friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent1 = new Intent(makeParty.this, inviteFR.class);
				intent1.putExtra("userid", userid);
				intent1.putExtra("writeName", writeName.getText().toString());
				intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);

			}
		});

		Button makeparty = (Button) findViewById(R.id.btn_make);
		makeparty.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onlyText();

				Intent intentput = new Intent(makeParty.this, PartyActivity.class);
				intentput.putExtra("partyid", regId);
				intentput.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intentput);
				Log.e("party", "partyid = " + regId);
			}
		});
	}

	class ByteLengthFilter implements InputFilter {

		private String mCharset;
		protected int mMaxByte;

		public ByteLengthFilter(int maxbyte, String charset) {
			this.mMaxByte = maxbyte;
			this.mCharset = charset;
		}

		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			String expected = new String();
			expected += dest.subSequence(0, dstart);
			expected += source.subSequence(start, end);
			expected += dest.subSequence(dend, dest.length());
			int keep = calculateMaxLength(expected)
					- (dest.length() - (dend - dstart));
			if (keep < 0) {
				keep = 0;
			}
			int Rekeep = plusMaxLength(dest.toString(), source.toString(),
					start);

			if (keep <= 0 && Rekeep <= 0) {
				return "";

			} else if (keep >= end - start) {
				return null;
			} else {
				if (dest.length() == 0 && Rekeep <= 0) {
					return source.subSequence(start, start + keep);
				} else if (Rekeep <= 0) {
					return source.subSequence(start, start
							+ (source.length() - 1));
				} else {
					return source.subSequence(start, start + Rekeep); // source중
																		// 일부만입력
																		// 허용
				}
			}
		}

		protected int plusMaxLength(String expected, String source, int start) {
			int keep = source.length();
			int maxByte = mMaxByte - getByteLength(expected.toString()); // 입력가능한
																			// byte

			while (getByteLength(source.subSequence(start, start + keep)
					.toString()) > maxByte) {
				keep--;
			}
			;
			return keep;
		}

		protected int calculateMaxLength(String expected) {
			int expectedByte = getByteLength(expected);
			if (expectedByte == 0) {
				return 0;
			}
			return mMaxByte - (getByteLength(expected) - expected.length());
		}

		private int getByteLength(String str) {
			try {
				return str.getBytes(mCharset).length;
			} catch (UnsupportedEncodingException e) {

			}
			return 0;
		}
	}
	String regId;

	public void onlyText() {

		if (writeName.getText().toString().equals("")) {

			new CustomDialog(makeParty.this, CustomDialog.TYPE_BASIC_OK, null,
					"insert partyName.").show();
			return;

		} else if (writeName.getText().toString().length() > 16) {
			new CustomDialog(makeParty.this, CustomDialog.TYPE_BASIC_OK, null,
					"your party name is so long").show();
			return;
		}

		try {

			HttpClient client = new DefaultHttpClient();
			String postURL = "http://funsumer.net/json/";
			HttpPost post = new HttpPost(postURL);

			List params1 = new ArrayList();
			params1.add(new BasicNameValuePair("oopt", "6"));
			params1.add(new BasicNameValuePair("mynoteid", mynoteid));
			params1.add(new BasicNameValuePair("array", array));
			params1.add(new BasicNameValuePair("num", String.valueOf(num)));
			params1.add(new BasicNameValuePair("newname", writeName.getText()
					.toString()));

			System.out.println("iahiashefa03" + "//" + params1);

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
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
			if (resEntity != null) {
				JSONArray obj = new JSONArray(json);

				for (int i = 0; i < obj.length(); i++) {

					JSONObject tjo = obj.getJSONObject(i);
					regId = tjo.getString("pid");
				}
			}

		} catch (Exception e) {

			// TODO: handle exception
		}

	}

}
