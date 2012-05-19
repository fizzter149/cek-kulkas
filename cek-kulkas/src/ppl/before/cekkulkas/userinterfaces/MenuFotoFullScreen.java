package ppl.before.cekkulkas.userinterfaces;

import ppl.before.cekkulkas.R;
import android.app.Activity;
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
		
		ImageView img = (ImageView)findViewById(R.id.fotofullscreen);
		img.setImageResource(getResources().getIdentifier(foto, "drawable", getPackageName()));
		img.setScaleType(ScaleType.FIT_XY);
		
	}
}
