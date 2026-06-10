package manajemenmusik.model;

/**
 * Class RegularUser — menerapkan Inheritance (extends User) dan Polymorphism (override getRole).
 * Digunakan untuk pengguna biasa (bukan Admin).
 */
public class RegularUser extends User {

    public RegularUser(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "User";
    }
}
