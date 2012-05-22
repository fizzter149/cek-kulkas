package ppl.before.cekkulkas.userinterfaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ppl.before.cekkulkas.R;
import ppl.before.cekkulkas.controllers.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * class view untuk halaman menu utama
 * @author Team Before
 */
public class MenuUtama extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // title bar aplikasi
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.menuutama);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        
        // membuat database saat aplikasi pertama kali dijalankan (baru install)
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.createDatabase();
        dbHelper.close();
        
        copyPhoto();
        
        // listener untuk tombol cek kulkas
        ((Button) findViewById(R.id.tombolCekKulkas)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				Intent intentCekKulkas = new Intent(v.getContext(), MenuCekKulkas.class);
				startActivity(intentCekKulkas);
			}
		});
        
        // listener untuk tombol pilih bahan
        ((Button) findViewById(R.id.tombolPilihBahan)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
				Intent intentPilihBahan = new Intent(v.getContext(), MenuPilihBahan.class);
				startActivity(intentPilihBahan);
			}
		});
        

        // listener untuk tombol daftar resep favorit
        ((Button) findViewById(R.id.tombolDaftarResepFavorit)).setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	Intent intentDaftarResepFavorit = new Intent(v.getContext(), MenuDaftarResepFavorit.class);
	        	startActivity(intentDaftarResepFavorit);
	        }
        });

        // listener untuk tombol buat resep baru
        ((Button) findViewById(R.id.tombolBuatResepBaru)).setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Intent intentBuatResepBaru = new Intent(v.getContext(), MenuBuatResepBaru.class);
        		startActivity(intentBuatResepBaru);
        	}
        });
    }
    
    private void copyPhoto() {
    	String path = "/data/data/ppl.before.cekkulkas/";
    	if (!(new File(path+"r0.jpg")).exists()) {
    		InputStream is = null;
    		OutputStream os = null;
    		try {
    			AssetManager am = getAssets();
    			String[] listFoto = am.list("fotoresep");
    			for(String nama: listFoto){
    				is = am.open("fotoresep/" + nama);
    				os = new FileOutputStream(path + nama);
    				byte[] buffer = new byte[1024];
    	    		int length;
    	    		while ((length = is.read(buffer)) > 0) {
    	    			os.write(buffer,0,length);
    	    		}
    	    		os.flush();
    	    		os.close();
    	    		is.close();
    			}
    		} catch (IOException e) {
    		}
    	}
    }
}