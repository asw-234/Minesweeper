package uni.minesweeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import uni.minesweeper.adapter.RecyclerAdapter;
import uni.minesweeper.database.FirebaseManager;

public class RankingActivity extends AppCompatActivity {
  private RecyclerView recyclerView;
  private RecyclerAdapter recyclerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ranking);

    recyclerView = findViewById(R.id.recyclerID);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    FirebaseManager.getInstance().fetchRankingUsers(rankingList -> {
      recyclerAdapter = new RecyclerAdapter(RankingActivity.this, rankingList);
      recyclerView.setAdapter(recyclerAdapter);
    });

    findViewById(R.id.ibBack).setOnClickListener(view ->
      Utils.sendToActivity(this, IntroActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
    );
  }

  @Override
  public void onBackPressed() {
    finish();
    super.onBackPressed();
  }
}