package edu.gatech.gaze;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {
	public Intent mSettings;
	public final static String EXTRA_MESSAGE = "edu.gatech.gaze.MESSAGE";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		String currentDateTimeString = DateFormat.getDateInstance().format(
				new Date());
		((TextView) findViewById(R.id.timestamp)).setText(currentDateTimeString);
	}

	public void onNextButtonClick(View v) {
		mSettings = new Intent(this, SettingActivity.class);
		String mExperimenterName = ((EditText) findViewById(R.id.tb_exp_name))
				.getText().toString();
		String mPId = ((EditText) findViewById(R.id.tb_p_id)).getText().toString();
		String mAgeYrs = ((EditText) findViewById(R.id.tb_p_age_yr)).getText()
				.toString();
		mAgeYrs = (mAgeYrs.length() == 0) ? "0yrs" : mAgeYrs + "yrs";
		String mAgeMos = ((EditText) findViewById(R.id.tb_p_age_mo)).getText()
				.toString();
		mAgeMos = (mAgeMos.length() == 0) ? "0mos" : mAgeMos + "mos";
		String mAgeDays = ((EditText) findViewById(R.id.tb_p_age_da)).getText()
				.toString();
		mAgeDays = (mAgeDays.length() == 0) ? "0days" : mAgeDays + "days";
		RadioButton mMode = (RadioButton) findViewById(((RadioGroup) findViewById(R.id.rg_mode))
				.getCheckedRadioButtonId());
		RadioButton mGender = (RadioButton) findViewById(((RadioGroup) findViewById(R.id.rg_p_gen))
				.getCheckedRadioButtonId());
		String message = "Mode:" + mMode.getText().charAt(0)+","+
				"Experimenter Name:" + mExperimenterName+","+
				"Participant Id:"+ mPId+","+
				"Participant Gender:" + mGender.getText().charAt(0)+","+
				"Participant Age:"+ mAgeYrs+" "+mAgeMos+" "+mAgeDays;
		mSettings.putExtra(EXTRA_MESSAGE, message);
		startActivity(mSettings);
	}
}