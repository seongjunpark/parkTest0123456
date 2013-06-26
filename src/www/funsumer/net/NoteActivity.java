package www.funsumer.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NoteActivity extends Activity {

	private ViewPager mPager;

	SessionManager session;
	// public ListView m_lv = null;
	// LazyAdapter adapter;

	public ImageLoader imageLoader;

	JSONArray guflAPI = null;

	JSONArray graiAPI = null;
	String Result = null;
	JSONArray Result_data = null;
	JSONArray guplAPI = null;
	static JSONArray guiAPI = null;

	static String mynoteid = MainActivity.mynoteid;
	static String userid;
	String getuserid;
	// String authorid;

	private ArrayList<FriendInfo> mFriendList;
	private FriendAdapter f_adapter;
	private static ArrayList<TweetInfo> mTweetList;
	private static CustomAdapter mAdapter;
	private ArrayList<PartyInfo> mPartyList;
	private GridAdapter gridadapter;

	static ImageLoader imageloader;

	// @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_base);

		Intent intentget = getIntent();
		getuserid = intentget.getStringExtra("userid");

		// userid = authorid;
		userid = getuserid;

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
				Intent intentput = new Intent(NoteActivity.this,
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
			return 3;
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

				getData(0);

				v = mInflater.inflate(R.layout.note_left, null);

				ListView m_lv1 = (ListView) v.findViewById(R.id.friend_list);
				mFriendList = new ArrayList<FriendInfo>();
				f_adapter = new FriendAdapter(getApplicationContext(),
						R.layout.friendlist_item, mFriendList);
				m_lv1.setAdapter(f_adapter);

				setFriendListView();

			} else if (position == 1) {

				getData(1);

				v = mInflater.inflate(R.layout.inflate_two, null);

				ListView m_lv = (ListView) v.findViewById(R.id.list2);
				m_lv.addHeaderView(my_header02());

				mTweetList = new ArrayList<TweetInfo>();
				mAdapter = new CustomAdapter(NoteActivity.this, R.layout.item,
						mTweetList);
				m_lv.setAdapter(mAdapter);

				setListView();

			} else if (position == 2) {

				getData(2);

				v = mInflater.inflate(R.layout.partylist, null);

				GridView gridview = (GridView) v.findViewById(R.id.gridview);

				mPartyList = new ArrayList<PartyInfo>();
				gridadapter = new GridAdapter(NoteActivity.this,
						R.layout.partylist_grid, mPartyList);
				gridview.setAdapter(gridadapter);

				setPartyView();

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
			// guflAPI
			String url = "http://funsumer.net/json?opt=3&mynoteid=" + mynoteid
					+ "&userid=" + userid;

			Log.e("note", "f url = " + url);

			try {
				JSONObject jsonurl = jParser.getJSONFromUrl(url);
				guflAPI = jsonurl.getJSONArray("guflAPI");
				JSONObject resultdata = guflAPI.getJSONObject(0);
				Result = resultdata.getString("Result");
				Result_data = resultdata.getJSONArray("Result_data");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (i == 1) {
			if (userid == null) {
				userid = mynoteid;
			} else {
				userid = getuserid;
			}

			// list2 url
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

			// header02 url
			String noteheaderurl = "http://funsumer.net/json/?opt=2"
					+ "&mynoteid=" + mynoteid + "&userid=" + userid;

			try {
				JSONObject json = jParser.getJSONFromUrl(noteheaderurl);
				guiAPI = json.getJSONArray("guiAPI");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (i == 2) {

			String url = "http://funsumer.net/json/?opt=4&mynoteid=" + mynoteid
					+ "&userid=" + userid;

			Log.e("note", "gridurl = " + url);

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

	public void setListView() {
		mTweetList.clear();

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

	public static RelativeLayout m_header02 = null;
	private LayoutInflater mInflater;

	// NOTE HEADER
	public RelativeLayout my_header02() {
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_header02 = (RelativeLayout) mInflater.inflate(R.layout.my_header02,
				null);

		head_data();

		Button btn_go_noteleft = (Button) m_header02
				.findViewById(R.id.btn_go_noteleft);
		btn_go_noteleft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setCurrentInflateItem(0);
			}
		});
		Button btn_makearticle = (Button) m_header02
				.findViewById(R.id.btn_makearticle);
		btn_makearticle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent1 = new Intent(NoteActivity.this, WriteNote.class);
				intent1.putExtra("position", "1");
				startActivity(intent1);

			}
		});

		Button btn_go_noteright = (Button) m_header02
				.findViewById(R.id.btn_go_noteright);
		btn_go_noteright.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setCurrentInflateItem(2);
			}
		});

		return m_header02;
	}

	// HEAD_DATA
	static String ResultinOPT2 = null;
	static String Result_WidePic = null;
	static String Result_ProfilePic = null;
	static String Result_Name = null;
	static String Result_Fnum = null;
	static String Result_Vnum = null;
	static String Result_Vote = null;

	static TextView friendnumber;
	static TextView votenumber;
	static ImageView votenumber_pic;

	static ImageView novote;
	static ImageView yesvote;

	public void head_data() {

		try {
			JSONObject wide = guiAPI.getJSONObject(0);
			ResultinOPT2 = wide.getString("Result");

			Result_WidePic = "http://www.funsumer.net/"
					+ wide.getString("Result_WidePic");
			Result_ProfilePic = "http://www.funsumer.net/"
					+ wide.getString("Result_ProfilePic");

			Result_Name = wide.getString("Result_Name");
			Result_Fnum = wide.getString("Result_Fnum");
			Result_Vnum = wide.getString("Result_Vnum");
			Result_Vote = wide.getString("Result_Vote");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		TextView result_Name = (TextView) m_header02
				.findViewById(R.id.result_Name);
		result_Name.setText(Result_Name);

		friendnumber = (TextView) m_header02.findViewById(R.id.Result_Fnum);
		friendnumber.setText(Result_Fnum);

		votenumber = (TextView) m_header02.findViewById(R.id.Result_Vnum);
		votenumber.setText(Result_Vnum);

		ImageView notepic = (ImageView) m_header02.findViewById(R.id.notepic);
		imageloader.DisplayImage(Result_WidePic, notepic);

		ImageView profilepic = (ImageView) m_header02
				.findViewById(R.id.profilepic);
		imageloader.DisplayImage(Result_ProfilePic, (profilepic));

		votenumber_pic = (ImageView) m_header02.findViewById(R.id.countvote);

		// try {
		// notepic.setImageBitmap((Bitmap) getURLImage(new
		// URL(Result_WidePic)));
		//
		// Bitmap bitmap = (Bitmap) getURLImage(new URL(Result_ProfilePic));
		// profilepic.setImageBitmap(getRoundedCornerImage(bitmap));
		//
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }

		// VOTE ACTION
		novote = (ImageView) m_header02.findViewById(R.id.novote);
		yesvote = (ImageView) m_header02.findViewById(R.id.yesvote);
		Log.e("note", "here is HEADdata.. mynoteid = " + mynoteid
				+ "  userid = " + userid);
		if (mynoteid.equals(userid)) {
			Log.e("note", "��������" + mynoteid + userid);
			novote.setImageResource(R.drawable.stub_image);
			defaultImage();
			votenumber_pic.setVisibility(View.VISIBLE);
			votenumber.setVisibility(View.VISIBLE);
			/*
			 * novote.setOnClickListener(new View.OnClickListener() {
			 * 
			 * @Override public void onClick(View arg0) { Intent intentput = new
			 * Intent(ALL.getAppContext(), Friendlist_Activity.class);
			 * intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * intentput.putExtra("mynoteid", mynoteid);
			 * ALL.getAppContext().startActivity(intentput); } });
			 */
		} else {
			Log.e("note", "�ٸ�����" + mynoteid + userid);
			// novote = (ImageView) m_header02.findViewById(R.id.novote);
			// yesvote = (ImageView) m_header02.findViewById(R.id.yesvote);
			if (Result_Vote.equals("1")) {
				Log.e("note", "111 Result_vote is = " + Result_Vote);
				votenumber_pic.setVisibility(View.VISIBLE);
				votenumber.setVisibility(View.VISIBLE);
				novote.setImageResource(R.drawable.vote_after);
				novote.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						/*
						 * new CustomDialog(ALL.getAppContext(),
						 * CommentDialog.TYPE_BASIC_OK, null,
						 * "�̹� ��ǥ�Ͽ����ϴ�.").show();
						 */
						Toast.makeText(ALL.getAppContext(), "�̹� ��ǥ�Ͽ����ϴ�.",
								Toast.LENGTH_LONG).show();
					}
				});
			} else if (Result_Vote.equals("0")) {
				Log.e("note", "000 Result_vote is = " + Result_Vote);
				votenumber_pic.setVisibility(View.INVISIBLE);
				votenumber.setVisibility(View.INVISIBLE);
				novote.setVisibility(View.VISIBLE);
				yesvote.setVisibility(View.INVISIBLE);
				novote.setImageResource(R.drawable.vote_before);
				novote.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							HttpClient client = new DefaultHttpClient();
							String postURL = "http://funsumer.net/json/";
							HttpPost post = new HttpPost(postURL);

							List params1 = new ArrayList();
							params1.add(new BasicNameValuePair("oopt", "4"));
							params1.add(new BasicNameValuePair("mynoteid",
									mynoteid));
							params1.add(new BasicNameValuePair("userid", userid));

							UrlEncodedFormEntity ent = new UrlEncodedFormEntity(
									params1, HTTP.UTF_8);
							post.setEntity(ent);
							HttpResponse responsePOST = client.execute(post);
							// HttpEntity resEntity = responsePOST.getEntity();

							// if (resEntity != null) {
							// JSONArray obj = new JSONArray(EntityUtils
							// .toString(resEntity));
							//
							// for (int i = 0; i < obj.length(); i++) {
							// JSONObject tjo = obj.getJSONObject(i);
							// String regId = tjo.getString("ID");
							// registrationIds.add(0, regId);
							// // setMessage();
							// // sendMessage();
							// }
							// }
							Log.e("note", "��ǥ ��");

							changeImage();
							votenumber_pic.setVisibility(View.VISIBLE);
							votenumber.setVisibility(View.VISIBLE);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
				yesvote.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// new CustomDialog(ALL.getAppContext(),
						// CommentDialog.TYPE_BASIC_OK, null,
						// "�̹� ��ǥ�Ͽ����ϴ�.").show();
						Toast.makeText(ALL.getAppContext(), "�̹� ��ǥ�Ͽ����ϴ�.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}

	}

	public static void changeImage() {
		novote.setVisibility(View.INVISIBLE);
		yesvote.setVisibility(View.VISIBLE);
		yesvote.setImageResource(R.drawable.vote_after);
	}

	public static void defaultImage() {
		novote.setVisibility(View.VISIBLE);
		yesvote.setVisibility(View.INVISIBLE);
	}

	public final static OnClickListener deleteArticle = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();

			if (mTweetList == null || mTweetList.equals("")) {
				/*
				 * new CustomDialog(NoteActivity.this,
				 * CustomDialog.TYPE_BASIC_OK, null,
				 * "can't delete this article") .show(); return;
				 */
			} else {

				if (mynoteid == userid) {

					TweetInfo tweet = mTweetList.get(position);
					String articleid = tweet.getArticleid();

					try {
						HttpClient client = new DefaultHttpClient();
						String postURL = "http://funsumer.net/json/";
						HttpPost post = new HttpPost(postURL);

						List params1 = new ArrayList();
						params1.add(new BasicNameValuePair("oopt", "11"));
						params1.add(new BasicNameValuePair("position", "1"));
						params1.add(new BasicNameValuePair("toid", articleid));

						UrlEncodedFormEntity ent = new UrlEncodedFormEntity(
								params1, HTTP.UTF_8);
						post.setEntity(ent);
						HttpResponse responsePOST = client.execute(post);

					} catch (Exception e) {
					}
					mTweetList.remove(position);
					mAdapter.notifyDataSetChanged();
				}else{
					/*
					  new CustomDialog(NoteActivity.this,
					  CustomDialog.TYPE_BASIC_OK, null,
					  "can't delete this article") .show(); return;*/
					 
				}
			}
		}
	};

}
