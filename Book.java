import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.Random;


public class Book implements Comparable {
	private String authorName;
	private String bookName;
	private String ISBN;
	private String ID;
	private LocalDateTime lendingDate;
	private LocalDateTime dueDate;
	
	public String getAuthorName() {
		return this.authorName;
	}
	public String getBookName() {
		return this.bookName;
	}
	public String getISBN() {
		return this.ISBN;
	}
	public String getID() {
		return this.ID;
	}
	public LocalDateTime getLendingDate(){
		return this.lendingDate;
	}
	public LocalDateTime getDueDate(){
		return this.dueDate;
	}
	
	//set lending date manually
	public void setLendingDate(LocalDateTime date) {
		this.lendingDate = date;
	}
	//sets date at current time
	public void setLendingDateAtCurrentTime() {
		this.lendingDate = LocalDateTime.now();
	}
	//sets the due date to 2 weeks (for testing)
	public void setDueDateAuto() {
		this.dueDate = this.lendingDate.plusDays(14);
	}
	//manually sets the due date
	public void setDuteDate(LocalDateTime date) 
	{
		this.dueDate = date;
	}
	
	// sets ID, lendingDate and dueDate to null. used for returning a book
	public void nullVars() {
		ID = null;
		lendingDate = null;
		dueDate = null;
	}
	
	// sets ID, lendingDate and dueDate when renting a book
	public void setVars(LocalDateTime due) {
		ID = this.generateID();
		lendingDate = LocalDateTime.now();
		dueDate = due;
	}
	// sets ID, lendingDate and dueDate when reading from the file
	public void initVars(String id, String lendDate, String due) {
		ID = id;
		lendingDate = LocalDateTime.parse(lendDate);
		dueDate = LocalDateTime.parse(due);
	}
	
	
	public int compareTo(Object book) {
		Book book2 = (Book)book;
		int compare = 0;
		if(this.lendingDate.isBefore(book2.getLendingDate())) {compare = 1;}
		else if(this.lendingDate.equals(book2.lendingDate)){compare = 0;}
		else if(this.lendingDate.isAfter(book2.lendingDate)) {compare =-1;}
		return compare;
	}
	
	public boolean equals(Object obj) {
		return (this.bookName.equals(((Book)obj).bookName));
	}
	
	public Book(String authorName, String bookName, String ISBN) {
		this.authorName = authorName;
		this.bookName = bookName;
		this.ISBN = ISBN;
		
	}
	
	public Book(String authorName, String bookName, String ISBN, String ID, LocalDateTime lendingDate, LocalDateTime dueDate) {
		this.authorName = authorName;
		this.bookName = bookName;
		this.ISBN = ISBN;
		this.ID = ID;
		this.lendingDate = lendingDate;
		this.dueDate = dueDate;
	}
	
	public String generateID(){
		Random r = new Random();
		String alphabet = "1234567890abcdefghijklmnopqrstuvwxyz"; //A list of characters usable in the id
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMddhhmmss");// all number representation of the date
		String ID = LocalDateTime.now().format(formatter).toString();// ID is partially currentTime
		for (int i = 0; i < 5; i++) {
			ID += alphabet.charAt(r.nextInt(alphabet.length()));// generate 5 random characters at the end of the ID to make it unique
		}
		return ID;
	}
	
	public String displayInfo() {
		return "Author: " + this.authorName + "\n" +
				"Book Name: " + this.bookName + "\n" + 
				"ISBN: " + this.ISBN;
	}

}