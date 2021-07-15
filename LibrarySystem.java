import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

	public class LibrarySystem {
		private ArrayList<Book> allBooks;
		private ArrayList<Book> availableBooks;
		private Map<Student, ArrayList<Book>> lentBooks;
		private ArrayList<User> users;

		public LibrarySystem() {
			allBooks = new ArrayList<>();
			availableBooks = new ArrayList<>();
			lentBooks = new HashMap<>();
			users = new ArrayList<>();
		}
		
		public LibrarySystem(File file) {
			allBooks = new ArrayList<>();
			availableBooks = new ArrayList<>();
			lentBooks = new HashMap<>();
			users = new ArrayList<>();
			try {
				Scanner scan = new Scanner(file);
				while(scan.hasNext()){
					String unparsedInput = scan.nextLine();
					if(unparsedInput.contains("+")) break;
					String[] parsedInput = unparsedInput.split("`");
					if(parsedInput[3].contains("S")) {
						Student student = new Student(parsedInput[0], parsedInput[1], parsedInput[2], Integer.parseInt(parsedInput[4]));
						users.add(student);
					}
					else users.add(new Librarian(parsedInput[0], parsedInput[1]));
					}
				int index = 0;
				while(scan.hasNext()){
					String unparsedInput = scan.nextLine();
					if(unparsedInput.contains("+")) break;
					String[] parsedInput = unparsedInput.split("`");
					this.addBook(parsedInput[0], parsedInput[1], parsedInput[2]);
					if(parsedInput[3].contains("N")) {
						availableBooks.remove(index);
						index -= 1;
					}
					index += 1;
					}
				ArrayList<Book> usedBooks = new ArrayList<>();// a list to check against to make sure unique books are added to lent books
				while(scan.hasNextLine()){
					String unparsedInput = scan.nextLine();
					if(unparsedInput.contains("+")) break;
					String[] parsedInput = unparsedInput.split("`");
					Student student = (Student)users.get(Integer.parseInt(parsedInput[0]));
					ArrayList<Book> books = searchBookName(parsedInput[1]);
					if (books!=null) {
						Book book = null;
						for (Book b1 : books) {
							boolean cons = false;// is true if used books contains the reference of b1
							for (Book b2 : usedBooks) {
								if (b2 == b1) cons = true;
							}
							if (!cons) {
								book = b1;// gets a new book from the books searched by name that is not in lent books yet
								usedBooks.add(b1);
								break;
							}
						}
						book.initVars(parsedInput[2], parsedInput[3], parsedInput[4]);
						if (!lentBooks.containsKey(student)) {
							ArrayList<Book> al = new ArrayList<Book>();
							al.add(book);
							lentBooks.put((Student)users.get(Integer.parseInt(parsedInput[0])), al);
						} else {
							ArrayList<Book> al = lentBooks.get(student);
							al.add(book);
							lentBooks.replace(student, al);
						}
						student.addRentedBooks(book);
					}
				}
				scan.close();
				
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			// updates to fines for all students
			for (User u : users) {
				if (u instanceof Student) {
					Student s = (Student)u;
					s.addBalance();
				}
			}
		}
		

		public void initialize() {
			System.out.println("Welcome to the Library!");
			System.out.println("Please login or register.");
			System.out.println("Type \"help\" for list of commands.");
			Scanner scan = new Scanner(System.in);
			User currentUser = null;
			// stops the invalid command message from appearing after entering login info since login info is read as a command once
			boolean suppressInvalCom = false;
			
			while (currentUser == null) {
				
				switch (scan.nextLine()) {
				
				case "login student":
					currentUser = initHelper(0, scan);
					suppressInvalCom = true;
					break;
				case "login librarian":
					currentUser = initHelper(1, scan);
					suppressInvalCom = true;
					break;
				case "register student":
					currentUser = initHelper(2, scan);
					suppressInvalCom = true;
					break;
				case "register librarian":
					currentUser = initHelper(3, scan);
					suppressInvalCom = true;
					break;
				case "help":
					System.out.println("COMMAND LIST:");
					System.out.println("login student");
					System.out.println("login librarian");
					System.out.println("register student");
					System.out.println("register librarian");
					System.out.println("quit");
					break;
				case "quit":
					System.out.println("Thank you for using our library!");
					System.out.println("Please come again soon.");
					scan.close();
					logout();
					break;
				
				default:
					// will not trigger if the user just tried to log in or register and thus is reading their password as the last input
					if (!suppressInvalCom) {
						System.out.println("INVALID COMMAND. Type \"help\" for list of commands.");
					}
					suppressInvalCom = false;
					break;
				}
			}
			run(currentUser, scan);
		}

		// asks for username and password to login or register
		private User initHelper(int info, Scanner scan) {
			// info is an int representation of which login/register function needs to be called
			// "`" and "+" are used for saving info to text so login info cannot contain either symbol
			System.out.println("Enter Username:");
			String username = scan.nextLine();
			if(username.contains("+") || username.contains("`")) 
			{
				System.out.println("INVALID USERNAME. CANNOT CONTAIN \"+\" or \"`\". Please try again.");
				return null;
			}
			
			System.out.println("Enter Password:");
			String password = scan.nextLine();
			if(password.contains("+") || password.contains("`")) 
			{
				System.out.println("INVALID PASSWORD. CANNOT CONTAIN \"+\" or \"`\". Please try again.");
				return null;
			}
			
			User currentUser = null;

			switch (info) {
			case 0:
				currentUser = loginStudent(username, password);
				break;
			case 1:
				currentUser = loginLibrarian(username, password);
				break;
			case 2:
				currentUser = registerStudent(username, password);
				break;
			case 3:
				currentUser = registerLibrarian(username, password);
				break;
			}
			if (currentUser == null) {
				System.out.println("Incorrect username or password.");
			}
			return currentUser;
		}

		public void run(User user, Scanner scan) {
			boolean loggedIn = true;
			boolean suppressInvalCom = false;// stops the invalid command message from appearing when it shouldn't
			System.out.println("Welcome "+user.getUsername());
			
			while (loggedIn) {
				if(scan.hasNextLine())
				switch (scan.nextLine()) {
				case "view all books":
					printBooks(allBooks, false);
					break;
				case "search by author name":
					System.out.println("Enter author name:");
					printBooks(searchAuthorName(scan.nextLine()), false);
					break;
				case "search by book name":
					System.out.println("Enter book name:");
					printBooks(searchBookName(scan.nextLine()), false);
					break;
				case "search by isbn":
					System.out.println("Enter ISBN:");
					printBooks(searchISBN(scan.nextLine()), false);
					break;
				case "logout":
					if (user instanceof Student) {
						Student student = (Student)user;
						student.setLastOnline(LocalDateTime.now());
					}
					logout();
					loggedIn = false;
					break;
				case "help":
					System.out.println("COMMAND LIST:");
					System.out.println("view all books");
					System.out.println("search by author name");
					System.out.println("search by book name");
					System.out.println("search by isbn");
					if (user instanceof Student) {
						System.out.println("rent");
						System.out.println("return");
						System.out.println("view my books");
						System.out.println("view fines");
						System.out.println("pay fines");
					}
					if (user instanceof Librarian) {
						System.out.println("add book");
						System.out.println("delete book");
					}
					System.out.println("logout");
					break;
				case "rent":
					if (user instanceof Student) {
						Student student = (Student)user;
						System.out.println("Please enter the name of the book you wish to rent:");
						// needs to change info from lists and access their info
						rentBook(student, scan.nextLine(), scan);
						suppressInvalCom = true;
					}
					break;
				case "return":
					if (user instanceof Student) {
						Student student = (Student)user;
						System.out.println("Please enter the rental ID of the book you wish to return:");
						// needs to change info from lists and access their info
						returnBook(student, scan.nextLine());
					}
					break;
				case "view my books":
					if (user instanceof Student) {
						Student student = (Student)user;
						printBooks(student.getRentedBooks(), true);
					}
					break;
				case "view fines":
					if (user instanceof Student) {
						Student student = (Student)user;
						System.out.print("You have a balance of ");
						int bal = student.getBalance();
						if (bal < 0) {
							System.out.print("-");
							bal = bal * -1;
						}
						System.out.println("$"+bal);
					}
					break;
				case "pay fines":
					if (user instanceof Student) {
						Student student = (Student)user;
						System.out.println("Enter amount you wish to pay:");
						int amount = scan.nextInt();
						if(student.getBalance() - amount < 0) 
						{student.setBalance(0);}
						else {student.subtractBalance(amount);}
						
						System.out.println("Remaining balance is $"+student.getBalance());
						suppressInvalCom = true;
					}
					break;
				case "add book":
					if (user instanceof Librarian) {
						System.out.println("Enter Author's name:");
						String authorName = scan.nextLine();
						if(authorName.contains("+")) {System.out.println("INVALID AUTHOR NAME. CANNOT CONTAIN \"+\". Please try again.");break;}
						System.out.println("Enter book title:");
						String bookName = scan.nextLine();
						if(bookName.contains("+")) {System.out.println("INVALID TITLE. CANNOT CONTAIN \"+\". Please try again.");break;}
						System.out.println("Enter ISBN:");
						String ISBN = scan.nextLine();
						if(ISBN.contains("+")) {System.out.println("INVALID ISBN. CANNOT CONTAIN \"+\". Please try again.");break;}
						System.out.println("Enter quantity of this book to add:");
						int quantity = scan.nextInt();
						int counter = 0;
						while(quantity > counter){
							addBook(authorName, bookName, ISBN);
							counter += 1;
						}
						suppressInvalCom = true;
						System.out.println("Book(s) added.");
						}
					break;
				case "delete book":
					if (user instanceof Librarian) {
						System.out.println("Enter book name:");
						String bookName = scan.nextLine();
						System.out.println("Enter quantity of this book to add:");
						int quantity = scan.nextInt();
						int counter = 0;
						while(quantity > counter && searchBookName(bookName).size() > 0){
							deleteBook(bookName);
							counter += 1;
						}
					}
					break;
				default:
					if (!suppressInvalCom) {
						System.out.println("INVALID COMMAND. Type \"help\" for list of commands.");
						suppressInvalCom = false;
					}
					break;
				}
			}
			System.out.println("Thank you for using our library!");
			System.out.println("Please come again soon.");
			scan.close();
		}
		
		private Integer containNum(Book book, ArrayList<Book> books){ // helper function for availability;
			Integer quantity = null;// will return null if given an arrayList that doesn't have the given book
			// TODO does contains work?
			if(books.contains(book)){ // Assuming the contains method will use the equal method to compare the books
				quantity = 0; // if their is one, initialize it to 0 instead of having null
				for(Book targetBook:books){ // enhanced for loop to go through and make increments
					if(book.equals(targetBook)){
						quantity +=1;
					}
				}
			}

			return quantity;// quantity is the number of book in books
		}

		private void printBooks(ArrayList<Book> books, boolean isMyBooks) {
			// isMyBooks is true if printing books rented by a student and false if viewing library books
			if (books != null) {

				if (books.size() == 0) {
					System.out.println("Sorry, no results found.");
					return;
				}
				// tracks book names already printed
				ArrayList<String> printedNames = new ArrayList<>();
				for (Book book : books) {
					if (!printedNames.contains(book.getBookName()) && !isMyBooks) {
						System.out.println(book.displayInfo());
						printedNames.add(book.getBookName());
						
						if (containNum(book,availableBooks)!=null) {
							System.out.println("Availibility: "+containNum(book,availableBooks)+"/"+containNum(book,allBooks));
							if (containNum(book,availableBooks)==0 && containNum(book,allBooks)!=null) {
								LocalDateTime soonestDate = this.getSoonestReturnDate(book);
								if (soonestDate!=null) {
									DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
									System.out.println("Will be in stock next by "+soonestDate.format(dtf));
								}
							}
						} else
						if (!isMyBooks) {
							System.out.println("Availibility: "+0+"/"+containNum(book,allBooks));
							LocalDateTime soonestDate = this.getSoonestReturnDate(book);
							if (soonestDate!=null) {
								DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd");
								System.out.println("Will be in stock next by "+soonestDate.format(dtf));
							}
						}
						System.out.println();// empty line to increase readability
					}
					if (isMyBooks) {
						System.out.println(book.displayInfo());
						if (book.getID() != null) System.out.println("Rental ID: "+book.getID());
						DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("MM/dd");
						DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("hh:mm");
						if (book.getDueDate() != null) {
							System.out.print("Due on "+book.getDueDate().format(formatterDate));
							System.out.println(" at "+book.getDueDate().format(formatterTime));
						}
						System.out.println();// empty line to increase readability
					}
				}

			} //else System.out.println("null books");
		}


		
		public LocalDateTime getSoonestReturnDate(Book book) {
			LocalDateTime soonestDate = LocalDateTime.now().plusDays(15);
			for (Student student : lentBooks.keySet()) {
				for (Book b2 : lentBooks.get(student)) {
					if (b2!=null && book!=null) {
						if (b2.equals(book) && b2.getDueDate().isBefore(soonestDate)) {
							soonestDate = b2.getDueDate();
						}
					}
				}
			}
			return soonestDate;
		}

		public ArrayList<Book> getAllBooks(){
			return allBooks;
		}

		public ArrayList<Book> getAvailableBooks(){
			return availableBooks;
		}

		public Map<Student,ArrayList<Book>> getLentBooks(){
			return lentBooks;
		}

		public User loginStudent(String username, String password){
			User login = null;
			if (users != null) {
				for(User user:users){
					if(user.getUsername().equals(username)){
						if(user.getPassword().equals(password) && user instanceof Student){
							login = user;
						}
					}
				}
				return login;
			} else {
				return null;
			}
		}
		public User loginLibrarian(String username, String password){
			User login = null;
			if (users != null) {
				for(User user:users){
					if(user.getUsername().equals(username)){
						if(user.getPassword().equals(password) && user instanceof Librarian){
							login = user;
						}
					}
				}
				return login;
			} else {
				return null;
			}
		}

		public User registerStudent(String username, String password){
			boolean newName = true;
			for (User u : users) {
				if (u.getUsername().equals(username)) { newName = false; }
			}
			if (newName) {
				Student student = new Student(username,password);
				users.add(student);
				return student;
			} else {
				return null;
			}
		}
		public User registerLibrarian(String username, String password){
			boolean newName = true;
			for (User u : users) {
				if (u.getUsername().equals(username)) { newName = false; }
			}
			if (newName) {
				Librarian librarian = new Librarian(username,password);
				users.add(librarian);
				return librarian;
			} else {
				return null;
			}
		}

		public ArrayList<Book> searchAuthorName(String authorName){
			ArrayList<Book> match = new ArrayList<>();
			for(int i = 0; i<allBooks.size(); i++){
				if(allBooks.get(i).getAuthorName().equalsIgnoreCase(authorName)){
					match.add(allBooks.get(i));
				}
			}
			return match;
		}

		public ArrayList<Book> searchBookName(String bookName){
			ArrayList<Book> match = new ArrayList<>();
			for(int i = 0; i<allBooks.size(); i++){
				if(allBooks.get(i).getBookName().equalsIgnoreCase(bookName)){
					match.add(allBooks.get(i));
				}
			}
			return match;
		}

		public ArrayList<Book> searchISBN(String ISBN){
			ArrayList<Book> match = new ArrayList<>();
			for(int i = 0; i<allBooks.size(); i++){
				if(allBooks.get(i).getISBN().equals(ISBN)){
					match.add(allBooks.get(i));
				}
			}
			return match;
		}

		public void addBook(String authorName, String bookName, String ISBN){ // changed to book instead of string
			Book book = new Book(authorName,bookName,ISBN);
			this.allBooks.add(book);
			this.availableBooks.add(book);
		}

		public void deleteBook(String bookName){
     		Integer targetAvail = null;

     		breakLoop:
     		for(Book book1: availableBooks) {
         			if(book1.getBookName().equals(bookName)){
             		targetAvail = (int) availableBooks.indexOf(book1);
             		allBooks.remove(allBooks.indexOf(book1));
             		break breakLoop;
         			}
     		}
     		if(targetAvail != null) {
         			availableBooks.remove(targetAvail.intValue());
         			System.out.println("Book not found.");
     		}
 		}

		public String generateID(Book book, User user){
			Random r = new Random();
			String alphabet = "1234567890abcdefghijklmnopqrstuvwxyz"; //A list of characters
			String ID = book.getBookName()+user.getUsername();// ID is combined
			for (int i = 0; i < 3; i++) {
				ID += alphabet.charAt(r.nextInt(alphabet.length()));// generate 3 random characters at the end of the ID
			}
			return ID;
		}
		
		public void rentBook(Student student, String bookName, Scanner scan) {
 			if (student.getBalance() > 0) {
 				System.out.println("Please pay fines before renting new books");
 				return;
 			} else {
 				ArrayList<Book> books = searchBookName(bookName);
 				//int numAvailable = containNum(books.get(0), availableBooks);
 				if (!books.isEmpty()) { //&& numAvailable > 0) {
 					Book book = null;
 					// gets a book to rent that is not already rented
 					for (Book b : availableBooks) {
 						if (b.equals(books.get(0)) && b.getDueDate()==null) {
 							book = b;
 							break;
 						}
 					}
 					while (book.getDueDate()==null) {
 						System.out.println("How many days do you wish to rent the book for?");
 						int days = scan.nextInt();
 						if (days > 0 && days <= 14) {// checks that a valid number of days is entered and loops until it has been
 							book.setVars(LocalDateTime.now().plusDays(days));
 						} else {
 							System.out.println("Maximum rental time is 14 days.");
 						}
 					}
 					if(lentBooks.containsKey(student)){
 						ArrayList<Book> al = lentBooks.get(student);
 						al.add(book);
 						lentBooks.replace(student, al);
 						student.addRentedBooks(book);
 						availableBooks.remove(book);
 						System.out.println("rented "+book.getBookName());
 					} else {
 						ArrayList<Book> al = new ArrayList<Book>();
 						al.add(book);
 						lentBooks.put(student, al);
 						student.addRentedBooks(book);
 						availableBooks.remove(book);
 						System.out.println("Rented "+book.getBookName());
 					}
 				} else System.out.println("Book not found.");
 			}
		}
 			
		public void returnBook(Student student, String ID) {
			ArrayList<Book> books = lentBooks.get(student);
			for (Book b : books) {
				if (b.getID().equals(ID)) {
					b.nullVars();
					availableBooks.add(b);
					books.remove(b);
					student.getRentedBooks().remove(b);
					lentBooks.replace(student, books);
					System.out.println("Returned "+b.getBookName());
					return;
				}
			}
			System.out.println("That ID does not match your rented books.");
		}
		
		

		public void addBalance(Student student) {
			student.addBalance();
		}

		public void logout() {
			try {
				PrintWriter record = new PrintWriter(new File("myLibrary.txt"));
				// saves data for all of the users
				for(User person: users){
					String identifier;
					if(person instanceof Student){ identifier = "S";
					Student dummy = (Student) person;
					record.println(person.getUsername() + "`" + person.getPassword() + "`" + dummy.getLastOnline().toString() + "`" + identifier + "`" + dummy.getBalance());
					}
					else{ identifier = "L";
					record.println(person.getUsername() + "`" + person.getPassword() + "`" + "`" + identifier);
					}
				}
				record.println("+");
				// saves date for all books with the identifier Y if they are available and N if they are not
				for(Book book: allBooks){
					String identifier = "N";
					for(Book book1: availableBooks){
						if(book == book1){
							identifier = "Y";
						}
					}
					record.println(book.getAuthorName() + "`" + book.getBookName() + "`" + book.getISBN() + "`" + identifier);
				}
				record.println("+");
				// saves data for all lent books and the index of the the student who rented it
				for(Student student : lentBooks.keySet()){
					for (Book book1 : lentBooks.get(student)) {
						record.println(users.indexOf(student) 
								+ "`" + book1.getBookName() 
								+ "`" + book1.getID() 
								+ "`" + book1.getLendingDate().toString() 
								+ "`" + book1.getDueDate().toString());
					}
				}
				record.close();
			} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			}
		}

		public static void main(String[] args){
			LibrarySystem library = new LibrarySystem(new File("myLibrary.txt"));
			library.initialize();
		}
	}