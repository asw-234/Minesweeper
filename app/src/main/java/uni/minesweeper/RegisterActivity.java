package uni.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uni.minesweeper.state.UserClass;

public class RegisterActivity extends AppCompatActivity {


  private EditText inputEmail;
  private EditText inputPassword;
  private EditText confirmPwd;

  private ButtonRectangle btnRegister;
  private UserClass user;

  private FirebaseAuth firebaseAuth;
  private FirebaseUser firebaseUser;
  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    firebaseAuth = FirebaseAuth.getInstance();
    firebaseUser = firebaseAuth.getCurrentUser();

    firebaseDatabase = FirebaseDatabase.getInstance("https://bmeminesweeperhw-default-rtdb.europe-west1.firebasedatabase.app/");
    databaseReference = firebaseDatabase.getReference("users");

    inputEmail = findViewById(R.id.edtEmail);
    inputPassword = findViewById(R.id.edtPwd);
    confirmPwd = findViewById(R.id.edtConfirmPwd);
    btnRegister = findViewById(R.id.btnRegister);
    btnRegister.setOnClickListener(v -> performAut());
  }

  private void performAut() {
    final String email = inputEmail.getText().toString();
    final String pwd = inputPassword.getText().toString();
    final String cnfmPwd = confirmPwd.getText().toString();

    if (!Utils.validateEmail(email)) {
      inputEmail.setError("Enter valid email address*");
    } else if (pwd.length() < 6) {
      inputPassword.setError("Enter valid password*");
    } else if (!pwd.equals(cnfmPwd)) {
      confirmPwd.setError("No match with previous password*");
    } else {
      firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          user = new UserClass(email, "15");
          databaseReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).setValue(user);
          passUserToNextActivity();
          Toast.makeText(RegisterActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
        } else
          Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();

      });
    }
  }

  private void passUserToNextActivity() {
    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

}