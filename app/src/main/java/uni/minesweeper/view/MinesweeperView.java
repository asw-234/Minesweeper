package uni.minesweeper.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.gc.materialdesign.widgets.Dialog;

import uni.minesweeper.IntroActivity;
import uni.minesweeper.R;
import uni.minesweeper.model.MinesweeperModel;

public class MinesweeperView extends View {
  private final Paint linePaint;
  private final Paint backgroundPaint;
  private final Paint paddingPaint;
  private final Paint paintText;

  private final MinesweeperModel model;

  private final Drawable bombDrawable;
  private final Drawable bombLossDrawable;

  private final Drawable flagDrawable;
  private final Drawable flagLossDrawable;

  private final Rect imageBounds;

  private boolean isGameOver = false;

  @Override
  protected Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();
    bundle.putParcelable("state", super.onSaveInstanceState());
    bundle.putBoolean("isGameOver", isGameOver);
    return bundle;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    if (state != null) {
      Bundle bundle = (Bundle) state;
      isGameOver = bundle.getBoolean("isGameOver");
      state = bundle.getParcelable("state");
    }

    super.onRestoreInstanceState(state);
  }

  public MinesweeperView(Context context, AttributeSet attrs) {
    super(context, attrs);

    model = MinesweeperModel.getSingletonInstance();

    bombDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bomb, null);
    bombLossDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_explosion, null);
    flagDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_flag, null);
    flagLossDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_broken_flag, null);
    imageBounds = new Rect(0, 0, 0, 0);

    linePaint = new Paint();
    linePaint.setColor(Color.DKGRAY);
    linePaint.setStyle(Paint.Style.STROKE);
    linePaint.setStrokeWidth(5);

    backgroundPaint = new Paint();
    backgroundPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorBg, null));
    backgroundPaint.setStyle(Paint.Style.FILL);

    paddingPaint = new Paint();
    paddingPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPadding, null));
    paddingPaint.setStyle(Paint.Style.FILL);

    paintText = new Paint();
    paintText.setColor(Color.LTGRAY);
  }


  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

    final int size = model.getBoardSize();
    final float stepX = ((float) getWidth()) / size;
    final float stepY = ((float) getHeight()) / size;

    // Draw grid
    for (int i = 0; i <= size; ++i) {
      float startX = i * stepX;
      float startY = i * stepY;
      canvas.drawLine(startX, 0, startX, getHeight(), linePaint);
      canvas.drawLine(0, startY, getWidth(), startY, linePaint);
    }

    boolean allBombsFlagged = true;

    // Draw tiles
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        int left = (int) (j * stepX);
        int right = (int) ((j + 1) * stepX);
        int top = (int) (i * stepY);
        int bottom = (int) ((i + 1) * stepY);

        imageBounds.set(left, top, right, bottom);
        MinesweeperModel.ETileType currTile = model.getTile(i, j);

        if (currTile == MinesweeperModel.ETileType.SAFE_CHECKED) {
          final int coloredPadding = 10;
          left += coloredPadding;
          right -= coloredPadding;
          top += coloredPadding;
          bottom -= coloredPadding;
          imageBounds.set(left, top, right, bottom);
          canvas.drawRect(imageBounds, paddingPaint);

          if (model.getNearbyBombs(i, j) != 0) {
            canvas.drawText(
              String.valueOf(model.getNearbyBombs(i, j)),
              left + stepX / 4,
              bottom - stepY / 4,
              paintText
            );
          }
        } else if (currTile == MinesweeperModel.ETileType.BOMB) {
          allBombsFlagged = false;

          if (isGameOver) {
            bombDrawable.setBounds(imageBounds);
            bombDrawable.draw(canvas);
          }
        } else if (currTile == MinesweeperModel.ETileType.BOMB_LOSS) {
          bombLossDrawable.setBounds(imageBounds);
          bombLossDrawable.draw(canvas);
        } else if (currTile == MinesweeperModel.ETileType.FLAG) {
          flagDrawable.setBounds(imageBounds);
          flagDrawable.draw(canvas);
        } else if (currTile == MinesweeperModel.ETileType.FLAG_LOSS) {
          flagLossDrawable.setBounds(imageBounds);
          flagLossDrawable.draw(canvas);
        }
      }
    }

    if (!isGameOver && allBombsFlagged)
      endGame(false);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    paintText.setTextSize((float) (getHeight() / model.getBoardSize()) / 2);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!isGameOver && event.getAction() == MotionEvent.ACTION_UP) {
      int clickedRow = ((int) event.getY()) / (getHeight() / model.getBoardSize());
      int clickedCol = ((int) event.getX()) / (getWidth() / model.getBoardSize());

      switch (model.getTile(clickedRow, clickedCol)) {
        case SAFE:
          if (model.isFlagMode()) {
            model.setTile(clickedRow, clickedCol, MinesweeperModel.ETileType.FLAG_LOSS);
            endGame(true);
          } else {
            recursiveExpand(clickedRow, clickedCol);
          }

          break;
        case BOMB:
          if (model.isFlagMode()) {
            model.setTile(clickedRow, clickedCol, MinesweeperModel.ETileType.FLAG);
          } else {
            model.setTile(clickedRow, clickedCol, MinesweeperModel.ETileType.BOMB_LOSS);
            endGame(true);
          }

          break;
      }

      invalidate();
    }

    return true;
  }

  private void recursiveExpand(int row, int col) {
    if (model.getTile(row, col) == MinesweeperModel.ETileType.SAFE_CHECKED)
      return;

    if (model.getTile(row, col) == MinesweeperModel.ETileType.SAFE)
      model.setTile(row, col, MinesweeperModel.ETileType.SAFE_CHECKED);

    if (model.getNearbyBombs(row, col) == 0) {
      for (int i = row - 1; i <= row + 1; ++i) {
        if (i < 0 || i >= model.getBoardSize())
          continue;

        for (int j = col - 1; j <= col + 1; ++j) {
          if (j < 0 || j >= model.getBoardSize())
            continue;

          recursiveExpand(i, j);
        }
      }
    }
  }

  private void endGame(boolean isLoss) {
    isGameOver = true;

    final String buttonText = "Play again";

    final String message =
      (isLoss ? "Whoops! You lost!" : "Congratulations! You won the game!") +
        "\n\nPress \"" + buttonText + "\" to start a new game.";

    Dialog dialog = new Dialog(getContext(), "Game Over!", message);
    dialog.show();
    dialog.getButtonAccept().setText(buttonText);

    dialog.setOnAcceptButtonClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent toIntroActivity = new Intent(getContext(), IntroActivity.class);
        toIntroActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // https://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_NEW_TASK
        getContext().startActivity(toIntroActivity);
      }
    });
  }
}
