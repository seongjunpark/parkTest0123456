package www.funsumer.net;

import static www.funsumer.net.widget.CustomAdapter.authorid;
import static www.funsumer.net.widget.GridAdapter.getpartyid;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import www.funsumer.net.Join_Reg02.GetContact;
import www.funsumer.net.constants.PartyInfo;
import www.funsumer.net.constants.TweetInfo;
import www.funsumer.net.login.ListArrayItem;
import www.funsumer.net.login.SessionManager;
import www.funsumer.net.widget.CustomAdapter;
import www.funsumer.net.widget.GridAdapter;
import www.funsumer.net.widget.ImageLoader;
import www.funsumer.net.widget.gsHttpConnect;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static ViewPager mPager;

	SessionManager session;
	// public ListView m_lv = null;
	// LazyAdapter adapter;

	public ImageLoader imageLoader;

	static JSONArray graiAPI = null;
	static String Result = null;
	static JSONArray Result_data = null;
	static JSONArray guplAPI = null;

	public static String mynoteid;
	static String userid;
	static String partyid;
	private Cursor c;
	ContentResolver cr;
	int idd;
	String dial1 = "";
	private List<ListArrayItem> items = new ArrayList<ListArrayItem>();
	// String authorid;

	private static ArrayList<TweetInfo> mTweetList;
	private static CustomAdapter mAdapter;
	private static ArrayList<PartyInfo> mPartyList;
	private static GridAdapter gridadapter;
	
	private static final String SENDER_ID = "147463031498";
	private static final String INSERT_PAGE = "http://funsumer.net/GCM/insert_registration.php";
	private GCMHttpConnect httpConnect = null;
	private GCMHttpConnect.Request httpRequest = new GCMHttpConnect.Request() {

		@Override
		public void OnComplete() {
			// TODO Auto-generated method stub
		}
	};

	@SuppressLint({ "NewApi", "NewApi", "NewApi" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		HashMap<String, String> user = session.getUserDetails();
		mynoteid = user.get(SessionManager.KEY_NAME);

		if (mynoteid == null) {
			startActivity(new Intent(MainActivity.this, LoginActivity.class));
		} else {
			ALL myApp = (ALL) getApplicationContext();
			int state = myApp.getState();

			final String regID = GCMRegistrar.getRegistrationId(this);
			if (state == 0) {
				GCMRegistrar.register(this, SENDER_ID);
				insertRegistrationID(regID);
				idd = 0;
				cr = getContentResolver();
				c = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
				//send phone number!!
				//gsHttpConnect com = new gsHttpConnect();
				//GetContact contact = new GetContact();
				//contact.execute(c);
				myApp.setState(5);

			}

			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(new PagerAdapterClass(this));

//			setLayout();
			
			Button btn_two = (Button) findViewById(R.id.btn_two);
			btn_two.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intentput = new Intent(MainActivity.this,
							NoteActivity.class);
					intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intentput);
				}
			});

			Button btn_friendlist = (Button) findViewById(R.id.btn_friendlist);
			btn_friendlist.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intentput = new Intent(getApplicationContext(),
							Friendlist_Activity.class);
					intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intentput);
				}
			});

			Button btn_partylist = (Button) findViewById(R.id.btn_partylist);
			btn_partylist.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intentput = new Intent(MainActivity.this,
							PartylistActivity.class);
					intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intentput.putExtra("mynoteid", mynoteid);
					intentput.putExtra("userid", mynoteid);
					startActivity(intentput);
				}
			});

			Button btnLogout = (Button) findViewById(R.id.btn_logout);
			btnLogout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					session.logoutUser();
				}
			});

			Animation();
		}

	}

	public static void setCurrentInflateItem(int type) {
		if (type == 0) {
			mPager.setCurrentItem(0);
		} else if (type == 1) {
			mPager.setCurrentItem(1);
		} else {
			mPager.setCurrentItem(2);
		}
	}
	private class GetContact extends AsyncTask<Cursor, Void, Void> {

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

//	private Button btn_one;
//	private Button btn_two;
//	private Button btn_three;
//	
//	private void setLayout() {
//		btn_one = (Button) findViewById(R.id.btn_one);
//		btn_two = (Button) findViewById(R.id.btn_two);
//		btn_three = (Button) findViewById(R.id.btn_three);
//
//		btn_one.setOnClickListener(this);
//		btn_two.setOnClickListener(this);
//		btn_three.setOnClickListener(this);
//	}
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

	private View.OnClickListener mPagerListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String text = ((Button) v).getText().toString();
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
		}
	};

	public class PagerAdapterClass extends PagerAdapter {
		private LayoutInflater mInflater;

		public PagerAdapterClass(Context c) {
			super();
			mInflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return 1;
		}

		public Object instantiateItem(View pager, int position) {
			View v = null;

			if (position == 0) {

				getData(0);

				if (Result.equals("0")) {
					v = (View) mInflater.inflate(R.layout.inflate_one, null);

					ListView m_lv = (ListView) v.findViewById(R.id.list1);

					mTweetList = new ArrayList<TweetInfo>();
					mAdapter = new CustomAdapter(MainActivity.this,
							R.layout.item, mTweetList);
					m_lv.setAdapter(mAdapter);

					setListView();
					
					
				} else if (Result.equals("1")) {
					v = (View) mInflater.inflate(R.layout.inflate_one_none,
							null);
					
				}
			} else if (position == 1) {

//				getData(1);
//
//				if (Result.equals("0")) {
//					v = mInflater.inflate(R.layout.inflate_two, null);
//
//					ListView m_lv = (ListView) v.findViewById(R.id.list2);
//					m_lv.addHeaderView(my_header02());
//					mTweetList = new ArrayList<TweetInfo>();
//					mAdapter = new CustomAdapter(MainActivity.this,
//							R.layout.item, mTweetList);
//					m_lv.setAdapter(mAdapter);
//					setListView();
//

//				} else if (Result.equals("1")) {
//					v = (View) mInflater.inflate(R.layout.inflate_one_none,
//							null);
//				}

				// v.findViewById(R.id.iv_two);
				// v.findViewById(R.id.btn_click_2).setOnClickListener(mPagerListener);
			}

			((ViewPager) pager).addView(v, 0);

			return v;
		}

		@Override
		public void destroyItem(View pager, int position, Object view) {
			((ViewPager) pager).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			return pager == obj;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	public static void getData(int i) {

		JSONParser jParser = new JSONParser();
		if (i == 0) {
			String url = "http://funsumer.net/json/?opt=5&afrom=0&mynoteid="
					+ mynoteid;

			try {
				JSONObject jsonurl = jParser.getJSONFromUrl(url);
				graiAPI = jsonurl.getJSONArray("graiAPI");
				JSONObject resultdata = graiAPI.getJSONObject(0);
				Result = resultdata.getString("Result");
				Result_data = resultdata.getJSONArray("Result_data");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (i == 1) {
			if (userid == null) {
				userid = mynoteid;
			} else {
				userid = authorid;
			}
			String url = "http://funsumer.net/json/?opt=5&afrom=1&mynoteid="
					+ mynoteid + "&userid=" + userid;
			try {
				JSONObject jsonurl = jParser.getJSONFromUrl(url);
				graiAPI = jsonurl.getJSONArray("graiAPI");
				JSONObject resultdata = graiAPI.getJSONObject(0);
				Result_data = resultdata.getJSONArray("Result_data");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (i == 2) {
			partyid = getpartyid;
			if (partyid == null) {

				userid = mynoteid;

				String url = "http://funsumer.net/json/?opt=4&mynoteid="
						+ mynoteid + "&userid=" + userid;
				try {
					JSONObject jsonurl = jParser.getJSONFromUrl(url);
					guplAPI = jsonurl.getJSONArray("guplAPI");
					JSONObject resultdata = guplAPI.getJSONObject(0);
					Result = resultdata.getString("Result");
					Result_data = resultdata.getJSONArray("Result_data");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				// partyid = getpartyid;
				
				String url = "http://funsumer.net/json/?opt=5&afrom=2&mynoteid="
						+ mynoteid + "&partyid=" + partyid;

				try {
					JSONObject jsonurl = jParser.getJSONFromUrl(url);
					graiAPI = jsonurl.getJSONArray("graiAPI");
					JSONObject resultdata = graiAPI.getJSONObject(0);
					Result_data = resultdata.getJSONArray("Result_data");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void setListView() {
		mTweetList.clear();

		try {
			int arrayLength = Result_data.length();

			for (int i = 0; i < arrayLength; i++) {
				JSONObject object = Result_data.getJSONObject(i);
				TweetInfo tweet = new TweetInfo();

				tweet.setAuthor(object.getString("Author"));
				tweet.setArticleFrom(object.getString("ArticleFrom"));
				tweet.setArtime(object.getString("ArTime").replaceAll(
						"2013-04-", "Apr.").replaceAll("2013-05-", "May.").replaceAll("2013-06-", "Jun."));
				tweet.setArticle(object.getString("ArInfo").replaceAll("<br>",
						"\n"));
				tweet.setArticle_Like_Num(object.getString("Article_Like_Num"));
				tweet.setArticle_Comment_Num(object.getString("Article_Comment_Num"));

				tweet.setAuthorpic(object.getString("AuthorPic"));
				tweet.setArPic(object.getString("ArPic"));

				tweet.setAuthorid(object.getString("AuthorID"));
				tweet.setArticleid(object.getString("ArticleID"));

				tweet.setBelong(object.getString("Belong"));
				tweet.setIsparty(object.getString("Isparty"));

				mTweetList.add(tweet);
			}

			mAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.btn_one:
//			setCurrentInflateItem(0);
//			break;
//		case R.id.btn_two:
//			setCurrentInflateItem(1);
//			break;
//		case R.id.btn_three:
//			setCurrentInflateItem(2);
//			break;
//		}
//	}

	Bitmap getURLImage(URL url) {
		URLConnection conn = null;
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			conn.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(conn.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bm = BitmapFactory.decodeStream(bis);
		try {
			bis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bm;
	}

	public static Bitmap getRoundedCornerImage(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 50;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	LinearLayout slidingPage01;
	boolean isPageOpen = false;
//	ImageButton openBtn01;
	ImageView logomenu;
	Animation translateLeftAnim;
	Animation translateRightAnim;

	private void Animation() {
		slidingPage01 = (LinearLayout) findViewById(R.id.slidingPage01);

		translateLeftAnim = AnimationUtils.loadAnimation(this,
				R.anim.translate_left);
		translateRightAnim = AnimationUtils.loadAnimation(this,
				R.anim.translate_right);
		SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
		translateLeftAnim.setAnimationListener(animListener);
		translateRightAnim.setAnimationListener(animListener);

		logomenu = (ImageView) findViewById(R.id.logomenu);
		logomenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// start animation
				if (isPageOpen) {
					slidingPage01.startAnimation(translateLeftAnim);
				} else {
					slidingPage01.setVisibility(View.VISIBLE);
					slidingPage01.startAnimation(translateRightAnim);
				}
			}
		});
	}

	private class SlidingPageAnimationListener implements AnimationListener {
		public void onAnimationEnd(Animation animation) {
			if (isPageOpen) {
				slidingPage01.setVisibility(View.INVISIBLE);
				logomenu.setTag("Open");
				isPageOpen = false;
			} else {
				logomenu.setTag("Close");
				isPageOpen = true;
			}
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public void onAnimationStart(Animation animation) {
		}
	}
	public void insertRegistrationID(String id) {
		httpConnect = new GCMHttpConnect(INSERT_PAGE + "?regID=" + id
				+ "&mynoteid=" + mynoteid, httpRequest);
		httpConnect.start();
	}

}
