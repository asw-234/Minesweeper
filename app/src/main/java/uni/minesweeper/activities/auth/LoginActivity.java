package uni.minesweeper.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import uni.minesweeper.activities.MenuActivity;
import uni.minesweeper.R;
import uni.minesweeper.Utils;
import uni.minesweeper.database.FirebaseManager;

public class LoginActivity extends AppCompatActivity {

  private EditText inputEmail, inputPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    inputEmail = findViewById(R.id.edtLoginEmail);
    inputPassword = findViewById(R.id.edtLoginPwd);

    findViewById(R.id.btnLogin).setOnClickListener(view -> performLogin());
    findViewById(R.id.createAccount).setOnClickListener(view ->
      Utils.sendToActivity(this, RegisterActivity.class, 0)
    );
  }

  private void performLogin() {
    final String email = inputEmail.getText().toString();
    final String password = inputPassword.getText().toString();

    if (!Utils.validateEmail(email)) {
      inputEmail.setError("Enter valid email address*");
    } else if (password.length() < 6) {
      inputPassword.setError("Enter valid password*");
    } else {
      FirebaseManager.getInstance().performLogin(email, password, task -> {
        if (task.isSuccessful()) {
          Utils.sendToActivity(this, MenuActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

}