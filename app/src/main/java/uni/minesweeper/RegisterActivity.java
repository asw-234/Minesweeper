package uni.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText inputEmail, inputPassword, confirmPwd;
    ButtonRectangle btnRegister;
    UserClass user;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
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
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAut();
            }
        });
    }

    private void performAut() {
        String email = inputEmail.getText().toString();
        String pwd = inputPassword.getText().toString();
        String cnfmPwd = confirmPwd.getText().toString();

        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        if(!validateRegex(email,VALID_EMAIL_ADDRESS_REGEX)){
            inputEmail.setError("Enter valid email address*");
        }
        else if(pwd.isEmpty() || pwd.length()<6){
            inputPassword.setError("Enter valid password*");
        }
        else if(!pwd.equals(cnfmPwd)){
            confirmPwd.setError("No match with previous password*");
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        user = new UserClass(email,pwd,"15");
                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                        passUserToNextActivity();
                        Toast.makeText(RegisterActivity.this,"Successful Registration",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(RegisterActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    private void passUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static boolean validateRegex(String emailStr, Pattern validRegex) {
        Matcher matcher = validRegex.matcher(emailStr);
        return matcher.find();
    }
}