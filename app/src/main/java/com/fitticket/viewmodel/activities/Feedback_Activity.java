package com.fitticket.viewmodel.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fitticket.R;

public class Feedback_Activity extends AppCompatActivity {
	
	EditText Feedback;
	ImageView imageView1,imageView2,imageView3;
	protected static final int RESULT_LOAD_IMAGE = 1;
	private static final String TAG = Feedback_Activity.class.getSimpleName();
	private Button btnSend;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_activity);
		toolbar= (Toolbar)findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Feedback");
		Feedback=(EditText) findViewById(R.id.edtFeedback);
		btnSend=(Button) findViewById(R.id.btnSend);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		 
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendFeedback();
			}
		});
	}
	
	public void sendFeedback(){
		if(Feedback.getText().toString().trim().equals("")){
			if(Feedback.getText().toString().trim().equals(""))
			{
				Feedback.setError("Please Enter Some text");
				Feedback.setText("");
			}
		}else{

			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@fiticket.com"});
			i.putExtra(Intent.EXTRA_SUBJECT,"Feedback about FiTicket");
			i.putExtra(Intent.EXTRA_TEXT   ,Feedback.getText().toString());
			try {
				startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}

		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Feedback.setText("");
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		 
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	
}
