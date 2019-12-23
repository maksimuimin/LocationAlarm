package sleepless_nights.location_alarm.alarm.ui;
import androidx.appcompat.app.AppCompatActivity;

import sleepless_nights.location_alarm.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

public class AlarmRingingActivity extends AppCompatActivity {

    private static final String TAG = "AlarmRingingActivity";
    /** Play alarm up to 10 minutes before silencing */
    private static final int ALARM_TIMEOUT_SECONDS = 10 * 60;
    private static final long[] sVibratePattern = new long[] { 500, 500 };

    private boolean playing = false;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        play();
    }

    private void play() {
        stop(); // stop() checks to see if we are already playing

        mediaPlayer = new MediaPlayer();

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
            Log.v(TAG, "Unable to  startAlarm, Using the fallback ringtone", ex);
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
        vibrator.vibrate(sVibratePattern, 0);

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
