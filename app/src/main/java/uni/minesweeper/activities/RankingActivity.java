package uni.minesweeper.activities;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import uni.minesweeper.R;
import uni.minesweeper.adapter.RecyclerAdapter;
import uni.minesweeper.database.FirebaseManager;


public class RankingActivity extends AbstractActivity {
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
  }

  @Override
  public void onBackPressed() {
    finish();
    super.onBackPressed();
  }
}