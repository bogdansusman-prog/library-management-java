package app;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/*
Clasa de servicii care gestionează "baza de date" în memorie.
 */
public class LibraryService {

    private InMemoryRepository<Long, Author> authors = new InMemoryRepository<>();
    private InMemoryRepository<Long, Publisher> publishers = new InMemoryRepository<>();
    private InMemoryRepository<Long, Category> categories = new InMemoryRepository<>();
    private InMemoryRepository<Long, Book> books = new InMemoryRepository<>();
    private InMemoryRepository<Long, Member> members = new InMemoryRepository<>();
    private InMemoryRepository<Long, MemberCard> cards = new InMemoryRepository<>();
    private InMemoryRepository<Long, Loan> loans = new InMemoryRepository<>();

    private long nextAuthorId = 1;
    private long nextPublisherId = 1;
    private long nextCategoryId = 1;
    private long nextBookId = 1;
    private long nextMemberId = 1;
    private long nextCardId = 1;
    private long nextLoanId = 1;

    private long genAuthorId() { return nextAuthorId++; }
    private long genPublisherId() { return nextPublisherId++; }
    private long genCategoryId() { return nextCategoryId++; }
    private long genBookId() { return nextBookId++; }
    private long genMemberId() { return nextMemberId++; }
    private long genCardId() { return nextCardId++; }
    private long genLoanId() { return nextLoanId++; }


    // ------------ OPERATII DE CREARE (echivalent INSERT) ------------

    public Author addAuthor(String firstName, String lastName, Integer birthYear) {
        Author a = new Author(genAuthorId(), firstName, lastName, birthYear);
        authors.save(a);
        return a;
    }

    public Publisher addPublisher(String name, String city) {
        Publisher p = new Publisher(genPublisherId(), name, city);
        publishers.save(p);
        return p;
    }

    public Category addCategory(String name) {
        Category c = new Category(genPublisherId(), name);
        categories.save(c);
        return c;
    }

    public Book addBook(String title, String isbn, Integer year,
                        Publisher publisher, Category category, List<Author> bookAuthors) {

        // verificare ISBN duplicat
        boolean existsIsbn = books.findAll().stream()
                .anyMatch(b -> b.getIsbn() != null && b.getIsbn().equals(isbn));
        if (existsIsbn) {
            throw new DuplicateIsbnException("ISBN deja există: " + isbn);
        }

        Book b = new Book(genBookId(), title, isbn, year, publisher, category);
        for (Author a : bookAuthors) {
            b.addAuthor(a);
        }
        books.save(b);
        return b;
    }

    public Member addMember(String firstName, String lastName,
                            String email, LocalDate registrationDate) {
        Member m = new Member(genMemberId(), firstName, lastName, email, registrationDate);
        members.save(m);
        return m;
    }

    public MemberCard addMemberCard(Member member, String cardNumber,
                                    LocalDate issueDate, LocalDate expirationDate) {
        MemberCard card = new MemberCard(genMemberId(), member, cardNumber, issueDate, expirationDate);
        cards.save(card);
        member.setCard(card);
        return card;
    }

    public Loan borrowBook(Long memberId, Long bookId, int days) {
        Member member = members.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Nu exista membru cu id " + memberId));
        Book book = books.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Nu exista carte cu id " + bookId));

        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Cartea este deja împrumutată: " + book.getTitle());
        }

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(genLoanId(), member, book, today, today.plusDays(days));
        loans.save(loan);
        book.setAvailable(false);
        return loan;
    }

    public void returnBook(Long loanId) {
        Loan loan = loans.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Nu exista împrumut cu id " + loanId));
        loan.setReturnDate(LocalDate.now());
        loan.getBook().setAvailable(true);
    }

    // ------------ "INTEROGARI"  ------------

    /** 1. Toate cărțile cu editură + categorie, ordonate după titlu. */
    public List<Book> getAllBooksSortedByTitle() {
        return books.findAll().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /** 2. Numărul de cărți scrise de fiecare autor. */
    public Map<Author, Long> countBooksPerAuthor() {
        return books.findAll().stream()
                .flatMap(book -> book.getAuthors().stream())
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));
    }

    /** 3. Numarul de imprumuturi pentru fiecare membru. */
    public Map<Member, Long> countLoansPerMember() {
        return loans.findAll().stream()
                .collect(Collectors.groupingBy(Loan::getMember, Collectors.counting()));
    }

    /** 5. Lista cartilor nereturnate si membrii care le au. */
    public List<Loan> getActiveLoans() {
        return loans.findAll().stream()
                .filter(Loan::isActive)
                .collect(Collectors.toList());
    }

    /** 7. Numarul de carti pe fiecare categorie. */
    public Map<Category, Long> countBooksPerCategory() {
        return books.findAll().stream()
                .collect(Collectors.groupingBy(Book::getCategory, Collectors.counting()));
    }

    /** 8. Membrii care au mai mult de un imprumut. */
    public List<Member> getMembersWithMultipleLoans() {
        return countLoansPerMember().entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /** 9. Carti care nu au fost niciodata imprumutate. */
    public List<Book> getBooksNeverLoaned() {
        Set<Book> loanedBooks = loans.findAll().stream()
                .map(Loan::getBook)
                .collect(Collectors.toSet());
        return books.findAll().stream()
                .filter(b -> !loanedBooks.contains(b))
                .collect(Collectors.toList());
    }

    /** 10. Imprumuturi intarziate. */
    public List<Loan> getOverdueLoans(LocalDate today) {
        return loans.findAll().stream()
                .filter(l -> l.isOverdue(today))
                .sorted(Comparator.comparing(Loan::getDueDate))
                .collect(Collectors.toList());
    }

    /** 11. Top autori dupa numarul de imprumuturi ale cartilor lor. */
    public Map<Author, Long> getTopAuthorsByLoans() {
        return loans.findAll().stream()
                .flatMap(l -> l.getBook().getAuthors().stream())
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()));
    }

    /** 12. Prelungirea termenului de predare pentru toate imprumuturile active. */
    public void extendDueDatesForActiveLoans(int extraDays) {
        for (Loan l : getActiveLoans()) {
            LocalDate newDue = l.getDueDate().plusDays(extraDays);
            try {
                var field = Loan.class.getDeclaredField("dueDate");
                field.setAccessible(true);
                field.set(l, newDue);
            } catch (Exception e) {
                System.err.println("Nu pot modifica dueDate: " + e.getMessage());
            }
        }
    }

    /** 14. Membrii care au imprumutat carti din categoria data. */
    public List<Member> getMembersWhoBorrowedCategory(String categoryName) {
        return loans.findAll().stream()
                .filter(l -> l.getBook().getCategory().getName().equalsIgnoreCase(categoryName))
                .map(Loan::getMember)
                .distinct()
                .collect(Collectors.toList());
    }

    /** 15. Data ultimului imprumut pentru fiecare membru. */
    public Map<Member, LocalDate> getLastLoanDatePerMember() {
        return loans.findAll().stream()
                .collect(Collectors.groupingBy(Loan::getMember,
                        Collectors.mapping(Loan::getLoanDate,
                                Collectors.collectingAndThen(
                                        Collectors.maxBy(LocalDate::compareTo),
                                        opt -> opt.orElse(null)))));
    }

    // Getteri pentru liste (utile în main pentru afișare):

    public List<Author> getAllAuthors() { return authors.findAll(); }
    public List<Member> getAllMembers() { return members.findAll(); }
    public List<Loan> getAllLoans() { return loans.findAll(); }

    public void removeMember(Long memberID) {
        Member member = members.findById(memberID).orElseThrow(()-> new MemberNotFoundException("Nu exista membru cu id "+ memberID));

        boolean hasActiveLoans = loans.findAll().stream().anyMatch(l -> l.getMember().equals(member) && l.isActive());
        if (hasActiveLoans) {
            throw new RuntimeException("Membrul are imprumuturi active. Nu poate fi sters. ");
        }

        if(member.getCard()!=null){
            cards.deleteById(member.getCard().getId());
            member.setCard(null);
        }

        members.deleteById(memberID);

    }

    public List<Book> getAllBooks() { return books.findAll(); }
    public List<Publisher> getAllPublishers() { return publishers.findAll(); }
    public List<Category> getAllCategories() { return categories.findAll(); }
    public List<MemberCard> getAllMemberCards() {   return cards.findAll(); }

    public void removeBook(Long bookId) {
        Book book = books.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Nu exista carte cu id " + bookId));

        boolean hasActiveLoan = loans.findAll().stream()
                .anyMatch(l -> l.getBook().equals(book) && l.isActive());

        if (hasActiveLoan) {
            throw new RuntimeException("Cartea are un imprumut activ. Nu poate fi stearsa.");
        }

        loans.findAll().stream()
                .filter(l -> l.getBook().equals(book))
                .map(Loan::getId)
                .forEach(loans::deleteById);

        books.deleteById(bookId);
    }


    public String buildReportsText() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Toate cartile (ordonate dupa titlu) ===\n");
        for (Book b : getAllBooksSortedByTitle()) sb.append(b).append("\n");

        sb.append("\n=== Numar de carti per autor ===\n");
        for (var e : countBooksPerAuthor().entrySet())
            sb.append(e.getKey().getFullName()).append(" -> ").append(e.getValue()).append("\n");

        sb.append("\n=== Numar de imprumuturi per membru ===\n");
        for (var e : countLoansPerMember().entrySet())
            sb.append(e.getKey().getFullName()).append(" -> ").append(e.getValue()).append("\n");

        sb.append("\n=== Imprumuturi active ===\n");
        for (Loan l : getActiveLoans()) sb.append(l).append("\n");

        sb.append("\n=== Carti care nu au fost niciodata imprumutate ===\n");
        for (Book b : getBooksNeverLoaned()) sb.append(b).append("\n");

        sb.append("\n=== Imprumuturi intarziate ===\n");
        for (Loan l : getOverdueLoans(LocalDate.now())) sb.append(l).append("\n");

        sb.append("\n=== Membri cu mai mult de un imprumut ===\n");
        for (Member m : getMembersWithMultipleLoans()) sb.append(m.getFullName()).append("\n");

        sb.append("\n=== Membri care au imprumutat Fantasy ===\n");
        for (Member m : getMembersWhoBorrowedCategory("Fantasy")) sb.append(m.getFullName()).append("\n");

        sb.append("\n=== Data ultimului imprumut pentru fiecare membru ===\n");
        for (var e : getLastLoanDatePerMember().entrySet())
            sb.append(e.getKey().getFullName()).append(" -> ").append(e.getValue()).append("\n");

        return sb.toString();
    }


}

