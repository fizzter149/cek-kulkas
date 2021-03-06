package ppl.before.cekkulkas.userinterfaces;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

/**
 * class view untuk halaman detail resep menampilkan informasi kategori,
 * deskripsi, bahan, serta langkah dari bahan yang dipilih baik dari daftar
 * resep hasil pencarian, maupun dari daftar resep favorit
 * 
 * @author Team Before
 */
public class MenuDetailResep extends Activity {

	/** controller daftar resep untuk membantu akses ke database daftar resep */
	private ControllerDaftarResep cdr;

	/** controller untuk membantu akses ke database isi kulkas */
	private ControllerIsiKulkas cik;

	/** resep yang akan ditampilkan detailnya */
	private Resep resep;

	/** bahan-bahan dari resep */
	private List<Bahan> listBahan;

	private ArrayList<Bahan> listBahanSelected;

	private final int CAMERA_PIC_REQUEST = 149;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// title bar aplikasi
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.detailresep);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);

		cdr = new ControllerDaftarResep(getApplicationContext());
		cik = new ControllerIsiKulkas(getApplicationContext());
		// mengambil objek resep yang akan ditampilkan detailnya dari extra
		resep = (Resep) getIntent().getSerializableExtra("resep");
		listBahanSelected = (ArrayList<Bahan>) getIntent()
				.getSerializableExtra("listBahan");

		initTab();

		initView();
	}

	private void initView() {

		((TextView) findViewById(R.id.nama_resep)).setText(resep.getNama());

		((TextView) findViewById(R.id.kategori_resep)).setText(resep
				.getKategori());
		((TextView) findViewById(R.id.deskripsi_resep)).setText(resep
				.getDeskripsi());

		String foto = resep.getFoto();
		if (foto == null || foto.equals("")) {
			((ImageView) findViewById(R.id.fotoResep))
					.setImageResource(R.drawable.foto_resep_default);
		} else {
			((ImageView) findViewById(R.id.fotoResep))
					.setImageBitmap(BitmapFactory
							.decodeFile("/data/data/ppl.before.cekkulkas/"
									+ foto + ".jpg"));
		}

		listBahan = resep.getListBahan();
		String bahanStr = "";

		for (int i = 0; i < listBahan.size(); i++) {
			Bahan bahan = listBahan.get(i);
			float jumlah = bahan.getJumlah();
			if (jumlah % 1.0 == 0.0) {
				if (cik.adaDiKulkas(bahan.getNama())) {
					boolean isSelected = false;
					for (int j = 0; j < listBahanSelected.size(); j++) {
						if (listBahanSelected.get(j).getNama()
								.equalsIgnoreCase(bahan.getNama())) {
							isSelected = true;
							break;
						}
					}
					Log.i("detailResep",
							"listBahanSelected.contains(" + bahan.getNama()
									+ "): " + isSelected);
					if (isSelected) {
						bahanStr += "<font color='#0000ff'>"
								+ (int) bahan.getJumlah() + " "
								+ bahan.getSatuan() + " " + bahan.getNama()
								+ "</font><br />";
					} else {
						bahanStr += (int) bahan.getJumlah() + " "
								+ bahan.getSatuan() + " " + bahan.getNama()
								+ "<br />";
					}
				} else {
					bahanStr += "<font color='#ff0000'>"
							+ (int) bahan.getJumlah() + " " + bahan.getSatuan()
							+ " " + bahan.getNama() + "</font><br />";
				}
			} else {
				if (cik.adaDiKulkas(bahan.getNama())) {
					boolean isSelected = false;
					for (int j = 0; j < listBahanSelected.size(); j++) {
						if (listBahanSelected.get(j).getNama()
								.equalsIgnoreCase(bahan.getNama())) {
							isSelected = true;
							break;
						}
					}
					Log.i("detailResep",
							"listBahanSelected.contains(" + bahan.getNama()
									+ "): " + isSelected);
					if (isSelected) {
						bahanStr += "<font color='#0000ff'>"
								+ bahan.getJumlah() + " " + bahan.getSatuan()
								+ " " + bahan.getNama() + "</font><br />";
					} else {
						bahanStr += bahan.getJumlah() + " " + bahan.getSatuan()
								+ " " + bahan.getNama() + "<br />";
					}
				} else {
					bahanStr += "<font color='#ff0000'>" + bahan.getJumlah()
							+ " " + bahan.getSatuan() + " " + bahan.getNama()
							+ "</font><br />";
				}
			}
		}

		((TextView) findViewById(R.id.bahan_resep)).setText(Html
				.fromHtml(bahanStr));
		((TextView) findViewById(R.id.langkah_resep)).setText(resep
				.getLangkah());

		if (resep.getFlagFavorit() == Resep.BUKAN_FAVORIT)
			((ImageView) findViewById(R.id.favResep)).setVisibility(View.GONE);
		else
			((ImageView) findViewById(R.id.favResep))
					.setVisibility(View.VISIBLE);
	}

	private void initTab() {
		// inisialisasi tampilan tab
		final TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tabdeskripsi);
		spec1.setIndicator("Deskripsi");

		TabSpec spec2 = tabHost.newTabSpec("Tab 2");
		spec2.setIndicator("Bahan");
		spec2.setContent(R.id.tabbahan);

		TabSpec spec3 = tabHost.newTabSpec("Tab 3");
		spec3.setIndicator("Langkah");
		spec3.setContent(R.id.tablangkah);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = 40;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = 40;
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = 40;
	}

	public void fullScreen(View v) {
		Intent i = new Intent(getApplicationContext(), MenuFotoFullScreen.class);
		String foto = resep.getFoto();
		if (foto == null || foto.equals("")) {
			foto = "r0";
		}
		i.putExtra("foto", foto);
		startActivity(i);
		return;
	}

	public void ambilGambar() {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}

	@Override
	/**
	 * membuat menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menudetailresep, menu);
		if (resep.getFlagFavorit() == 1) {
			menu.getItem(2).setIcon(android.R.drawable.btn_star_big_on);
		}
		return true;

	}

	@Override
	/**
	 * listener untuk item di menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// return super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		// menu edit, pergi ke view edit resep dengan menyertakan objek resep
		case R.id.menuedit:
			Intent i = new Intent(getApplicationContext(),
					MenuAddEditResep.class);
			Bundle b = new Bundle();
			b.putSerializable("resep", resep);
			i.putExtra("mode", MenuAddEditResep.MODE_EDIT);
			i.putExtras(b);
			startActivity(i);
			return true;
			// menu hapus, hapus resep dari database dan beri notifikasi
		case R.id.menuhapus:
			DialogInterface.OnClickListener konfirmasiHapus = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						cdr.hapusResep(resep.getNama());
						Toast.makeText(MenuDetailResep.this,
								"resep berhasil dihapus", Toast.LENGTH_SHORT)
								.show();
						MenuDetailResep.this.finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder alertHapus = new AlertDialog.Builder(this);
			alertHapus
					.setMessage(
							"Anda yakin ingin menghapus resep "
									+ resep.getNama() + "?")
					.setPositiveButton("OK", konfirmasiHapus)
					.setNegativeButton("Batal", konfirmasiHapus).show();

			return true;
			// menu set/unset favorit, update flag favorit di database
		case R.id.menufavorite:
			if (resep.getFlagFavorit() == 0) {
				DialogInterface.OnClickListener konfirmasiSetFavorit = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							cdr.setFavorit(resep.getNama(), true);
							resep.setFlagFavorit(Resep.FAVORIT);
							Toast.makeText(
									MenuDetailResep.this,
									resep.getNama()
											+ " berhasil ditambahkan ke daftar resep favorit",
									Toast.LENGTH_SHORT).show();
							finish();
							startActivity(getIntent());
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							// No button clicked
							break;
						}
					}
				};

				AlertDialog.Builder alertSetFavorit = new AlertDialog.Builder(
						this);
				alertSetFavorit
						.setMessage(
								"Anda yakin ingin menambah " + resep.getNama()
										+ " ke daftar resep favorit?")
						.setPositiveButton("OK", konfirmasiSetFavorit)
						.setNegativeButton("Batal", konfirmasiSetFavorit)
						.show();
			} else if (resep.getFlagFavorit() == 1) {
				DialogInterface.OnClickListener konfirmasiUnsetFavorit = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							cdr.setFavorit(resep.getNama(), false);
							resep.setFlagFavorit(Resep.BUKAN_FAVORIT);
							Toast.makeText(
									MenuDetailResep.this,
									resep.getNama()
											+ " berhasil dihapus dari daftar resep favorit",
									Toast.LENGTH_SHORT).show();
							finish();
							startActivity(getIntent());
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							// No button clicked
							break;
						}
					}
				};

				AlertDialog.Builder alertUnsetFavorit = new AlertDialog.Builder(
						this);
				alertUnsetFavorit
						.setMessage(
								"Apakah Anda yakin menghapus "
										+ resep.getNama()
										+ " dari daftar resep favorit?")
						.setPositiveButton("OK", konfirmasiUnsetFavorit)
						.setNegativeButton("Batal", konfirmasiUnsetFavorit)
						.show();
			}

			return true;
		case R.id.menumasak:
			DialogInterface.OnClickListener konfirmasiMasak = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						for (int i = 0; i < listBahan.size(); i++) {
							Bahan bahan = listBahan.get(i);
							if (cik.adaDiKulkas(bahan.getNama())) {
								Bahan bahanDiKulkas = cik.ambilBahan(bahan
										.getNama());
								// konversi jumlah bahan
								String satuanDiKulkas = bahanDiKulkas
										.getSatuan();
								String satuanDiResep = bahan.getSatuan();
								float jumlahDiKulkas = bahanDiKulkas
										.getJumlah();
								float jumlahDiResep = cik.konversiSatuan(
										bahanDiKulkas.getNama(), satuanDiResep,
										satuanDiKulkas, bahan.getJumlah());

								// pengurangan jumlah bahan
								float hasilKurang = jumlahDiKulkas
										- jumlahDiResep;
								if (hasilKurang < 0) {
									// bahan dihilangkan dari kulkas
									cik.hapusBahan(bahan.getNama());
								} else {
									// ubah jumlah bahan
									cik.setJumlah(bahan.getNama(), hasilKurang);
								}
							}
						}
						Toast.makeText(MenuDetailResep.this,
								"berhasil memasak", Toast.LENGTH_SHORT).show();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};

			AlertDialog.Builder alertMasak = new AlertDialog.Builder(this);
			alertMasak
					.setMessage(
							"Anda yakin ingin memasak " + resep.getNama()
									+ " dengan bahan dari kulkas?")
					.setPositiveButton("OK", konfirmasiMasak)
					.setNegativeButton("Batal", konfirmasiMasak).show();
			return true;
		case R.id.menushare:
			DialogInterface.OnClickListener konfirmasiFoto = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						ambilGambar();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						Intent i2 = new Intent(getApplicationContext(),
								MenuPublikasiKeJejaringSosial.class);
						Bundle b2 = new Bundle();
						b2.putSerializable("resep", resep);
						i2.putExtras(b2);
						startActivity(i2);
						break;
					}

				}

			};
			AlertDialog.Builder alertFoto = new AlertDialog.Builder(this);
			alertFoto.setMessage("Anda ingin mengambil foto masakan Anda?")
					.setPositiveButton("Ya", konfirmasiFoto)
					.setNegativeButton("Tidak", konfirmasiFoto).show();
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
		resep = cdr.ambilResep(resep.getNama());
		initView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				if (thumbnail != null) {
					Intent i2 = new Intent(getApplicationContext(),
							MenuPublikasiKeJejaringSosial.class);
					Bundle b2 = new Bundle();
					b2.putSerializable("resep", resep);

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					byte[] foto = baos.toByteArray();
					b2.putByteArray("foto", foto);

					i2.putExtras(b2);
					startActivity(i2);
				}
			}

		}
	}

}
