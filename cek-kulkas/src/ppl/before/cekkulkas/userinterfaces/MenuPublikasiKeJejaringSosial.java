package ppl.before.cekkulkas.userinterfaces;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.models.Bahan;
import ppl.before.cekkulkas.models.Resep;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class MenuPublikasiKeJejaringSosial extends Activity{

	private Resep resep;
	
	private SharedPreferences sharedPref;
	
    /** Name to store the users access token */
	private String prefAccessTokenTwitter = "accessToken";
    /** Name to store the users access token secret */
    private static final String prefAccessTokenSecretTwitter = "accessTokenSecret";
	
	/** Consumer Key generated when you registered your app at https://dev.twitter.com/apps/ */
    private static final String CONSUMER_KEY_TWITTER = "pxrH07GZxEyZ02Na269MZA";
    /** Consumer Secret generated when you registered your app at https://dev.twitter.com/apps/  */
    private static final String CONSUMER_SECRET_TWITTER = "ITY5gC9DPDwOiPs9qkltHJPPAYZqJfz2KnvCMWHNY"; // XXX Encode in your app
    /** The url that Twitter will redirect to after a user log's in - this will be picked up by your app manifest and redirected into this activity */
    private static final String CALLBACK_URL = "cekkulkas:///";
    
    /** Twitter4j object */
    private Twitter twitter;
    /** The request token signifies the unique ID of the request you are sending to twitter  */
    private RequestToken reqToken;
    

    // FACEBOOK
    private static final String[] PERMISSIONS = new String[] {"publish_stream"};
    private static final String APP_ID = "127129437422417";
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";

	private Facebook facebook;
    private Bitmap fotoResep;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// title bar aplikasi
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.share);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		
		// mengambil objek resep yang akan ditampilkan detailnya dari extra
		resep = (Resep)getIntent().getSerializableExtra("resep");
		byte[] temp = (byte[])getIntent().getByteArrayExtra("foto");
		if (temp != null) {
			fotoResep = BitmapFactory.decodeByteArray(temp, 0, temp.length);
	        ((ImageView)findViewById(R.id.fotoresepshare)).setImageBitmap(fotoResep);
		} else {
			((ImageView)findViewById(R.id.fotoresepshare)).setVisibility(View.GONE);
		}
		
		((TextView)findViewById(R.id.namaresepshare)).setText(resep.getNama());
        ((EditText)findViewById(R.id.komentarresep)).setText("Saya baru saja memasak "+resep.getNama());
		
		sharedPref = getSharedPreferences("twitterPref", MODE_PRIVATE);
		
		if(sharedPref.contains(prefAccessTokenTwitter)){
			((Button)findViewById(R.id.logintwitter)).setVisibility(View.GONE);
		} else {
			((Button)findViewById(R.id.checkboxtwitter)).setVisibility(View.GONE);
		}
		
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY_TWITTER, CONSUMER_SECRET_TWITTER);
		
		facebook = new Facebook(APP_ID);
		
		if(restoreCredentials(facebook)){
			((Button)findViewById(R.id.loginfacebook)).setVisibility(View.GONE);
		} else {
			((Button)findViewById(R.id.checkboxfacebook)).setVisibility(View.GONE);
		}
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menuhapusloginan, menu);
//		
//		return super.onCreateOptionsMenu(menu);
//	}
//	
//	
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		switch(item.getItemId()){
//		case R.id.menuhapusloginan:
//			PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().commit();
//			((Button)findViewById(R.id.logintwitter)).setVisibility(View.VISIBLE);
//			((Button)findViewById(R.id.loginfacebook)).setVisibility(View.VISIBLE);
//			((CheckBox)findViewById(R.id.checkboxtwitter)).setVisibility(View.GONE);
//			((CheckBox)findViewById(R.id.checkboxfacebook)).setVisibility(View.GONE);
//			break;
//		}
//		
//		return super.onOptionsItemSelected(item);
//	}

	public void loginTwitter(View v){
		try{
			reqToken = twitter.getOAuthRequestToken(CALLBACK_URL);
			WebView twitterSite = new WebView(MenuPublikasiKeJejaringSosial.this);
			twitterSite.enablePlatformNotifications();
			twitterSite.loadUrl(reqToken.getAuthenticationURL());
			twitterSite.requestFocus(View.FOCUS_DOWN);
            twitterSite.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_UP:
                            if (!v.hasFocus()) {
                                v.requestFocus();
                            }
                            break;
                    }
                    return false;
                }
            });
            setContentView(twitterSite);
		} catch (TwitterException e){
			Toast.makeText(getApplicationContext(), "login twitter gagal", Toast.LENGTH_SHORT).show();
		}
	}

	public void loginFb(View v){
		facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if(uri!=null && uri.toString().startsWith(CALLBACK_URL)){
			String oauthVerifier = uri.getQueryParameter("oauth_verifier");
			
			try{
				AccessToken at = twitter.getOAuthAccessToken(reqToken, oauthVerifier);
				twitter.setOAuthAccessToken(at);
				
				String token = at.getToken();
                String secret = at.getTokenSecret();
                Editor editor = sharedPref.edit();
                editor.putString(prefAccessTokenTwitter, token);
                editor.putString(prefAccessTokenSecretTwitter, secret);
                editor.commit();
                
                setContentView(R.layout.share);
                
                ((TextView)findViewById(R.id.namaresepshare)).setText(resep.getNama());
                ((EditText)findViewById(R.id.komentarresep)).setText("Saya baru saja memasak "+resep.getNama());
                ((ImageView)findViewById(R.id.fotoresepshare)).setImageBitmap(fotoResep);
                
                ((Button)findViewById(R.id.logintwitter)).setVisibility(View.GONE);
                ((CheckBox)findViewById(R.id.checkboxtwitter)).setVisibility(View.VISIBLE);
                
                if (fotoResep != null) {
                    ((ImageView)findViewById(R.id.fotoresepshare)).setImageBitmap(fotoResep);
        		} else {
        			((ImageView)findViewById(R.id.fotoresepshare)).setVisibility(View.GONE);
        		}
                
                if(restoreCredentials(facebook)){
        			((Button)findViewById(R.id.loginfacebook)).setVisibility(View.GONE);
        		} else {
        			((Button)findViewById(R.id.checkboxfacebook)).setVisibility(View.GONE);
        		}
			} catch (TwitterException e){
				Toast.makeText(this, "autentikasi twitter error", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void share(View v){

		CheckBox checkBoxTwitter = (CheckBox)findViewById(R.id.checkboxtwitter);
		CheckBox checkBoxFb = (CheckBox)findViewById(R.id.checkboxfacebook);
		
		
		if(checkBoxTwitter.getVisibility() == View.VISIBLE && checkBoxTwitter.isChecked()){
			String token = sharedPref.getString(prefAccessTokenTwitter, null);
			String secret = sharedPref.getString(prefAccessTokenSecretTwitter,null);
			
			AccessToken at = new AccessToken(token, secret);
			
			twitter.setOAuthAccessToken(at);
			
			try{
				StatusUpdate status = new StatusUpdate(((EditText)findViewById(R.id.komentarresep)).getText()+" via @cekkulkas");

				if (fotoResep != null) {
					File f = new File("/data/data/ppl.before.cekkulkas/foto.jpg");
					try {
					    ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    fotoResep.compress(CompressFormat.JPEG, 100, baos);
					    byte[] bitmapData = baos.toByteArray();
					    
					    FileOutputStream fos = new FileOutputStream(f);
					    fos.write(bitmapData);
				    } catch (IOException e) {
				    }
					status.setMedia(f);
					
				} else {
					String temp = resep.getFoto();
					if (temp == null || temp.equals("")) {
						temp = "r0";
					}
					File f = new File("/data/data/ppl.before.cekkulkas/" + temp + ".jpg");
					status.setMedia(f);
				}

				
				
				twitter.updateStatus(status);
				Toast.makeText(this, "resep berhasil dibagikan ke twitter", Toast.LENGTH_SHORT).show();
			} catch (TwitterException e){
				Log.i("gagal tweet",e.getMessage());
				Toast.makeText(this, "gagal tweet ke twitter", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Tidak ada yang dishare ke twitter", Toast.LENGTH_SHORT).show();
		}
		
		if(checkBoxFb.getVisibility() == View.VISIBLE && checkBoxFb.isChecked() == true){
			String deskripsi = ((EditText)findViewById(R.id.komentarresep)).getText()+"";
			
			String bahan = "";
			List<Bahan> listBahan = resep.getListBahan();
			for(int i=0; i<listBahan.size(); i++){
				Bahan temp = listBahan.get(i);
				float jumlah = temp.getJumlah();
				if(jumlah%1.0 == 0.0){
					bahan += (i+1)+". "+(int)jumlah+" "+temp.getSatuan()+" "+temp.getNama()+"\n";
				}else {
					bahan += (i+1)+". "+jumlah+" "+temp.getSatuan()+" "+temp.getNama()+"\n";
				}
			}
			
			deskripsi+="\n\n\n"+resep.getNama().toUpperCase()+"\n\n"+resep.getDeskripsi()+"\n\nBahan-bahan:\n"+bahan+"\n\nCara Pembuatan:\n"+resep.getLangkah();

			byte[] data = null;
			if(fotoResep != null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				fotoResep.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				data = baos.toByteArray();
			} else {
				String temp = resep.getFoto();
				if (temp == null || temp.equals("")) {
					temp = "r0";
				}
				Bitmap bmp = BitmapFactory.decodeFile("/data/data/ppl.before.cekkulkas/" + temp + ".jpg");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if(bmp == null) Log.i("cek","null");
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				data = baos.toByteArray();
			}
			
			Bundle parameters = new Bundle();
	        parameters.putString("caption", deskripsi);
	        parameters.putString("method", "photos.upload");
			parameters.putByteArray("picture", data);
        	
			AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
            mAsyncRunner.request(null, parameters, "POST", new SimpleUploadListener(), null);
	        
            Toast.makeText(MenuPublikasiKeJejaringSosial.this, "resep berhasil dibagikan ke facebook", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Tidak ada yang dishare ke facebook", Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean saveCredentials(Facebook facebook) {
    	Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	editor.putString(TOKEN, facebook.getAccessToken());
    	editor.putLong(EXPIRES, facebook.getAccessExpires());
    	return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
    	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
    	return facebook.isSessionValid();
	}
	
	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	
	    	((Button)findViewById(R.id.loginfacebook)).setVisibility(View.GONE);
            ((CheckBox)findViewById(R.id.checkboxfacebook)).setVisibility(View.VISIBLE);
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("autentikasi facebook gagal!");
	    }
	    public void onError(DialogError error) {
	    	showToast("autentikasi facebook gagal!");
	        finish();
	    }
	    public void onCancel() {
	    	showToast("autentikasi facebook gagal!");
	    }
	}
	
	private class SimpleUploadListener extends BaseRequestListener {
	    public void onComplete(final String response, final Object state) {
	        try {
	            Log.d("Facebook-Example", "Response: " + response.toString());
	        } catch (FacebookError e) {
	            Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
	        }
	    }
	}


	private void showToast(String message){
		Toast.makeText(MenuPublikasiKeJejaringSosial.this, message, Toast.LENGTH_SHORT).show();
	}
}
