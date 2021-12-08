package uni.minesweeper.activities;

import androidx.appcompat.app.AppCompatActivity;

import uni.minesweeper.services.MusicService;

public class AbstractActivity extends AppCompatActivity {
  @Override
  protected void onResume() {
    if (MusicService.getInstance() != null) {
      MusicService.getInstance().resume();
    }

    super.onResume();
  }

  @Override
  protected void onPause() {
    if (MusicService.getInstance() != null) {
      MusicService.getInstance().pause();
    }

    super.onPause();
  }
}
