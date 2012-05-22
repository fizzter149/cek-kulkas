package ppl.before.cekkulkas.userinterfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.ControllerDaftarResep;
import ppl.before.cekkulkas.controllers.ControllerIsiKulkas;
import ppl.before.cekkulkas.models.Resep;
import ppl.before.cekkulkas.models.Bahan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * class view untuk halaman daftar resep (hasil pencarian berdasarkan bahan)
 * @author Team Before
 */
public class MenuDaftarResep extends Activity {
	
	/** controller daftar resep untuk membantu akses database */
	private ControllerDaftarResep cdr = new ControllerDaftarResep(MenuDaftarResep.this);
	
	/** controller untuk membantu akses ke database isi kulkas */
	private ControllerIsiKulkas cik = new ControllerIsiKulkas(this);
	
	/** list resep hasil pencarian */
	private ArrayList<Resep> listResep;
	
	/** list resep setelah difilter berdasarkan kategori */
	private ArrayList<Resep> tempList;
	
	/** list bahan yang mendasari pencarian */
	private ArrayList<Bahan> listBahan;
	
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // title bar aplikasi
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.pilihbahan_2);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        
        // mengambil informasi list bahan yang dijadikan dasar pencarian resep dari extras
        listBahan = (ArrayList<Bahan>) getIntent().getSerializableExtra("listBahan");
        
        // inisialisasi isi setiap elemen dari view
        initView();
    }
	
	
	/**
	 * mengassign isi setiap elemen dari view
	 */
	private void initView() {

		// jika list bahannya kosong, tampilkan semua resep
		if (listBahan.size() == 0) {
			listResep = (ArrayList<Resep>)cdr.getFavorite(0);
		// jika tidak kosong, cari resep berdasarkan bahan yang diberikan
		} else {
			listResep = cdr.findResep(listBahan);
		}
		
		// list nama bahan yang dipilih
		List<String> listBahanCocok = new ArrayList<String>();
		for (Bahan b: listBahan) {
			listBahanCocok.add(b.getNama());
		}
		for (int i = 0; i < listResep.size(); i++) {
			int jumlahBahanCocok = 0;
			int jumlahKurangBahan = 0;
			for (Bahan b: listResep.get(i).getListBahan()) {
				// hitung kecocokan bahan tiap resep
				if (listBahanCocok.contains(b.getNama())) {
					jumlahBahanCocok++;
				}
				listResep.get(i).setJumlahBahanCocok(jumlahBahanCocok);
				
				// hitung kurang bahan tiap resep
				if (!cik.contains(b.getNama())) {
					jumlahKurangBahan++;
				}
				listResep.get(i).setJumlahKurangBahan(jumlahKurangBahan);
			}
		}
		
		// Sorting berdasarkan jumlah bahan yang sesuai
		Collections.sort(listResep, new Comparator<Resep>() {			
			public int compare(Resep r1, Resep r2) {
				int comparison = 0;
				int b1 = r1.getJumlahBahanCocok();
				int b2 = r2.getJumlahBahanCocok();
				if (b2 == b1) {
					int k1 = r1.getJumlahKurangBahan();
					int k2 = r2.getJumlahKurangBahan();
					if (k1 == k2) {
						comparison = r1.getNama().compareToIgnoreCase(r2.getNama());
					} else {
						comparison = k1 - k2;
					}
				} else {
					comparison = b2 - b1;
				}
				return comparison;
			}
			
		});
		// awalnya, list resep hasil filter adalah list resep itu sendiri
		tempList = listResep;
		
		// assign isi view daftar resep 
		ListView lv = (ListView) findViewById(R.id.daftarResep);
        final DaftarResepAdapter daftarResepAdapter = new DaftarResepAdapter(this, tempList);
        lv.setAdapter(daftarResepAdapter);
        
        // listener untuk item di daftar resep
        // jika diklik, pergi ke halaman detil resep tersebut
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        			Intent intentDetailResep = new Intent(MenuDaftarResep.this, MenuDetailResep.class);
        			// sertakan resep yang dipilih sebagai extra
        			Bundle b = new Bundle();
        			b.putSerializable("resep", tempList.get(position));
        			b.putSerializable("listBahan", listBahan);
        			intentDetailResep.putExtras(b);
        			startActivity(intentDetailResep);
        		}
        });
        
        // assign spinner kategori sebagai filter
        final Spinner spinnerKategori = (Spinner) findViewById(R.id.spinnerKategori);
        ArrayList<String> listKategori = new ArrayList<String>();
        listKategori.add("Semua");
        for (Resep resep: listResep) {
        	if(!listKategori.contains(resep.getKategori().trim())) {
        		listKategori.add(resep.getKategori().trim());
        	}
        }
        ArrayAdapter<String> adapterKategori = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listKategori);
        adapterKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapterKategori);
        
        // listener untuk item spinner kategori
        // jika dipilih, terapkan filter pada daftar resep
        spinnerKategori.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long duration) {
				// TODO Auto-generated method stub
				daftarResepAdapter.getFilter().filter((CharSequence) spinnerKategori.getSelectedItem());
				daftarResepAdapter.notifyDataSetChanged();
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	
	/**
	 * inner class untuk adapter daftar resep (custom adapter)
	 * @author Team Before
	 */
	private class DaftarResepAdapter extends ArrayAdapter<Resep> implements Filterable {
		private List<Resep> rList;
		private final Object rLock = new Object();
		private ResepFilter resepFilter;
		
		public DaftarResepAdapter(Context context, List<Resep> rList) {
			super(context, R.layout.resep, rList);
			this.rList = rList;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public int getCount() {
			return rList.size();
		}

	    @Override
	    public Resep getItem(int position) {
	        return rList.get(position);
	    }
	    
		@Override
		public View getView (int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolderDaftarResep holder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.resep, parent, false);
				holder = new ViewHolderDaftarResep();
				holder.teksNama = (TextView) view.findViewById(R.id.namaresep);
				holder.teksKategori = (TextView) view.findViewById(R.id.kategoriresep);
				holder.teksKeterangan = (TextView) view.findViewById(R.id.keteranganresep);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolderDaftarResep) view.getTag();
			}
			holder.teksNama.setText(rList.get(position).getNama());
			holder.teksKategori.setText(rList.get(position).getKategori());
			// jumlah kecocokan bahan
			int jumlahCocok = rList.get(position).getJumlahBahanCocok();
			// jumlah bahan yang belum ada di kulkas
			int jumlahKurang = rList.get(position).getJumlahKurangBahan();
			String keterangan = "";
			if (jumlahCocok > 0) {
				keterangan = "<font color='#A4C639'>" + jumlahCocok + "</font> bahan cocok\t|";
			}
			if (jumlahKurang > 0) {
				keterangan += "\t<font color='#FF6A6A'>kurang " + jumlahKurang +" bahan</font>";
			} else {
				keterangan += "\t<font color='#A4C639'>semua bahan lengkap</font>";
			}
			holder.teksKeterangan.setText(Html.fromHtml(keterangan));
			return view;
		}
		
		public Filter getFilter() {
			if (resepFilter == null) {
				resepFilter = new ResepFilter();
			}
			return resepFilter;
		}

		private class ResepFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				// TODO Auto-generated method stub
				FilterResults results = new FilterResults();
				if (listResep == null) {
					synchronized (rLock) {
						listResep = new ArrayList<Resep>(rList);
					}
				}
				if (prefix == null || prefix.length() == 0) {
					synchronized (rLock) {
						results.values = listResep;
						results.count = listResep.size();
					}
				} else {
					String prefixString = prefix.toString().toLowerCase();
					final ArrayList<Resep> values = (ArrayList<Resep>) listResep;
					final int count = values.size();
					final ArrayList<Resep> newValues = new ArrayList<Resep>(count);
					
					for (int i = 0; i < count; i++) {
						final Resep value = values.get(i);
						final String valueText = value.getKategori().toLowerCase().trim();
						if (valueText.equals(prefixString)) {
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
					rList = (List<Resep>) results.values;
					tempList = (ArrayList<Resep>) rList;
					notifyDataSetChanged();
				} else {
					rList = listResep;
					notifyDataSetInvalidated();
				}
				
			}
			
		}
	}

	static class ViewHolderDaftarResep {
		TextView teksNama;
		TextView teksKategori;
		TextView teksKeterangan;
	}
	
	
	@Override
	/**
	 * jika view ini diresume, insialisasi kembali isi dari setiap elemen di view.
	 */
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}
}
