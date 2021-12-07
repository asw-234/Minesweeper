package uni.minesweeper.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import uni.minesweeper.R;
import uni.minesweeper.Utils;
import uni.minesweeper.activities.RankingActivity;
import uni.minesweeper.activities.auth.LoginActivity;
import uni.minesweeper.activities.play.IntroActivity;

public class MenuActivity extends AppCompatActivity {


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);

    findViewById(R.id.btnPlay).setOnClickListener(view -> Utils.sendToActivity(this, IntroActivity.class, 0));
    findViewById(R.id.btnLeaderboard).setOnClickListener(view -> Utils.sendToActivity(this, RankingActivity.class, 0));
    findViewById(R.id.btnLogout).setOnClickListener(view -> logout());
  }

  private void logout() {
    Utils.sendToActivity(this, LoginActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
  }
}
