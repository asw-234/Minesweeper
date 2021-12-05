package uni.minesweeper;

import java.util.UUID;

public class UserClass {
    String email;

    String password;
    String score;

    public UserClass(){
    }
    public UserClass(String email, String password, String score){
        this.email = email;
        this.password = password;
        this.score = score;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }



}
