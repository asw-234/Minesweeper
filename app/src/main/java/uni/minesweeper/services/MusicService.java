package uni.minesweeper.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MusicService extends Service  {

  public static final String INITIALIZED = "INITIALIZED";

  private static final String LOGTAG = "MusicService";
  private static MusicService instance;
  private MediaPlayer musicPlayer;

  @Override
  public IBinder onBind(final Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    musicPlayer = new MediaPlayer();
    musicPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    musicPlayer.setAudioAttributes(
      new AudioAttributes.Builder()
      .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
      .setUsage(AudioAttributes.USAGE_MEDIA)
      .build()
    );

    instance = this;
    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MusicService.INITIALIZED));
  }

  @Override
  public void onDestroy() {
    stop();
  }

  public static MusicService getInstance() {
    return instance;
  }

  public void playSong(final int resourceId) {
    final Resources resources = getResources();

    final Uri songUri = new Uri.Builder()
      .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
      .authority(resources.getResourcePackageName(resourceId))
      .appendPath(resources.getResourceTypeName(resourceId))
      .appendPath(resources.getResourceEntryName(resourceId))
      .build();

    musicPlayer.reset();

    try {
      musicPlayer.setDataSource(getApplicationContext(), songUri);
      musicPlayer.prepare();
      musicPlayer.start();
    } catch (Exception e) {
      Log.e(LOGTAG, "Error setting data source", e);
    }
  }

  public void setLooping(final boolean looping) {
    musicPlayer.setLooping(looping);
  }

  public void resume() {
    musicPlayer.start();
  }

  public void pause() {
    musicPlayer.pause();
  }

  public void stop() {
    musicPlayer.reset();
    musicPlayer.stop();
  }
}