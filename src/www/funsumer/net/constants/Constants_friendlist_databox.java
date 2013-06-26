package www.funsumer.net.constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Constants_friendlist_databox {

	public static String[] BDFNAME = null;
	public static String[] BDFID = null;
	public static String[] BDFPIC = null;
	
	public static void BDURL(JSONArray data) {
		JSONArray Result_data = data;
		try {
			BDFNAME = new String[Result_data.length()];
			BDFID = new String[Result_data.length()];
			BDFPIC = new String[Result_data.length()];
			for (int i = 0; i < Result_data.length(); i++) {
				JSONObject b = Result_data.getJSONObject(i);

				String Fname = b.getString("Fname");
				String Fid = b.getString("Fid");
				
				String Fpic = b.getString("Fpic");
				
				if (Fname.equals("")) {
				} else {
					// text
					String BDtext = Fname;
					BDFNAME[i] = BDtext;
					String BDtext1 = Fid;
					BDFID[i] = BDtext1;

					// picture
					String BDauthorpic = "http://funsumer.net/" + Fpic;
					BDFPIC[i] = BDauthorpic;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
