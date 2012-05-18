package ppl.before.cekkulkas.controllers;

import java.util.ArrayList;
import java.util.List;

import ppl.before.cekkulkas.models.Bahan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * CAUTION: NOT IMPLEMENTED YET
 * <p>Controller untuk daftar isi kulkas yang berurusan dengan database dan menghubungkan
 * antara View dan Model.</p>
 * @author Team Before
 *
 */
public class ControllerIsiKulkas extends SQLiteOpenHelper {
	
	/** Konstanta database path */
	private static final String DATABASE_PATH = "/data/data/ppl.before.cekkulkas/databases/";
	
	/** Konstanta database name */
    private static final String DATABASE_NAME = "cekkulkas_db.db";
    
    /** Konstanta schema version */
    private static final int SCHEMA_VERSION = 1;
    
    /** Database SQLite */
    public SQLiteDatabase db;
	
	/**
	 * Constructor controller isi kulkas
	 * @param context Context
	 */
	public ControllerIsiKulkas(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Menambah bahan
	 * @param nama Nama bahan
	 * @param jumlah Jumlah bahan
	 * @param satuan Satuan bahan
	 * @return status
	 */
	public boolean add(String nama, float jumlah, String satuan) {
		openDatabase(false);
		ContentValues bValues = new ContentValues();
		bValues.put("nama", nama);
		bValues.put("jumlah", jumlah);
		bValues.put("satuan", satuan);
		long status = db.insert("isikulkas", null, bValues);
		bValues.clear();
		db.close();
		if (status >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Mengganti jumlah bahan
	 * @param nama Nama bahan
	 * @param newJumlah Jumlah baru bahan
	 * @return status
	 */
	public boolean setJumlah(String nama, float newJumlah){
		openDatabase(false);
		ContentValues bValues = new ContentValues();
		bValues.put("jumlah", newJumlah);
		db.update("isikulkas", bValues, "nama='"+nama+"'", null);
		bValues.clear();
		db.close();
		return true;
	}
	
	public boolean setSatuan(String nama, String newSatuan) {
		openDatabase(false);
		ContentValues bValues = new ContentValues();
		bValues.put("satuan", newSatuan);
		db.update("isikulkas", bValues, "nama='"+nama+"'", null);
		bValues.clear();
		db.close();
		return true;
	}
	
	/**
	 * Menghapus bahan
	 * @param nama Nama Bahan
	 * @return status
	 */
	public boolean delete(String nama) {
		openDatabase(false);
		db.delete("isikulkas", "nama='"+nama+"'", null);
		db.close();
		return true;
	}
	
	public boolean contains(String nama) {
		openDatabase(true);
		boolean ada;
		Cursor cek = db.rawQuery("SELECT * FROM isikulkas WHERE nama='" + nama + "'", null);
		if (cek.getCount() > 0) {
			ada = true;
		} else {
			ada = false;
		}
		cek.close();
		db.close();
		return ada;
	}
	
	/**
	 * Menggambil semua bahan yang ada di kulkas
	 * @return Daftar bahan
	 */
	public List<Bahan> get(){
		openDatabase(true);
		List<Bahan> listBahan = new ArrayList<Bahan>();
		Cursor cursorBahan = db.rawQuery("SELECT * FROM isikulkas", null);
		cursorBahan.moveToFirst();
		while (!cursorBahan.isAfterLast()) {
			Bahan newBahan = new Bahan(cursorBahan.getString(1), cursorBahan.getFloat(2), cursorBahan.getString(3));
			listBahan.add(newBahan);
			cursorBahan.moveToNext();
		}
		cursorBahan.close();
		db.close();
		
		return listBahan;
	}
	
	public float getJumlah(String nama) {
		float jumlah;
		openDatabase(true);
		Cursor cursorBahan = db.rawQuery("SELECT jumlah FROM isikulkas WHERE nama='" + nama + "'", null);
		cursorBahan.moveToFirst();
		jumlah = cursorBahan.getFloat(0);
		cursorBahan.close();
		db.close();
		return jumlah;
	}
	
	public List<String> getSatuan(String nama) {
		openDatabase(true);
		List<String> listSatuan = new ArrayList<String>();
		Cursor cekMultiSatuan = db.rawQuery("SELECT * FROM konversi WHERE nama='" + nama + "'", null);
		if (cekMultiSatuan.getCount() > 0) {
			Cursor cursorSatuan1 = db.rawQuery("SELECT DISTINCT satuan1 FROM konversi WHERE nama='" + nama + "'", null);
			cursorSatuan1.moveToFirst();
			listSatuan.add(cursorSatuan1.getString(0));
			cursorSatuan1.close();
			Cursor cursorSatuan2 = db.rawQuery("SELECT * FROM konversi WHERE nama='" + nama + "'", null);
			cursorSatuan2.moveToFirst();
			while (!cursorSatuan2.isAfterLast()) {
				String satuan = cursorSatuan2.getString(3);
				listSatuan.add(satuan);
				cursorSatuan2.moveToNext();
			}
			cursorSatuan2.close();
		} else {
			Cursor cursorSatuan = db.rawQuery("SELECT satuan FROM bahan WHERE nama='" + nama + "'", null);
			cursorSatuan.moveToFirst();
			listSatuan.add(cursorSatuan.getString(0));
			cursorSatuan.close();
		}
		cekMultiSatuan.close();
		db.close();
		return listSatuan;
	}
	
	public float convertSatuan(String nama, String satuanTujuan) {
		openDatabase(true);
		
		return 0;
		
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	/**
	 * Membuka database
	 * @param isReadOnly Kalau buat get: true, kalau buat add/remove/modify: readwrite
	 */
	private void openDatabase(boolean isReadOnly) {
    	String myPath = DATABASE_PATH + DATABASE_NAME;
    	
    	if(isReadOnly){
    		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	} else {
    		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	}
	}
}
