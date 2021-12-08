package uni.minesweeper.activities;

import androidx.appcompat.app.AppCompatActivity;

import uni.minesweeper.services.MusicService;

public abstract class AbstractActivity extends AppCompatActivity {
  protected boolean ignorePause = false;

  @Override
  protected void onResume() {
    super.onResume();
    ignorePause = false;

    if (MusicService.getInstance() != null && !MusicService.getInstance().isPlaying()) {
      MusicService.getInstance().resumeBg();
    }
  }

  @Override
  public void onBackPressed() {
    ignorePause = true;
    super.onBackPressed();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (!ignorePause && MusicService.getInstance() != null && MusicService.getInstance().isPlaying()) {
      MusicService.getInstance().pauseBg();
    }
  }

  public void setIgnorePause(final boolean ignorePause) {
    this.ignorePause = ignorePause;
  }
}
