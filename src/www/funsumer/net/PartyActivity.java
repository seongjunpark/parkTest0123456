package www.funsumer.net;

import static www.funsumer.net.widget.CustomAdapter.authorid;
import static www.funsumer.net.widget.GridAdapter.getpartyid;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
import org.json.JSONException;
import org.json.JSONObject;

import www.funsumer.net.constants.FriendInfo;
import www.funsumer.net.constants.PartyInfo;
import www.funsumer.net.constants.TweetInfo;
import www.funsumer.net.login.SessionManager;
import www.funsumer.net.widget.CustomAdapter;
import www.funsumer.net.widget.FriendAdapter;
import www.funsumer.net.widget.GridAdapter;
import www.funsumer.net.widget.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PartyActivity extends Activity {

	private ViewPager mPager;

	SessionManager session;
	// public ListView m_lv = null;
	// LazyAdapter adapter;

	public ImageLoader imageloader;

	String mynoteid = MainActivity.mynoteid;
	String userid;
	String getuserid;
	static String partyid;
	// String authorid;

	JSONArray guflAPI = null;
	JSONArray graiAPI = null;
	String Result = null;
	JSONArray Result_data = null;
	JSONArray guplAPI = null;
	static JSONArray gpiAPI = null;

	private ArrayList<FriendInfo> mFriendList;
	private FriendAdapter f_adapter;
	private ArrayList<TweetInfo> mTweetList;
	private CustomAdapter mAdapter;
	private ArrayList<PartyInfo> mPartyList;
	private GridAdapter gridadapter;

	// @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.party_base);

		userid = NoteActivity.userid;
		// Intent intentget = getIntent();
		// userid = intentget.getStringExtra("userid");

		// partyid = getpartyid;
		Intent intentget = getIntent();
		partyid = intentget.getStringExtra("partyid");

		Log.e("party", "partyid = " + partyid);

		imageloader = new ImageLoader(this.getApplicationContext());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(new PagerAdapterClass(this));

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
				Intent intentput = new Intent(PartyActivity.this,
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

		setCurrentInflateItem(1);
	}

	public void setCurrentInflateItem(int type) {
		// type = 1;
		if (type == 0) {
			mPager.setCurrentItem(0);
		} else if (type == 1) {
			mPager.setCurrentItem(1);
		} else {
			mPager.setCurrentItem(2);
		}
	}

	private View.OnClickListener mPagerListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String text = ((Button) v).getText().toString();
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
					.show();
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
			return 2;
		}

		public float getPageWidth(int position) {
			if (position == 0 || position == 2) {
				return 0.85f;
			}
			return 1f;
		}

		public Object instantiateItem(View pager, int position) {
			View v = null;

			if (position == 0) {

				// getData(0);

				v = mInflater.inflate(R.layout.party_left, null);
				Button invite_fr = (Button) v.findViewById(R.id.invite_fr);
				invite_fr.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(PartyActivity.this,
								inviteFR.class);
						intent.putExtra("userid", userid);
						intent.putExtra("FROM_PARTY", "1");
						intent.putExtra("partyid", partyid);
						startActivity(intent);

					}
				});
				
				Button join_btn = (Button)v.findViewById(R.id.join_btn);
				join_btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						joinParty();
						
					}
				});


				// ListView m_lv1 = (ListView) v.findViewById(R.id.friend_list);
				// // m_lv1.addHeaderView(fr_header());
				//
				// mFriendList = new ArrayList<FriendInfo>();
				// f_adapter = new FriendAdapter(getApplicationContext(),
				// R.layout.friendlist_item, mFriendList);
				// m_lv1.setAdapter(f_adapter);
				//
				// setFriendListView();

			} else if (position == 1) {

				getData(1);

				if (Result.equals("0")) {
					v = mInflater.inflate(R.layout.party_main, null);

					ListView m_lv = (ListView) v
							.findViewById(R.id.partyplay_list);
					m_lv.addHeaderView(my_header03());

					mTweetList = new ArrayList<TweetInfo>();
					mAdapter = new CustomAdapter(PartyActivity.this,
							R.layout.item, mTweetList);
					m_lv.setAdapter(mAdapter);

					setListView();
				} else if (Result.equals("1")) {
					v = (View) mInflater.inflate(R.layout.inflate_one_none,
							null);
				}

				// if (Result.equals("0")) {

			} else if (position == 2) {

				// getData(2);

				v = mInflater.inflate(R.layout.party_right, null);

				// GridView gridview = (GridView) v
				// .findViewById(R.id.gridview);
				//
				// mPartyList = new ArrayList<PartyInfo>();
				// gridadapter = new GridAdapter(PartyActivity.this,
				// R.layout.partylist_grid, mPartyList);
				// gridview.setAdapter(gridadapter);

				// setPartyView();

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

	// GET DATA
	public void getData(int i) {

		JSONParser jParser = new JSONParser();
		if (i == 0) {

		} else if (i == 1) {
			if (userid == null) {
				userid = mynoteid;
			} else {
				userid = authorid;
			}
			// list url
			String url = "http://funsumer.net/json/?opt=5&afrom=2&mynoteid="
					+ mynoteid + "&partyid=" + partyid;
			Log.e("party", "partyplay url = " + url);
			try {
				JSONObject jsonurl = jParser.getJSONFromUrl(url);
				graiAPI = jsonurl.getJSONArray("graiAPI");
				JSONObject resultdata = graiAPI.getJSONObject(0);
				Result = resultdata.getString("Result");
				Result_data = resultdata.getJSONArray("Result_data");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			// header03 url
			String partyheadurl = "http://funsumer.net/json/?opt=7"
					+ "&mynoteid=" + mynoteid + "&userid=" + userid
					+ "&partyid=" + partyid;

			Log.e("party", "partyhead = " + partyheadurl);

			try {
				JSONObject json = jParser.getJSONFromUrl(partyheadurl);
				gpiAPI = json.getJSONArray("gpiAPI");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("party", "gpiAPI = " + gpiAPI);

		} else if (i == 2) {
			partyid = getpartyid;
			if (partyid == null) {
				// userid = mynoteid;

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

			}
		}
	}

	public void setListView() {
		mTweetList.clear();

		Log.e("party", "result = " + Result_data);
		try {
			int arrayLength = Result_data.length();

			for (int i = 0; i < arrayLength; i++) {
				JSONObject object = Result_data.getJSONObject(i);
				TweetInfo tweet = new TweetInfo();

				tweet.setAuthor(object.getString("Author"));
				tweet.setArticleFrom(object.getString("ArticleFrom"));
				tweet.setArtime(object.getString("ArTime")
						.replaceAll("2013-04-", "Apr.")
						.replaceAll("2013-05-", "May.")
						.replaceAll("2013-06-", "Jun."));
				tweet.setArticle(object.getString("ArInfo").replaceAll("<br>",
						"\n"));
				tweet.setArticle_Like_Num(object.getString("Article_Like_Num"));
				tweet.setArticle_Comment_Num(object
						.getString("Article_Comment_Num"));

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

	public void setFriendListView() {
		mFriendList.clear();

		try {
			int arrayLength = Result_data.length();

			for (int i = 0; i < arrayLength; i++) {
				JSONObject object = Result_data.getJSONObject(i);
				FriendInfo tweet = new FriendInfo();

				tweet.setFname(object.getString("Fname"));

				tweet.setFpic(object.getString("Fpic"));

				tweet.setFid(object.getString("Fid"));

				mFriendList.add(tweet);
			}

			f_adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setPartyView() {
		mPartyList.clear();

		try {
			int arrayLength = Result_data.length();

			for (int i = 0; i < arrayLength; i++) {
				JSONObject object = Result_data.getJSONObject(i);
				PartyInfo par_ls = new PartyInfo();

				par_ls.setBDPNAME(object.getString("Pname"));

				par_ls.setPAR_IMAGES(object.getString("Ppic"));

				par_ls.setPid(object.getString("Pid"));

				mPartyList.add(par_ls);
			}

			gridadapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

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

	public Bitmap getRoundedCornerImage(Bitmap bitmap) {
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
	// ImageButton openBtn01;
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

	// PARTY HEADER 03
	public LinearLayout my_header03 = null;

	private String partypageurl = "http://funsumer.net/json/?opt=7";
	private final String TAG_GPIAPI = "gpiAPI";
	// private static final String TAG_RESULT = "Result";
	private final String TAG_PARTYPIC = "PartyPic";
	private final String TAG_PARTYSSIERID = "PartySSierID";
	private final String TAG_PARTYSSIERPIC = "PartySSierPic";
	private final String TAG_PARTYNAME = "PartyName";
	private final String TAG_PARTYSSIERNAME = "PartySSierName";

	public LinearLayout m_header03 = null;
	private LayoutInflater minflater;

	public LinearLayout my_header03() {
		minflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_header03 = (LinearLayout) minflater.inflate(R.layout.my_header03,
				null);

		head_data();

		Button btn_go_partyleft = (Button) m_header03
				.findViewById(R.id.btn_go_partyleft);
		btn_go_partyleft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setCurrentInflateItem(0);
			}
		});

		Button writePT = (Button) m_header03.findViewById(R.id.writePT);
		writePT.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intentput = new Intent(PartyActivity.this,
						WriteNote.class);
				intentput.putExtra("position", "2");
				intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intentput);
			}
		});

		return m_header03;
	}

	String ResultinOPT7 = null;
	String PartyPic = null;
	String PartySSierPic = null;
	String PartyName = null;
	String PartySSierName = null;

	public void head_data() {

		try {
			JSONObject wide = gpiAPI.getJSONObject(0);
			ResultinOPT7 = wide.getString("Result");

			PartyPic = "http://www.funsumer.net/" + wide.getString("PartyPic");
			PartySSierPic = "http://www.funsumer.net/"
					+ wide.getString("PartySSierPic");

			PartyName = wide.getString("PartyName");
			PartySSierName = wide.getString("PartySSierName");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		TextView partyname = (TextView) m_header03.findViewById(R.id.partyname);
		partyname.setText(PartyName);

		TextView partyssiername = (TextView) m_header03
				.findViewById(R.id.partyssiername);
		partyssiername.setText(PartySSierName);

		ImageView partypic = (ImageView) m_header03.findViewById(R.id.partypic);
		imageloader.DisplayImage(PartyPic, partypic);

		ImageView partyssierpic = (ImageView) m_header03
				.findViewById(R.id.partyssierpic);
		imageloader.DisplayImage(PartySSierPic, partyssierpic);

		// try {
		// partypic.setImageBitmap((Bitmap) getURLImage(new URL(PartyPic)));
		//
		// Bitmap bitmap = (Bitmap) getURLImage(new URL(PartySSierPic));
		// partyssierpic.setImageBitmap(getRoundedCornerImage(bitmap));
		//
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }
	}
	public void joinParty() {

		try {

			HttpClient client = new DefaultHttpClient();
			String postURL = "http://funsumer.net/json/";
			HttpPost post = new HttpPost(postURL);
			
			List params1 = new ArrayList();
			params1.add(new BasicNameValuePair("oopt", "12"));
			params1.add(new BasicNameValuePair("pid", partyid));
			params1.add(new BasicNameValuePair("mynoteid", mynoteid));

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1,
					HTTP.UTF_8);
			post.setEntity(ent);

			HttpResponse responsePOST = client.execute(post);

			HttpEntity resEntity = responsePOST.getEntity();

		} catch (Exception e) {

			// TODO: handle exception
		}

	}

}
