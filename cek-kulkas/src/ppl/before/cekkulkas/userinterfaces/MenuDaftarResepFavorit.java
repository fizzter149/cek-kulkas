package ppl.before.cekkulkas.userinterfaces;

import java.util.ArrayList;
import java.util.List;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.ControllerDaftarResep;
import ppl.before.cekkulkas.models.Bahan;
import ppl.before.cekkulkas.models.Resep;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * class view untuk halaman daftar resep favorit
 * menampilkan daftar resep yang ada dalam daftar resep favorit
 * 
 * @author Team Before
 */
public class MenuDaftarResepFavorit extends Activity {
	
	/** controller daftar resep untuk membantu akses database */
	private ControllerDaftarResep cdf = new ControllerDaftarResep(this);
	
	/** daftar resep favorit */
	private List<Resep> listResep;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // title bar aplikasi
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.daftarresepfavorit);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        
        // inisialisasi isi setiap elemen di view
        initView();
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
	}
	
	
	/**
	 * inisialisasi isi list daftar resep favorit
	 */
	private void initView(){
		// ambil resep favorit dari database
		listResep = cdf.getFavorite(1);
		final ArrayList<Bahan> listBahanKosong = new ArrayList<Bahan>(0);
        ListView lv = (ListView) findViewById(R.id.listresepfavorit);
        lv.setAdapter(new DaftarResepFavoritAdapter(this, listResep));
        
        // listener untuk item di list, pergi ke halaman detil resep tersebut
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        			Intent intentDetailResep = new Intent(MenuDaftarResepFavorit.this, MenuDetailResep.class);
        			// sertakan objek resep yang dipilih sebagai extra
        			Bundle b = new Bundle();
        			b.putSerializable("resep", listResep.get(position));
        			b.putSerializable("listBahan", listBahanKosong);
        			intentDetailResep.putExtras(b);
        			startActivity(intentDetailResep);
        		}
		
        });
	}

	/**
	 * inner class untuk adapter daftar resep favorit (custom adapter)
	 * 
	 * @author Team Before
	 */
	private class DaftarResepFavoritAdapter extends ArrayAdapter<Resep> {
		List<Resep> listResep;
		public DaftarResepFavoritAdapter(Context context, List<Resep> listResep) {
			super(context, R.layout.resepfavorit, listResep);
			this.listResep = listResep;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView (int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolderDaftarFavorit holder;
			if (convertView == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.resepfavorit, parent, false);
				holder = new ViewHolderDaftarFavorit();
				holder.teksNama = (TextView) view.findViewById(R.id.namaresepfavorit);
				holder.teksKategori = (TextView) view.findViewById(R.id.kategoriresepfavorit);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolderDaftarFavorit) view.getTag();
			}
			holder.teksNama.setText(listResep.get(position).getNama());
			holder.teksKategori.setText(listResep.get(position).getKategori());
			return view;
		}
	}

	static class ViewHolderDaftarFavorit {
		TextView teksNama;
		TextView teksKategori;
	}
	
}
