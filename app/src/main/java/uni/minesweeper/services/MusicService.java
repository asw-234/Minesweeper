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
  private MediaPlayer sfxPlayer;

  @Override
  public IBinder onBind(final Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    musicPlayer = buildMusicPlayer(true);
    sfxPlayer = buildMusicPlayer(false);

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

  public void play(final int resourceId, final boolean isSFX) {
    final Resources resources = getResources();

    final Uri songUri = new Uri.Builder()
      .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
      .authority(resources.getResourcePackageName(resourceId))
      .appendPath(resources.getResourceTypeName(resourceId))
      .appendPath(resources.getResourceEntryName(resourceId))
      .build();

    MediaPlayer player = isSFX ? sfxPlayer : musicPlayer;

    player.reset();

    try {
      player.setDataSource(getApplicationContext(), songUri);
      player.prepareAsync();
      player.setOnPreparedListener(MediaPlayer::start);
    } catch (Exception e) {
      Log.e(LOGTAG, "Error setting data source", e);
    }
  }

  public void resumeBg() {
    musicPlayer.start();
  }

  public void pauseBg() {
    musicPlayer.pause();
  }

  public void stop() {
    musicPlayer.reset();
    musicPlayer.stop();
    sfxPlayer.reset();
    sfxPlayer.stop();
  }

  public boolean isPlaying() {
    return musicPlayer.isPlaying() || sfxPlayer.isPlaying();
  }

  private MediaPlayer buildMusicPlayer(final boolean isLooping) {
    MediaPlayer player = new MediaPlayer();
    player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    player.setAudioStreamType(AudioManager.STREAM_MUSIC);

    if (isLooping) {
      player.setLooping(true);
      player.setOnCompletionListener(MediaPlayer::start);
      player.setVolume(0.7f, 0.7f);
    }

    player.setAudioAttributes(
      new AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()
    );

    return player;
  }

}