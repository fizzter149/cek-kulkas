package ppl.before.cekkulkas.userinterfaces;

import java.util.List;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.ControllerDaftarResep;
import ppl.before.cekkulkas.controllers.ControllerIsiKulkas;
import ppl.before.cekkulkas.models.Bahan;
import ppl.before.cekkulkas.models.Resep;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;


/**
 * class view untuk halaman detail resep
 * menampilkan informasi kategori, deskripsi, bahan, serta langkah
 * dari bahan yang dipilih baik dari daftar resep hasil pencarian,
 * maupun dari daftar resep favorit
 * 
 * @author Team Before
 */
public class MenuDetailResep extends Activity {
	
	/** controller daftar resep untuk membantu akses ke database daftar resep */
	private final ControllerDaftarResep cdr = new ControllerDaftarResep(this);
	
	/** controller untuk membantu akses ke database isi kulkas */
	private ControllerIsiKulkas cik = new ControllerIsiKulkas(this);
	
	/** resep yang akan ditampilkan detailnya */
	private Resep resep;
	
	/** bahan-bahan dari resep */
	private List<Bahan> listBahan;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// title bar aplikasi
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.detailresep);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		
		// mengambil objek resep yang akan ditampilkan detailnya dari extra
		resep = (Resep)getIntent().getSerializableExtra("resep");
		
		// inisialisasi tampilan tab
		final TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        
        TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tabdeskripsi);
        spec1.setIndicator("Deskripsi");
        
        TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setIndicator("Bahan");
        spec2.setContent(R.id.tabbahan);
        
        TabSpec spec3=tabHost.newTabSpec("Tab 3");
        spec3.setIndicator("Langkah");
        spec3.setContent(R.id.tablangkah);
        
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
		
        
        // mengisi setiap elemen pada view
		((TextView)findViewById(R.id.nama_resep)).setText(resep.getNama());
		((TextView)findViewById(R.id.kategori_resep)).setText(resep.getKategori());
		String foto = resep.getFoto();
		
		// jika resep tidak memiliki foto, pakai foto default
		if(foto == null || foto.equals("")){
			foto = "r0";
		}
		
		((ImageView)findViewById(R.id.fotoResep)).setImageResource(getResources().getIdentifier(foto, "drawable", getPackageName()));
		
		((TextView)findViewById(R.id.deskripsi_resep)).setText(resep.getDeskripsi());
		
		listBahan = resep.getListBahan();
		String bahanStr = "";
		
		for (int i = 0; i < listBahan.size(); i++) {
			Bahan bahan = listBahan.get(i);
			float jumlah = bahan.getJumlah();
			if (jumlah % 1.0 == 0.0) {
				bahanStr += (int)bahan.getJumlah()+" "+bahan.getSatuan()+" "+bahan.getNama()+"\n";
			} else {
				bahanStr += bahan.getJumlah()+" "+bahan.getSatuan()+" "+bahan.getNama()+"\n";
			}
		}
		
		((TextView)findViewById(R.id.bahan_resep)).setText(bahanStr);
		
		((TextView)findViewById(R.id.langkah_resep)).setText(resep.getLangkah());
	}

	@Override
	/**
	 * membuat menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
//		return super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menudetailresep, menu);
	    return true;

	}

	@Override
	/**
	 * listener untuk item di menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
//		return super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		// menu edit, pergi ke view edit resep dengan menyertakan objek resep
		case R.id.menuedit:
			Intent i = new Intent(MenuDetailResep.this, MenuEditResep.class);
			Bundle b = new Bundle();
			b.putSerializable("resep", resep);
			i.putExtras(b);
			startActivity(i);
			return true;
		// menu hapus, hapus resep dari database dan beri notifikasi
		case R.id.menuhapus:
			DialogInterface.OnClickListener konfirmasiHapus = new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			        	cdr.removeResep(resep.getNama());
						Toast.makeText(MenuDetailResep.this, "resep berhasil dihapus", Toast.LENGTH_SHORT).show();
						MenuDetailResep.this.finish();
			            break;

			        case DialogInterface.BUTTON_NEGATIVE:
			            //No button clicked
			            break;
			        }
			    }
			};

			AlertDialog.Builder alertHapus = new AlertDialog.Builder(this);
			alertHapus.setMessage("Anda yakin ingin menghapus resep " + resep.getNama() + "?").setPositiveButton("OK", konfirmasiHapus)
			    .setNegativeButton("Batal", konfirmasiHapus).show();
            
			return true;
		// menu set/unset favorit, update flag favorit di database
		case R.id.menufavorite:
			if(resep.getFlagFavorit() == 0){
				DialogInterface.OnClickListener konfirmasiSetFavorit = new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            cdr.setFavorite(resep.getNama(), true);
				            resep.setFlagFavorit(1);
				            Toast.makeText(MenuDetailResep.this, resep.getNama()+" berhasil ditambahkan ke daftar resep favorit", Toast.LENGTH_SHORT).show();
				            break;
	
				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};
	
				AlertDialog.Builder alertSetFavorit = new AlertDialog.Builder(this);
				alertSetFavorit.setMessage("Anda yakin ingin menambah "+resep.getNama()+" ke daftar resep favorit?").setPositiveButton("OK", konfirmasiSetFavorit)
				    .setNegativeButton("Batal", konfirmasiSetFavorit).show();
			} else if(resep.getFlagFavorit() == 1){
				DialogInterface.OnClickListener konfirmasiUnsetFavorit = new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which) {
				        case DialogInterface.BUTTON_POSITIVE:
				            cdr.setFavorite(resep.getNama(), false);
				            resep.setFlagFavorit(0);
				            Toast.makeText(MenuDetailResep.this, resep.getNama()+" berhasil dihapus dari daftar resep favorit", Toast.LENGTH_SHORT).show();
				            break;
	
				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};
	
				AlertDialog.Builder alertUnsetFavorit = new AlertDialog.Builder(this);
				alertUnsetFavorit.setMessage("Apakah Anda yakin menghapus "+resep.getNama()+" dari daftar resep favorit?").setPositiveButton("OK", konfirmasiUnsetFavorit)
				    .setNegativeButton("Batal", konfirmasiUnsetFavorit).show();
			}

			return true;
		case R.id.menumasak:
			DialogInterface.OnClickListener konfirmasiMasak = new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which) {
			        case DialogInterface.BUTTON_POSITIVE:
			        	List<Bahan> listIsiKulkas = cik.get();
			        	Log.i("masak", "listBahan.size() = "+listBahan.size());
			        	for (int i = 0; i < listBahan.size(); i++) {
			        		Bahan bahan = listBahan.get(i);
			        		Log.i("masak", "isiKulkas.size() = "+listIsiKulkas.size());
			        		Log.i("masak", "cik.contains("+bahan.getNama()+") = "+cik.contains(bahan.getNama()));
			        		if (cik.contains(bahan.getNama())) {
			        			// pengurangan jumlah bahan
			        			float hasilKurang = cik.getJumlah(bahan.getNama()) - bahan.getJumlah();
			        			Log.i("masak", cik.getJumlah(bahan.getNama()) + "-" +bahan.getJumlah());
			        			if (hasilKurang < 0) {
			        				// bahan dihilangkan dari kulkas
			        				cik.delete(bahan.getNama());
			        			} else {
			        				// ubah jumlah bahan
			        				cik.setJumlah(bahan.getNama(), hasilKurang);
			        			}
			        		}
			        	}
			        	Toast.makeText(MenuDetailResep.this, "berhasil memasak", Toast.LENGTH_SHORT).show();
			            break;

			        case DialogInterface.BUTTON_NEGATIVE:
			            //No button clicked
			            break;
			        }
			    }
			};

			AlertDialog.Builder alertMasak = new AlertDialog.Builder(this);
			alertMasak.setMessage("Anda yakin ingin memasak "+resep.getNama()+" dengan bahan dari kulkas?").setPositiveButton("OK", konfirmasiMasak)
			    .setNegativeButton("Batal", konfirmasiMasak).show();
			return true;
		case R.id.menushare:
			Intent i2 = new Intent(MenuDetailResep.this, MenuPublikasiKeJejaringSosial.class);
			Bundle b2 = new Bundle();
			b2.putSerializable("resep", resep);
			i2.putExtras(b2);
			startActivity(i2);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	/**
	 * saat view di-resume, inisialisasi ulang semua elemen di view
	 */
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("masak",resep.getNama());
		resep = cdr.getResep(resep.getNama());
		
		((TextView)findViewById(R.id.kategori_resep)).setText(resep.getKategori());
		((TextView)findViewById(R.id.deskripsi_resep)).setText(resep.getDeskripsi());
		
		List<Bahan> listBahan = resep.getListBahan();
		String bahanStr = "";
		
		for(int i = 0; i < listBahan.size(); i++) {
			Bahan bahan = listBahan.get(i);
			float jumlah = bahan.getJumlah();
			if (jumlah % 1.0 == 0.0) {
				bahanStr += (int)bahan.getJumlah()+" "+bahan.getSatuan()+" "+bahan.getNama()+"\n";
			} else {
				bahanStr += bahan.getJumlah()+" "+bahan.getSatuan()+" "+bahan.getNama()+"\n";
			}
		}
		
		((TextView)findViewById(R.id.bahan_resep)).setText(bahanStr);
		((TextView)findViewById(R.id.langkah_resep)).setText(resep.getLangkah());
	}

}
