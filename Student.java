import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Student extends User {
	private ArrayList<Book> rentedBooks;
	private int balance;
	private LocalDateTime lastOnline;
	
	public Student(String username, String password) {
		super(username, password);
		rentedBooks = new ArrayList<>();
	}
	public Student(String username, String password, String lastonline) {
		super(username, password);
		rentedBooks = new ArrayList<>();
		lastOnline = LocalDateTime.parse(lastonline);
	}
	
	public Student(String username, String password, String lastonline, int balance) {
		super(username, password);
		rentedBooks = new ArrayList<>();
		this.balance = balance;
		lastOnline = LocalDateTime.parse(lastonline);
	}

	public int getBalance(){
		return balance;
	}
	public void setBalance(int b) {
		balance = b;
	}
	
	public void subtractBalance(int bal) {
		this.balance -= bal;
	}
	
	public ArrayList<Book> getRentedBooks() {
		return rentedBooks;
	}
	public void addRentedBooks(Book book) {
		rentedBooks.add(book);
	}
	
	public LocalDateTime getLastOnline() {
		return this.lastOnline;
	}
	public void setLastOnline(LocalDateTime date) {
		lastOnline = date;
	}
	
	public void addBalance() {
		LocalDateTime now = LocalDateTime.now();
		
		for (Book book : rentedBooks) {
			LocalDateTime dueDate = book.getDueDate();
			if (now.isAfter(dueDate)) {
				if (lastOnline.isBefore(dueDate)) {
					balance += daysBetween(dueDate, now);
				} else {
					balance += daysBetween(lastOnline, now);
				}
			}
		}
	}
	
	private static int daysBetween(LocalDateTime d1, LocalDateTime d2) {
		long days = d1.until(d2, ChronoUnit.DAYS);
		if (days < 0) {
			days = 0;
		}
		int elapsedDays = (int)days;
		return elapsedDays;
		
	}
	
}