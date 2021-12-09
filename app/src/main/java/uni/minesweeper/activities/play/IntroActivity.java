package uni.minesweeper.activities.play;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gc.materialdesign.views.ButtonFloat;
import com.github.nikartm.button.FitButton;
import com.ramotion.fluidslider.FluidSlider;

import kotlin.Unit;
import uni.minesweeper.R;
import uni.minesweeper.activities.AbstractActivity;
import uni.minesweeper.board.MinesweeperModel;
import uni.minesweeper.services.MusicService;


public class IntroActivity extends AbstractActivity {

  private static final int BOARD_MAX_SIZE = 15;
  private static final int BOARD_MIN_SIZE = 5;
  private static final int MINES_MIN = 1;
  private static final int MINUTES_MAX = 20;
  private static final int MINUTES_MIN = 1;

  private static final int MINES_DEFAULT = 3;
  private static final int MINUTES_DEFAULT = 5;
  private static final int SIZE_DEFAULT = 7;

  private int totalMines = MINES_DEFAULT;
  private int boardSize = SIZE_DEFAULT;
  private int timerMinutes = MINUTES_DEFAULT;

  @Override
  public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt("boardSize", boardSize);
    savedInstanceState.putInt("totalMines", totalMines);
    savedInstanceState.putInt("timerMinutes", timerMinutes);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    if (savedInstanceState != null) {
      boardSize = savedInstanceState.getInt("boardSize");
      totalMines = savedInstanceState.getInt("totalMines");
      timerMinutes = savedInstanceState.getInt("timerMinutes");
    }

    initSizeSlider();
    initTimeSlider();

    ButtonFloat btnIncrease = findViewById(R.id.btnIncrease);
    ButtonFloat btnDecrease = findViewById(R.id.btnDecrease);
    btnDecrease.setOnClickListener(createFloatBtnListener(false));
    btnIncrease.setOnClickListener(createFloatBtnListener(true));

    final FitButton btnPlay = findViewById(R.id.btnPlay);
    final MinesweeperModel model = MinesweeperModel.getInstance();

    btnPlay.setOnClickListener(v -> {
      MusicService.getInstance().play(R.raw.select, true);
      model.setSize(boardSize);
      model.setTotalMines(totalMines);
      model.resetModel();

      Intent playIntent = new Intent(this, PlayActivity.class);
      playIntent.putExtra("timerMinutes", timerMinutes);
      startActivity(playIntent);
    });
  }

  private void initTimeSlider() {
    final TextView minutesTextView = findViewById(R.id.minutesTextView);
    minutesTextView.setText(getApplicationContext().getString(R.string.time_challenge, String.valueOf(timerMinutes)));

    final FluidSlider timeSlider = findViewById(R.id.timeSlider);
    timeSlider.setStartText(String.valueOf(MINUTES_MIN));
    timeSlider.setEndText(String.valueOf(MINUTES_MAX));
    timeSlider.setBubbleText(String.valueOf(timerMinutes));
    timeSlider.setPosition(MINUTES_DEFAULT);

    timeSlider.setPositionListener(value -> {
      timerMinutes = (int) (value * (MINUTES_MAX - MINUTES_MIN)) + MINUTES_MIN;
      minutesTextView.setText(getApplicationContext().getString(R.string.time_challenge, String.valueOf(timerMinutes)));
      timeSlider.setBubbleText(String.valueOf(timerMinutes));
      return Unit.INSTANCE;
    });
  }

  private void initSizeSlider() {
    final TextView sizeTextView = findViewById(R.id.sizeTextView);
    sizeTextView.setText(getApplicationContext().getString(R.string.board_size, String.valueOf(boardSize)));

    final TextView minesTextView = findViewById(R.id.minesTextView);
    minesTextView.setText(getApplicationContext().getString(R.string.mines, String.valueOf(totalMines)));

    final FluidSlider sizeSlider = findViewById(R.id.sizeSlider);
    sizeSlider.setStartText(String.valueOf(BOARD_MIN_SIZE));
    sizeSlider.setEndText(String.valueOf(BOARD_MAX_SIZE));
    sizeSlider.setBubbleText(String.valueOf(boardSize));
    sizeSlider.setPosition(0);

    sizeSlider.setPositionListener(value -> {
      boardSize = (int) (value * (BOARD_MAX_SIZE - BOARD_MIN_SIZE)) + BOARD_MIN_SIZE;
      sizeTextView.setText(getApplicationContext().getString(R.string.board_size, String.valueOf(boardSize)));
      sizeSlider.setBubbleText(String.valueOf(boardSize));

      if (totalMines > boardSize * boardSize) {
        totalMines = boardSize * boardSize;
        minesTextView.setText(getApplicationContext().getString(R.string.mines, String.valueOf(totalMines)));
      }

      return Unit.INSTANCE;
    });
  }

  private View.OnClickListener createFloatBtnListener(final boolean isIncrease) {
    final TextView minesTextView = findViewById(R.id.minesTextView);

    return view -> {
      totalMines += isIncrease ? 1 : -1;
      totalMines = (totalMines < MINES_MIN) ? MINES_MIN : Math.min(totalMines, boardSize * boardSize);
      minesTextView.setText(getApplicationContext().getString(R.string.mines, String.valueOf(totalMines)));
    };
  }


}
