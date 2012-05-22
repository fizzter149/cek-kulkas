package ppl.before.cekkulkas.userinterfaces;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.ControllerDaftarResep;
import ppl.before.cekkulkas.controllers.ControllerIsiKulkas;
import ppl.before.cekkulkas.models.Bahan;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


/**
 * class view untuk halaman buat resep baru
 * 
 * @author Team Before
 */
public class MenuBuatResepBaru extends Activity {
	
	
	/** bahan dari resep baru yang akan ditambahkan */
	private final ArrayList<Bahan> listBahan = new ArrayList<Bahan>();
	
	/** controller daftar resep untuk membantu akses database resep */
	private ControllerDaftarResep cdr = new ControllerDaftarResep(this);
	
	/** controller untuk membantu akses ke database isi kulkas */
	private ControllerIsiKulkas cik = new ControllerIsiKulkas(this);
	
	/** list semua bahan yang terdapat di database resep */
	private final ArrayList<String> listAllNamaBahan = cdr.getAllNamaBahan();
	
	private String tempSatuan;
	
	private ArrayAdapter<String> adapterSatuan;
	
	private final int CAMERA_PIC_REQUEST = 149;
	
	private Bitmap fotoCamera;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// title bar aplikasi
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tambahresep);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

		((ImageView)findViewById(R.id.fotoreseptambah)).setImageBitmap(BitmapFactory.decodeFile("/data/data/ppl.before.cekkulkas/r0.jpg"));
		
		// insialisasi tampilan tab (deskripsi, bahan, langkah)
		final TabHost tabHost = (TabHost)findViewById(R.id.tabHost_tambah);
		tabHost.setup();

		TabSpec spec = tabHost.newTabSpec("Tab Deskripsi");
		spec.setContent(R.id.tabdeskripsi_tambah);
		spec.setIndicator("Deskripsi");
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("Tab Bahan");
		spec.setContent(R.id.tabbahan_tambah);
		spec.setIndicator("Bahan");
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("Tab Langkah");
		spec.setContent(R.id.tablangkah_tambah);
		spec.setIndicator("Langkah");
		tabHost.addTab(spec);
		
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = 40;

		// listener untuk event ganti fokus pada text field nama resep
		((EditText)findViewById(R.id.nama_resep_tambah)).setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View arg0, boolean arg1) {
				
				// hanya handle untuk ganti fokus dari aktif ke tidak aktif 
				if(!arg1){
					
					// cek nama resep kosong/tidak
					if(!(((EditText)findViewById(R.id.nama_resep_tambah)).getText()+"").equals("")){
						
						// resep dapat disimpan (tombol simpan resep di-enable)
						((Button)findViewById(R.id.tombolsimpan_tambah)).setEnabled(true);
					} else {
						
						//  resep tidak dapat disimpan (tombol simpan resep di-disable)
						((Button)findViewById(R.id.tombolsimpan_tambah)).setEnabled(false);
					}
				}
			}
		});


		// panggil method untuk menambah baris bahan pada tampilan table di tab bahan
		tambahRowBahan();

		
		// listener untuk tombol simpan
		((Button)findViewById(R.id.tombolsimpan_tambah)).setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				
				// ambil semua informasi yang telah dimasukkan user
				String nama = ((TextView)findViewById(R.id.nama_resep_tambah)).getText()+"";
				String kategori = ((TextView)findViewById(R.id.kategori_resep_tambah)).getText()+"";
				String deskripsi = ((TextView)findViewById(R.id.deskripsi_resep_tambah)).getText()+"";
				String langkah = ((TextView)findViewById(R.id.langkah_resep_tambah)).getText()+"";
				String foto = "";
				
				if(fotoCamera != null){
					File f = new File("/data/data/ppl.before.cekkulkas/"+nama+".jpg");
					try {
					    ByteArrayOutputStream baos = new ByteArrayOutputStream();
					    fotoCamera.compress(CompressFormat.JPEG, 100, baos);
					    byte[] bitmapData = baos.toByteArray();
					    
					    FileOutputStream fos = new FileOutputStream(f);
					    fos.write(bitmapData);
				    } catch (IOException e) {
				    }
					foto = nama;
				}

				// tambahkan resep ke database, beri notifikasi
				if(cdr.addResep(nama, deskripsi, listBahan, langkah, kategori, foto)){
					Toast.makeText(MenuBuatResepBaru.this, "resep berhasil dibuat", Toast.LENGTH_SHORT).show();
					listBahan.clear();
					MenuBuatResepBaru.this.finish();
				} else {
					Toast.makeText(MenuBuatResepBaru.this, "nama resep sudah ada", Toast.LENGTH_SHORT).show();
				}
			}
		});

		
		// listener untuk tombol batal
		((Button)findViewById(R.id.tombolbatal_tambah)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				// keluar dari view ini, kembali ke menu utama 
				MenuBuatResepBaru.this.finish();
			}
		});
	}
	
	public void ambilGambar(View v){
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				fotoCamera = (Bitmap) data.getExtras().get("data");
				((ImageView)findViewById(R.id.fotoreseptambah)).setImageBitmap(fotoCamera);
			}
		}
	}

	
	/**	menambahkan baris baru pada table layout bahan di tab bahan. 
	 *  setiap row terdiri dari 4 elemen, nama bahan (auto suggestion), banyaknya, satuan, dan tombol
	 *  untuk menambahkan baris baru.
	 */
	private void tambahRowBahan(){

		final TableLayout tabelBahan = (TableLayout)findViewById(R.id.tabelbahan);

		final TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));

		// text field berupa autocomplete
		final AutoCompleteTextView namaBahan = new AutoCompleteTextView(this);
		namaBahan.setLayoutParams(new LayoutParams(0,LayoutParams.WRAP_CONTENT,0.5f));
		namaBahan.setHint("nama bahan");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.autocomplete_namabahan, listAllNamaBahan);
		namaBahan.setAdapter(adapter);

		final EditText banyakBahan = new EditText(this);
		int maxLength = 9;
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		// jumlah digit dibatasi hanya 9
		banyakBahan.setFilters(FilterArray);
		banyakBahan.setLayoutParams(new LayoutParams(0,LayoutParams.WRAP_CONTENT,0.2f));
		banyakBahan.setHint("jml");
		// banyak bahan yang valid hanya angka desimal
		banyakBahan.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

		final Spinner spinnerSatuan = new Spinner(this);

		// listener untuk event ganti fokus pada field nama bahan
		// setelah user memasukkan nama bahan, informasi satuan diupdate sesuai nama bahan tersebut
		// jika nama bahan yang dimasukkan tidak ada di database, field direset dan tampilkan notifikasi
		namaBahan.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {

				// hanya handle event dari fokus aktif ke tidak aktif 
				if(!hasFocus){
					
					// jika nama bahan tidak ada di database
					if(!(namaBahan.getText()+"").equals("") && !listAllNamaBahan.contains(""+namaBahan.getText())){
						Toast.makeText(MenuBuatResepBaru.this, "maaf, "+namaBahan.getText()+" tidak ada di database bahan kami", Toast.LENGTH_LONG).show();
						namaBahan.setText("");
					// jika nama bahan ada di database
					} else if(!(namaBahan.getText()+"").equals("")){
						adapterSatuan = new ArrayAdapter<String>(MenuBuatResepBaru.this, android.R.layout.simple_spinner_item, cik.getMultiSatuan(namaBahan.getText().toString()));
			    		adapterSatuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			    		spinnerSatuan.setAdapter(adapterSatuan);
					}
				}
			}
		});
		
		spinnerSatuan.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long duration) {
				// TODO Auto-generated method stub
				tempSatuan = adapterSatuan.getItem(position);
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		final ImageButton tambahHapus = new ImageButton(this);
		tambahHapus.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		tambahHapus.setImageResource(R.drawable.ic_tambah);

		// listener untuk tombol tambah row baru
		tambahHapus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String nama = namaBahan.getText()+"";

				// cek dulu field nama bahan tidak boleh kosong
				if(nama.equals("")) {
					Toast.makeText(MenuBuatResepBaru.this, "Isi nama bahan terlebih dahulu", Toast.LENGTH_SHORT).show();
					return;
				}
					
				float jumlahBahan;
				String temp = banyakBahan.getText()+"";
				
				if(temp.equals("")) temp = "0";
				jumlahBahan = Float.parseFloat(temp);
				
				final Bahan bahan = new Bahan(nama, jumlahBahan, tempSatuan);

				// informasi bahan ditambahkan ke list bahan
				listBahan.add(bahan);
				
				// tombol tambah pada row tersebut diubah behaviornya menjadi untuk menghapus row tersebut.
				tambahHapus.setImageResource(R.drawable.ic_hapus);
				namaBahan.setEnabled(false);
				banyakBahan.setEnabled(false);
				spinnerSatuan.setEnabled(false);
				tambahRowBahan();
				tambahHapus.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						tabelBahan.removeView(tr);
						listBahan.remove(bahan);
					}
				});
			}
		});

		tr.addView(namaBahan);
		tr.addView(banyakBahan);
		tr.addView(spinnerSatuan);
		tr.addView(tambahHapus);

		tabelBahan.addView(tr);
	}
}
