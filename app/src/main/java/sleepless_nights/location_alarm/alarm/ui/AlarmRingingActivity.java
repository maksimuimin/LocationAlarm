package sleepless_nights.location_alarm.alarm.ui;
import androidx.appcompat.app.AppCompatActivity;
import sleepless_nights.location_alarm.R;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class AlarmRingingActivity extends AppCompatActivity {

    private static final String TAG = "AlarmRingingActivity";
    public static final String ALARM_NAME = "alarm_name";
    public static final String ALARM_ADDRESS = "alarm_address";

    private static final long[] VIBRATE_PATTERN = new long[] { 500, 500 };

    private boolean playing = false;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        String alarmName;
        String alarmAddress;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras == null) {
                alarmName = null;
                alarmAddress = null;
            } else {
                alarmName = extras.getString(ALARM_NAME);
                alarmAddress = extras.getString(ALARM_ADDRESS);
            }
        } else {
            alarmName = (String) savedInstanceState.getSerializable(ALARM_NAME);
            alarmAddress = (String) savedInstanceState.getSerializable(ALARM_ADDRESS);
        }

        TextView alarmNameView = findViewById(R.id.name);
        TextView alarmAddressView = findViewById(R.id.address);

        alarmNameView.setText(alarmName);
        alarmAddressView.setText(alarmAddress);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = new MediaPlayer();

        play();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_UP) {
            stop();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }

        return true;
    }

    private void play() {
        stop(); // stop() checks to see if we are already playing

        try {
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Error occurred while preparing mediaPlayer");
                return true;
            });

            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.setOnErrorListener((inner_mp, what, extra) -> {
                    Log.e(TAG, "Error occurred while playing audio.");
                    stop();
                    return true;
                });

                start();
            });

            prepareAlarm();
        } catch (Exception ex) {
            Log.e(TAG, "Unable to  startAlarm, Using the fallback ringtone", ex);
        }
    }

    private void prepareAlarm()
            throws java.io.IOException, IllegalArgumentException,
            IllegalStateException {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) {
            Log.wtf(TAG, "Got null AudioManager");
            return;
        }

        // do not play alarms if stream volume is 0
        // (typically because ringer mode is silent).
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, alert);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
        }
    }

    public void start() {
        mediaPlayer.start();
        /* Start the vibrator after everything is ok with the media player */
        vibrator.vibrate(VIBRATE_PATTERN, 0);

        playing = true;
    }

    /**
     * Stops alarm audio and disables alarm if it not snoozed and not
     * repeating
     */
    public void stop() {
        if (playing) {
            playing = false;

            // Stop audio playing
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Stop vibrator
            vibrator.cancel();
        }
    }
}
