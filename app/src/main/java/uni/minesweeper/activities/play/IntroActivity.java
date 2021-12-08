package uni.minesweeper.activities.play;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.ramotion.fluidslider.FluidSlider;

import kotlin.Unit;
import uni.minesweeper.R;
import uni.minesweeper.activities.AbstractActivity;
import uni.minesweeper.activities.RankingActivity;
import uni.minesweeper.Utils;
import uni.minesweeper.model.MinesweeperModel;

public class IntroActivity extends AbstractActivity {

  private static final int BOARD_MAX_SIZE = 15;
  private static final int BOARD_MIN_SIZE = 5;
  private static final int MINES_MIN = 1;

  private static final int MINES_DEFAULT = 3;
  private static final int SIZE_DEFAULT = 5;

  private int totalMines = MINES_DEFAULT;
  private int boardSize = SIZE_DEFAULT;

  @Override
  public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
    savedInstanceState.putInt("boardSize", boardSize);
    savedInstanceState.putInt("totalMines", totalMines);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);
    Toolbar toolbar = findViewById(R.id.toolbarID);
    setSupportActionBar(toolbar);

    final TextView sizeTextView = findViewById(R.id.sizeTextView);
    final TextView minesTextView = findViewById(R.id.minesTextView);

    if (savedInstanceState != null) {
      boardSize = savedInstanceState.getInt("boardSize");
      totalMines = savedInstanceState.getInt("totalMines");
    }

    sizeTextView.setText(getApplicationContext().getString(R.string.board_size, String.valueOf(boardSize)));
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
        minesTextView.setText(String.valueOf(totalMines));
      }

      return Unit.INSTANCE;
    });

    ButtonFloat btnIncrease = findViewById(R.id.btnIncrease);
    ButtonFloat btnDecrease = findViewById(R.id.btnDecrease);
    btnDecrease.setOnClickListener(createFloatBtnListener(false));
    btnIncrease.setOnClickListener(createFloatBtnListener(true));

    final ButtonRectangle btnPlay = findViewById(R.id.btnPlay);
    final MinesweeperModel model = MinesweeperModel.getInstance();

    btnPlay.setOnClickListener(v -> {
      model.setSize(boardSize);
      model.setTotalMines(totalMines);
      model.resetModel();
      Utils.sendToActivity(this, PlayActivity.class, 0);
    });
  }

  private View.OnClickListener createFloatBtnListener(final boolean isIncrease) {
    final TextView minesTextView = findViewById(R.id.minesTextView);

    return view -> {
      totalMines += isIncrease ? 1 : -1;
      totalMines = (totalMines < MINES_MIN) ? MINES_MIN : Math.min(totalMines, boardSize * boardSize);
      minesTextView.setText(String.valueOf(totalMines));
    };
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    Utils.sendToActivity(this, RankingActivity.class, 0);
    return true;
  }

}
