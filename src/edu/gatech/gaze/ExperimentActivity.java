package edu.gatech.gaze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;


public class ExperimentActivity extends Activity {
	private static String selectedSeq;
	private final Handler handler = new Handler();
	private static int currIndex = 0;
	private static String logFileName;
	public Intent mMain;
	private MediaPlayer mediaPlayerLeft, mediaPlayerRight,mediaPlayerCenter, mediaPlayerReset;
	public String message;
	public String msgArr[];
	private String mPId;
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
  //External storage directory
	private File rootDir;
	//File handlers 
	private File trialDataFile;
	private FileWriter trialFileWriter;
	public static int mInterval;
	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;
	String state = Environment.getExternalStorageState();
	private static final Long startTime = System.currentTimeMillis();
	PowerManager mPowerManager;
	WakeLock mWakeLock;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Reset Values
        ExperimentActivity.currIndex = 0;
        setContentView(R.layout.experiment);
        
     // Get an instance of the PowerManager
    		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
    		// Create a bright wake lock
    		mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
    				getClass().getName());
        
    }

    @Override
    protected void onResume(){
    	super.onResume();
    	mWakeLock.acquire();
    		setVolumeControlStream(AudioManager.STREAM_MUSIC);
      // Get the message from the intent
     		Intent intent = getIntent();
     		mMain = new Intent(this, MainActivity.class);
     		message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
     		msgArr = message.split(",");
     		ExperimentActivity.selectedSeq = msgArr[5].split(":")[1];
     		mInterval = Integer.parseInt(msgArr[6].split(":")[1].trim());
     		mPId = msgArr[2].split(":")[1];
     		setUpLogFiles();
         setUpControls();
         setUpMediaPlayers();
         builder = new AlertDialog.Builder(this);
         builder.setMessage("File "+ logFileName + " has been saved. Do you want to start new experiment?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                         ExperimentActivity.this.finish();        
                         startActivity(mMain);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                   	 ExperimentActivity.this.finish(); 
                   	 //Open the app again from first screen
                   	 startActivity(mMain);
                   	 moveTaskToBack(true);
                    }
                });
         alertDialog = builder.create();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerLeft.release();
        mediaPlayerRight.release();
        mediaPlayerCenter.release();
        mediaPlayerReset.release();
        
        try {
					trialFileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        // Another activity is taking focus (this activity is about to be "paused").
        mWakeLock.release();
    }
    
    @Override
    public void onBackPressed() {
    	//When back key is pressed
    }
    
    public void setUpMediaPlayers() {
    	mediaPlayerLeft = MediaPlayer.create(getApplicationContext(),R.raw.l);
    	mediaPlayerRight = MediaPlayer.create(getApplicationContext(),R.raw.r);
    	mediaPlayerCenter = MediaPlayer.create(getApplicationContext(),R.raw.c);
    	mediaPlayerReset = MediaPlayer.create(getApplicationContext(),R.raw.reset);
    }
    
    public void setUpLogFiles() {
  		if (Environment.MEDIA_MOUNTED.equals(state)) {
  			// We can read and write the media
  			mExternalStorageAvailable = mExternalStorageWriteable = true;
  			try {
  				// A bug was detected that if the Logs folder wasn't there then it is not created. So create it
  				File folder = new File(Environment.getExternalStorageDirectory() + "/EyeGazeLogs");
  				boolean success = false;
  				if (!folder.exists()) {
  				    success = folder.mkdir();
  				}
  				if (!success) {
  				    // Do something on success
  				} else {
  				    // Do something else on failure 
  				}
  				
  				rootDir = Environment.getExternalStorageDirectory();
  				logFileName = "EyeGazeLogs/trial_"
  						+ mPId +"_"
  						+ DateFormat.getDateInstance().format(new Date()).replaceAll("\\s","").replace(',','_')
  						+ ".csv";
  				trialDataFile = new File(rootDir, logFileName );
  				
  				trialFileWriter = new FileWriter(trialDataFile);
  				
  				trialFileWriter.append("Date:"+DateFormat.getDateInstance().format(new Date())+"\n");
  				//Set up headers for each log file	
  				for(int i=0; i<msgArr.length; i++) {
  					trialFileWriter.append(msgArr[i]+"\n");
  				}
  				trialFileWriter.append("Timestamp, Side\n");
  				trialFileWriter.flush();
  			} catch (IOException e) {
  				e.printStackTrace();
  			}

  		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
  			// We can only read the media
  			mExternalStorageAvailable = true;
  			mExternalStorageWriteable = false;
  		} else {
  			// Something else is wrong. It may be one of many other states, but
  			// all we need
  			// to know is we can neither read nor write
  			mExternalStorageAvailable = mExternalStorageWriteable = false;
  		}
  	}
    
    private void setUpControls() {
    	findViewById(R.id.experiment).setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch(ExperimentActivity.selectedSeq.charAt(ExperimentActivity.currIndex)) {
          case 'L':
          	mediaPlayerLeft.start();
            handler.postDelayed(new Runnable() {
              public void run() {
                //Do something after mInterval (converted to milliseconds)
              	mediaPlayerCenter.start();
              }
            }, ExperimentActivity.mInterval*1000);
          	try {
							trialFileWriter.append(Long.toString((System.currentTimeMillis()-ExperimentActivity.startTime)/1000));
							trialFileWriter.append(',');
							trialFileWriter.append("L\n");
							trialFileWriter.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}           
          	break;
          case 'R':
          	mediaPlayerRight.start();
            handler.postDelayed(new Runnable() {
              public void run() {
                //Do something after mInterval (converted to milliseconds)
              	mediaPlayerCenter.start();
              }
            }, ExperimentActivity.mInterval*1000);
          	try {
							trialFileWriter.append(Long.toString((System.currentTimeMillis()-ExperimentActivity.startTime)/1000));
							trialFileWriter.append(',');
							trialFileWriter.append("R\n");
							trialFileWriter.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}       
          	break;
          }
					ExperimentActivity.currIndex = (ExperimentActivity.currIndex+1)%4;
				}
			});
    	
    	
    	
        findViewById(R.id.experiment).setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                mediaPlayerReset.start();
                
                alertDialog.show();
                
                return true;
            }
        });
    	
    }
    
    
    
}
