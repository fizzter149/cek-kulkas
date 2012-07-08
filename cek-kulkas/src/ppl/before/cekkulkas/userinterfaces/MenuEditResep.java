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
import ppl.before.cekkulkas.models.Resep;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * class view untuk halaman edit resep
 * 
 * @author Team Before
 */
public class MenuEditResep extends Activity {

	/** bahan baru dari resep yang diedit */
	private final ArrayList<Bahan> listBahanBaru = new ArrayList<Bahan>();

	/** controller daftar resep untuk membantu akses ke database resep */
	private ControllerDaftarResep cdr = new ControllerDaftarResep();

	/** controller untuk membantu akses ke database isi kulkas */
	private ControllerIsiKulkas cik = new ControllerIsiKulkas();

	/** list semua bahan yang terdapat di database resep, untuk membantu auto suggestion */
	private final ArrayList<String> listAllNamaBahan = cdr.ambilNamaBahan();

	/** resep yang sedang diedit */
	private Resep resep;

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
		setContentView(R.layout.editresep);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

		findViewById(R.id.tabdeskripsi_edit).requestFocus();
		
		// mengambil resep yang akan diedit dari extras
		resep = (Resep)getIntent().getSerializableExtra("resep");
		String foto = resep.getFoto();

		// jika resep tidak memiliki foto, pakai foto default
		if(foto == null || foto.equals("")){
			foto = "r0";
		}
		((ImageView)findViewById(R.id.fotoresepedit)).setImageBitmap(BitmapFactory.decodeFile("/data/data/ppl.before.cekkulkas/"+foto+".jpg"));
		
		// pada awalnya, bahan dari resep yang diedit adalah bahan yang lama
		for(Bahan bahan: resep.getListBahan()){
			listBahanBaru.add(bahan);
		}

		// inisialisasi tampilan tab (deskripsi, bahan, langkah)
		final TabHost tabHost = (TabHost)findViewById(R.id.tabHost_edit);
		tabHost.setup();

		TabSpec spec = tabHost.newTabSpec("Tab Deskripsi");
		spec.setContent(R.id.tabdeskripsi_edit);
		spec.setIndicator("Deskripsi");
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("Tab Bahan");
		spec.setContent(R.id.tabbahan_edit);
		spec.setIndicator("Bahan");
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("Tab Langkah");
		spec.setContent(R.id.tablangkah_edit);
		spec.setIndicator("Langkah");
		tabHost.addTab(spec);
		
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = 40;

		((TextView)findViewById(R.id.nama_resep_edit)).setText(resep.getNama());
		((EditText)findViewById(R.id.kategori_resep_edit)).setText(resep.getKategori());
		((EditText)findViewById(R.id.deskripsi_resep_edit)).setText(resep.getDeskripsi());
		((EditText)findViewById(R.id.langkah_resep_edit)).setText(resep.getLangkah());

		// menambahkan baris bahan sesuai isi list bahan
		for(Bahan bahan: resep.getListBahan()){
			tambahRowBahan(bahan);
		}

		// menambah satu baris bahan lagi (baris kosong)
		tambahRowBahan();

		// listener untuk tombol simpan
		((Button)findViewById(R.id.tombolsimpan_edit)).setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				// mengambil semua informasi yang dimasukkan user
				String nama = ((TextView)findViewById(R.id.nama_resep_edit)).getText()+"";
				String kategori = ((TextView)findViewById(R.id.kategori_resep_edit)).getText()+"";
				String deskripsi = ((TextView)findViewById(R.id.deskripsi_resep_edit)).getText()+"";
				String langkah = ((TextView)findViewById(R.id.langkah_resep_edit)).getText()+"";
				String foto = resep.getFoto();
				
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
				// membuat objek resep baru untuk menggantikan objek resep yang lama
				Resep resepBaru = new Resep(nama,deskripsi,listBahanBaru,langkah,kategori,resep.getFlagFavorit(),foto);

				// memperbarui resep di database, tampilkan notifikasi
				if(cdr.ubahResep(resep, resepBaru)){
					Toast.makeText(MenuEditResep.this, "resep berhasil diedit", Toast.LENGTH_SHORT).show();
					listBahanBaru.clear();
					MenuEditResep.this.finish();
				} else {
					Toast.makeText(MenuEditResep.this, "edit resep gagal", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// listener untuk tombol batal
		((Button)findViewById(R.id.tombolbatal_edit)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// keluar dari view, kembali ke halaman detail resep
				MenuEditResep.this.finish();
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
				((ImageView)findViewById(R.id.fotoresepedit)).setImageBitmap(fotoCamera);
			}
		}
	}

	/**
	 * menambah baris bahan pada tampilan table layout di tab bahan
	 * menerima argumen informasi bahan yang ingin ditambahkan,
	 * oleh karenanya, method ini dipanggil untuk inisialisasi bahan-bahan yang lama
	 * 
	 * @param bahan informasi bahan sebagai isi dari baris bahan yang ingin ditambahkan
	 */
	private void tambahRowBahan(final Bahan bahan){
		final TableLayout tabelBahan = (TableLayout)findViewById(R.id.tabelbahan_edit);

		final TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		final EditText namaBahan = new EditText(this);
		namaBahan.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		namaBahan.setText(bahan.getNama());
		namaBahan.setEnabled(false);

		final EditText banyakBahan = new EditText(this);
		banyakBahan.setLayoutParams(new LayoutParams(0,LayoutParams.WRAP_CONTENT,0.5f));
		float jumlah = bahan.getJumlah();
		String jmlStr = "";
		if(jumlah % 1.0 == 0.0){
			jmlStr += (int) jumlah;
		} else {
			jmlStr += jumlah;
		}
		banyakBahan.setText("" + jmlStr);
		banyakBahan.setEnabled(false);

		final Spinner spinnerSatuan = new Spinner(this);
		ArrayList<String> listSatuan = new ArrayList<String>(1);
		listSatuan.add(bahan.getSatuan());
		adapterSatuan = new ArrayAdapter<String>(MenuEditResep.this, android.R.layout.simple_spinner_item, listSatuan);
		adapterSatuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSatuan.setLayoutParams(new LayoutParams(0,LayoutParams.WRAP_CONTENT,0.5f));
		spinnerSatuan.setAdapter(adapterSatuan);
		spinnerSatuan.setEnabled(false);

		final ImageButton tambahHapus = new ImageButton(this);
		tambahHapus.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.MATCH_PARENT));
		tambahHapus.setImageResource(android.R.drawable.ic_delete);

		tambahHapus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				tabelBahan.removeView(tr);
				listBahanBaru.remove(bahan);
			}
		});

		LinearLayout leftContainer = new LinearLayout(this);
		leftContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.8f));
		leftContainer.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout leftBottomContainer = new LinearLayout(this);
		leftBottomContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		leftBottomContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		leftBottomContainer.addView(banyakBahan);
		leftBottomContainer.addView(spinnerSatuan);
		
		leftContainer.addView(namaBahan);
		leftContainer.addView(leftBottomContainer);
		
		tr.addView(leftContainer);
		tr.addView(tambahHapus);

		tabelBahan.addView(tr);
	}


	/** 
	 * menambah baris bahan pada tampilan table layout pada tab bahan.
	 * terdiri dari 4 elemen: nama bahan, jumlah bahan, satuan, dan tombol tambah row baru 
	 */
	private void tambahRowBahan(){

		final TableLayout tabelBahan = (TableLayout)findViewById(R.id.tabelbahan_edit);

		final TableRow tr = new TableRow(this);
		tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		// field nama bahan berupa auto suggestion dari bahan yang ada di database
		final AutoCompleteTextView namaBahan = new AutoCompleteTextView(this);
		namaBahan.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		namaBahan.setHint("nama bahan");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.autocomplete_namabahan, listAllNamaBahan);
		namaBahan.setAdapter(adapter);

		final EditText banyakBahan = new EditText(this);
		int maxLength = 9;
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxLength);
		// jumlah digit dibatasi hanya 9
		banyakBahan.setFilters(FilterArray);
		banyakBahan.setLayoutParams(new LayoutParams(0,LayoutParams.WRAP_CONTENT,0.5f));
		banyakBahan.setHint("jml");
		// banyak bahan hanya menerima inputan angka desimal
		banyakBahan.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

		final Spinner spinnerSatuan = new Spinner(this);
		spinnerSatuan.setLayoutParams(new LayoutParams(0,LayoutParams.WRAP_CONTENT,0.5f));

		// pengecekan setiap user memasukkan informasi nama bahan
		namaBahan.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){

					// jika bahan tidak ada di database, resep field nama bahan, tampilkan notifikasi
					if(!(namaBahan.getText()+"").equals("") && !listAllNamaBahan.contains(""+namaBahan.getText())){
						Toast.makeText(MenuEditResep.this, "maaf, "+namaBahan.getText()+" tidak ada di database bahan kami", Toast.LENGTH_LONG).show();
						namaBahan.setText("");
						// jika bahan ada di database, update field satuan
					} else if(!(namaBahan.getText()+"").equals("")){
						adapterSatuan = new ArrayAdapter<String>(MenuEditResep.this, android.R.layout.simple_spinner_item, cik.getMultiSatuan(namaBahan.getText().toString()));
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
		tambahHapus.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.MATCH_PARENT));
		tambahHapus.setImageResource(android.R.drawable.ic_input_add);

		// listener untuk tombol tambah row
		tambahHapus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String nama = namaBahan.getText()+"";

				// cek nama bahan tidak boleh kosong
				if(nama.equals("")) {
					Toast.makeText(MenuEditResep.this, "Isi nama bahan terlebih dahulu", Toast.LENGTH_SHORT).show();
					return;
				}

				float jumlahBahan;
				String temp = banyakBahan.getText()+"";

				if(temp.equals("")) temp = "0";
				jumlahBahan = Float.parseFloat(temp);
				String jmlStr = "";
				if(jumlahBahan % 1.0 == 0.0){
					jmlStr += (int) jumlahBahan;
				} else {
					jmlStr += jumlahBahan;
				}
				banyakBahan.setText(jmlStr);
				final Bahan bahan = new Bahan(nama,jumlahBahan,tempSatuan);

				// tambahkan objek bahan baru ke list bahan
				listBahanBaru.add(bahan);
				tambahHapus.setImageResource(android.R.drawable.ic_delete);
				namaBahan.setEnabled(false);
				banyakBahan.setEnabled(false);
				spinnerSatuan.setEnabled(false);
				tambahRowBahan();

				// ubah listener untuk tombol tambah row menjadi hapus row
				tambahHapus.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						tabelBahan.removeView(tr);
						listBahanBaru.remove(bahan);
					}
				});
			}
		});

		LinearLayout leftContainer = new LinearLayout(this);
		leftContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.8f));
		leftContainer.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout leftBottomContainer = new LinearLayout(this);
		leftBottomContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		leftBottomContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		leftBottomContainer.addView(banyakBahan);
		leftBottomContainer.addView(spinnerSatuan);
		
		leftContainer.addView(namaBahan);
		leftContainer.addView(leftBottomContainer);
		
		tr.addView(leftContainer);
		tr.addView(tambahHapus);

		tabelBahan.addView(tr);
	}
}
