package ui;

import app.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class MainFrame extends JFrame {

    private final LibraryService service;
    // Selection tables for borrow (instead of typing IDs)
    private JTable availableMembersTable;
    private JTable availableBooksTable;

    private final DefaultTableModel availableMembersModel = new DefaultTableModel(
            new Object[]{"ID", "Member"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final DefaultTableModel availableBooksModel = new DefaultTableModel(
            new Object[]{"ID", "Title", "Category"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    // ===== MODELS =====
    private final DefaultTableModel booksModel = new DefaultTableModel(
            new Object[]{"ID", "Title", "ISBN", "Category", "Publisher", "Available"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final DefaultTableModel membersModel = new DefaultTableModel(
            new Object[]{"ID", "Full Name", "Email", "Registered"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final DefaultTableModel loansModel = new DefaultTableModel(
            new Object[]{"LoanID", "Member", "Book", "LoanDate", "DueDate", "ReturnDate"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    // ===== UI FIELDS =====
    private JLabel todayLabel;

    private JTable booksTable;
    private JTable membersTable;
    private JTable loansTable;

    private JTextField memberIdField;
    private JTextField bookIdField;
    private JSpinner daysSpinner;

    private JTextArea reportsArea;

    // ===== CONSTRUCTOR =====
    public MainFrame(LibraryService service) {
        this.service = service;

        setTitle("Library Automation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setContentPane(buildRoot());

        // initial load
        refreshAll();
    }

    // ===== ROOT =====
    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(), BorderLayout.CENTER);

        return root;
    }

    // ===== HEADER =====
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));

        JLabel title = new JLabel("Library Automation");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        header.add(title, BorderLayout.WEST);

        todayLabel = new JLabel("Today: " + LocalDate.now());
        header.add(todayLabel, BorderLayout.EAST);

        return header;
    }

    // ===== TABS =====
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Books", buildBooksTab());
        tabs.addTab("Members", buildMembersTab());
        tabs.addTab("Loans", buildLoansTab());
        tabs.addTab("Reports", buildReportsTab());
        tabs.addTab("About", buildAboutTab());

        return tabs;
    }

    // =========================================================
    // ======================= BOOKS ===========================
    // =========================================================
    private JPanel buildBooksTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton addBookBtn = new JButton("Add Book");
        JButton removeBookBtn = new JButton("Remove Book");

        refreshBtn.addActionListener(e -> loadBooks());

        addBookBtn.addActionListener(e -> {
            AddBookDialog d = new AddBookDialog(this, service);
            d.setVisible(true);
            if (d.isOk()) loadBooks();
        });

        removeBookBtn.addActionListener(e -> removeSelectedBook());


        top.add(refreshBtn);
        top.add(addBookBtn);
        top.add(removeBookBtn);

        p.add(top, BorderLayout.NORTH);

        booksTable = new JTable(booksModel);
        booksTable.setRowHeight(24);
        booksTable.setAutoCreateRowSorter(true);

        p.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        return p;
    }

    private void loadBooks() {
        booksModel.setRowCount(0);
        for (Book b : service.getAllBooksSortedByTitle()) {
            booksModel.addRow(new Object[]{
                    b.getId(),
                    b.getTitle(),
                    b.getIsbn(),
                    b.getCategory() != null ? b.getCategory().getName() : "",
                    b.getPublisher() != null ? b.getPublisher().getName() : "",
                    b.isAvailable()
            });
        }
    }

    // =========================================================
    // ====================== MEMBERS ==========================
    // =========================================================
    private JPanel buildMembersTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Member");
        JButton refreshBtn = new JButton("Refresh");
        JButton removeBtn = new JButton("Remove Member");

        top.add(addBtn);
        top.add(refreshBtn);
        top.add(removeBtn);

        membersTable = new JTable(membersModel);
        membersTable.setRowHeight(24);
        membersTable.setAutoCreateRowSorter(true);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(membersTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            AddMemberDialog d = new AddMemberDialog(this, service);
            d.setVisible(true);
            if (d.isOk()) loadMembers();
        });

        refreshBtn.addActionListener(e -> loadMembers());

        removeBtn.addActionListener(e -> removeSelectedMember());

        return p;
    }

    private void loadMembers() {
        membersModel.setRowCount(0);
        for (Member m : service.getAllMembers()) {
            membersModel.addRow(new Object[]{
                    m.getId(),
                    m.getFullName(),
                    m.getEmail(),
                    m.getRegistrationDate()
            });
        }
    }

    private void removeSelectedMember() {
        int viewRow = membersTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecteaza un membru din tabel!");
            return;
        }

        int row = membersTable.convertRowIndexToModel(viewRow);
        long memberId = (long) membersModel.getValueAt(row, 0);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Sigur vrei sa stergi membrul cu ID " + memberId + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            service.removeMember(memberId);
            refreshAll();
            JOptionPane.showMessageDialog(this, "Membrul a fost sters.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedBook() {
        int viewRow = booksTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecteaza o carte din tabel!");
            return;
        }

        int row = booksTable.convertRowIndexToModel(viewRow);
        long bookId = ((Number) booksModel.getValueAt(row, 0)).longValue();



        int choice = JOptionPane.showConfirmDialog(
                this,
                "Sigur vrei sa stergi cartea cu ID " + bookId + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            service.removeBook(bookId);
            refreshAll();
            JOptionPane.showMessageDialog(this, "Cartea a fost stearsa.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }


    // =========================================================
    // ======================= LOANS ===========================
    // =========================================================
    private JPanel buildLoansTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        // Top bar with Days + buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Days:"));
        daysSpinner = new JSpinner(new SpinnerNumberModel(14, 1, 365, 1));
        top.add(daysSpinner);

        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        JButton refreshBtn = new JButton("Refresh");

        borrowBtn.addActionListener(e -> onBorrowFromSelection());
        returnBtn.addActionListener(e -> onReturn());
        refreshBtn.addActionListener(e -> {
            loadLoans();
            loadAvailableMembers();
            loadAvailableBooks();
        });

        top.add(borrowBtn);
        top.add(returnBtn);
        top.add(refreshBtn);

        p.add(top, BorderLayout.NORTH);

// Two selection tables
        availableMembersTable = new JTable(availableMembersModel);
        availableMembersTable.setRowHeight(22);
        availableMembersTable.setAutoCreateRowSorter(true);
        availableMembersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        availableBooksTable = new JTable(availableBooksModel);
        availableBooksTable.setRowHeight(22);
        availableBooksTable.setAutoCreateRowSorter(true);
        availableBooksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

// panel with both pickers side-by-side
        JPanel pickers = new JPanel(new GridLayout(1, 2, 10, 10));
        pickers.add(wrapWithTitle(new JScrollPane(availableMembersTable), "Available Members (no active loan)"));
        pickers.add(wrapWithTitle(new JScrollPane(availableBooksTable), "Available Books"));

// LIMIT HEIGHT (make it smaller)
        pickers.setPreferredSize(new Dimension(100, 160));
        pickers.setMinimumSize(new Dimension(100, 140));

// Loans table
        loansTable = new JTable(loansModel);
        loansTable.setRowHeight(24);
        loansTable.setAutoCreateRowSorter(true);

        JScrollPane loansScroll = new JScrollPane(loansTable);

// Vertical split: small pickers on top, big loans below
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pickers, loansScroll);
        split.setResizeWeight(0.25); // 25% top, 75% bottom
        split.setDividerSize(6);

        p.add(split, BorderLayout.CENTER);
        return p;

    }

    private JComponent wrapWithTitle(JComponent comp, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }
    private void loadAvailableMembers() {
        availableMembersModel.setRowCount(0);

        for (Member m : service.getAllMembers()) {
            boolean hasActiveLoan = service.getActiveLoans().stream()
                    .anyMatch(l -> l.getMember().getId().equals(m.getId()));
            if (!hasActiveLoan) {
                availableMembersModel.addRow(new Object[]{ m.getId(), m.getFullName() });
            }
        }
    }

    private void loadAvailableBooks() {
        availableBooksModel.setRowCount(0);

        for (Book b : service.getAllBooksSortedByTitle()) {
            if (b.isAvailable()) {
                availableBooksModel.addRow(new Object[]{
                        b.getId(),
                        b.getTitle(),
                        b.getCategory() != null ? b.getCategory().getName() : ""
                });
            }
        }
    }




    private void loadLoans() {
        loansModel.setRowCount(0);
        for (Loan l : service.getActiveLoans()) {
            loansModel.addRow(new Object[]{
                    l.getId(),
                    l.getMember().getFullName(),
                    l.getBook().getTitle(),
                    l.getLoanDate(),
                    l.getDueDate(),
                    l.getReturnDate()
            });
        }
    }

    private void onBorrowFromSelection() {
        int mView = availableMembersTable.getSelectedRow();
        if (mView < 0) {
            JOptionPane.showMessageDialog(this, "Selecteaza un membru disponibil!");
            return;
        }

        int bView = availableBooksTable.getSelectedRow();
        if (bView < 0) {
            JOptionPane.showMessageDialog(this, "Selecteaza o carte disponibila!");
            return;
        }

        int mRow = availableMembersTable.convertRowIndexToModel(mView);
        int bRow = availableBooksTable.convertRowIndexToModel(bView);

        long memberId = ((Number) availableMembersModel.getValueAt(mRow, 0)).longValue();
        long bookId = ((Number) availableBooksModel.getValueAt(bRow, 0)).longValue();
        int days = (Integer) daysSpinner.getValue();

        try {
            service.borrowBook(memberId, bookId, days);
            JOptionPane.showMessageDialog(this, "Borrow successful!");
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Borrow error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void onReturn() {
        int viewRow = loansTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecteaza un imprumut din tabel!");
            return;
        }

        int row = loansTable.convertRowIndexToModel(viewRow);
        long loanId = (long) loansModel.getValueAt(row, 0);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Return loan ID " + loanId + " ?",
                "Confirm return",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            service.returnBook(loanId);
            refreshAll();
            JOptionPane.showMessageDialog(this, "Returned!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // ====================== REPORTS ==========================
    // =========================================================
    private JPanel buildReportsTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JButton generateBtn = new JButton("Generate Reports");

        reportsArea = new JTextArea();
        reportsArea.setEditable(false);
        reportsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        generateBtn.addActionListener(e -> {
            // update everything, then generate
            refreshAll();
            reportsArea.setText(service.buildReportsText());
            reportsArea.setCaretPosition(0);
        });

        p.add(generateBtn, BorderLayout.NORTH);
        p.add(new JScrollPane(reportsArea), BorderLayout.CENTER);
        return p;
    }

    // ===== ABOUT =====
    private JPanel buildAboutTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("<html><h2>Library Automation</h2><p>Swing UI</p></html>"), BorderLayout.NORTH);
        return p;
    }

    // ===== REFRESH =====
    private void refreshAll() {
        if (todayLabel != null) todayLabel.setText("Today: " + LocalDate.now());
        loadBooks();
        loadMembers();
        loadLoans();
        loadAvailableMembers();
        loadAvailableBooks();

        if (reportsArea != null) {
            reportsArea.setText(service.buildReportsText());
            reportsArea.setCaretPosition(0);
        }
    }

}
