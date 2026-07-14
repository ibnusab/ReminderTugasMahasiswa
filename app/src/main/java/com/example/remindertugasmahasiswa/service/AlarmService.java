package com.example.remindertugasmahasiswa.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.remindertugasmahasiswa.R;

public class AlarmService extends Service {

    public static MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        try {

            // =========================
            // CEGAH DOUBLE PLAY
            // =========================

            if (mediaPlayer != null) {

                if (mediaPlayer.isPlaying()) {

                    return START_STICKY;
                }
            }

            // =========================
            // INIT MEDIA PLAYER
            // =========================

            mediaPlayer =
                    MediaPlayer.create(
                            this,
                            R.raw.alarm
                    );

            if (mediaPlayer == null) {

                stopSelf();

                return START_NOT_STICKY;
            }

            // =========================
            // AUDIO ATTRIBUTES
            // =========================

            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()

                            .setUsage(
                                    AudioAttributes.USAGE_ALARM
                            )

                            .setContentType(
                                    AudioAttributes.CONTENT_TYPE_MUSIC
                            )

                            .build()
            );

            // =========================
            // LOOPING TERUS
            // =========================

            mediaPlayer.setLooping(true);

            // =========================
            // VOLUME MAX
            // =========================

            mediaPlayer.setVolume(
                    1.0f,
                    1.0f
            );

            // =========================
            // START ALARM
            // =========================

            mediaPlayer.start();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return START_STICKY;
    }

    // =========================
    // SERVICE DESTROY
    // =========================

    @Override
    public void onDestroy() {

        super.onDestroy();

        stopAlarm();
    }

    // =========================
    // STOP ALARM
    // =========================

    public static void stopAlarm() {

        try {

            if (mediaPlayer != null) {

                if (mediaPlayer.isPlaying()) {

                    mediaPlayer.stop();
                }

                mediaPlayer.reset();

                mediaPlayer.release();

                mediaPlayer = null;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}