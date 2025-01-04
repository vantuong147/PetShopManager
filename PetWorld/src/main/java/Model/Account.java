package Model;

public class Account {
    public int id;
    public String username;
    public String password;
    public String accountType;

    public Account(int id, String username, String password, String accountType) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accountType = accountType;
    }

    // Getters and Setters
}
