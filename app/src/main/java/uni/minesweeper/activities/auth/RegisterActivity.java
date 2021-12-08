package uni.minesweeper.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import uni.minesweeper.R;
import uni.minesweeper.Utils;
import uni.minesweeper.activities.AbstractActivity;
import uni.minesweeper.activities.MenuActivity;
import uni.minesweeper.database.FirebaseManager;


public class RegisterActivity extends AbstractActivity {
  private EditText inputEmail;
  private EditText inputPassword;
  private EditText confirmPwd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    inputEmail = findViewById(R.id.edtEmail);
    inputPassword = findViewById(R.id.edtPwd);
    confirmPwd = findViewById(R.id.edtConfirmPwd);

    findViewById(R.id.btnRegister).setOnClickListener(v -> performRegistration());
  }

  private void performRegistration() {
    final String email = inputEmail.getText().toString();
    final String password = inputPassword.getText().toString();
    final String confPassword = confirmPwd.getText().toString();

    if (!Utils.validateEmail(email)) {
      inputEmail.setError("Enter valid email address*");
    } else if (password.length() < 6) {
      inputPassword.setError("Enter valid password*");
    } else if (!password.equals(confPassword)) {
      confirmPwd.setError("No match with previous password*");
    } else {
      FirebaseManager.getInstance().performRegistration(email, password, task -> {
        if (task.isSuccessful()) {
          Utils.sendToActivity(this, MenuActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          Toast.makeText(RegisterActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(RegisterActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

}