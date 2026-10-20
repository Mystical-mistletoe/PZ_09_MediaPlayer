package com.example.pz_09_mediaplayer;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Color;
import java.util.Random;

import java.io.IOException;
import android.app.Activity;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

public class MainActivity extends AppCompatActivity implements
        OnPreparedListener,
        OnCompletionListener {
    final String LOG_TAG = "myLogs";
    final String DATA_HTTP = "https://archive.org/download/freefloatingmusic012/01%20-%20Dawn%20Chorus.mp3";
    final String DATA_STREAM = "https://archive.org/download/Solitudes.Forest.Piano/02%20-%20Pileated%20Woodpecker%20Swamp%20Sparrow%20White-Throated%20Sparroe%20Red%20Squirrel.mp3";
    final String DATA_SD = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            + "/wolf_moon_unicorn_heads.mp3";
    final Uri DATA_URI = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            39);
    MediaPlayer mediaPlayer;
    AudioManager am;
    CheckBox chbLoop;
    View centerSquare; //смена цвета квадрата
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        centerSquare = findViewById(R.id.centerSquare);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        chbLoop = (CheckBox) findViewById(R.id.chbLoop);
        chbLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(isChecked);
                }
            }
        });
        //поиск музыки
        if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_MEDIA_AUDIO)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.e("MY_URI", "Нет разрешения, запрашиваем...");
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_MEDIA_AUDIO}, 1);
        } else {
            Log.e("MY_URI", "Разрешение есть! Ищем треки...");
            getMusicList();
        }
    }
    //выбор источника
    public void onClickStart(View view) {
        releaseMP();
        setRandomColor();
        try {
            int id = view.getId();
            if (id == R.id.btnStartHttp) {
                Log.d(LOG_TAG, "start HTTP");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_HTTP);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
            } else if (id == R.id.btnStartStream) {
                Log.d(LOG_TAG, "start Stream");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_STREAM);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
            } else if (id == R.id.btnStartSD) {
                Log.d(LOG_TAG, "start SD");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_SD);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } else if (id == R.id.btnStartUri) {
                Log.d(LOG_TAG, "start URI");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, DATA_URI);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } else if (id == R.id.btnStartRaw) {
                Log.d(LOG_TAG, "start RAW");
                mediaPlayer = MediaPlayer.create(this, R.raw.runway_fashion_spence);
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mediaPlayer == null) return;
        mediaPlayer.setLooping(chbLoop.isChecked());
        mediaPlayer.setOnCompletionListener(this);
    }
    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //управление
    public void onClick(View view) {
        if (mediaPlayer == null) return;

        int id = view.getId();
        if (id == R.id.btnPause) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        } else if (id == R.id.btnResume) {
            if (!mediaPlayer.isPlaying())
                mediaPlayer.start();
        } else if (id == R.id.btnStop) {
            mediaPlayer.stop();
        } else if (id == R.id.btnBackward) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
        } else if (id == R.id.btnForward) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
        } else if (id == R.id.btnInfo) {
            Log.d(LOG_TAG, "Playing = " + mediaPlayer.isPlaying());
            Log.d(LOG_TAG, "Time = " + mediaPlayer.getCurrentPosition()
                    + " / " + mediaPlayer.getDuration());
            Log.d(LOG_TAG, "Looping = " + mediaPlayer.isLooping());
            Log.d(LOG_TAG,
                    "Volume = " + am.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOG_TAG, "onPrepared");
        mp.start();
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "onCompletion");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
    }
    private void setRandomColor() {
        if (centerSquare != null) {
            Random random = new Random();
            int color = Color.argb(255, random.nextInt(256),
                    random.nextInt(256), random.nextInt(256));
            centerSquare.setBackgroundColor(color);
        }
    }
    private void getMusicList() {
        Log.e("MY_URI", "getMusicList() вызван");
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            Log.e("MY_URI", "Найдено треков: " + cursor.getCount());
            do {
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                Log.e("MY_URI", "ID: " + id + " | Название: " + title);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.e("MY_URI", "Курсор пуст! На телефоне нет музыки в MediaStore.");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.e("MY_URI", "Разрешение получено! Ищем треки...");
                getMusicList();
            } else {
                Log.e("MY_URI", "В разрешении отказано!");
            }
        }
    }
}