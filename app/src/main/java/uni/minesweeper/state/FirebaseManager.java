package uni.minesweeper.state;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class FirebaseManager {
  private static final String DB_ENDPOINT = "https://bmeminesweeperhw-default-rtdb.europe-west1.firebasedatabase.app/";

  private static final FirebaseManager INSTANCE = new FirebaseManager();

  private final FirebaseDatabase firebaseDatabase;
  private final FirebaseAuth firebaseAuth;
  private UserClass user;

  private FirebaseManager() {
    firebaseDatabase = FirebaseDatabase.getInstance(DB_ENDPOINT);
    firebaseAuth = FirebaseAuth.getInstance();
  }

  public static FirebaseManager getInstance() {
    return INSTANCE;
  }

  public void initUser() {
    final FirebaseUser dbUser = firebaseAuth.getCurrentUser();

    if (dbUser == null) {
      throw new IllegalStateException("Cannot init user without having done authentication first!");
    }

    final DatabaseReference dbRef = firebaseDatabase.getReference().child("users").child(dbUser.getUid());

    dbRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        user = snapshot.getValue(UserClass.class);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
      }
    });
  }

  public void performLogin(final String email, final String password, final OnCompleteListener<AuthResult> callback) {
    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback);
  }

  public void performRegistration(final String email, final String password, final OnCompleteListener<AuthResult> callback) {
    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        user = new UserClass(email, "15");
        final String userUID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        firebaseDatabase.getReference().child("users").child(userUID).setValue(user);
      }

      callback.onComplete(task);
    });
  }

  public UserClass getUser() {
    return user;
  }

}
