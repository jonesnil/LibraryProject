
public class LibrarianDriver {

	public static void main(String[] args) {
		Librarian test = new Librarian("testUsername", "testPassword");
		System.out.println(test.getUsername());
		System.out.println(test.getPassword());
		test.setUsername("newUsername");
		test.setPassword("newPassword");
		System.out.println(test.getUsername());
		System.out.println(test.getPassword());
	}

}
