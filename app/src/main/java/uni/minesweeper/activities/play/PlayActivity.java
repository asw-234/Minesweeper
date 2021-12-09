package uni.minesweeper.activities.play;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import uni.minesweeper.R;
import uni.minesweeper.Utils;
import uni.minesweeper.activities.AbstractActivity;
import uni.minesweeper.board.MinesweeperModel;
import uni.minesweeper.board.MinesweeperView;
import uni.minesweeper.services.MusicService;


public class PlayActivity extends AbstractActivity {
  private static final int DEFAULT_TIMER_MINUTES = 1;

  private boolean isFlagMode = false;
  private MinesweeperModel model = null;

  private TextView timerField;

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

    btnToggleMode.setOnClickListener(view -> {
      isFlagMode = !isFlagMode;
      btnToggleMode.setChecked(isFlagMode);
      model.setFlagMode(isFlagMode);
    });

    findViewById(R.id.btnNewGame).setOnClickListener(v ->
      Utils.sendToActivity(this, IntroActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP));

    initTimer(getIntent().getIntExtra("timerMinutes", DEFAULT_TIMER_MINUTES));
  }

  @Override
  protected void onDestroy() {
    MusicService.getInstance().play(R.raw.main_theme, false);
    super.onDestroy();
  }

  private void initTimer(final int timerMinutes) {
    timerField = findViewById(R.id.timerView);

    final Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    final Timer gameTimer = new Timer();

    final TimerTask lockTask = new TimerTask() {
      int totalTime = (int) TimeUnit.MINUTES.toSeconds(timerMinutes);

      public void run() {
        final int minutesMark = (int) (totalTime / TimeUnit.MINUTES.toSeconds(1));
        final int secondsMark = (int) (totalTime % TimeUnit.MINUTES.toSeconds(1));

        uiThreadHandler.post(() -> {
          timerField.setText(getApplicationContext().getString(
            R.string.time_left,
            String.format("%02d", minutesMark),
            String.format("%02d", secondsMark)
          ));

          if (totalTime-- == 0) {
            MusicService.getInstance().stop();
            MusicService.getInstance().play(R.raw.loss, true);
            ((MinesweeperView) findViewById(R.id.minesweeperView)).endGame(true);
          }
        });
      }
    };

    gameTimer.schedule(lockTask, 0, TimeUnit.SECONDS.toMillis(1));
  }
}
