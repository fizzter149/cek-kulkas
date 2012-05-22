package ppl.before.cekkulkas.userinterfaces;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import ppl.before.cekkulkas.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
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
    
    
    /**
     * inner class untuk membantu akses database
     * melakukan pengecekan eksistensi database dan membuatnya jika belum ada
     * 
     * @author Team Before
     */
    private class DatabaseHelper extends SQLiteOpenHelper{
   	 
    	/** path database */
        private static final String DATABASE_PATH = "/data/data/ppl.before.cekkulkas/databases/";
        
        /** nama file database */
        private static final String DATABASE_NAME = "cekkulkas_db.db";
        
        /** versi dari database */
        private static final int SCHEMA_VERSION = 1;
        
        /** context */
        private final Context myContext;
        
        
        private DatabaseHelper(Context context){
        	super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        	this.myContext = context;
        }
        
        /**
         * membuat database, sebelumnya dicek dulu apakah sudah ada/belum
         */
        private void createDatabase(){
        	boolean dbExist = DBExists();
        	
        	if(!dbExist){
        		this.getReadableDatabase();
        		copyDBFromResource();
        	}
        }
        
        /**
         * mengecek eksistensi database
         * @return true: database sudah ada; false: database belum ada
         */
        private boolean DBExists(){
        	SQLiteDatabase db = null;
        	try{
        		String databasePath = DATABASE_PATH + DATABASE_NAME;
        		db = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
        		db.setLocale(Locale.getDefault());
        		db.setLockingEnabled(true);
        		db.setVersion(1);
        	} catch (SQLException e){
        		Log.e("SqlHelper", "database not found");
        	}
        	
        	if(db!=null){
        		db.close();
        	}
        	return db != null ? true : false;
        }
        
        /**
         * menyalin file database dari assets ke data aplikasi
         */
        private void copyDBFromResource(){
        	InputStream inputStream = null;
        	OutputStream outStream = null;
        	String dbFilePath = DATABASE_PATH + DATABASE_NAME;
        	
        	try {
        		inputStream = myContext.getAssets().open(DATABASE_NAME);
        		
        		outStream = new FileOutputStream(dbFilePath);
        		
        		byte[] buffer = new byte[1024];
        		int length;
        		while((length = inputStream.read(buffer)) > 0){
        			outStream.write(buffer,0,length);
        		}
        		
        		outStream.flush();
        		outStream.close();
        		inputStream.close();
        	} catch (IOException e){
        		throw new Error("Problem copying database from resource file.");
        	}
        }

    	@Override
    	public void onCreate(SQLiteDatabase arg0) {}
    	@Override
    	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
     
    }
}