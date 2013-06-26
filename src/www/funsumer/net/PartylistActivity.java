package www.funsumer.net;

//import static fun.example.park.constants.Constants_PartyGrid.PAR_IMAGES;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import www.funsumer.net.constants.PartyInfo;
import www.funsumer.net.widget.GridAdapter;
import www.funsumer.net.widget.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;


public class PartylistActivity extends Activity {

	private Context mContext;
	private int mResource;
	private LayoutInflater mLiInflater = null;
	public ImageLoader imageLoader;
	
	String mynoteid, userid;

	JSONArray guplAPI = null;
	String Result = null;
	JSONArray Result_data = null;
	
	GridAdapter gridadapter;
	private ArrayList<PartyInfo> mPartyList;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partylist);
		
		Intent intent = getIntent();
		mynoteid = intent.getStringExtra("mynoteid");
		userid = intent.getStringExtra("userid");
		
		getData(2);
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		
//		String url = "http://funsumer.net/json/?opt=4&mynoteid=" + mynoteid + "&userid=" + userid;
//		
//		JSONParser jParser = new JSONParser();
//		try {
//			JSONObject jsonurl = jParser.getJSONFromUrl(url);
//			guplAPI = jsonurl.getJSONArray("guplAPI");
//			JSONObject resultdata = guplAPI.getJSONObject(0);
//			Result = resultdata.getString("Result");
//			Result_data = resultdata.getJSONArray("Result_data");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
				
		mPartyList = new ArrayList<PartyInfo>();
		gridadapter = new GridAdapter(PartylistActivity.this, R.layout.partylist_grid, mPartyList);
		gridview.setAdapter(gridadapter);
		
		setPartyView();
		
		Button makeparty = (Button)findViewById(R.id.makeparty);
		makeparty.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PartylistActivity.this, makeParty.class);
				intent.putExtra("userid", userid);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				
			}
		});
		
	}
	
	// GET DATA
	public void getData(int i) {

		JSONParser jParser = new JSONParser();
		if (i == 2) {

			String url = "http://funsumer.net/json/?opt=4&mynoteid=" + mynoteid
					+ "&userid=" + userid;

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
}