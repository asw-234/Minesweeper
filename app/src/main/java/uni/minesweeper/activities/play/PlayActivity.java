package uni.minesweeper.activities.play;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import uni.minesweeper.R;
import uni.minesweeper.Utils;
import uni.minesweeper.activities.AbstractActivity;
import uni.minesweeper.board.MinesweeperModel;
import uni.minesweeper.services.MusicService;


public class PlayActivity extends AbstractActivity {
  private boolean isFlagMode = false;
  private MinesweeperModel model = null;

  @Override
  public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putBoolean("isFlagMode", isFlagMode);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_play);

    MusicService.getInstance().play(R.raw.excellent_mood, false);

    model = MinesweeperModel.getInstance();
    final ToggleButton btnToggleMode = findViewById(R.id.btnToggleMode);

    if (savedInstanceState != null) {
      isFlagMode = savedInstanceState.getBoolean("isFlagMode");
    }

    btnToggleMode.setTextOn(getApplicationContext().getString(R.string.mode, "FLAG"));
    btnToggleMode.setTextOff(getApplicationContext().getString(R.string.mode, "CHECK"));
    btnToggleMode.setChecked(isFlagMode);

    btnToggleMode.setOnClickListener(v -> {
      isFlagMode = !isFlagMode;
      btnToggleMode.setChecked(isFlagMode);
      model.setFlagMode(isFlagMode);
    });

    findViewById(R.id.btnNewGame).setOnClickListener(v ->
      Utils.sendToActivity(this, IntroActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP));
  }

  @Override
  protected void onDestroy() {
    MusicService.getInstance().play(R.raw.main_theme, false);
    super.onDestroy();
  }
}
