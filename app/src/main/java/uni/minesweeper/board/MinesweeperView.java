package uni.minesweeper.board;

import android.app.AlertDialog;
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

import uni.minesweeper.Utils;
import uni.minesweeper.activities.play.IntroActivity;
import uni.minesweeper.R;
import uni.minesweeper.services.MusicService;

public class MinesweeperView extends View {
  private static final int MARGIN = 2;
  private static final int PADDING = 4;

  private final Paint linePaint;
  private final Paint backgroundPaint;
  private final Paint paintText;

  private final MinesweeperModel model;

  private final Drawable bgBoardDrawable;
  private final Drawable dirtDrawable;
  private final Drawable dirtDrawableDark;

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

    model = MinesweeperModel.getInstance();

    bgBoardDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_board, null);
    dirtDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_dirt, null);
    dirtDrawableDark = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_dirt_dark, null);

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

    paintText = new Paint();
    paintText.setColor(Color.LTGRAY);
  }


  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    bgBoardDrawable.setBounds(0,0, getWidth(), getHeight());
    bgBoardDrawable.draw(canvas);

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

        imageBounds.set(left+MARGIN, top+MARGIN, right-MARGIN, bottom-MARGIN);

        switch (model.getTile(i, j)) {
          case SAFE: {
            dirtDrawable.setBounds(imageBounds);
            dirtDrawable.draw(canvas);
            break;
          }
          case FLAG: {
            flagDrawable.setBounds(imageBounds);
            flagDrawable.draw(canvas);
            break;
          }
          case FLAG_LOSS: {
            flagLossDrawable.setBounds(imageBounds);
            flagLossDrawable.draw(canvas);
            break;
          }
          case BOMB_LOSS: {
            bombLossDrawable.setBounds(imageBounds);
            bombLossDrawable.draw(canvas);
            break;
          }
          case BOMB: {
            allBombsFlagged = false;

            if (isGameOver) {
              bombDrawable.setBounds(imageBounds);
              bombDrawable.draw(canvas);
            } else {
              dirtDrawable.setBounds(imageBounds);
              dirtDrawable.draw(canvas);
            }

            break;
          }
          case SAFE_CHECKED: {
            imageBounds.set(left+PADDING, top+PADDING, right-PADDING, bottom-PADDING);
            dirtDrawableDark.setBounds(imageBounds);
            dirtDrawableDark.draw(canvas);

            if (model.getNearbyBombs(i, j) != 0) {
              final String text = String.valueOf(model.getNearbyBombs(i, j));
              final float textSize = paintText.measureText(text);

              canvas.drawText(
                text,
                imageBounds.centerX() - textSize/2,
                imageBounds.centerY() + textSize/2,
                paintText
              );
            }

            break;
          }
        }
      }
    }

    if (!isGameOver && allBombsFlagged) {
      MusicService.getInstance().stop();
      MusicService.getInstance().play(R.raw.victory, true);
      endGame(false);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    paintText.setTextSize((float) (getHeight() / model.getBoardSize()) / 2);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!isGameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
      int clickedRow = ((int) event.getY()) / (getHeight() / model.getBoardSize());
      int clickedCol = ((int) event.getX()) / (getWidth() / model.getBoardSize());

      switch (model.getTile(clickedRow, clickedCol)) {
        case SAFE: {
          if (model.isFlagMode()) {
            MusicService.getInstance().stop();
            MusicService.getInstance().play(R.raw.loss, true);
            model.setTile(clickedRow, clickedCol, TileType.FLAG_LOSS);
            endGame(true);
          } else {
            MusicService.getInstance().play(R.raw.dig, true);
            recursiveExpand(clickedRow, clickedCol);
          }

          break;
        }
        case BOMB: {
          if (model.isFlagMode()) {
            MusicService.getInstance().play(R.raw.put_flag, true);
            model.setTile(clickedRow, clickedCol, TileType.FLAG);
          } else {
            MusicService.getInstance().stop();
            MusicService.getInstance().play(R.raw.explosion, true);
            model.setTile(clickedRow, clickedCol, TileType.BOMB_LOSS);
            endGame(true);
          }

          break;
        }
      }

      invalidate();
    }

    return true;
  }

  private void recursiveExpand(int row, int col) {
    if (model.getTile(row, col) == TileType.SAFE_CHECKED) {
      return;
    }

    if (model.getTile(row, col) == TileType.SAFE) {
      model.setTile(row, col, TileType.SAFE_CHECKED);
    }

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

  public void endGame(boolean isLoss) {
    isGameOver = true;

    final String buttonText = "Play again";

    final String message =
      (isLoss ? "Whoops! You lost!" : "Congratulations! You won the game!") +
        "\n\nPress \"" + buttonText + "\" to start a new game.";

    new AlertDialog.Builder(getContext())
      .setTitle(isLoss ? "Game over!" : "Congratulations!")
      .setMessage(message)
      .setCancelable(false)
      .setPositiveButton(buttonText, (dialogInterface, i) ->
        Utils.sendToActivity(getContext(), IntroActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP)
      )
      .show();
  }
}
