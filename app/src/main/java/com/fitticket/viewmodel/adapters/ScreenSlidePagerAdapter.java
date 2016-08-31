package com.fitticket.viewmodel.adapters;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.fitticket.R;
import com.fitticket.viewmodel.activities.TrainningSlidesActivity;

public class ScreenSlidePagerAdapter extends PagerAdapter {

	private TrainningSlidesActivity activity;
	private int [] images={R.drawable.cardio,R.drawable.yoga,R.drawable.gym,R.drawable.spinning};
	private String [] sliderText={"ONE TICKET.\nLIMITLESS ACTIVITIES.","FITNESS WILL NEVER\nBE BORING AGAIN!","EASY AS A\nTAP","GOOD THINGS COME\nUNLIMITED!"};
	private String [] LowersliderText={"FITICKET gives you access to unlimited fitness classes & gyms near you with just one membership!!!",
										"Select from Yoga, Zumba, Swimming, Surfing, Spinning, Pilates, Kick-boxing, Strength training and many more activities!",
										"Book your classes & activities with a single tap and enjoy a good sweat- Anywhere, Anytime!",
										"Take as many classes as you like on the go. What's more...You can try a different studio or gym every day!"};

	private LayoutInflater inflater;
	private ImageView pagerImage;
	private TextView tvPagerText,tvLowerPagerText;
	private Button Skip;
	
	public ScreenSlidePagerAdapter(TrainningSlidesActivity activity){
		this.activity=activity;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.length;
	}

	@SuppressWarnings("static-access")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		inflater = (LayoutInflater) activity
				.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.viewpageritem, container,
				false);
		pagerImage=(ImageView) itemView.findViewById(R.id.ivImage);
		tvPagerText=(TextView) itemView.findViewById(R.id.tvPagerText);
		Skip=(Button) itemView.findViewById(R.id.btnSkip);
		tvLowerPagerText=(TextView) itemView.findViewById(R.id.tvLowerPagerText);
		 
		
		pagerImage.setImageDrawable(activity.getResources().getDrawable(images[position]));
		tvPagerText.setText(sliderText[position]);
		tvLowerPagerText.setText(LowersliderText[position]);
		
		//
		//tvFiticket.setText(Html.fromHtml("<strong>FIT</strong>"+"ICKET"));
		 
		
		itemView.setTag(position);
		
//		Skip.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//					Intent i=new Intent(activity,Bookactivity.class);
//					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//					activity.startActivity(i);
//			}
//		});
		// Add viewpager_item.xml to ViewPager
		((ViewPager) container).addView(itemView);
		return itemView;
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == ((RelativeLayout) arg1);
	} 
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// Remove viewpager_item.xml from ViewPager
		((ViewPager) container).removeView((RelativeLayout) object);

	}

}
