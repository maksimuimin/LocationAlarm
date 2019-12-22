package sleepless_nights.location_alarm.alarm.ui;
import androidx.appcompat.app.AppCompatActivity;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.util.Log;

public class AlarmRingingActivity extends AppCompatActivity {

    private static final String TAG = "AlarmRingingActivity";
    /** Play alarm up to 10 minutes before silencing */
    private static final int ALARM_TIMEOUT_SECONDS = 10 * 60;
    private static final long[] sVibratePattern = new long[] { 500, 500 };

    private boolean playing = false;
    private Vibrator vibrator;
    private int initialCallState;
    private MediaPlayer mediaPlayer;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ringing);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void startAlarm(MediaPlayer player)
            throws java.io.IOException, IllegalArgumentException,
            IllegalStateException {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (audioManager == null) {
            return;
        }

        // do not play alarms if stream volume is 0
        // (typically because ringer mode is silent).
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setLooping(true);
            player.prepare();
            player.start();
        }
    }

    private void play() {
        // stop() checks to see if we are already playing.
        stop();

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        // TODO: Reuse mMediaPlayer instead of creating a new one and/or use
        // RingtoneManager.
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "Error occurred while playing audio.");
            mp.stop();
            mp.release();

            mediaPlayer = null;
            return true;
        });

        try {
            startAlarm(mediaPlayer);
        } catch (Exception ex) {
            Log.v(TAG, "Using the fallback ringtone");
        }

        /* Start the vibrator after everything is ok with the media player */
        vibrator.vibrate(sVibratePattern, 0);

        playing = true;
        startTime = System.currentTimeMillis();
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
