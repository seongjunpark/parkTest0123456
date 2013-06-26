package www.funsumer.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import www.funsumer.net.constants.Constants_friendlist_databox;
import www.funsumer.net.constants.FriendInfo;
import www.funsumer.net.widget.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Friendlist_Activity extends Activity {

	public ListView m_lv = null;
	public LinearLayout fr_header = null;

	ArrayList<FriendInfo> mFriendList;
	FriendAdapter f_adapter;

	JSONParser jParser = new JSONParser();

	JSONArray guflAPI = null;
	String Result = null;
	JSONArray Result_data = null;

	String mynoteid = MainActivity.mynoteid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendlist);

		String url = "http://funsumer.net/json?opt=3&mynoteid=" + mynoteid
				+ "&userid=" + mynoteid;

		JSONParser jParser = new JSONParser();

		try {
			JSONObject jsonurl = jParser.getJSONFromUrl(url);
			guflAPI = jsonurl.getJSONArray("guflAPI");
			JSONObject resultdata = guflAPI.getJSONObject(0);
			Result = resultdata.getString("Result");
			Result_data = resultdata.getJSONArray("Result_data");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Constants_friendlist_databox.BDURL(Result_data);

		try {
			m_lv = (ListView) findViewById(R.id.friendlist1);
			m_lv.addHeaderView(fr_header());

			mFriendList = new ArrayList<FriendInfo>();
			f_adapter = new FriendAdapter(this, R.layout.friendlist_item,
					mFriendList);
			m_lv.setAdapter(f_adapter);
			m_lv.setDividerHeight(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setFriendListView();
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
				;

				tweet.setFid(object.getString("Fid"));

				mFriendList.add(tweet);
			}

			Log.e("note", "tweet.setFname = " + mFriendList);

			f_adapter.notifyDataSetChanged();
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

	public LinearLayout fr_header() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fr_header = (LinearLayout) inflater.inflate(R.layout.friendlist_header,
				null);

		return fr_header;
	}

	public class FriendAdapter extends ArrayAdapter<FriendInfo> {
		private Context mContext;
		private int mResource;
		private ArrayList<FriendInfo> mFriendList;
		private LayoutInflater mLiInflater = null;
		public ImageLoader imageLoader;

		public FriendAdapter(Context context, int layoutResource,
				ArrayList<FriendInfo> objects) {
			super(context, layoutResource, objects);
			this.mContext = context;
			this.mResource = layoutResource;
			this.mFriendList = objects;
			this.mLiInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.imageLoader = new ImageLoader(mContext.getApplicationContext());
		}

		public int getCount() {
			return mFriendList.size();
		}

		public FriendInfo getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			FriendInfo fri_info = mFriendList.get(position);

			// View vi = convertView;
			if (convertView == null) {
				convertView = mLiInflater.inflate(mResource, null);
			}

			TextView fname = (TextView) convertView.findViewById(R.id.fname);
			fname.setText(fri_info.getFname());

			ImageView fpic = (ImageView) convertView.findViewById(R.id.fpic);
			imageLoader.DisplayImage(fri_info.getFpic(), fpic);
			fpic.setOnClickListener(friend_Click);
			fpic.setTag(Integer.valueOf(position));

			return convertView;
		}
	}

	final OnClickListener friend_Click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			FriendInfo fri_info = mFriendList.get(position);
			String userid_fromflist = fri_info.getFid();

			Intent intent = new Intent(Friendlist_Activity.this,
					NoteActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("userid", userid_fromflist);
			startActivity(intent);
		}
	};
}
