package edu.gatech.gaze;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingActivity extends Activity {
	public Intent mExperiment;
	public final static String EXTRA_MESSAGE = "edu.gatech.gaze.MESSAGE";
	public String message;
	public String msgArr[];
	public static Integer mInterval;
	private final int MIN = 0;
	private final int MAX = 3;
	private final String sequences[][] = { { "LRLR", "LRRL" }, { "RLLR", "RLRL" } };// Discarded
	// by
	// experimenter
	// "LLRR",
	// "RRLL"
	private static String selectedSeq;
	private boolean isExpMode = true;
	private boolean isPMale = true;
	private int sameGenderPrevSeqType; // L(0) or R(1)
	private int currentSeqType;
	public static final String PREFS_NAME = "MyPrefsFile";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Get the message from the intent
		Intent intent = getIntent();
		message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		msgArr = message.split(",");
		isExpMode = (msgArr[0].split(":")[1].equalsIgnoreCase("E"));
		isPMale = (msgArr[3].split(":")[1].equalsIgnoreCase("M"));

		SharedPreferences settings = getPreferences(MODE_APPEND);
		SharedPreferences.Editor editor = settings.edit();
		if (isExpMode) {
			if (isPMale) {
				editor.putInt("NoOfM", settings.getInt("NoOfM", 0) + 1);
				sameGenderPrevSeqType = settings.getInt("MLastSeqType", -1);
				//TODO randomize the starting seq type else it will always start with left
				if (sameGenderPrevSeqType == -1)
					currentSeqType = 0 + (int) (Math.random()*2.0);
				else
					currentSeqType = (sameGenderPrevSeqType+1)%2;
				editor.putInt("MLastSeqType", currentSeqType);
				editor.commit();
			} else {
				editor.putInt("NoOfF", settings.getInt("NoOfF", 0) + 1);
				sameGenderPrevSeqType = settings.getInt("FLastSeqType", -1);
				if (sameGenderPrevSeqType == -1)
					currentSeqType = 0 + (int) (Math.random()*2.0);
				else
					currentSeqType = (sameGenderPrevSeqType+1)%2;
				editor.putInt("FLastSeqType", currentSeqType);
				editor.commit();
			}
		} else {
				editor.putInt("NoOfT", settings.getInt("NoOfT", 0) + 1);
				sameGenderPrevSeqType = settings.getInt("TLastSeqType", -1);
				//TODO randomize the starting seq type else it will always start with left
				if (sameGenderPrevSeqType == -1)
					currentSeqType = 0 + (int) (Math.random()*2.0);
				else
					currentSeqType = (sameGenderPrevSeqType+1)%2;
				editor.putInt("TLastSeqType", currentSeqType);
				editor.commit();
		}
			generateRandomSeq();
			((TextView) findViewById(R.id.tv_curr_pattern)).setText(SettingActivity.selectedSeq);
			SettingActivity.mInterval = ((SeekBar)findViewById(R.id.sb_interval)).getProgress();
			((TextView) findViewById(R.id.tv_alert_interval)).setText(Integer.toString(mInterval));
			
			((SeekBar) findViewById(R.id.sb_interval)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					// TODO Auto-generated method stub
					SettingActivity.mInterval = progress;
					((TextView) findViewById(R.id.tv_alert_interval)).setText(Integer.toString(progress));
				}
			});
		
	}

	public void onStartExperimentButtonClick(View v) {
		mExperiment = new Intent(this, ExperimentActivity.class);
		message += ",Selected Sequence:" + SettingActivity.selectedSeq;
		message += ",Interval(sec):" + SettingActivity.mInterval;
		mExperiment.putExtra(EXTRA_MESSAGE, message);
		startActivity(mExperiment);
	}

	public void generateRandomSeq() {

		/*
		 * //For truly random sequence SettingActivity.selectedSeq = sequences[MIN +
		 * (int) (Math.random() * ((MAX - MIN) + 1))];
		 */

		// For one of the two possible sequence in the give seq type
		SettingActivity.selectedSeq = sequences[currentSeqType][0 + (int) (Math.random()*2.0)];
	}

}
