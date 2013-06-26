package www.funsumer.net.widget;

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

import www.funsumer.net.CommentDialog;
import www.funsumer.net.CustomDialog;
import www.funsumer.net.JSONParser;
import www.funsumer.net.MainActivity;
import www.funsumer.net.MainActivity.PagerAdapterClass;
import www.funsumer.net.NoteActivity;
import www.funsumer.net.PartyActivity;
import www.funsumer.net.R;
import www.funsumer.net.constants.CommentInfo;
import www.funsumer.net.constants.TweetInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<TweetInfo> {
	private Context mContext;
	private int mResource;
	private ArrayList<TweetInfo> mList;
	private LayoutInflater mInflater;
	public ImageLoader imageLoader;
	public PagerAdapterClass pager;
	
	TextView Article_Like_Num;

	/**
	 * @param context
	 * @param layoutResource
	 * @param objects
	 */
	public CustomAdapter(Context context, int layoutResource,
			ArrayList<TweetInfo> objects) {
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
		TweetInfo tweet = mList.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(mResource, null);
		}
//		Log.e("ooo", "converView= " + convertView);
		if (tweet != null) {
			TextView author = (TextView) convertView.findViewById(R.id.author);
			author.setText(tweet.getAuthor());
			
			TextView articlefrom = (TextView) convertView.findViewById(R.id.articlefrom);
			articlefrom.setText(tweet.getArticleFrom());
			articlefrom.setOnClickListener(on_PartyClick);
			articlefrom.setTag(Integer.valueOf(position));
			
			TextView time = (TextView) convertView.findViewById(R.id.time);
			time.setText(tweet.getArtime());
			
			TextView article = (TextView) convertView.findViewById(R.id.article);
			article.setText(tweet.getArticle());
			
			Article_Like_Num = (TextView) convertView.findViewById(R.id.Article_Like_Num);
//			int likenum = Integer.valueOf(tweet.getArticle_Like_Num());
//			Log.e("ooo","likenum = " + likenum);
			int likenum = Integer.parseInt(tweet.getArticle_Like_Num());
			String likenum2 = Integer.toString(likenum + 1);
			Article_Like_Num.setText(likenum2);
//			Article_Like_Num.setText(tweet.getArticle_Like_Num());
			
			TextView Article_Comment_Num = (TextView) convertView.findViewById(R.id.Article_Comment_Num);
			Article_Comment_Num.setText(tweet.getArticle_Comment_Num());
			
			ImageView authorpic = (ImageView) convertView
					.findViewById(R.id.authorpic);
			imageLoader.DisplayImage(tweet.getAuthorpic(), authorpic);
			authorpic.setOnClickListener(on_AuthorpicClick);
			authorpic.setTag(Integer.valueOf(position));

			ImageView arpic = (ImageView) convertView.findViewById(R.id.arpic);
			imageLoader.DisplayImage(tweet.getArPic(), arpic);

			ImageButton likebtn = (ImageButton) convertView.findViewById(R.id.btclick1);
			likebtn.setOnClickListener(likeClick);
			likebtn.setTag(Integer.valueOf(position));
			
			ImageButton commentbtn = (ImageButton) convertView.findViewById(R.id.btclick3);
			commentbtn.setOnClickListener(commentClick);
			commentbtn.setTag(Integer.valueOf(position));
			
			Button delete = (Button)convertView.findViewById(R.id.delete);
			delete.setOnClickListener(NoteActivity.deleteArticle);
			delete.setTag(Integer.valueOf(position));
			
			ImageButton scrapbtn = (ImageButton)convertView.findViewById(R.id.btclick2);
			scrapbtn.setOnClickListener(scrapClick);
			scrapbtn.setTag(Integer.valueOf(position));

		}

		return convertView;
	}

	public static String authorid = null;
	final OnClickListener on_AuthorpicClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			TweetInfo tweet = mList.get(position);
			authorid = tweet.getAuthorid();
			
			Intent intentput = new Intent(mContext,
					NoteActivity.class);
			intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentput.putExtra("userid", authorid);
			v.getContext().startActivity(intentput);
			
			Log.e("note", "userid= " + authorid);
		}
	};
	
	// ONCLICK PARTY TEXT
	String isparty;
	public static String belong = null;
	final OnClickListener on_PartyClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			TweetInfo tweet = mList.get(position);
			isparty = tweet.getIsparty();
			belong = tweet.getBelong();
			
			if (isparty.equals("0")) {
//				CustomAdapter.authorid = belong;
				Intent intentput = new Intent(mContext,
						NoteActivity.class);
				intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intentput.putExtra("userid", belong);
				v.getContext().startActivity(intentput);
			} else if (isparty.equals("1")) {
//				GridAdapter.getpartyid = belong;
				Intent intentput = new Intent(mContext,
						PartyActivity.class);
				intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intentput.putExtra("partyid", belong);
				v.getContext().startActivity(intentput);
			}
		}
	};
	
	// ONCLICK LIKE BUTTON
	final OnClickListener likeClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			TweetInfo tweet = mList.get(position);
			String articleid = tweet.getArticleid();
			
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = "http://funsumer.net/json/";
				HttpPost post = new HttpPost(postURL);

				List params1 = new ArrayList();
				params1.add(new BasicNameValuePair("oopt", "8"));
				params1.add(new BasicNameValuePair("mynoteid", mynoteid));
				params1.add(new BasicNameValuePair("aid", articleid));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(
						params1, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
			} catch (Exception e) {
			}			
			new CustomDialog(mContext, CommentDialog.TYPE_BASIC_OK, null,
					"ĵ�� �־���ϴ�.").show();
		}
	};
	
	// ONCLICK COMMENT BUTTON

	public static String articleid = null;
	private ArrayList<CommentInfo> mCommentList;
	private CommentAdapter mAdapter;

	String mynoteid = MainActivity.mynoteid;

	JSONArray gaciAPI = null;
	String Result = null;
	public static String goResult;
	String Article_Comment_Num = null;
	JSONArray Article_Comment = null;

	final OnClickListener commentClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			TweetInfo tweet = mList.get(position);
			articleid = tweet.getArticleid();

			getCommentData(0);

			if (Result.equals("0")) {
				mCommentList = new ArrayList<CommentInfo>();
				mAdapter = new CommentAdapter(mContext, R.layout.comment_item,
						mCommentList);

				setCommentView();

				new CommentDialog(mContext, CommentDialog.TYPE_BASIC_OK, null,
						mAdapter).show();
			} else if (Result.equals("1")) {
				mCommentList = new ArrayList<CommentInfo>();
				mAdapter = new CommentAdapter(mContext, R.layout.comment_item,
						mCommentList);

				new CommentDialog(mContext, CommentDialog.TYPE_BASIC_OK, null,
						mAdapter).show();
			}
			return;
		}
	};
	//scrap mynote
	final OnClickListener scrapClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			TweetInfo tweet = mList.get(position);
			String articleid = tweet.getArticleid();
			
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = "http://funsumer.net/json/";
				HttpPost post = new HttpPost(postURL);

				List params1 = new ArrayList();
				params1.add(new BasicNameValuePair("oopt", "9"));
				params1.add(new BasicNameValuePair("origin_article", articleid));
				params1.add(new BasicNameValuePair("mynoteid", mynoteid));
				params1.add(new BasicNameValuePair("position", "1"));
				params1.add(new BasicNameValuePair("toid", mynoteid));
				params1.add(new BasicNameValuePair("content", "design"));

				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(
						params1, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
			} catch (Exception e) {
			}
//			int likenum = Integer.valueOf(tweet.getArticle_Like_Num());
//			Article_Like_Num.setText(likenum);
//			Log.e("ooo","likenum = " + likenum);
			
			new CustomDialog(mContext, CommentDialog.TYPE_BASIC_OK, null,
					"ĵ�� �־���ϴ�.").show();
		}
	};
	

	public void getCommentData(int i) {

		JSONParser jParser = new JSONParser();
		if (i == 0) {
			String url = "http://funsumer.net/json/?opt=8&mynoteid=" + mynoteid
					+ "&articleID=" + articleid;
			try {
				JSONObject jsonurl = jParser.getJSONFromUrl(url);
				gaciAPI = jsonurl.getJSONArray("gaciAPI");
				JSONObject article_com = gaciAPI.getJSONObject(0);
				Result = article_com.getString("Result");
				if (Result.equals("0")) {
					Article_Comment_Num = article_com
							.getString("Article_Comment_Num");
					Article_Comment = article_com
							.getJSONArray("Article_Comment");
				}
				goResult = Result;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void setCommentView() {
		mCommentList.clear();

		try {
			int arrayLength = Article_Comment.length();

			for (int i = 0; i < arrayLength; i++) {
				JSONObject object = Article_Comment.getJSONObject(i);
				CommentInfo tweet = new CommentInfo();

				tweet.setComment_Name(object.getString("Comment_Name"));
				tweet.setComment_Time(object.getString("Comment_Time"));
				tweet.setComment_Info(object.getString("Comment_Info"));
				// tweet.setArticle(object.getString("ArInfo").replaceAll("<br>","\n"));

				tweet.setComment_Pic(object.getString("Comment_Pic"));
				
				tweet.setComment_ID(object.getString("Comment_ID"));

				mCommentList.add(tweet);
			}

			mAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
