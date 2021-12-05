package uni.minesweeper;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import uni.minesweeper.state.FirebaseManager;

public class LoginActivity extends AppCompatActivity {

  private EditText inputEmail, inputPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    inputEmail = findViewById(R.id.edtLoginEmail);
    inputPassword = findViewById(R.id.edtLoginPwd);

    findViewById(R.id.btnLogin).setOnClickListener(view -> performLogin());
    findViewById(R.id.createAccount).setOnClickListener(view -> Utils.sendToActivity(this, RegisterActivity.class));
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
          FirebaseManager.getInstance().initUser();
          Utils.sendToActivity(this, IntroActivity.class);
          Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

}