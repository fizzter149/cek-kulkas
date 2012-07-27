package ppl.before.cekkulkas.userinterfaces;

import ppl.before.cekkulkas.R;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MenuFotoFullScreen extends Activity{
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// title bar aplikasi
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.detailresep);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		
		setContentView(R.layout.fotofullscreen);
		
		String foto = (String) getIntent().getExtras().get("foto");
		
		if(foto == null || foto.equals("")){
			((ImageView)findViewById(R.id.fotofullscreen)).setImageResource(R.drawable.foto_resep_default);
		} else {
			((ImageView)findViewById(R.id.fotofullscreen)).setImageBitmap(BitmapFactory.decodeFile("/data/data/ppl.before.cekkulkas/"+foto+".jpg"));
		}
		
	}
}
