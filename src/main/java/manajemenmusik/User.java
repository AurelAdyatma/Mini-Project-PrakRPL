package manajemenmusik;

public class User {
    protected String username;

    public User(String username) {
        this.username = username;
    }

    public void tampilkanPeran() {
        System.out.println("Peran: User");
    }
}