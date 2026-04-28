import java.util.*;


class Book {
    public int isbn;
    public String title;
    public String author;
    public String genre;
    public String status;
    public int qty;

    public Book(int isbn, String title, String author, String genre, int qty) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.qty = qty;
        this.status = "available";
    }

    public boolean checkAvailability() {
        return qty > 0;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateQuantity(int change) {
        qty += change;
        if(qty > 0) {
            status = "available";
        } else {
            qty = Math.max(qty, 0);
            status = "unavailable";
        }
    }
}

class Member {
    public int memberId;
    public String name;
    public String email;
    public String libraryCardNumber;
    public List<BorrowRecord> activeBorrows;
    public int fines;

    public Member(int memberId, String name, String email, String libraryCardNumber) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.libraryCardNumber = libraryCardNumber;
        this.activeBorrows = new ArrayList<>();
        this.fines = 0;
    }

    public boolean borrowBook(Book b, String issueDate, String dueDate, LibrarySystem system) {
        if(activeBorrows.size() < 3 && b.checkAvailability() && fines == 0) {
            BorrowRecord record = new BorrowRecord(memberId, b.isbn, issueDate, dueDate);
            activeBorrows.add(record);
            b.updateQuantity(-1);
            System.out.println("Book borrowed");
            return true;
        } else {
            System.out.println("Cannot borrow book");
            return false;
        }
    }

    public void payFine(int amount) {
        fines -= amount;
        System.out.println("Fine paid: " + amount + "\n Remaining: " + fines);
    }

    public void returnBook(Book b) {
        b.updateQuantity(1);
        System.out.println("Book returned: " + b.title + " | qty=" + b.qty + " | status=" + b.status);
    }

}

class BorrowRecord {
    private int recordId;
    private int memberId;
    public int isbn;
    public String issueDate;
    public String dueDate;
    public String returnDate;

    public BorrowRecord(int memberId, int isbn, String issueDate, String dueDate) {
        this.memberId = memberId;
        this.isbn = isbn;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    public int calculateFine() {
        String[] dueParts = dueDate.split("-");
        int dueYear = Integer.parseInt(dueParts[0]);
        int dueMonth = Integer.parseInt(dueParts[1]);
        int dueDay = Integer.parseInt(dueParts[2]);
        
        String[] returnParts = returnDate.split("-");
        int returnYear = Integer.parseInt(returnParts[0]);
        int returnMonth = Integer.parseInt(returnParts[1]);
        int returnDay = Integer.parseInt(returnParts[2]);
        
        int daysOverdue = (returnYear - dueYear) * 365 + (returnMonth - dueMonth) * 30 + (returnDay - dueDay);
        if(daysOverdue > 0) {
            FinePolicy policy = new FinePolicy();
            return policy.computeFine(daysOverdue);
        }
        return 0;
    }
}

class Librarian {
    private int staffId;
    public String name;

    public Librarian(int staffId, String name) {
        this.staffId = staffId;
        this.name = name;
    }

    public boolean issueBook(Member member, Book book, LibrarySystem system) {
        if(book.checkAvailability()) {
            Date today = new Date();
            int year = today.getYear();
            int month = today.getMonth();
            int day = today.getDate();
            String issueDate = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
            day += 14;
            if(day > 31) {
                day -= 31;
                month += 1;
                if(month > 12) {
                    month = 1;
                    year += 1;
                }
            }
            String dueDate = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
            boolean issued = member.borrowBook(book, issueDate, dueDate, system);
            if(issued) {
                System.out.println("Librarian " + name + " issued book to " + member.name);
            } else {
                System.out.println("Librarian " + name + " could not issue book to " + member.name);
            }
            return issued;
        } else {
            System.out.println("Book not available for issue");
            return false;
        }
    }

    public void addBook(Book book, LibrarySystem system) {
        system.addBook(book);
        System.out.println("Book added: " + book.title);
    }

    public void removeBook(int isbn, LibrarySystem system) {
        system.removeBook(isbn);
        System.out.println("Book removed with ISBN: " + isbn);
    }
}

class FinePolicy {
    public int maxFine = 500;

    public int computeFine(int daysOverdue) {
        int fine = 0;
        if(daysOverdue == 0)
            return 0;
        else if(daysOverdue >= 1 && daysOverdue <= 7) {
            fine = (2 * daysOverdue);
        }
        else if(daysOverdue >= 8 && daysOverdue <= 15) {
            fine = (5 * daysOverdue);
        }
        else if(daysOverdue > 15) {
            fine = (10 * daysOverdue);
        }
        return Math.min(fine, maxFine);
    }
}

class LibrarySystem {
    public List<Book> books;
    public List<Member> members;
    public Map<Integer, Queue<Integer>> waitlists;

    public LibrarySystem() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.waitlists = new HashMap<>();
    }

    public void initializeDatabase() {
        books.add(new Book(101, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 3));
        books.add(new Book(102, "To Kill a Mockingbird", "Harper Lee", "Fiction", 1));
        books.add(new Book(103, "1984", "George Orwell", "Dystopian", 4));
        books.add(new Book(104, "Pride and Prejudice", "Jane Austen", "Romance", 2));
        books.add(new Book(105, "The Catcher in the Rye", "J.D. Salinger", "Fiction", 5));
        System.out.println("Database initialized");
    }

    public void registerMember(int memberId, String name, String email, String libraryCardNumber) {
        Member member = new Member(memberId, name, email, libraryCardNumber);
        members.add(member);
        System.out.println("Member registered");
    }

    public Book searchByISBN(int isbn) {
        for(Book book : books) {
            if(book.isbn == isbn) {
                return book;
            }
        }
        return null;
    }

    public Book searchByTitle(String title) {
        for(Book book : books) {
            if(book.title.equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public Member getMemberById(int memberId) {
        for(Member member : members) {
            if(member.memberId == memberId) {
                return member;
            }
        }
        return null;
    }

    public void addToWaitlist(int memberId, int isbn) {
        Queue<Integer> waitlist = waitlists.computeIfAbsent(isbn, k -> new LinkedList<>());
        if(waitlist.contains(memberId)) {
            System.out.println("Member " + memberId + " is already on the waitlist for ISBN " + isbn);
            return;
        }
        waitlist.add(memberId);
        System.out.println("Member " + memberId + " added to waitlist for ISBN " + isbn);
    }

    public void displayWaitlist(int isbn) {
        Queue<Integer> waitlist = waitlists.get(isbn);
        if(waitlist == null || waitlist.isEmpty()) {
            System.out.println("No members are waiting for ISBN " + isbn);
            return;
        }

        System.out.println("Waitlist for ISBN " + isbn + ":");
        int position = 1;
        for(Integer memberId : waitlist) {
            Member member = getMemberById(memberId);
            if(member != null) {
                System.out.println(position + ". " + member.name + " (ID " + member.memberId + ")");
                position++;
            }
        }
    }

    public void processBorrowRequest(int memberId, int isbn, Librarian librarian) {
        Member member = getMemberById(memberId);
        Book book = searchByISBN(isbn);
        
        if(member == null) {
            System.out.println("Member not found");
            return;
        }
        if(book == null) {
            System.out.println("Book not found");
            return;
        }
        if(!book.checkAvailability()) {
            System.out.println("Book is not available");
            addToWaitlist(memberId, isbn);
            return;
        }
        
        librarian.issueBook(member, book, this);
    }

    public void processReturnRequest(int memberId, int isbn) {
        Member member = getMemberById(memberId);
        Book book = searchByISBN(isbn);

        if(member == null) {
            System.out.println("Member not found");
            return;
        }
        if(book == null) {
            System.out.println("Book not found");
            return;
        }

        member.returnBook(book);
        if(book.checkAvailability()) {
            Queue<Integer> waitlist = waitlists.get(isbn);
            if(waitlist != null && !waitlist.isEmpty()) {
                int nextMemberId = waitlist.peek();
                Member nextMember = getMemberById(nextMemberId);
                if(nextMember != null) {
                    System.out.println("Book ISBN " + isbn + " is now available for waitlisted member " + nextMember.name + " (ID " + nextMemberId + ")");
                }
            }
        }
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(int isbn) {
        for(Book b : books) {
            if(b.isbn == isbn) {
                books.remove(b);
                break;
            }
        }
    }

    public void displayAllBooks() {
        System.out.println("\nLibrary Books");
        for(Book book : books) {
            System.out.println(book.isbn + " " + book.title);
        }
    }

    public void displayAllMembers() {
        System.out.println("\nLibrary Members");
        for(Member member : members) {
            System.out.println("ID: " + member.memberId + " | Name: " + member.name + " | Email: " + member.email + " | Card: " + member.libraryCardNumber + " | Fines: " + member.fines);
        }
    }
}

public class LMS {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibrarySystem system = new LibrarySystem();
        system.initializeDatabase();
        system.registerMember(1, "John Doe", "john@email.com", "L001");
        system.registerMember(2, "Jane Smith", "jane@email.com", "L002");
        Librarian librarian = new Librarian(101, "Mr. Wilson");
        int nextMemberId = 3;

        FinePolicy finePolicyDemo = new FinePolicy();
        int[] overdueDays = {0, 3, 10, 20};
        for(int days : overdueDays) {
            System.out.println("Days overdue: " + days + " => Fine: " + finePolicyDemo.computeFine(days));
        }

        Member demoMember = system.getMemberById(1);
        if(demoMember != null) {
            int demoFine = finePolicyDemo.computeFine(10);
            demoMember.fines += demoFine;
            System.out.println("\nDemo fine assigned to ID " + demoMember.memberId + ": " + demoFine + " | Total fines: " + demoMember.fines);
        }

        while(true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. List all books");
            System.out.println("2. Search book by title");
            System.out.println("3. Search book by ISBN");
            System.out.println("4. Register new member");
            System.out.println("5. Borrow book");
            System.out.println("6. Pay fine");
            System.out.println("7. Add book");
            System.out.println("8. Remove book");
            System.out.println("9. View all members");
            System.out.println("10. Return book");
            System.out.println("11. Display waitlist");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine();
            if(option.equals("0")) {
                System.out.println("Exiting system...");
                break;
            }

            switch(option) {
                case "1" -> system.displayAllBooks();
                case "2" -> {
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    Book book = system.searchByTitle(title);
                    if(book != null) {
                        System.out.println("Found: " + book.isbn + " " + book.title + " " + book.author + " " + book.genre + " qty=" + book.qty + " status=" + book.status);
                    } else {
                        System.out.println("Book not found.");
                    }
                }
                case "3" -> {
                    System.out.print("Enter ISBN: ");
                    int isbn = Integer.parseInt(scanner.nextLine());
                    Book book = system.searchByISBN(isbn);
                    if(book != null) {
                        System.out.println("Found: " + book.isbn + " | " + book.title + " | " + book.author + " | " + book.genre + " | qty=" + book.qty + " | status=" + book.status);
                    } else {
                        System.out.println("Book not found.");
                    }
                }
                case "4" -> {
                    System.out.print("Enter member name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter member email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter library card number: ");
                    String card = scanner.nextLine();
                    system.registerMember(nextMemberId++, name, email, card);
                }
                case "5" -> {
                    System.out.print("Enter member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter ISBN to borrow: ");
                    int isbn = Integer.parseInt(scanner.nextLine());
                    system.processBorrowRequest(memberId, isbn, librarian);
                }
                
                case "6" -> {
                    System.out.print("Enter member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter fine amount to pay: ");
                    int amount = Integer.parseInt(scanner.nextLine());
                    Member member = system.getMemberById(memberId);
                    if(member != null) {
                        member.payFine(amount);
                    } else {
                        System.out.println("Member not found.");
                    }
                }
                case "7" -> {
                    System.out.print("Enter ISBN: ");
                    int isbn = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter genre: ");
                    String genre = scanner.nextLine();
                    System.out.print("Enter quantity: ");
                    int qty = Integer.parseInt(scanner.nextLine());
                    Book book = new Book(isbn, title, author, genre, qty);
                    librarian.addBook(book, system);
                }
                case "8" -> {
                    System.out.print("Enter ISBN to remove: ");
                    int isbn = Integer.parseInt(scanner.nextLine());
                    librarian.removeBook(isbn, system);
                }
                case "9" -> system.displayAllMembers();
                case "10" -> {
                    System.out.print("Enter member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter ISBN to return: ");
                    int isbn = Integer.parseInt(scanner.nextLine());
                    system.processReturnRequest(memberId, isbn);
                }
                case "11" -> {
                    System.out.print("Enter ISBN to view waitlist: ");
                    int isbn = Integer.parseInt(scanner.nextLine());
                    system.displayWaitlist(isbn);
                }
                default -> System.out.println("Invalid number");
            }
        }
    }
}