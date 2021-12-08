package uni.minesweeper.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import uni.minesweeper.R;
import uni.minesweeper.Utils;
import uni.minesweeper.activities.auth.LoginActivity;
import uni.minesweeper.activities.play.IntroActivity;
import uni.minesweeper.services.MusicService;


public class MenuActivity extends AbstractActivity {


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    startService(new Intent(this, MusicService.class));

    LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
    IntentFilter filter = new IntentFilter();
    filter.addAction(MusicService.INITIALIZED);
    broadcastManager.registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(MusicService.INITIALIZED)) {
          initView();
        }
      }
    }, filter);
  }

  private void initView() {
    final MusicService musicInstance = MusicService.getInstance();
    musicInstance.setLooping(true);
    musicInstance.playSong(R.raw.main_theme);

    setContentView(R.layout.activity_menu);
    findViewById(R.id.btnPlay).setOnClickListener(view -> Utils.sendToActivity(this, IntroActivity.class, 0));
    findViewById(R.id.btnLeaderboard).setOnClickListener(view -> Utils.sendToActivity(this, RankingActivity.class, 0));
    findViewById(R.id.btnLogout).setOnClickListener(view -> logout());
  }

  private void logout() {
    Utils.sendToActivity(this, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
  }
}
