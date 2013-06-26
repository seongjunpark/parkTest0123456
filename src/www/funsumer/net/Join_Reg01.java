package www.funsumer.net;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.google.android.gcm.GCMRegistrar;

import www.funsumer.net.GCMHttpConnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class Join_Reg01 extends Activity {

	private AutoCompleteTextView autoComplete;
	private ArrayList<String> list;
	private ArrayAdapter<String> adapter;

	private String gender;
	private static final String SENDER_ID = "147463031498";	
	private EditText email1, myname1, pnumber1, password1, school1;
	private static final String INSERT_PAGE = "http://funsumer.net/GCM/insert_registration.php";
	private GCMHttpConnect httpConnect = null;
	private GCMHttpConnect.Request httpRequest = new GCMHttpConnect.Request() {
		
		@Override
		public void OnComplete() {
			// TODO Auto-generated method stub
			showToast();
		}
	};
	public void showToast(){
		Toast.makeText(this, "RegID Join_REG01", Toast.LENGTH_LONG).show();
	}
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	DataOutputStream dos;

	private RadioGroup sexGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_reg01);
		
		//for GCM server
		final String regID = GCMRegistrar.getRegistrationId(this);
		System.out.println("agseasegs"+regID);
		if (regID.equals("") || regID == null) {
      		GCMRegistrar.register(this, SENDER_ID);
      	}else{
      		Log.w("funsumer", "Already Registered : " + regID);
      	}

		email1 = (EditText) findViewById(R.id.email);
		email1.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		myname1 = (EditText) findViewById(R.id.myname);
		pnumber1 = (EditText) findViewById(R.id.phone_number);
		password1 = (EditText) findViewById(R.id.password);

		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String phoneNumber = manager.getLine1Number();

		int phonelength = phoneNumber.length();
		pnumber1.setText("0" + phoneNumber.substring(phonelength-10,phonelength));


		sexGroup = (RadioGroup) findViewById(R.id.sexgroup);
		sexGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton sex = (RadioButton) findViewById(sexGroup
						.getCheckedRadioButtonId());
				switch (checkedId) {
				case R.id.male:
					gender = "1";
					break;
				case R.id.female:
					gender = "2";
					break;
				}

			}
		});
		school1 = (EditText) findViewById(R.id.school);

		/*
		 * autoComplete = (AutoCompleteTextView) findViewById(R.id.school); list
		 * = new ArrayList<String>();
		 * 
		 * // 占쏙옙 list.add("寃쏀씗��븰援�); list.add("�쒓뎅��븰援�); list.add("�쒖슱��븰援�);
		 * list.add("�곗꽭��븰援�);
		 * 
		 * adapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_dropdown_item_1line, list);
		 * 
		 * autoComplete.setAdapter(adapter);
		 * autoComplete.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
		 * arg2, long arg3) {
		 * 
		 * school = autoComplete.getText().toString(); // TODO Auto-generated
		 * method stub
		 * 
		 * } });
		 */

		Button btn_next02 = (Button) findViewById(R.id.go_reg02);

		btn_next02.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				boolean mailCheck = isEmail(email1.getText().toString());

				if (email1.getText().toString().equals("")) {

					new CustomDialog(Join_Reg01.this,
							CustomDialog.TYPE_BASIC_OK, null, "이메일을 입력해 주세요.")
							.show();
					return;

				} else if (mailCheck == false) {

					new CustomDialog(Join_Reg01.this,
							CustomDialog.TYPE_BASIC_OK, null, "이메일 형식을 확인해 주세요.")
							.show();
					return;

				}

				else if (myname1.getText().toString().equals("")) {
					new CustomDialog(Join_Reg01.this,
							CustomDialog.TYPE_BASIC_OK, null, "이름을 입력해 주세요.")
							.show();
					return;
				} else if (pnumber1.getText().toString().equals("")) {
					new CustomDialog(Join_Reg01.this,
							CustomDialog.TYPE_BASIC_OK, null,
							"핸드폰 번호를 입력해 주세요.").show();
					return;
				} else if (password1.getText().toString().equals("")) {
					new CustomDialog(Join_Reg01.this,
							CustomDialog.TYPE_BASIC_OK, null, "비밀번호를 입력해 주세요.")
							.show();
					return;
				} else if (gender == null) {
					new CustomDialog(Join_Reg01.this,
							CustomDialog.TYPE_BASIC_OK, null, "성별을 선택해 주세요.")
							.show();
					return;
				} else if (school1.getText().toString().equals("")) {
					new CustomDialog(Join_Reg01.this,
							CustomDialog.TYPE_BASIC_OK, null, "출신학교를 작성해 주세요.")
							.show();
					return;
				} else {
					//for funsumer server
					insertRegistrationID(regID);
					try {

						HttpClient client = new DefaultHttpClient();
						String postURL = "http://funsumer.net/json/";
						HttpPost post = new HttpPost(postURL);
						Toast.makeText(getApplicationContext(), "�곌껐�꾨즺",
								Toast.LENGTH_LONG).show();
						List params1 = new ArrayList();
						params1.add(new BasicNameValuePair("oopt", "1"));
						params1.add(new BasicNameValuePair("reg_email", email1
								.getText().toString()));
						params1.add(new BasicNameValuePair("reg_name", myname1
								.getText().toString()));
						params1.add(new BasicNameValuePair("reg_phone",
								pnumber1.getText().toString()));
						params1.add(new BasicNameValuePair("reg_pass",
								password1.getText().toString()));
						params1.add(new BasicNameValuePair("reg_gender", gender));
						params1.add(new BasicNameValuePair("reg_univ", school1
								.getText().toString()));

						UrlEncodedFormEntity ent = new UrlEncodedFormEntity(
								params1, HTTP.UTF_8);
						post.setEntity(ent);
						HttpResponse responsePOST = client.execute(post);
						HttpEntity resEntity = responsePOST.getEntity();

						Intent intentput = new Intent(Join_Reg01.this,
								Join_Reg02.class);
						intentput
								.putExtra("email", email1.getText().toString());
						intentput.putExtra("password", password1.getText()
								.toString());
						intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intentput);
						if (resEntity != null) {
							Log.w("RESPONSE", EntityUtils.toString(resEntity));
						}

					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			}
		});

	}

	public static void main(String string) {

	}

	public static boolean isEmail(String email) {
		if (email == null)
			return false;
		boolean b = Pattern.matches(
				"[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+", email.trim());
		return b;
	}

	public void insertRegistrationID(String id) {
		httpConnect = new GCMHttpConnect(INSERT_PAGE + "?regID=" + id,
				httpRequest);
		httpConnect.start();
	}

}