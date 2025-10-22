package CIA.app.dtos;

public class ChangePassword {
    private String passwordCurrent;
    private String passwordNew;
    
    public ChangePassword() {
    }

    public ChangePassword(String passwordCurrent, String passwordNew) {
        this.passwordCurrent = passwordCurrent;
        this.passwordNew = passwordNew;
    }

    public String getPasswordCurrent() {
        return passwordCurrent;
    }

    public void setPasswordCurrent(String passwordCurrent) {
        this.passwordCurrent = passwordCurrent;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }
}
