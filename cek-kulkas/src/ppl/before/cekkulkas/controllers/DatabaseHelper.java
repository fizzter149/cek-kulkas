package ppl.before.cekkulkas.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	/** path database */
    private static final String DATABASE_PATH = "/data/data/ppl.before.cekkulkas/databases/";
    
    /** nama file database */
    private static final String DATABASE_NAME = "cekkulkas_db.db";
    
    /** versi dari database */
    private static final int SCHEMA_VERSION = 1;
    
    private static DatabaseHelper dbHelper;
    
    private SQLiteDatabase db;
    
    private Context myContext;
    
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
		this.myContext = context;
		createDatabase();
	}
    
    public static synchronized DatabaseHelper getHelper(Context context) {
    	if (dbHelper == null) {
    		dbHelper = new DatabaseHelper(context);
    	}
    	return dbHelper;
    }
    
	@Override
	public void onCreate(SQLiteDatabase arg0) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
	
	public Cursor query(String sql) {
		return db.rawQuery(sql, null);
	}
	
	public boolean insert(String table, ContentValues values) {
		long rowid = db.insert(table, null, values);
		if (rowid >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update(String table, ContentValues values, String whereClause) {
		long rowid = db.update(table, values, whereClause, null);
		if (rowid >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delete(String table, String whereClause) {
		db.delete(table, whereClause, null);
		return true;
	}
	
	/**
     * membuat database, sebelumnya dicek dulu apakah sudah ada/belum
     */
    private void createDatabase() {
    	boolean dbExist = isDBExists();
    	if (!dbExist) {
    		db = this.getReadableDatabase();
    		copyDBFromResource();
    	}
    	openDatabase();
    }
    
	private void openDatabase() {
		db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
		db.rawQuery("PRAGMA foreign_keys = ON", null);
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
    	} catch (SQLException e) {
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
    	}
    }
}
