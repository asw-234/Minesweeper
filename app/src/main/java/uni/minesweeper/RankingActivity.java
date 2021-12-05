package uni.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uni.minesweeper.adapter.RecyclerAdapter;

public class RankingActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userUID;
    List<UserClass> users = new ArrayList<UserClass>();

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);


        Intent intent = getIntent();
        userUID = intent.getStringExtra("user_key");

        firebaseDatabase = FirebaseDatabase.getInstance("https://bmeminesweeperhw-default-rtdb.europe-west1.firebasedatabase.app/");


        recyclerView = findViewById(R.id.recyclerID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        readUsersFromFirebase();

        btnBack = findViewById(R.id.ibBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RankingActivity.this,IntroActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("user_key",userUID);
                startActivity(intent1);
            }
        });


    }

    private void readUsersFromFirebase(){
        databaseReference = firebaseDatabase.getReference().child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    users.add(ds.getValue(UserClass.class));
                }

                Collections.sort(users, new Comparator<UserClass>() {

                    public int compare(UserClass u1, UserClass u2) {
                        return u2.getScore().compareTo(u1.getScore());
                    }
                });

                recyclerAdapter = new RecyclerAdapter(RankingActivity.this, users);
                recyclerView.setAdapter(recyclerAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}