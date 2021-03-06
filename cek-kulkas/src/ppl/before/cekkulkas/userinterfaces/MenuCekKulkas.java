package ppl.before.cekkulkas.userinterfaces;

import java.util.ArrayList;
import java.util.List;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.ControllerDaftarResep;
import ppl.before.cekkulkas.controllers.ControllerIsiKulkas;
import ppl.before.cekkulkas.models.Bahan;
import ppl.before.quickaction.ActionItem;
import ppl.before.quickaction.QuickAction;
import ppl.before.quickaction.QuickAction.OnActionItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Team Before
 */
public class MenuCekKulkas extends Activity {

	/** controller untuk membantu akses ke database isi kulkas */
	private ControllerIsiKulkas cik;

	/** list bahan dari database isi kulkas */
	private List<Bahan> listBahan;

	/** list bahan setelah difilter */
	private List<Bahan> tempList;

	/** controller daftar resep untuk membantu akses database resep */
	private ControllerDaftarResep cdr;

	/** list semua bahan yang terdapat di database resep */
	private ArrayList<String> listAllNamaBahan;

	private String tempSatuan;

	private ArrayAdapter<String> adapterSatuan;

	private final int MODE_ADD_BAHAN = 0;
	private final int MODE_EDIT_BAHAN = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// title bar aplikasi
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.lihatisikulkas);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		cik = new ControllerIsiKulkas(getApplicationContext());
		cdr = new ControllerDaftarResep(getApplicationContext());
		listAllNamaBahan = cdr.ambilNamaBahan();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		cik = new ControllerIsiKulkas(getApplicationContext());
		cdr = new ControllerDaftarResep(getApplicationContext());
		listAllNamaBahan = cdr.ambilNamaBahan();
		initView();
	}

	/**
	 * inisialisasi isi list daftar resep favorit
	 */
	private void initView() {

		// ambil bahan dari database isi kulkas
		listBahan = cik.ambilSemuaBahan();

		if (listBahan.size() == 0) {
			((TextView) findViewById(R.id.notiflistkosong))
					.setVisibility(View.VISIBLE);
			((ListView) findViewById(R.id.listbahan)).setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.notiflistkosong))
					.setVisibility(View.GONE);
			((ListView) findViewById(R.id.listbahan))
					.setVisibility(View.VISIBLE);
		}

		tempList = listBahan;
		ListView lv = (ListView) findViewById(R.id.listbahan);
		final IsiKulkasAdapter isiKulkasAdapter = new IsiKulkasAdapter(this,
				tempList);
		lv.setAdapter(isiKulkasAdapter);
		lv.setTextFilterEnabled(true);
		// listener
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				final Bahan bahan = tempList.get(position);

				ActionItem ubah = new ActionItem(0, "ubah");
				ActionItem hapus = new ActionItem(1, "hapus");

				QuickAction qa = new QuickAction(MenuCekKulkas.this);
				qa.addActionItem(ubah);
				qa.addActionItem(hapus);
				qa.setOnActionItemClickListener(new OnActionItemClickListener() {

					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						if (actionId == 0) {
							// Set an EditText view to get user input
							float jumlah = bahan.getJumlah();
							String jmlStr = "";
							if (jumlah % 1.0 == 0.0) {
								jmlStr += (int) bahan.getJumlah();
							} else {
								jmlStr += bahan.getJumlah();
							}
							final EditText inputJumlah = new EditText(
									MenuCekKulkas.this);
							int maxLength = 9;
							InputFilter[] FilterArray = new InputFilter[1];
							FilterArray[0] = new InputFilter.LengthFilter(
									maxLength);
							inputJumlah
									.setInputType(InputType.TYPE_CLASS_NUMBER
											| InputType.TYPE_NUMBER_FLAG_DECIMAL);
							inputJumlah.setFilters(FilterArray);
							inputJumlah.setMinWidth(40);
							inputJumlah.setHint(jmlStr);

							final Spinner spinnerSatuan = new Spinner(
									MenuCekKulkas.this);
							adapterSatuan = new ArrayAdapter<String>(
									MenuCekKulkas.this,
									android.R.layout.simple_spinner_item, cik
											.getMultiSatuan(bahan.getNama()));
							adapterSatuan
									.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spinnerSatuan.setAdapter(adapterSatuan);
							spinnerSatuan
									.setOnItemSelectedListener(new OnItemSelectedListener() {

										public void onItemSelected(
												AdapterView<?> parent,
												View view, int position,
												long duration) {
											tempSatuan = adapterSatuan
													.getItem(position);
										}

										public void onNothingSelected(
												AdapterView<?> arg0) {
										}
									});

							final LinearLayout layoutUbahBahan = new LinearLayout(
									MenuCekKulkas.this);
							layoutUbahBahan
									.setOrientation(LinearLayout.HORIZONTAL);
							layoutUbahBahan.setPadding(6, 0, 6, 0);
							layoutUbahBahan.addView(inputJumlah);
							layoutUbahBahan.addView(spinnerSatuan);

							AlertDialog.Builder alertUbah = new AlertDialog.Builder(
									MenuCekKulkas.this);
							alertUbah.setTitle(bahan.getNama());
							alertUbah.setView(layoutUbahBahan);
							alertUbah.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											if (inputJumlah.getText()
													.toString().length() > 0) {
												cik.setJumlah(
														bahan.getNama(),
														Float.parseFloat(inputJumlah
																.getText()
																.toString()));
												isiKulkasAdapter.updateJumlah(
														position,
														Float.parseFloat(inputJumlah
																.getText()
																.toString()));
											}
											cik.setSatuan(bahan.getNama(),
													tempSatuan);
											isiKulkasAdapter.updateSatuan(
													position, tempSatuan);
											Toast.makeText(
													MenuCekKulkas.this,
													bahan.getNama()
															+ " berhasil diubah",
													Toast.LENGTH_SHORT).show();
										}
									});
							alertUbah.setNegativeButton("Batal",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
							alertUbah.show();
						} else if (actionId == 1) {
							AlertDialog.Builder alertHapus = new AlertDialog.Builder(
									MenuCekKulkas.this);
							alertHapus.setMessage("Anda yakin menghapus "
									+ bahan.getNama() + " dari kulkas?");
							alertHapus.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											cik.hapusBahan(bahan.getNama());
											isiKulkasAdapter.remove(bahan);
											Toast.makeText(
													MenuCekKulkas.this,
													bahan.getNama()
															+ " berhasil dihapus dari kulkas",
													Toast.LENGTH_SHORT).show();
											if (isiKulkasAdapter.getCount() == 0) {

												((TextView) findViewById(R.id.notiflistkosong))
														.setVisibility(View.VISIBLE);
												((ListView) findViewById(R.id.listbahan))
														.setVisibility(View.GONE);
											}
										}
									});
							alertHapus.setNegativeButton("Batal",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
							alertHapus.show();
						}
					}
				});

				qa.show(view);

				// final CharSequence[] items = { "Ubah jumlah bahan", "Hapus"
				// };
				// AlertDialog.Builder onClickDialog = new AlertDialog.Builder(
				// MenuCekKulkas.this);
				// final Bahan bahan = tempList.get(position);
				// final int pos = position;
				// onClickDialog.setTitle("Bahan: " + bahan.getNama());
				// onClickDialog.setItems(items,
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int item) {
				// switch (item) {
				// case 0:
				// // Set an EditText view to get user input
				// float jumlah = bahan.getJumlah();
				// String jmlStr = "";
				// if (jumlah % 1.0 == 0.0) {
				// jmlStr += (int) bahan.getJumlah();
				// } else {
				// jmlStr += bahan.getJumlah();
				// }
				// final EditText inputJumlah = new EditText(
				// MenuCekKulkas.this);
				// int maxLength = 9;
				// InputFilter[] FilterArray = new InputFilter[1];
				// FilterArray[0] = new InputFilter.LengthFilter(
				// maxLength);
				// inputJumlah
				// .setInputType(InputType.TYPE_CLASS_NUMBER
				// | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				// inputJumlah.setFilters(FilterArray);
				// inputJumlah.setMinWidth(40);
				// inputJumlah.setHint(jmlStr);
				//
				// final Spinner spinnerSatuan = new Spinner(
				// MenuCekKulkas.this);
				// adapterSatuan = new ArrayAdapter<String>(
				// MenuCekKulkas.this,
				// android.R.layout.simple_spinner_item,
				// cik.getMultiSatuan(bahan.getNama()));
				// adapterSatuan
				// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// spinnerSatuan.setAdapter(adapterSatuan);
				// spinnerSatuan
				// .setOnItemSelectedListener(new OnItemSelectedListener() {
				//
				// public void onItemSelected(
				// AdapterView<?> parent,
				// View view,
				// int position,
				// long duration) {
				// // TODO Auto-generated
				// // method stub
				// tempSatuan = adapterSatuan
				// .getItem(position);
				// }
				//
				// public void onNothingSelected(
				// AdapterView<?> arg0) {
				// }
				// });
				//
				// final LinearLayout layoutUbahBahan = new LinearLayout(
				// MenuCekKulkas.this);
				// layoutUbahBahan
				// .setOrientation(LinearLayout.HORIZONTAL);
				// layoutUbahBahan.setPadding(6, 0, 6, 0);
				// layoutUbahBahan.addView(inputJumlah);
				// layoutUbahBahan.addView(spinnerSatuan);
				//
				// AlertDialog.Builder alertUbah = new AlertDialog.Builder(
				// MenuCekKulkas.this);
				// alertUbah.setTitle(bahan.getNama());
				// alertUbah.setView(layoutUbahBahan);
				// alertUbah
				// .setPositiveButton(
				// "OK",
				// new DialogInterface.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int id) {
				// if (inputJumlah
				// .getText()
				// .toString()
				// .length() > 0) {
				// cik.setJumlah(
				// bahan.getNama(),
				// Float.parseFloat(inputJumlah
				// .getText()
				// .toString()));
				// isiKulkasAdapter
				// .updateJumlah(
				// pos,
				// Float.parseFloat(inputJumlah
				// .getText()
				// .toString()));
				// }
				// cik.setSatuan(bahan
				// .getNama(),
				// tempSatuan);
				// isiKulkasAdapter
				// .updateSatuan(
				// pos,
				// tempSatuan);
				// Toast.makeText(
				// MenuCekKulkas.this,
				// bahan.getNama()
				// + " berhasil diubah",
				// Toast.LENGTH_SHORT)
				// .show();
				// }
				// });
				// alertUbah
				// .setNegativeButton(
				// "Batal",
				// new DialogInterface.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int id) {
				// dialog.cancel();
				// }
				// });
				// alertUbah.show();
				// break;
				// case 1:
				// AlertDialog.Builder alertHapus = new AlertDialog.Builder(
				// MenuCekKulkas.this);
				// alertHapus
				// .setMessage("Anda yakin menghapus "
				// + bahan.getNama()
				// + " dari kulkas?");
				// alertHapus
				// .setPositiveButton(
				// "OK",
				// new DialogInterface.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int id) {
				// cik.hapusBahan(bahan
				// .getNama());
				// isiKulkasAdapter
				// .remove(bahan);
				// Toast.makeText(
				// MenuCekKulkas.this,
				// bahan.getNama()
				// + " berhasil dihapus dari kulkas",
				// Toast.LENGTH_SHORT)
				// .show();
				// }
				// });
				// alertHapus
				// .setNegativeButton(
				// "Batal",
				// new DialogInterface.OnClickListener() {
				// public void onClick(
				// DialogInterface dialog,
				// int id) {
				// dialog.cancel();
				// }
				// });
				// alertHapus.show();
				// break;
				// }
				// }
				// });
				// onClickDialog.show();
			}
		});

		// listener untuk tombol tambah bahan
		((Button) findViewById(R.id.tambahBahan))
				.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						// text field berupa autocomplete
						final AutoCompleteTextView namaBahan = new AutoCompleteTextView(
								MenuCekKulkas.this);
						namaBahan.setHint("nama bahan");
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								MenuCekKulkas.this,
								R.layout.autocomplete_namabahan,
								listAllNamaBahan);
						namaBahan.setAdapter(adapter);

						final EditText jumlahBahan = new EditText(
								MenuCekKulkas.this);
						int maxLength = 9;
						InputFilter[] FilterArray = new InputFilter[1];
						FilterArray[0] = new InputFilter.LengthFilter(maxLength);
						jumlahBahan.setFilters(FilterArray);
						jumlahBahan.setMinWidth(40);
						jumlahBahan.setHint("jml");
						// banyak bahan yang valid hanya angka desimal
						jumlahBahan.setInputType(InputType.TYPE_CLASS_NUMBER
								| InputType.TYPE_NUMBER_FLAG_DECIMAL);

						final Spinner spinnerSatuan = new Spinner(
								MenuCekKulkas.this);

						// listener ketika bahan dipilih dari auto complete.
						// set isi dari spinner satuan menjadi kemungkinan
						// satuan untuk bahan yang dipilih tersebut

						namaBahan
								.setOnItemClickListener(new OnItemClickListener() {

									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										if (listAllNamaBahan.contains(namaBahan
												.getText() + "")) {
											adapterSatuan = new ArrayAdapter<String>(
													MenuCekKulkas.this,
													android.R.layout.simple_spinner_item,
													cik.getMultiSatuan(namaBahan
															.getText()
															.toString()));
											adapterSatuan
													.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
											spinnerSatuan
													.setAdapter(adapterSatuan);
										}
									}
								});

						spinnerSatuan
								.setOnItemSelectedListener(new OnItemSelectedListener() {
									public void onItemSelected(
											AdapterView<?> parent, View view,
											int position, long duration) {
										// TODO Auto-generated method stub
										tempSatuan = adapterSatuan
												.getItem(position);
									}

									public void onNothingSelected(
											AdapterView<?> arg0) {
									}
								});

						final LinearLayout layoutJmlSatuan = new LinearLayout(
								MenuCekKulkas.this);
						layoutJmlSatuan.setOrientation(LinearLayout.HORIZONTAL);
						layoutJmlSatuan.addView(jumlahBahan);
						layoutJmlSatuan.addView(spinnerSatuan);

						final LinearLayout layoutTambahBahan = new LinearLayout(
								MenuCekKulkas.this);
						layoutTambahBahan.setOrientation(LinearLayout.VERTICAL);
						layoutTambahBahan.setPadding(6, 0, 6, 0);
						layoutTambahBahan.addView(namaBahan);
						layoutTambahBahan.addView(layoutJmlSatuan);

						AlertDialog.Builder alertTambah = new AlertDialog.Builder(
								MenuCekKulkas.this);
						alertTambah.setTitle("Tambah Bahan");
						alertTambah.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										String nama = namaBahan.getText() + "";
										String jumlah = jumlahBahan.getText()
												+ "";
										String validation = validateForm(nama,
												jumlah, tempSatuan,
												MODE_ADD_BAHAN);

										if (!validation.equals("")) {
											Toast.makeText(MenuCekKulkas.this,
													validation,
													Toast.LENGTH_SHORT).show();
										} else if (!cik.tambahBahan(nama,
												Float.parseFloat(jumlah),
												tempSatuan)) {
											Toast.makeText(MenuCekKulkas.this,
													"Gagal simpan ke database",
													Toast.LENGTH_SHORT).show();
										} else {
											isiKulkasAdapter.add(new Bahan(
													nama,
													Float.parseFloat(jumlah),
													tempSatuan));
											Toast.makeText(
													MenuCekKulkas.this,
													"Bahan berhasil ditambahkan",
													Toast.LENGTH_SHORT).show();

											((TextView)findViewById(R.id.notiflistkosong)).setVisibility(View.GONE);
											((ListView)findViewById(R.id.listbahan)).setVisibility(View.VISIBLE);
										}
									}
								});
						alertTambah.setNegativeButton("Batal",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
						alertTambah.setView(layoutTambahBahan);
						alertTambah.show();
					}
				});
	}

	private String validateForm(String nama, String jumlah, String satuan,
			int mode) {
		String ret = "";

		if (mode == MODE_ADD_BAHAN)
			if (nama.equals(""))
				ret += "\nNama belum diisi";
			else if (!cdr.isBahanExist(nama))
				ret += "\n" + nama + " tidak ada di database kami";
			else if (cik.adaDiKulkas(nama))
				ret += "\n" + nama + " sudah ada di kulkas";
			else {
				if (satuan.equals(""))
					ret += "\nSatuan belum ditentukan";
				else if (!cik.getMultiSatuan(nama).contains(satuan))
					ret += "\n" + satuan + " bukan satuan yang valid untuk "
							+ nama;
			}

		if (jumlah.equals(""))
			ret += "\nJumlah belum diisi";

		return ret.trim();
	}

	/**
	 * inner class untuk adapter daftar resep favorit (custom adapter)
	 * 
	 * @author Team Before
	 */
	private class IsiKulkasAdapter extends ArrayAdapter<Bahan> implements
			Filterable {
		List<Bahan> bList;
		private List<Bahan> emptyList = new ArrayList<Bahan>(0);
		private final Object bLock = new Object();
		private BahanFilter bFilter;

		public IsiKulkasAdapter(Context context, List<Bahan> bList) {
			super(context, R.layout.isikulkas, bList);
			this.bList = bList;
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount() {
			return bList.size();
		}

		@Override
		public Bahan getItem(int position) {
			return bList.get(position);
		}

		@Override
		public void add(Bahan bahan) {
			// TODO Auto-generated method stub
			bList.add(bahan);
			notifyDataSetChanged();
		}

		@Override
		public void remove(Bahan bahan) {
			// TODO Auto-generated method stub
			bList.remove(bahan);
			notifyDataSetChanged();
		}

		public void updateJumlah(int position, float jumlah) {
			getItem(position).setJumlah(jumlah);
			notifyDataSetChanged();
		}

		public void updateSatuan(int position, String satuan) {
			getItem(position).setSatuan(satuan);
			notifyDataSetChanged();
		}

		public int getPosition(String nama) {
			int position = 0;
			for (int i = 0; i < bList.size(); i++) {
				if (bList.get(i).getNama().equalsIgnoreCase(nama)) {
					position = i;
					break;
				}
			}
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolderIsiKulkas holder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(
						R.layout.isikulkas, parent, false);
				holder = new ViewHolderIsiKulkas();
				holder.teksNama = (TextView) view.findViewById(R.id.namabahan);
				holder.teksJumlah = (TextView) view
						.findViewById(R.id.jumlahbahan);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolderIsiKulkas) view.getTag();
			}
			float jumlah = bList.get(position).getJumlah();
			String jmlStr = "";
			if (jumlah % 1.0 == 0.0) {
				jmlStr += (int) bList.get(position).getJumlah();
			} else {
				jmlStr += bList.get(position).getJumlah();
			}
			holder.teksNama.setText(bList.get(position).getNama());
			holder.teksJumlah.setText(jmlStr + " "
					+ bList.get(position).getSatuan());
			return view;
		}

		public Filter getFilter() {
			if (bFilter == null) {
				bFilter = new BahanFilter();
			}
			return bFilter;
		}

		/**
		 * Inner class untuk filter
		 * 
		 * @author Team Before
		 */
		private class BahanFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				// TODO Auto-generated method stub
				FilterResults results = new FilterResults();
				if (listBahan == null) {
					synchronized (bLock) {
						listBahan = new ArrayList<Bahan>(bList);
					}
				}
				if (prefix == null || prefix.length() == 0) {
					synchronized (bLock) {
						results.values = listBahan;
						results.count = listBahan.size();
					}
				} else {
					String prefixString = prefix.toString().toLowerCase();
					final ArrayList<Bahan> values = (ArrayList<Bahan>) listBahan;
					final int count = values.size();
					final ArrayList<Bahan> newValues = new ArrayList<Bahan>(
							count);
					for (int i = 0; i < count; i++) {
						final Bahan value = values.get(i);
						final String valueText = value.getNama().toLowerCase();
						if (valueText.startsWith(prefixString)) {
							newValues.add(value);
						}
					}
					results.values = newValues;
					results.count = newValues.size();
				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// TODO Auto-generated method stub
				if (results.count > 0) {
					bList = (List<Bahan>) results.values;
					tempList = bList;
					notifyDataSetChanged();
				} else {
					bList = emptyList;
					notifyDataSetInvalidated();
				}

			}

		}
	}

	static class ViewHolderIsiKulkas {
		TextView teksNama;
		TextView teksJumlah;
	}
}
