package app;

import ui.MainFrame;

import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) {

        LibraryService library = new LibraryService();

        Author orwell = library.addAuthor("George", "Orwell", 1903);
        Author rowling = library.addAuthor("J.K.", "Rowling", 1965);
        Author tolkien = library.addAuthor("J.R.R.", "Tolkien", 1892);

        Publisher penguin = library.addPublisher("Penguin Books", "Londra");

        Category fiction = library.addCategory("Fictiune");
        Category fantasy = library.addCategory("Fantasy");

        Book b1984 = library.addBook("1984", "9780451524935", 1949, penguin, fiction, List.of(orwell));
        Book animalFarm = library.addBook("Animal Farm", "9780451526342", 1945, penguin, fiction, List.of(orwell));
        Book harryPotter = library.addBook("Harry Potter si Piatra Filozofala", "9780439554930", 1997, penguin, fantasy, List.of(rowling));
        Book lotr = library.addBook("Stapanul Inelelor: Fratia Inelului", "9780261103573", 1954, penguin, fantasy, List.of(tolkien));

        Member ana = library.addMember("Ana", "Popescu", "ana@example.com", LocalDate.of(2023, 10, 1));
        Member andrei = library.addMember("Andrei", "Ionescu", "andrei@example.com", LocalDate.of(2023, 11, 15));

        library.addMemberCard(ana, "CARD-0001", LocalDate.of(2023, 10, 1), LocalDate.of(2025, 10, 1));
        library.addMemberCard(andrei, "CARD-0002", LocalDate.of(2023, 11, 15), LocalDate.of(2025, 11, 15));

        try {
            library.borrowBook(ana.getId(), b1984.getId(), 14);
            library.borrowBook(ana.getId(), animalFarm.getId(), 10);
            library.borrowBook(andrei.getId(), harryPotter.getId(), 20);
        } catch (BookNotFoundException | MemberNotFoundException | BookNotAvailableException e) {
            System.out.println("Eroare la imprumut: " + e.getMessage());
        }

        System.out.println("=== Toate cartile (ordonate dupa titlu) ===");
        for (Book b : library.getAllBooksSortedByTitle()) {
            System.out.println(b);
        }

        SwingUtilities.invokeLater(() -> new MainFrame(library).setVisible(true));


        System.out.println("\n=== Numar de carti per autor ===");
        for (Map.Entry<Author, Long> e : library.countBooksPerAuthor().entrySet()) {
            System.out.println(e.getKey().getFullName() + " -> " + e.getValue());
        }

        System.out.println("\n=== Numar de imprumuturi per membru ===");
        for (Map.Entry<Member, Long> e : library.countLoansPerMember().entrySet()) {
            System.out.println(e.getKey().getFullName() + " -> " + e.getValue());
        }

        System.out.println("\n=== Imprumuturi active ===");
        library.getActiveLoans().forEach(System.out::println);

        System.out.println("\n=== Carti care nu au fost niciodata imprumutate ===");
        library.getBooksNeverLoaned().forEach(System.out::println);

        System.out.println("\n=== Imprumuturi intarziate ===");
        library.getOverdueLoans(LocalDate.now()).forEach(System.out::println);

        System.out.println("\n=== Membri cu mai mult de un imprumut ===");
        library.getMembersWithMultipleLoans().forEach(m ->
                System.out.println(m.getFullName()));

        System.out.println("\n=== Membri care au imprumutat Fantasy ===");
        library.getMembersWhoBorrowedCategory("Fantasy").forEach(m ->
                System.out.println(m.getFullName()));

        System.out.println("\n=== Data ultimului imprumut pentru fiecare membru ===");
        for (Map.Entry<Member, LocalDate> e : library.getLastLoanDatePerMember().entrySet()) {
            System.out.println(e.getKey().getFullName() + " -> " + e.getValue());
        }

        SwingUtilities.invokeLater(() -> new MainFrame(library).setVisible(true));
    }
}
