# Library Management System (LMS)

A robust, console-based Java application designed to manage library operations including book inventory, member registrations, borrowing logistics, and automated fine calculations.

## 1. Problem Statement
Libraries often struggle with manual tracking of book availability, member eligibility, and overdue returns. This system addresses these challenges by providing a centralized digital solution to:
* **Inventory Management:** Track book quantities and availability status in real-time.
* **Member Tracking:** Manage member profiles, borrowing limits, and financial standing (fines).
* **Operational Efficiency:** Automate the calculation of due dates and overdue fines.
* **Waitlist Management:** Handle high-demand books by placing members in a queue when a book is unavailable.

---

## 2. Approach & Logic
The system is built using **Object-Oriented Programming (OOP)** principles in Java. Key logic components include:

### Core Classes
* **`Book`**: Stores ISBN, title, author, and quantity. It manages its own "Availability" status based on the current stock.
* **`Member`**: Contains personal details and a list of active borrows. It includes logic to prevent borrowing if the member has reached the limit (3 books) or has outstanding fines.
* **`Librarian`**: Acts as the administrative actor responsible for issuing books and managing the inventory.
* **`LibrarySystem`**: The central controller that manages the database of books and members, and facilitates requests between them.

### Key Features Logic
* **Fine Policy:** Uses a tiered logic based on the number of days overdue:
    * 1–7 days: \Rs2/day
    * 8–15 days: \Rs5/day
    * 15+ days: \Rs10/day
    * **Max Fine:** Capped at \Rs500.
* **Waitlist System:** Implemented using a `Queue` within a `HashMap`. If a book is out of stock, the member is added to a specific queue for 그 ISBN. When the book is returned, the system automatically alerts that the book is available for the next person in line.
* **Borrowing Validation:** Before a book is issued, the system checks:
    1.  Book quantity > 0.
    2.  Member has < 3 active borrows.
    3.  Member has 0 outstanding fines.

---

## 3. Steps to Execute
To run this application on your local machine, follow these steps:

### Prerequisites
* **Java Development Kit (JDK)** installed (Version 17 or higher recommended for `switch` expressions).
* A terminal or Command Prompt.

### Execution
1.  **Download the file:**
    Ensure you have the [LMS.java](https://github.com/vishwaksuda-16/Library_Management_System/blob/main/LMS.java) file in your working directory.

2.  **Compile the code:**
    Open your terminal and run the following command:
    ```bash
    javac LMS.java
    ```

3.  **Run the application:**
    After compilation, execute the program using:
    ```bash
    java LMS
    ```

4.  **Using the Menu:**
    The system will initialize with a default database of 5 books. Follow the on-screen prompts (0-11) to:
    * Search for books.
    * Register members.
    * Borrow and return items.
    * Pay fines and view waitlists.

---

### Example Interaction
> **Option 5 (Borrow Book):** The system will ask for your Member ID and the Book ISBN. If successful, the quantity decreases and a `BorrowRecord` is created. If the book is unavailable, you are automatically moved to the **Waitlist**.
