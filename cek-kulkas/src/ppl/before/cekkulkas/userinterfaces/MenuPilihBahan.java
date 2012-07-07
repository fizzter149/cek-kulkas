package ppl.before.cekkulkas.userinterfaces;

import java.util.ArrayList;
import java.util.List;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.ControllerIsiKulkas;
import ppl.before.cekkulkas.models.Bahan;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

/**
 * class view untuk halaman pilih bahan
 * 
 * @author Team Before
 */
public class MenuPilihBahan extends Activity {
	
	/** controller untuk membantu akses ke database isi kulkas */
	private ControllerIsiKulkas cik = new ControllerIsiKulkas();
	
	/** list bahan dari database isi kulkas */
	private List<Bahan> listBahan = cik.ambilSemuaBahan();
	
	/** list bahan setelah difilter */
	private List<Bahan> tempList = listBahan;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // title bar aplikasi
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.pilihbahan);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        
        findViewById(R.id.llpb).requestFocus();
        
        // adapter untuk item dari daftar bahan
        final ArrayAdapter<Bahan> bahanAdapter = new PilihBahanAdapter(this, tempList);
        
        // filter untuk daftar bahan yang di-"embed" ke text field
        EditText filter = (EditText) findViewById(R.id.filterpilihbahan);
        TextWatcher filterTextWatcher = new TextWatcher() {
        	
        	public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
        		// setiap isi text field berubah, terapkan filter ke adapter daftar bahan
                bahanAdapter.getFilter().filter(s);
            }
        	
        	public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {}
        };
        
        // "embed" textwatcher ke text field filter
        filter.addTextChangedListener(filterTextWatcher);
        
        // inisialisasi daftar bahan
        ListView lv = (ListView) findViewById(R.id.listpilihbahan);
        lv.setAdapter(bahanAdapter);
        lv.setTextFilterEnabled(true);
        
        // listener untuk item daftar bahan, set bahan tersebut menjadi terpilih
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        		tempList.get(position).setSelected(!tempList.get(position).isSelected());
        		bahanAdapter.notifyDataSetChanged();
        		}
        });
        lv.setItemsCanFocus(true);
        
        
        // listener untuk tombol cari resep
        ((Button)findViewById(R.id.cariResep)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				ArrayList<Bahan> listBahanSelected = new ArrayList<Bahan>();
				
				// menambahkan bahan yang telah dipilih ke list
				for(Bahan bahan: listBahan){
					if(bahan.isSelected()){
						listBahanSelected.add(bahan);
					}
				}

				// pergi ke halaman daftar resep, sertakan objek list bahan yang telah dipilih
				Intent i = new Intent(MenuPilihBahan.this, MenuDaftarResep.class);
    			Bundle b = new Bundle();
    			b.putSerializable("listBahan", listBahanSelected);
    			i.putExtras(b);
    			startActivity(i);
			}
		});
    }
    
    
    /**
     * Adapter untuk list bahan
     * 
     * @author Team Before
     */
	private class PilihBahanAdapter extends ArrayAdapter<Bahan> implements Filterable {
		private List<Bahan> bList;
		private List<Bahan> emptyList = new ArrayList<Bahan>(0);
		private final Object bLock = new Object();
		private BahanFilter bFilter;
		public PilihBahanAdapter(Context context, List<Bahan> bList) {
			super(context, R.layout.bahanpilihbahan, bList);
			this.bList = bList;
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
		public View getView (int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolderPilihBahan holder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.bahanpilihbahan, parent, false);
				holder = new ViewHolderPilihBahan();
				holder.teksNama = (TextView) view.findViewById(R.id.labelbahan);
				holder.checkbox = (CheckBox) view.findViewById(R.id.checkpilihbahan);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolderPilihBahan) view.getTag();
			}
			holder.teksNama.setText(bList.get(position).getNama());
			holder.checkbox.setChecked(bList.get(position).isSelected());
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
					final ArrayList<Bahan> newValues = new ArrayList<Bahan>(count);
					for (int i = 0; i < count; i++) {
						final Bahan value = values.get(i);
						final String valueText = value.getNama().toLowerCase();
						if (valueText.contains(prefixString)) {
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

	/**
	 * inner class untuk adapter bahan
	 * @author Team Before
	 */
	static class ViewHolderPilihBahan {
		TextView teksNama;
		CheckBox checkbox;
	}
}
