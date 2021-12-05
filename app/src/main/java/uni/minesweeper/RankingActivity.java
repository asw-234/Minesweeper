package uni.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import uni.minesweeper.adapter.RecyclerAdapter;
import uni.minesweeper.database.UserClass;

public class RankingActivity extends AppCompatActivity {

  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;
  private String userUID;
  private List<UserClass> users = new ArrayList<>();

  private RecyclerView recyclerView;
  private RecyclerAdapter recyclerAdapter;

  private ImageButton btnBack;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ranking);

    final Intent intent = getIntent();
    userUID = intent.getStringExtra("user_key");

    recyclerView = findViewById(R.id.recyclerID);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    readUsersFromFirebase();

    btnBack = findViewById(R.id.ibBack);
    btnBack.setOnClickListener(view -> {
      Intent backIntent = new Intent(RankingActivity.this, IntroActivity.class);
      backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      backIntent.putExtra("user_key", userUID);
      startActivity(backIntent);
    });
  }

  private void readUsersFromFirebase() {
    databaseReference = firebaseDatabase.getReference().child("users");
    databaseReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot ds : snapshot.getChildren()) {
          users.add(ds.getValue(UserClass.class));
        }

        users.sort((u1, u2) -> u2.getScore().compareTo(u1.getScore()));

        recyclerAdapter = new RecyclerAdapter(RankingActivity.this, users);
        recyclerView.setAdapter(recyclerAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {}
    });
  }
}