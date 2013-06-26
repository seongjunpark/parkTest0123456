package www.funsumer.net.widget;

import java.util.ArrayList;

import www.funsumer.net.NoteActivity;
import www.funsumer.net.MainActivity.PagerAdapterClass;
import www.funsumer.net.R;
import www.funsumer.net.constants.FriendInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendAdapter extends ArrayAdapter<FriendInfo> {
	private Context mContext;
	private int mResource;
	private ArrayList<FriendInfo> mList;
	private LayoutInflater mInflater;
	public ImageLoader imageLoader;
	public PagerAdapterClass pager;

	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public FriendAdapter(Context context, int layoutResource,
			ArrayList<FriendInfo> objects) {
		super(context, layoutResource, objects);
		this.mContext = context;
		this.mResource = layoutResource;
		this.mList = objects;
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(mContext.getApplicationContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FriendInfo tweet = mList.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(mResource, null);
		}

		if (tweet != null) {
			// 시작
			TextView fname = (TextView) convertView.findViewById(R.id.fname);
			fname.setText(tweet.getFname());
			
			ImageView fpic = (ImageView) convertView.findViewById(R.id.fpic);
			imageLoader.DisplayImage(tweet.getFpic(), fpic);
			fpic.setOnClickListener(on_FrpicClick);
			fpic.setTag(Integer.valueOf(position));

		}

		Log.e("note", "fname = " + tweet.getFname());
		
		return convertView;
	}

//	public static String userid_fromnoteleft = null;
	final OnClickListener on_FrpicClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			FriendInfo tweet = mList.get(position);
			
			String userid_fromnoteleft = tweet.getFid();
			
			Log.e("flist", "tweet.get = " + tweet.getFid());
			Log.e("flist", "userid_fromnoteleft = " + userid_fromnoteleft);
			
			Intent intentput = new Intent(mContext,
					NoteActivity.class);
			intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentput.putExtra("userid", userid_fromnoteleft);
			Log.e("flist", "무엇이 문제인고 " + userid_fromnoteleft);
			v.getContext().startActivity(intentput);
		}
	};
}
