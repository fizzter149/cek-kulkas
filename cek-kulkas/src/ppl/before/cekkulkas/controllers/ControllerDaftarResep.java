package ppl.before.cekkulkas.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ppl.before.cekkulkas.models.Bahan;
import ppl.before.cekkulkas.models.Resep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * <p>Controller untuk daftar resep yang berurusan dengan database dan menghubungkan
 * antara View dan Model.</p>
 * @author Team Before
 *
 */
public class ControllerDaftarResep extends SQLiteOpenHelper {
	
	/** Konstanta database path */
	private static final String DATABASE_PATH = "/data/data/ppl.before.cekkulkas/databases/";
	
	/** Konstanta database name */
    private static final String DATABASE_NAME = "cekkulkas_db.db";
    
    /** Konstanta schema version */
    private static final int SCHEMA_VERSION = 1;
    
    /** Database SQLite */
    public SQLiteDatabase db;
    
	/**
	 * Constructor controller daftar resep
	 * @param context Context
	 */
	public ControllerDaftarResep(Context context){
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	
	/**
	 * Menambah atau membuat resep baru
	 * @param nama Nama resep
	 * @param deskripsi Deskripsi resep
	 * @param listBahan Bahan-bahan yang digunakan
	 * @param langkah Langkah memasak
	 * @param kategori Kategori resep
	 * @param foto Foto resep
	 * @return status
	 */
	public boolean addResep(String nama, String deskripsi, List<Bahan> listBahan, String langkah, String kategori, String foto){
		openDatabase(false);
		
		Cursor cursorCekNamaSudahAda = db.rawQuery("SELECT nama FROM resep WHERE nama='"+nama+"'", null);
		cursorCekNamaSudahAda.moveToFirst();
		
		if(cursorCekNamaSudahAda.getCount() > 0) {
			cursorCekNamaSudahAda.close();
			db.close(); 
			return false;
		}
		cursorCekNamaSudahAda.close(); 
		
		ContentValues rValues = new ContentValues();
		rValues.put("nama", nama);
		rValues.put("deskripsi", deskripsi);
		rValues.put("langkah", langkah);
		rValues.put("kategori", kategori);
		rValues.put("foto", foto);
		db.insert("resep", null, rValues);
		Cursor cursorAssignKodeResep = db.rawQuery("SELECT _id FROM resep WHERE nama='"+nama+"'", null);
		cursorAssignKodeResep.moveToFirst();
		int kodeResep = cursorAssignKodeResep.getInt(0);
		
		
		ContentValues bValues = new ContentValues();
		for (Bahan bahan : listBahan) {
			bValues.put("koderesep", kodeResep);
			bValues.put("nama", bahan.getNama());
			bValues.put("jumlah", bahan.getJumlah());
			bValues.put("satuan", bahan.getSatuan());
			db.insert("bahan", null, bValues);
			bValues.clear();
		}
		
		cursorAssignKodeResep.close();
		
		db.close();
		return true;
	}
	
	/**
	 * Mendapatkan resep berdasarkan nama
	 * @param nama Nama resep
	 * @return Resep
	 */
	public Resep getResep(String nama) {
		openDatabase(true);
		Resep resep = null;
		
		Cursor cursor = db.rawQuery("SELECT * FROM resep WHERE nama='"+nama+"'", null);
		cursor.moveToFirst();
		
		if (!cursor.isBeforeFirst()) {
			Cursor cursor2 = db.rawQuery("SELECT * FROM bahan WHERE koderesep="+cursor.getInt(0), null);
			cursor2.moveToFirst();
			ArrayList<Bahan> listBahan = new ArrayList<Bahan>();
			while (!cursor2.isAfterLast()) {
				listBahan.add(new Bahan(cursor2.getString(2),cursor2.getFloat(3),cursor2.getString(4)));
				cursor2.moveToNext();
			}
			cursor2.close();
			
			resep = new Resep(nama,cursor.getString(2),listBahan,cursor.getString(3),cursor.getString(4),cursor.getInt(5),cursor.getString(6));
		}
		
		cursor.close();
		db.close();
		return resep;
	}
	
	/**
	 * Menghapus resep berdasarkan nama
	 * @param nama Nama resep
	 * @return status
	 */
	public boolean removeResep(String nama) {
		openDatabase(false);
		db.delete("resep", "nama='"+nama+"'", null);
		db.close();
		return true;
	}
	
	/**
	 * Mencari resep berdasarkan bahan
	 * @param listBahan Bahan-bahan
	 * @return Daftar resep
	 */
	public ArrayList<Resep> findResep(final List<Bahan> listBahan) {
		
		String query = "SELECT r._id FROM resep r, bahan b WHERE b.koderesep=r._id and (b.nama in (";
		
		for(int i = 0; i < listBahan.size(); i++) {
			query += "'"+listBahan.get(i).getNama()+"'";
			if(i != listBahan.size() - 1){
				query += ",";
			}
		}
		
		query += ")) GROUP BY r._id";
		
		openDatabase(true);
		
		Cursor cursor = db.rawQuery(query, null);
		
		cursor.moveToFirst();

		ArrayList<Resep> listResep = new ArrayList<Resep>();
		while (!cursor.isAfterLast()) {
			int kodeResep = cursor.getInt(0);
			
			String query2 = "SELECT * FROM bahan WHERE koderesep=" + kodeResep;
			
			Cursor cursor2 = db.rawQuery(query2, null);
			cursor2.moveToFirst();
			
			List<Bahan> listBahanResep = new ArrayList<Bahan>();
			
			while (!cursor2.isAfterLast()) {
				Bahan bahan = new Bahan(cursor2.getString(2), cursor2.getFloat(3), cursor2.getString(4)); 
				
				listBahanResep.add(bahan);
				cursor2.moveToNext();
			}
			cursor2.close();
			String query3 = "SELECT * FROM resep WHERE _id=" + kodeResep;
			
			Cursor cursor3 = db.rawQuery(query3, null);
			
			cursor3.moveToFirst();
			Resep resep = null;
			
			if (!cursor3.isAfterLast()) {
				resep = new Resep(cursor3.getString(1),cursor3.getString(2),listBahanResep,cursor3.getString(3),cursor3.getString(4),cursor3.getInt(5),cursor3.getString(6));
				cursor3.moveToNext();
			}
			cursor3.close();
			if (resep != null) {
				listResep.add(resep);
			}
			
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		
		// Sorting berdasarkan jumlah bahan yang sesuai
		Collections.sort(listResep, new Comparator<Resep>() {			
			public int compare(Resep r1, Resep r2) {
				int b1 = 0, b2 = 0;
				List<String> lb = new ArrayList<String>();
				
				for (Bahan b: listBahan){
					lb.add(b.getNama());
				}
				
				for(Bahan b: r1.getListBahan()){
					if (lb.contains(b.getNama())) b1++;
				}
				
				for(Bahan b: r2.getListBahan()){
					if (lb.contains(b.getNama())) b2++;
				}
				
				return b2 - b1;
			}
			
		});
		
		return listResep;
	}
	
	/**
	 * Mengubah resep
	 * @param oldResep Resep lama
	 * @param newResep Resep baru
	 * @return status
	 */
	public boolean modifyResep(Resep oldResep, Resep newResep) {
		openDatabase(false);
		ContentValues newValues = new ContentValues();
		newValues.put("deskripsi", newResep.getDeskripsi());
		newValues.put("langkah", newResep.getLangkah());
		newValues.put("kategori", newResep.getKategori());
		db.update("resep", newValues, "nama='"+oldResep.getNama()+"'", null);
		
		Cursor cursor = db.rawQuery("SELECT _id FROM resep WHERE nama='"+oldResep.getNama()+"'", null);
		cursor.moveToFirst();
		int kodeResep = 0;
		if(!cursor.isBeforeFirst()){
			kodeResep = cursor.getInt(0);
		}
		cursor.close();
		
		db.delete("bahan", "koderesep="+kodeResep, null);
		
		ContentValues bValues = new ContentValues();
		for (Bahan bahan : newResep.getListBahan()) {
			bValues.put("koderesep", kodeResep);
			bValues.put("nama", bahan.getNama());
			bValues.put("jumlah", bahan.getJumlah());
			bValues.put("satuan", bahan.getSatuan());
			db.insert("bahan", null, bValues);
			bValues.clear();
		}
		
		db.close();
		return true;
	}
	
	/**
	 * Menentukan status kefavoritan resep
	 * @param nama Nama resep
	 * @param bool Apakah favorit? true or false
	 * @return status
	 */
	public boolean setFavorite(String nama, boolean bool) {
		openDatabase(false);
		ContentValues args = new ContentValues();
		if(bool){
			args.put("flagfavorit",1);
		} else {
			args.put("flagfavorit",0);
		}
		db.update("resep", args, "nama='"+nama+"'", null);
		db.close();
		return true;
	}
	
	/**
	 * Mengambil daftar resep favorit atau sebaliknya
	 * @param favorite Kalau true, kembalikan resep favorit, false sebaliknya
	 * @return Daftar resep
	 */
	public ArrayList<Resep> getFavorite(int favorite) {
		openDatabase(true);
		
		ArrayList<Resep> listResep = new ArrayList<Resep>();
		
		Cursor cursorResep = null;
		
		if(favorite == 1){
			cursorResep = db.rawQuery("SELECT * FROM resep WHERE flagfavorit="+favorite, null);
		} else {
			cursorResep = db.rawQuery("SELECT * FROM resep", null);
		}
		
		cursorResep.moveToFirst();
		while (!cursorResep.isAfterLast()) {
			
			List<Bahan> listBahan = new ArrayList<Bahan>();
			int kodeResep = cursorResep.getInt(0);
			Cursor cursorBahan = db.rawQuery("SELECT * FROM bahan WHERE koderesep="+kodeResep,null);
			
			cursorBahan.moveToFirst();
			while(!cursorBahan.isAfterLast()) {
				Bahan newBahan = new Bahan(cursorBahan.getString(2),cursorBahan.getFloat(3),cursorBahan.getString(4));
				
				listBahan.add(newBahan);
				
				cursorBahan.moveToNext();
			}
			
			Resep newResep = new Resep(cursorResep.getString(1),cursorResep.getString(2),listBahan,cursorResep.getString(3),cursorResep.getString(4),cursorResep.getInt(5),cursorResep.getString(6));
			
			listResep.add(newResep);
			cursorResep.moveToNext();
			
			cursorBahan.close();
		}
		
		cursorResep.close();
		db.close();		
		return listResep;
	}
	
	/**
	 * Mengambil semua bahan (data diambil dari resep, buat iterasi 1)
	 * @return Daftar bahan
	 */
	public ArrayList<String> getAllNamaBahan(){
		openDatabase(true);
		ArrayList<String> listBahan = new ArrayList<String>();
		
		Cursor cursor = db.rawQuery("SELECT DISTINCT nama FROM bahan", null);
		
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()){
			listBahan.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return listBahan;
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}
	
	/**
	 * Membuka database
	 * @param isReadOnly Kalau buat get: true, kalau buat add/remove/modify: readwrite
	 */
	private void openDatabase(boolean isReadOnly) {
    	String myPath = DATABASE_PATH + DATABASE_NAME;
    	
    	if(isReadOnly) {
    		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	} else {
    		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	}
    	db.rawQuery("PRAGMA foreign_keys = ON", null);
	}
}
