package uni.minesweeper.database;


public class UserClass {
  private String email;
  private String score;

  public UserClass() {
  }

  public UserClass(final String email, final String score) {
    this.email = email;
    this.score = score;
  }

  public String getEmail() {
    return email;
  }

  public String getScore() {
    return score;
  }

}
