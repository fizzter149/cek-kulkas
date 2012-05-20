package ppl.before.cekkulkas.models;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Resep merupakan suatu keterangan tentang cara memasak dan bahan-bahan
 * yang dibutuhkan. Agar lebih menarik, resep ditambahkan deskripsi, kategori,
 *  status kefavoritan dan foto.</p>
 * @author Team Before
 *
 */
public class Resep implements Serializable {
	
	/** ID hasil generate otomatis */
	private static final long serialVersionUID = 9174929443141918131L;
	
	/** Nama resep */
	private String nama;
	
	/** Deskrisi resep */
	private String deskripsi;
	
	/** Bahan-bahan yang dibutuhkan */
	private List<Bahan> listBahan;
	
	/** Langkah memasak */
	private String langkah;
	
	/** Kategori resep */
	private String kategori;
	
	/** Status kefavoritan resep */
	private int flagFavorit;
	
	/** Jumlah bahan yang kurang */
	private int jumlahKurangBahan;
	
	/** Foto masakan hasil resep */
	private String foto;
	
	/**
	 * Constructor Resep default
	 */
	public Resep() {
	}
	
	/**
	 * Constructor Resep
	 * @param nama Nama resep
	 * @param deskripsi Deskripsi resep
	 * @param listBahan Bahan-bahan yang dibutuhkan
	 * @param langkah Langkah memasak
	 * @param kategori Kategori resep
	 * @param flagFavorit Status kefavoritan resep
	 * @param foto Foto masakan hasil resep
	 */
	public Resep(String nama, String deskripsi, List<Bahan> listBahan,
			String langkah, String kategori, int flagFavorit, String foto) {
		this.nama = nama;
		this.deskripsi = deskripsi;
		this.listBahan = listBahan;
		this.langkah = langkah;
		this.kategori = kategori;
		this.flagFavorit = flagFavorit;
		this.foto = foto;
		this.jumlahKurangBahan = 0;
	}

	/**
	 * Mendapatkan nama resep
	 * @return nama resep
	 */
	public String getNama() {
		return nama;
	}
	
	/**
	 * Mengganti nama resep
	 * @param nama Nama resep
	 */
	public void setNama(String nama) {
		this.nama = nama;
	}
	
	/**
	 * Mendapatkan deskripsi resep
	 * @return Deskripsi resep
	 */
	public String getDeskripsi() {
		return deskripsi;
	}

	/**
	 * Mengganti deskripsi resep
	 * @param deskripsi Deskripsi resep
	 */
	public void setDeskripsi(String deskripsi) {
		this.deskripsi = deskripsi;
	}
	
	/**
	 * Mendapatkan bahan-bahan yang dibutuhkan di resep
	 * @return Bahan-bahan yang dibutuhkan
	 */
	public List<Bahan> getListBahan() {
		return listBahan;
	}
	
	/**
	 * Mengganti bahan-bahan yang dibutuhkan di resep
	 * @param listBahan Bahan-bahan yang dibutuhkan
	 */
	public void setListBahan(List<Bahan> listBahan) {
		this.listBahan = listBahan;
	}
	
	/**
	 * Mendapatkan langkah memasak
	 * @return Langkah memasak
	 */
	public String getLangkah() {
		return langkah;
	}
	
	/**
	 * Mengganti langkah memasak
	 * @param langkah Langkah memasak
	 */
	public void setLangkah(String langkah) {
		this.langkah = langkah;
	}
	
	/**
	 * Mendapatkan foto resep
	 * @return Foto resep
	 */
	public String getFoto() {
		return foto;
	}
	
	/**
	 * Mengganti foto resep
	 * @param foto Foto resep
	 */
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	/**
	 * Mendapatkan kategori resep
	 * @return Ketegori resep
	 */
	public String getKategori() {
		return kategori;
	}

	/**
	 * Mengganti kategori resep
	 * @param kategori Kategori Resep
	 */
	public void setKategori(String kategori) {
		this.kategori = kategori;
	}

	/**
	 * Mendapatkan status kefavoritan resep
	 * @return Status kefavoritan resep
	 */
	public int getFlagFavorit() {
		return flagFavorit;
	}

	/**
	 * Mengganti status kefavoritan resep
	 * @param flagFavorit Status kefavoritan resep
	 */
	public void setFlagFavorit(int flagFavorit) {
		this.flagFavorit = flagFavorit;
	}
	
	/**
	 * Mendapatkan jumlah kekurangan bahan
	 * @return Jumlah kekurangan bahan
	 */
	public int getJumlahKurangBahan() {
		return jumlahKurangBahan;
	}

	/**
	 * Mengganti jumlah kekurangan bahan
	 * @param jumlahKurangBahan umlah kekurangan bahan
	 */
	public void setJumlahKurangBahan(int jumlahKurangBahan) {
		this.jumlahKurangBahan = jumlahKurangBahan;
	}
}
