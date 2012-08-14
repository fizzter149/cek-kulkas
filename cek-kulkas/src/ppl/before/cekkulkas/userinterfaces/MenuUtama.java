package ppl.before.cekkulkas.userinterfaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.ControllerIsiKulkas;
import ppl.before.cekkulkas.controllers.DatabaseHelper;
import ppl.before.cekkulkas.models.Bahan;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * class view untuk halaman menu utama
 * 
 * @author Team Before
 */
public class MenuUtama extends Activity implements OnItemClickListener {

	private GridView gridview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// title bar aplikasi
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.gridmenuutama);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);

		gridview = (GridView) findViewById(R.id.gridmenuutama);
		gridview.setAdapter(new ImageAdapter(this.getApplicationContext()));
		gridview.setOnItemClickListener(this);

		// membuat database saat aplikasi pertama kali dijalankan (baru install)
		DatabaseHelper.getHelper(getApplicationContext());

		copyPhotoFromResource();
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setMessage("Anda ingin keluar dari aplikasi?")
				.setPositiveButton("Ya", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setNegativeButton("Tidak", null).create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menudimenutama, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		View v = View.inflate(getApplicationContext(), R.layout.webview, null);

		WebView wv = (WebView) (v.findViewById(R.id.webview));
		String title = "";

		if (item.getItemId() == R.id.menutentang) {
			wv.loadUrl("file:///android_asset/about.html");
			title = "tentang";
		} else if (item.getItemId() == R.id.menubantuan) {
			wv.loadUrl("file:///android_asset/help.html");
			title = "bantuan";
		}

		new AlertDialog.Builder(this).setTitle(title).setView(v)
				.setPositiveButton("OK", null).create().show();
		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg2) {
		case 0:
			startActivity(new Intent(getApplicationContext(),
					MenuCekKulkas.class));
			break;
		case 1:
			if (new ControllerIsiKulkas(this).ambilSemuaBahan().size() == 0) {
				// pergi ke halaman daftar resep, sertakan objek list bahan yang
				// telah dipilih
				Intent i = new Intent(this, MenuDaftarResep.class);
				Bundle b = new Bundle();
				b.putSerializable("listBahan", new ArrayList<Bahan>());
				i.putExtras(b);
				startActivity(i);
			} else {
				startActivity(new Intent(getApplicationContext(),
						MenuPilihBahan.class));
			}
			break;
		case 2:
			Intent i = new Intent(getApplicationContext(),
					MenuAddEditResep.class);
			i.putExtra("mode", MenuAddEditResep.MODE_ADD);

			startActivity(i);
			break;
		case 3:
			startActivity(new Intent(getApplicationContext(),
					MenuDaftarResepFavorit.class));
			break;
		}
	}

	private void copyPhotoFromResource() {
		String path = "/data/data/ppl.before.cekkulkas/";
		if (!(new File(path + "r0.jpg")).exists()) {
			InputStream is = null;
			OutputStream os = null;
			try {
				AssetManager am = getAssets();
				String[] listFoto = am.list("fotoresep");
				for (String nama : listFoto) {
					is = am.open("fotoresep/" + nama);
					os = new FileOutputStream(path + nama);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = is.read(buffer)) > 0) {
						os.write(buffer, 0, length);
					}
					os.flush();
					os.close();
					is.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private class ImageAdapter extends BaseAdapter {

		public ImageAdapter(Context c) {
		}

		public int getCount() {
			return 4;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.iconmenuutama, null);
			}
			TextView textview = (TextView) convertView
					.findViewById(R.id.icon_text);
			ImageView imgview = (ImageView) convertView
					.findViewById(R.id.icon_image);
			switch (position) {
			case 0:
				textview.setText(MenuUtama.this
						.getString(R.string.menu_cek_kulkas));
				imgview.setImageResource(R.drawable.ic_menucekkulkas);
				break;
			case 1:
				textview.setText(MenuUtama.this
						.getString(R.string.menu_pilih_bahan));
				imgview.setImageResource(R.drawable.ic_menupilihbahan);
				break;
			case 2:
				textview.setText(MenuUtama.this
						.getString(R.string.menu_tambah_resep));
				imgview.setImageResource(R.drawable.ic_menubuatresep);
				break;
			case 3:
				textview.setText(MenuUtama.this
						.getString(R.string.menu_daftar_resep_favorit));
				imgview.setImageResource(R.drawable.ic_menudaftarfavorit);
				break;
			}
			return convertView;
		}
	}
}