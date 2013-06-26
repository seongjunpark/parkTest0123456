package www.funsumer.net.widget;

import java.util.ArrayList;

import www.funsumer.net.MainActivity;
import www.funsumer.net.NoteActivity;
import www.funsumer.net.R;
import www.funsumer.net.constants.CommentInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<CommentInfo> {
	private Context mContext;
	private int mResource;
	private ArrayList<CommentInfo> mList;
	private LayoutInflater mInflater;
	public ImageLoader imageLoader;
	
	public CommentAdapter(Context context, int layoutResource,
			ArrayList<CommentInfo> objects) {
		super(context, layoutResource, objects);
		this.mContext = context;
		this.mResource = layoutResource;
		this.mList = objects;
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = new ImageLoader(mContext.getApplicationContext());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentInfo tweet = mList.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(mResource, null);
		}

		if (tweet != null) {
			TextView comment = (TextView) convertView.findViewById(R.id.comment);
			comment.setText(tweet.getComment_Name());
			
			TextView comment_time = (TextView) convertView.findViewById(R.id.comment_time);
			comment_time.setText(tweet.getComment_Time());
			
			TextView comment_info = (TextView) convertView.findViewById(R.id.comment_info);
			comment_info.setText(tweet.getComment_Info());
			
			ImageView commentpic = (ImageView) convertView.findViewById(R.id.commentpic);
			imageLoader.DisplayImage(tweet.getComment_Pic(), commentpic);
			commentpic.setOnClickListener(on_CommentAuthorpicClick);
			commentpic.setTag(Integer.valueOf(position));
			
			// 텍스트 세팅
			// time.setText(Html.fromHtml(tweet.getArticle()));
		}

		return convertView;
	}
	
	String commentid;
	final OnClickListener on_CommentAuthorpicClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			CommentInfo tweet = mList.get(position);
			commentid = tweet.getComment_ID();

			Intent intentput = new Intent(mContext,
					NoteActivity.class);
			intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			v.getContext().startActivity(intentput);
			
			// NoteActivity.setListView();

//			MainActivity.mTabHost.setCurrentTab(1);
//			NoteActivity.getData(1, commentid);
//			NoteActivity.head_data();
//			NoteActivity.setListView();

			Log.e("comment", "authorid On Click CustomAdapter = " + commentid);
		}
	};
}
