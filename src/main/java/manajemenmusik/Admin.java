package manajemenmusik;

public class Admin extends User {

    public Admin(String username) {
        super(username);
    }

    @Override
    public void tampilkanPeran() {
        System.out.println("Peran: Admin");
    }
}