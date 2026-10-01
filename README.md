# Library Management

A desktop application for managing a library, built in Java with a Swing GUI. It handles books, authors, publishers, categories, members and loans, and generates reports about library activity.

Built as an extra-credit project for the **Object-Oriented Programming** course (2025).

## Features

- **Books:**
  - add and remove books with title, ISBN, year, publisher, category and one or more authors
  - publishers, categories and authors can be created directly from the *Add Book* dialog
  - duplicate ISBNs are rejected
- **Members:** add and remove members, each with an optional member card that has issue and expiry dates.
- **Loans:** borrow and return books. A book that is already on loan cannot be borrowed again.
- **Reports:** one click generates an overview built with the Java Streams API:
  - books per author and per category
  - loans per member
  - active and overdue loans
  - books that were never borrowed
  - members with several loans
  - the most borrowed authors
  - each member's latest loan

## OOP design

| Concept | Where |
|---|---|
| **Abstraction and inheritance** | `Person` is an abstract base class for `Author` and `Member` |
| **Interfaces** | `Identifiable<ID>` (every entity has an id) and `Borrowable` (`Book`) |
| **Generics** | `InMemoryRepository<ID, T extends Identifiable<ID>>` is one repository class reused for every entity type |
| **Encapsulation** | All business rules are in `LibraryService`, and the GUI only calls its methods |
| **Custom exceptions** | `BookNotFoundException`, `BookNotAvailableException`, `MemberNotFoundException`, `DuplicateIsbnException` |
| **`Comparable`** | `Book` sorts by title |
| **Reflection** | `extendDueDatesForActiveLoans` updates the private `dueDate` field |

The class diagram is in [`docs/class-diagram.pdf`](docs/class-diagram.pdf).

## Project structure

```
src/app/   domain model, repository, service, exceptions, Main (sample data)
src/ui/    Swing GUI: MainFrame (tabs: Books, Members, Loans, Reports, About) and dialogs
```

## Running

Requires JDK 25 or newer.

```bash
javac -d out $(find src -name "*.java")
java -cp out app.Main
```

Or open the folder in IntelliJ IDEA, mark `src` as the sources root and run `app.Main`. The application starts with sample data: books by Orwell, Rowling and Tolkien, two members and a few loans.

## Tech stack

Java 25 · Swing · Java Streams API
