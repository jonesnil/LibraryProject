To get started, make sure you have at least a blank myLibrary.txt right outside the file where you're keeping
your LibrarySystem. Now, just run the LibrarySystem file (it has main) and use the below guide to start.

The first thing the system will ask you to do 
is login/register. Just as you would see if 
you typed help into the prompt, these are
your options for input at this step:

P.S. Make sure you write the full line of the command as written here before hitting enter.
This goes for all commands here and in the other two lists.

INIT COMMAND LIST:
-login student
-login librarian
-register student
-register librarian
-quit

Once you've logged in/ registered, your options
will vary depending on if you are a librarian or
a student. If you are a student, these are the
commands available:

P.S. If you want the system to keep your changes, don't terminate when you're done, use 
the logout command. It is responsible for writing.

STUDENT COMMAND LIST:
-view all books
-search by author name
-search by book name
-search by isbn
-rent
.This will then prompt you for a title, and if that title is available it will rent it to your account.
-return
.This will return a book by ID from your rented books.
-view my books
-view fines
-pay fines
-logout

If you are a librarian, these are the commands
at your disposal:

LIBRARIAN COMMAND LIST:
-view all books
-search by author name
-search by book name
-search by isbn
-add book
.This will prompt you to give the book variables and then add a quantity of your choice to the library.
-delete book
.This will delete a quantity of books of your choice by title from the library.
-logout