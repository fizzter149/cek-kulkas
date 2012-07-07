package ppl.before.cekkulkas.controllers;

import java.util.ArrayList;
import java.util.List;

import ppl.before.cekkulkas.models.Bahan;
import ppl.before.cekkulkas.models.Resep;
import android.content.ContentValues;
import android.database.Cursor;


/**
 * <p>Controller untuk daftar resep yang berurusan dengan database dan menghubungkan
 * antara View dan Model.</p>
 * @author Team Before
 *
 */
public class ControllerDaftarResep {
		
    private static DatabaseHelper dbHelper = DatabaseHelper.getHelper(null);
	
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
	public boolean tambahResep(String nama, String deskripsi, List<Bahan> listBahan, String langkah, String kategori, String foto){
		ContentValues rValues = new ContentValues();
		rValues.put("nama", nama);
		rValues.put("deskripsi", deskripsi);
		rValues.put("langkah", langkah);
		rValues.put("kategori", kategori);
		rValues.put("foto", foto);
		boolean status = dbHelper.insert("resep", rValues);
		if (status == true) {
			Cursor cursorAssignKodeResep = dbHelper.query("SELECT _id FROM resep WHERE nama='" + nama + "'");
			cursorAssignKodeResep.moveToFirst();
			int kodeResep = cursorAssignKodeResep.getInt(0);
			ContentValues bValues = new ContentValues();
			for (Bahan bahan : listBahan) {
				bValues.put("koderesep", kodeResep);
				bValues.put("nama", bahan.getNama());
				bValues.put("jumlah", bahan.getJumlah());
				bValues.put("satuan", bahan.getSatuan());
				dbHelper.insert("bahan", bValues);
				bValues.clear();
			}
			cursorAssignKodeResep.close();
		}
		return status;
	}
	
	/**
	 * Mendapatkan resep berdasarkan nama
	 * @param nama Nama resep
	 * @return Resep
	 */
	public Resep ambilResep(String nama) {
		Resep resep = null;
		Cursor cursor = dbHelper.query("SELECT * FROM resep WHERE nama='" + nama + "'");
		cursor.moveToFirst();
		if (!cursor.isBeforeFirst()) {
			Cursor cursor2 = dbHelper.query("SELECT * FROM bahan WHERE koderesep=" + cursor.getInt(0));
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
		return resep;
	}
	
	/**
	 * Menghapus resep berdasarkan nama
	 * @param nama Nama resep
	 * @return status
	 */
	public boolean hapusResep(String nama) {
		dbHelper.delete("resep", "nama='" + nama + "'");
		return true;
	}
	
	/**
	 * Mencari resep berdasarkan bahan
	 * @param listBahan Bahan-bahan
	 * @return Daftar resep
	 */
	public ArrayList<Resep> cariResep(List<Bahan> listBahan) {
		String query = "SELECT r._id FROM resep r, bahan b WHERE b.koderesep=r._id and (b.nama in (";
		for(int i = 0; i < listBahan.size(); i++) {
			query += "'" + listBahan.get(i).getNama() + "'";
			if(i != listBahan.size() - 1) {
				query += ",";
			}
		}
		query += ")) GROUP BY r._id";
		Cursor cursor = dbHelper.query(query);
		cursor.moveToFirst();
		ArrayList<Resep> listResep = new ArrayList<Resep>();
		while (!cursor.isAfterLast()) {
			int kodeResep = cursor.getInt(0);
			String query2 = "SELECT * FROM bahan WHERE koderesep=" + kodeResep;
			Cursor cursor2 = dbHelper.query(query2);
			cursor2.moveToFirst();
			List<Bahan> listBahanResep = new ArrayList<Bahan>();
			while (!cursor2.isAfterLast()) {
				Bahan bahan = new Bahan(cursor2.getString(2), cursor2.getFloat(3), cursor2.getString(4)); 
				listBahanResep.add(bahan);
				cursor2.moveToNext();
			}
			cursor2.close();
			String query3 = "SELECT * FROM resep WHERE _id=" + kodeResep;
			Cursor cursor3 = dbHelper.query(query3);
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
		return listResep;
	}
	
	/**
	 * Mengubah resep
	 * @param oldResep Resep lama
	 * @param newResep Resep baru
	 * @return status
	 */
	public boolean ubahResep(Resep oldResep, Resep newResep) {
		ContentValues newValues = new ContentValues();
		newValues.put("deskripsi", newResep.getDeskripsi());
		newValues.put("langkah", newResep.getLangkah());
		newValues.put("kategori", newResep.getKategori());
		dbHelper.update("resep", newValues, "nama='" + oldResep.getNama() + "'");
		Cursor cursor = dbHelper.query("SELECT _id FROM resep WHERE nama='" + oldResep.getNama() + "'");
		cursor.moveToFirst();
		int kodeResep = 0;
		if(!cursor.isBeforeFirst()){
			kodeResep = cursor.getInt(0);
		}
		cursor.close();
		dbHelper.delete("bahan", "koderesep=" + kodeResep);
		ContentValues bValues = new ContentValues();
		for (Bahan bahan : newResep.getListBahan()) {
			bValues.put("koderesep", kodeResep);
			bValues.put("nama", bahan.getNama());
			bValues.put("jumlah", bahan.getJumlah());
			bValues.put("satuan", bahan.getSatuan());
			dbHelper.insert("bahan", bValues);
			bValues.clear();
		}
		return true;
	}
	
	/**
	 * Menentukan status kefavoritan resep
	 * @param nama Nama resep
	 * @param favorit Apakah favorit? true or false
	 * @return status
	 */
	public boolean setFavorit(String nama, boolean favorit) {
		ContentValues rValues = new ContentValues();
		if (favorit) {
			rValues.put("flagfavorit",1);
		} else {
			rValues.put("flagfavorit",0);
		}
		dbHelper.update("resep", rValues, "nama='" + nama + "'");
		return true;
	}
	
	/**
	 * Mengambil daftar resep favorit atau sebaliknya
	 * @param favorit Kalau true, kembalikan resep favorit, false semua resep
	 * @return Daftar resep
	 */
	public ArrayList<Resep> getFavorit(int favorit) {
		ArrayList<Resep> listResep = new ArrayList<Resep>();
		Cursor cursorResep = null;
		if(favorit == 1) {
			cursorResep = dbHelper.query("SELECT * FROM resep WHERE flagfavorit=" + favorit);
		} else {
			cursorResep = dbHelper.query("SELECT * FROM resep");
		}
		cursorResep.moveToFirst();
		while (!cursorResep.isAfterLast()) {	
			List<Bahan> listBahan = new ArrayList<Bahan>();
			int kodeResep = cursorResep.getInt(0);
			Cursor cursorBahan = dbHelper.query("SELECT * FROM bahan WHERE koderesep=" + kodeResep);
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
		return listResep;
	}
	
	/**
	 * Mengambil semua bahan (data diambil dari resep, buat iterasi 1)
	 * @return Daftar bahan
	 */
	public ArrayList<String> ambilNamaBahan() {
		ArrayList<String> listBahan = new ArrayList<String>();
		Cursor cursor = dbHelper.query("SELECT DISTINCT nama FROM bahan");
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			listBahan.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		return listBahan;
	}
}
