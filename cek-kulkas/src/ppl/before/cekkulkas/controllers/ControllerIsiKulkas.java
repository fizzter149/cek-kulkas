package ppl.before.cekkulkas.controllers;

import java.util.ArrayList;
import java.util.List;

import ppl.before.cekkulkas.models.Bahan;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * <p>Controller untuk daftar isi kulkas yang berurusan dengan database dan menghubungkan
 * antara View dan Model.</p>
 * @author Team Before
 *
 */
public class ControllerIsiKulkas {
	
    private DatabaseHelper dbHelper;
	
    public ControllerIsiKulkas(Context context) {
    	dbHelper = DatabaseHelper.getHelper(context);
    }
	/**
	 * Menambah bahan
	 * @param nama Nama bahan
	 * @param jumlah Jumlah bahan
	 * @param satuan Satuan bahan
	 * @return status
	 */
	public boolean tambahBahan(String nama, float jumlah, String satuan) {
		ContentValues bValues = new ContentValues();
		bValues.put("nama", nama);
		bValues.put("jumlah", jumlah);
		bValues.put("satuan", satuan);
		boolean status = dbHelper.insert("isikulkas", bValues);
		bValues.clear();
		if (status == false) {
			Cursor cursorBahan = dbHelper.query("SELECT * from isikulkas WHERE nama='" + nama + "'");
			cursorBahan.moveToFirst();
			float jumlahLama = cursorBahan.getFloat(2);
			Log.i("jml", "" + jumlahLama);
			String satuanSebelum = cursorBahan.getString(3);
			cursorBahan.close();
			float jumlahBaru = jumlahLama + konversiSatuan(nama, satuan, satuanSebelum, jumlah);
			setJumlah(nama, jumlahBaru);
			Log.i("jmlb", "" + jumlahBaru);
		}
		return status;
	}
	
	/**
	 * Mengganti jumlah bahan
	 * @param nama Nama bahan
	 * @param newJumlah Jumlah baru bahan
	 * @return status
	 */
	public boolean setJumlah(String nama, float newJumlah) {
		ContentValues bValues = new ContentValues();
		bValues.put("jumlah", newJumlah);
		dbHelper.update("isikulkas", bValues, "nama='" + nama + "'");
		bValues.clear();
		return true;
	}
	
	public boolean setSatuan(String nama, String newSatuan) {
		ContentValues bValues = new ContentValues();
		bValues.put("satuan", newSatuan);
		dbHelper.update("isikulkas", bValues, "nama='" + nama + "'");
		bValues.clear();
		return true;
	}
	
	/**
	 * Menghapus bahan
	 * @param nama Nama Bahan
	 * @return status
	 */
	public boolean hapusBahan(String nama) {
		dbHelper.delete("isikulkas", "nama='" + nama + "'");
		return true;
	}
	
	public boolean adaDiKulkas(String nama) {
		boolean ada;
		Cursor cek = dbHelper.query("SELECT * FROM isikulkas WHERE nama='" + nama + "'");
		if (cek.getCount() > 0) {
			ada = true;
		} else {
			ada = false;
		}
		cek.close();
		return ada;
	}
	
	public Bahan ambilBahan(String nama) {
		Bahan bahan;
		Cursor cursorBahan = dbHelper.query("SELECT * FROM isikulkas WHERE nama='" + nama + "'");
		cursorBahan.moveToFirst();
		bahan = new Bahan(nama, cursorBahan.getFloat(2), cursorBahan.getString(3));
		cursorBahan.close();
		return bahan;
	}
	
	/**
	 * Menggambil semua bahan yang ada di kulkas
	 * @return Daftar bahan
	 */
	public List<Bahan> ambilSemuaBahan() {
		List<Bahan> listBahan = new ArrayList<Bahan>();
		Cursor cursorBahan = dbHelper.query("SELECT * FROM isikulkas");
		cursorBahan.moveToFirst();
		while (!cursorBahan.isAfterLast()) {
			Bahan newBahan = new Bahan(cursorBahan.getString(1), cursorBahan.getFloat(2), cursorBahan.getString(3));
			listBahan.add(newBahan);
			cursorBahan.moveToNext();
		}
		cursorBahan.close();
		return listBahan;
	}
		
	public List<String> getMultiSatuan(String nama) {
		List<String> listSatuan = new ArrayList<String>();
		Cursor cekMultiSatuan = dbHelper.query("SELECT * FROM konversi WHERE nama='" + nama + "'");
		if (cekMultiSatuan.getCount() > 0) {
			Cursor cursorSatuan1 = dbHelper.query("SELECT DISTINCT satuan1 FROM konversi WHERE nama='" + nama + "'");
			cursorSatuan1.moveToFirst();
			listSatuan.add(cursorSatuan1.getString(0));
			cursorSatuan1.close();
			Cursor cursorSatuan2 = dbHelper.query("SELECT * FROM konversi WHERE nama='" + nama + "'");
			cursorSatuan2.moveToFirst();
			while (!cursorSatuan2.isAfterLast()) {
				String satuan = cursorSatuan2.getString(3);
				listSatuan.add(satuan);
				cursorSatuan2.moveToNext();
			}
			cursorSatuan2.close();
		} else {
			Cursor cursorSatuan = dbHelper.query("SELECT satuan FROM bahan WHERE nama='" + nama + "'");
			cursorSatuan.moveToFirst();
			listSatuan.add(cursorSatuan.getString(0));
			cursorSatuan.close();
		}
		cekMultiSatuan.close();
		return listSatuan;
	}
	
	public float konversiSatuan(String nama, String satuanSebelum, String satuanSesudah, float jumlah) {
		float hasil = 0;
		if (satuanSebelum.equalsIgnoreCase(satuanSesudah)) {
			hasil = jumlah;
		} else {
			Cursor cursorSatuanDefault = dbHelper.query("SELECT DISTINCT satuan1 FROM konversi WHERE nama='" + nama + "'");
			cursorSatuanDefault.moveToFirst();
			String satuanDefault = cursorSatuanDefault.getString(0);
			cursorSatuanDefault.close();
			float faktorDefault = 1;
			if (satuanSebelum.equalsIgnoreCase(satuanDefault)) {
				faktorDefault = 1;
			} else {
				Cursor cursorKonversiDefault = dbHelper.query("SELECT faktor FROM konversi WHERE nama='" + nama + "' AND satuan2='" + satuanSebelum + "' AND satuan1='" + satuanDefault + "'");
				cursorKonversiDefault.moveToFirst();
				faktorDefault = cursorKonversiDefault.getFloat(0);
				cursorKonversiDefault.close();
			}
			hasil = faktorDefault * jumlah;
			
			float faktor = 1;
			if (satuanSesudah.equalsIgnoreCase(satuanDefault)) {
				faktor = 1;
			} else {
				Cursor cursorKonversi = dbHelper.query("SELECT faktor FROM konversi WHERE nama='" + nama + "' AND satuan2='" + satuanSesudah + "' AND satuan1='" + satuanDefault + "'");
				cursorKonversi.moveToFirst();
				faktor = cursorKonversi.getFloat(0);
				cursorKonversi.close();
			}
			hasil = hasil / faktor;
		}
		return hasil;
	}
}
