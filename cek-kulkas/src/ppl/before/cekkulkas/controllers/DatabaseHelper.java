package ppl.before.cekkulkas.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	/** path database */
    private static final String DATABASE_PATH = "/data/data/ppl.before.cekkulkas/databases/";
    
    /** nama file database */
    private static final String DATABASE_NAME = "cekkulkas_db.db";
    
    /** versi dari database */
    private static final int SCHEMA_VERSION = 1;
    
    private static DatabaseHelper dbHelper;
    
    public SQLiteDatabase db;
    
    private Context myContext;
    
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
		this.myContext = context;
	}
	
	/**
     * membuat database, sebelumnya dicek dulu apakah sudah ada/belum
     */
    public void createDatabase() {
    	boolean dbExist = isDBExists();
    	if (!dbExist) {
    		db = this.getReadableDatabase();
    		copyDBFromResource();
    	}
    }
    
    public static synchronized DatabaseHelper getHelper(Context context) {
    	if (dbHelper == null) {
    		dbHelper = new DatabaseHelper(context);
    	}
    	return dbHelper;
    }
    
    /**
     * mengecek eksistensi database
     * @return true: database sudah ada; false: database belum ada
     */
    private boolean isDBExists() {
    	SQLiteDatabase db = null;
    	try {
    		String databasePath = DATABASE_PATH + DATABASE_NAME;
    		db = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
    		db.setLocale(Locale.getDefault());
    		db.setLockingEnabled(true);
    		db.setVersion(1);
    	} catch (SQLException e) {
    		Log.e("SqlHelper", "database not found");
    	}
    	
    	if (db != null) {
    		db.close();
    	}
    	return db != null ? true : false;
    }
    
    /**
     * menyalin file database dari assets ke data aplikasi
     */
    private void copyDBFromResource() {
    	InputStream inputStream = null;
    	OutputStream outStream = null;
    	String dbFilePath = DATABASE_PATH + DATABASE_NAME;
    	try {
    		inputStream = myContext.getAssets().open(DATABASE_NAME);
    		
    		outStream = new FileOutputStream(dbFilePath);
    		
    		byte[] buffer = new byte[1024];
    		int length;
    		while ((length = inputStream.read(buffer)) > 0) {
    			outStream.write(buffer,0,length);
    		}
    		outStream.flush();
    		outStream.close();
    		inputStream.close();
    	} catch (IOException e) {
    		throw new Error("Problem copying database from resource file.");
    	}
    }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
