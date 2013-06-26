package www.funsumer.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

public class WriteNote extends Activity {

	private static String selectedImagePath;
	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int CROP_FROM_GALLERY = 3;
	private Uri mImageCaptureUri;
	private AlertDialog mDialog;

	private String full_path;

	private URL connectUrl = null;

	private Bitmap bitmap;
	private ImageView imgView;
	private String goid;

	public String id;
	private EditText writeAT;
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	private ImageView mPhotoImageView;
	private FileInputStream mFileInputStream = null;
	static InputStream is = null;
	static String json = "";

	DataOutputStream dos;
	private String mynoteid, position;
	private String toid, partyid;

	public static String getsetTabid_afterwrite = null;
	private List<String> registrationIds = new ArrayList<String>();
	private String GCMname, GCMfrom;
	

	private Sender gcmSender; // GCM Sender
	private Message gcmMessage; // GCM Message
	private MulticastResult gcmMultiResult; // GCM Multi Result(�ϰ� ���)
	// 개발자 콘솔에서 발급받은 API Key
	private static String API_KEY = "AIzaSyDPayDjS8UbkzGg7sezdcJlvaVihY7S2m8";
	// 메세지의 고유 ID(?)정도로 생각하면 됩니다. 메세지의 중복수신을 막기 위해 랜덤값을 지정합니다
	//private static String COLLAPSE_KEY = String.valueOf(Math.random() % 100 + 1);
	// 기기가 활성화 상태일 때 보여줄 것인지
	private static boolean DELAY_WHILE_IDLE = true;// origin true
	// 기기가 비활성화 상태일 때 GCM Storage에 보관되는 시간
	private static int TIME_TO_LIVE = 1800;// origin 3
	// 메세지 전송 실패시 재시도할 횟수
	private static int RETRY = 3;

	private static final String TAG = "GCM";
	private String GCMposition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		partyid = PartyActivity.partyid;
		setContentView(R.layout.upload_test);

		mynoteid = MainActivity.mynoteid;
		toid = NoteActivity.userid;
		partyid = PartyActivity.partyid;

		mPhotoImageView = (ImageView) findViewById(R.id.image);
		imgView = (ImageView) findViewById(R.id.ImageView);

		writeAT = (EditText) findViewById(R.id.writeAT);

		Intent getintent = getIntent();
		position = getintent.getStringExtra("position");

		Button btn_next02 = (Button) findViewById(R.id.btn);
		btn_next02.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (writeAT.getText().toString().equals("")
						&& full_path == null) {
					new CustomDialog(WriteNote.this,
							CustomDialog.TYPE_BASIC_OK, null, "글을 작성해 주세요.")
							.show();
					return;
				} else {
					if (selectedImagePath == null) {
						onlyText();

					} else {
						try {
							String mFilePath = selectedImagePath;
							DoFileUpload(mFilePath);
						} catch (Exception e) {

						}
						// ///////////text upload
					}
					getsetTabid_afterwrite = position;

					MainActivity.userid = toid;

					Intent intentput = new Intent(WriteNote.this,
							MainActivity.class);
					intentput.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intentput);
				}
			}
		});
		Log.e("abc", " mynoteid = " + mynoteid + "     toid = " + toid);
	}

	public void onButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btn_image_crop:
			mDialog = createDialog();
			mDialog.show();
			break;
		}
	}

	private AlertDialog createDialog() {
		final View innerView = getLayoutInflater().inflate(
				R.layout.image_crop_row, null);

		Button camera = (Button) innerView.findViewById(R.id.btn_camera_crop);
		Button gellary = (Button) innerView.findViewById(R.id.btn_gellary_crop);

		camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doTakePhotoAction();
				setDismiss(mDialog);
			}
		});

		gellary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doTakeAlbumAction();
				setDismiss(mDialog);
			}
		});

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("�대�吏�Crop");
		ab.setView(innerView);

		return ab.create();
	}

	private void setDismiss(AlertDialog dialog) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	private void doTakePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//making Crop image save path
		mImageCaptureUri = createSaveCropFile();
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	String mFileName;
	private static final String BACKGROUNDIMAGE_FILE = "_funsumer.jpg";
	private void doTakeAlbumAction() {
		// �⑤쾾 �몄텧
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {

		case PICK_FROM_ALBUM: {
			mImageCaptureUri = data.getData();
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(mImageCaptureUri, "image/*");
			try {

				Uri selectedImageUri = data.getData();

				selectedImagePath = getPath(selectedImageUri);
				intent.putExtra("outputX", 500);
				intent.putExtra("outputY", 500);
				intent.putExtra("aspectX", 500);
				intent.putExtra("aspectY", 500);
				intent.putExtra("scale", true);
				intent.putExtra("output", mImageCaptureUri);
				intent.putExtra("return-data", false);
				startActivityForResult(intent, CROP_FROM_GALLERY);
				 
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Internal error",
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
			break;
		}

		case PICK_FROM_CAMERA: {
			
			
			//mImageCaptureUri = data.getData();
			//full_path = mImageCaptureUri.getPath();
			// �대�吏�� 媛�졇���댄썑��由ъ궗�댁쫰���대�吏��ш린瑜�寃곗젙�⑸땲��
			// �댄썑���대�吏��щ∼ �댄뵆由ъ��댁뀡���몄텧�섍쾶 �⑸땲��

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(mImageCaptureUri, "image/*");
			mFileName = String.valueOf(System.currentTimeMillis()) + BACKGROUNDIMAGE_FILE;
			Uri u = Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/funsumer", mFileName));
			
			// Crop���대�吏�� ��옣��Path
			intent.putExtra("outputX", 500);
			intent.putExtra("outputY", 500);
			intent.putExtra("aspectX", 500);
			intent.putExtra("aspectY", 500);
			intent.putExtra("scale", true);
			intent.putExtra("output", mImageCaptureUri);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, CROP_FROM_CAMERA);

			break;
		}
		case CROP_FROM_CAMERA: {
			 full_path = mImageCaptureUri.getPath();

			Bitmap photo;
			BitmapFactory.Options options = new BitmapFactory.Options();
			photo = BitmapFactory.decodeFile(full_path, options);
			mPhotoImageView.setImageBitmap(photo);
			Log.e(TAG, "PICK_FROM_CAMERA size : " + photo.getHeight()*photo.getWidth()); 
			selectedImagePath = full_path;
			break;

		}
		case CROP_FROM_GALLERY: {

			final Bundle extras = data.getExtras();
			if (extras != null) {
				//Bitmap photo1 = extras.getParcelable("data");
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap photo1 = BitmapFactory.decodeFile(selectedImagePath, options);
				mPhotoImageView.setImageBitmap(photo1);
				 Log.e(TAG, "PICK_FROM_ALBUM : " + photo1.getHeight()*photo1.getWidth());
				File f = new File(mImageCaptureUri.getPath());
				if (f.exists()) {
					f.delete();
				}

				break;
			}

		}

		}
	}

	private Uri createSaveCropFile() {
		Uri uri;
		String url = "tmp_" + String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				url));
		return uri;
	}

	private void DoFileUpload(String filePath) throws IOException {
		Log.d("Test", "file path = " + filePath);
		HttpFileUpload("http://funsumer.net/json/", filePath);

	}

	// void>>>>>Returncode
	private void HttpFileUpload(String urlString, String fileName) {
		try {

			if (position.equals("1")) {
				goid = toid;
			} else {
				goid = partyid;

			}
			mFileInputStream = new FileInputStream(fileName);
			connectUrl = new URL(urlString);
			Log.d("Test", "mFileInputStream  is " + mFileInputStream);

			HttpURLConnection conn = (HttpURLConnection) connectUrl
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.connect();

			// write data
			dos = new DataOutputStream(conn.getOutputStream());
			writeFormField("oopt", "2");
			writeFormField("position", position);
			writeFormField("mynoteid", mynoteid);
			writeFormField("toid", goid);
			writeFormField("content", writeAT.getText().toString());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ fileName + "\"" + lineEnd);

			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Test", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.e("Test", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...

			// get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer b = new StringBuffer();
			// System.out.println("lihiasefase" + "//" + is);
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
				// System.out.println("lihiasefase" + "//" + b);
			}
			String s = b.toString();
			Log.e("Test", "result = " + s);
			dos.close();
			selectedImagePath = null;
			if (position.equals("1")) {
				GCMposition = "1";
			} else {
				GCMposition = "2";
			}
			getJson(mynoteid, GCMposition, goid, writeAT.getText().toString());

		} catch (Exception e) {
			Log.d("Test", "exception " + e.getMessage());
			// TODO: handle exception
		}

	}

	private void writeFormField(String fieldName, String fieldValue) {
		// TODO Auto-generated method stub
		try {

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\""
					+ fieldName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			dos.write(fieldValue.getBytes("utf-8"));
			dos.writeBytes(lineEnd);

		} catch (Exception e) {
			System.out.println("GeoPictureUploader.writeFormField: got: "
					+ e.getMessage());
		}

	}

	public void onlyText() {

		try {

			HttpClient client = new DefaultHttpClient();
			String postURL = "http://funsumer.net/json/";
			HttpPost post = new HttpPost(postURL);
			/*
			 * Toast.makeText(getApplicationContext(), "�곌껐�꾨즺",
			 * Toast.LENGTH_LONG) .show();
			 */
			if (position.equals("1")) {
				goid = toid;
			} else {
				goid = partyid;

			}
			Log.w("funsumerWOW", mynoteid);
			List params1 = new ArrayList();
			params1.add(new BasicNameValuePair("oopt", "2"));
			params1.add(new BasicNameValuePair("position", position));
			params1.add(new BasicNameValuePair("mynoteid", mynoteid));
			params1.add(new BasicNameValuePair("toid", goid));
			params1.add(new BasicNameValuePair("content", writeAT.getText()
					.toString()));

			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1,
					HTTP.UTF_8);
			post.setEntity(ent);

			HttpResponse responsePOST = client.execute(post);

			HttpEntity resEntity = responsePOST.getEntity();
			is = resEntity.getContent();

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "euc-kr"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			if (resEntity != null) {
				JSONArray obj = new JSONArray(json);

				for (int i = 0; i < obj.length(); i++) {

					JSONObject tjo = obj.getJSONObject(i);
					String regId = tjo.getString("ID");
					GCMname = tjo.getString("Name");
					GCMfrom = tjo.getString("From");

					registrationIds.add(0, regId);

				}
				setMessage();
				sendMessage();
			}

		} catch (Exception e) {

			// TODO: handle exception
		}

	}

	public void decodeFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		bitmap = BitmapFactory.decodeFile(filePath, o2);

		imgView.setImageBitmap(bitmap);

	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}
	String GCMFrom_id;
	public void setMessage() {
		gcmSender = new Sender(API_KEY);
		gcmMessage = new Message.Builder()
				// .collapseKey(COLLAPSE_KEY)
				.delayWhileIdle(DELAY_WHILE_IDLE).timeToLive(TIME_TO_LIVE)
				.addData("ticker", GCMname + "님이 글을 남겼습니다.")
				.addData("title", "[Funsumer]" + GCMfrom)
				.addData("msg", writeAT.getText().toString())
				.addData("ids",GCMFrom_id).build();
		
		System.out.println("asefasef"+"//"+GCMFrom_id);
	}

	public void sendMessage() {
		// 일괄전송시에 사용
		try {
			gcmMultiResult = gcmSender.send(gcmMessage, registrationIds, RETRY);
			System.out.println("testV01" + gcmMessage);
			System.out.println("testV02" + registrationIds);
			System.out.println("textV03" + RETRY);
		} catch (IOException e) {
			Log.w(TAG, "IOException " + e.getMessage());
		}
		Log.w(TAG, "getCanonicalIds : " + gcmMultiResult.getCanonicalIds()
				+ "\n" + "getSuccess : " + gcmMultiResult.getSuccess() + "\n"
				+ "getTotal : " + gcmMultiResult.getTotal() + "\n"
				+ "getMulticastId : " + gcmMultiResult.getMulticastId());

	}

	public void getJson(String a, String b, String c, String d) {
		try {

			HttpClient client = new DefaultHttpClient();
			String postURL = "http://funsumer.net/json/";
			HttpPost post = new HttpPost(postURL);
			/*
			 * Toast.makeText(getApplicationContext(), "�곌껐�꾨즺",
			 * Toast.LENGTH_LONG) .show();
			 */
			List params1 = new ArrayList();
			params1.add(new BasicNameValuePair("mynoteid",a));
			params1.add(new BasicNameValuePair("optgcm", b));
			params1.add(new BasicNameValuePair("toid", c));
			params1.add(new BasicNameValuePair("content", d));
			System.out.println("asefasefasef" + "//" + a + "//" + b + "/" + c);
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1,
					HTTP.UTF_8);
			post.setEntity(ent);
			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			is = resEntity.getContent();

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "euc-kr"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			if (resEntity != null) {
				JSONArray obj = new JSONArray(json);

				for (int i = 0; i < obj.length(); i++) {

					JSONObject tjo = obj.getJSONObject(i);
					String regId = tjo.getString("ID");
					GCMname = tjo.getString("Name");
					GCMfrom = tjo.getString("From");
					GCMFrom_id = tjo.getString("FROM_ID");
					registrationIds.add(0, regId);
				}
					setMessage();
					sendMessage();

				
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}