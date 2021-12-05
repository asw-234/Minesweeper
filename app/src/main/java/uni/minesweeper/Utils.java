package uni.minesweeper;

import android.content.Context;
import android.content.Intent;

import java.util.regex.Pattern;


public class Utils {
  private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
    "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
    Pattern.CASE_INSENSITIVE
  );

  public static boolean validateEmail(final String emailStr) {
    return VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr).find();
  }

  public static <T> void sendToActivity(final Context context, final Class<T> destinationClazz) {
    Intent intent = new Intent(context, destinationClazz);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}