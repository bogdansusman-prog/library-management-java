package ui;

import app.*;

import javax.swing.*;
import java.awt.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class AddBookDialog extends JDialog {

    private final LibraryService service;
    private boolean ok = false;

    private JTextField titleField;
    private JTextField isbnField;
    private JSpinner yearSpinner;

    private JComboBox<Publisher> publisherBox;
    private JComboBox<Category> categoryBox;

    private JList<Author> authorsList;

    public AddBookDialog(Frame owner, LibraryService service) {
        super(owner, "Add Book", true);
        this.service = service;

        setContentPane(buildUI());
        pack();                 // IMPORTANT: face dialogul cât are nevoie
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    public boolean isOk() {
        return ok;
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        int row = 0;

        // Title
        titleField = new JTextField(22);
        addRow(form, c, row++, "Title:", titleField);

        // ISBN
        isbnField = new JTextField(22);
        addRow(form, c, row++, "ISBN:", isbnField);

        // Year
        int thisYear = Year.now().getValue();
        yearSpinner = new JSpinner(new SpinnerNumberModel(thisYear, 1500, thisYear + 5, 1));
        addRow(form, c, row++, "Year:", yearSpinner);

        // Publisher + button
        publisherBox = new JComboBox<>();
        reloadPublishers();
        JButton addPublisherBtn = new JButton("+");
        addPublisherBtn.setToolTipText("Add publisher");
        addPublisherBtn.addActionListener(e -> onAddPublisher());

        addRowWithButton(form, c, row++, "Publisher:", publisherBox, addPublisherBtn);

        // Category + button
        categoryBox = new JComboBox<>();
        reloadCategories();
        JButton addCategoryBtn = new JButton("+");
        addCategoryBtn.setToolTipText("Add category");
        addCategoryBtn.addActionListener(e -> onAddCategory());

        addRowWithButton(form, c, row++, "Category:", categoryBox, addCategoryBtn);

        // Authors list + add author button
        authorsList = new JList<>();
        authorsList.setVisibleRowCount(5);
        authorsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        authorsList.setFixedCellWidth(260);
        reloadAuthors();

        JScrollPane authorsScroll = new JScrollPane(authorsList);
        authorsScroll.setPreferredSize(new Dimension(320, 120));

        JButton addAuthorBtn = new JButton("+ Author");
        addAuthorBtn.addActionListener(e -> onAddAuthor());

        // label
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        form.add(new JLabel("Authors:"), c);

        // list
        c.gridx = 1; c.gridy = row; c.weightx = 1;
        form.add(authorsScroll, c);

        // button
        c.gridx = 2; c.gridy = row; c.weightx = 0;
        form.add(addAuthorBtn, c);

        row++;

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");

        addBtn.addActionListener(e -> onAddBook());
        cancelBtn.addActionListener(e -> dispose());

        btns.add(addBtn);
        btns.add(cancelBtn);

        root.add(btns, BorderLayout.SOUTH);

        return root;
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1; c.gridy = row; c.weightx = 1;
        form.add(field, c);

        c.gridx = 2; c.gridy = row; c.weightx = 0;
        form.add(Box.createHorizontalStrut(30), c); // spațiu pt col 3
    }

    private void addRowWithButton(JPanel form, GridBagConstraints c, int row, String label, JComponent field, JButton btn) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1; c.gridy = row; c.weightx = 1;
        form.add(field, c);

        c.gridx = 2; c.gridy = row; c.weightx = 0;
        form.add(btn, c);
    }

    private void reloadPublishers() {
        publisherBox.removeAllItems();
        for (Publisher p : service.getAllPublishers()) {
            publisherBox.addItem(p);
        }
    }

    private void reloadCategories() {
        categoryBox.removeAllItems();
        for (Category c : service.getAllCategories()) {
            categoryBox.addItem(c);
        }
    }

    private void reloadAuthors() {
        DefaultListModel<Author> model = new DefaultListModel<>();
        for (Author a : service.getAllAuthors()) model.addElement(a);
        authorsList.setModel(model);
    }

    private void onAddPublisher() {
        String name = JOptionPane.showInputDialog(this, "Publisher name:");
        if (name == null || name.trim().isEmpty()) return;

        String city = JOptionPane.showInputDialog(this, "City:");
        if (city == null || city.trim().isEmpty()) return;

        service.addPublisher(name.trim(), city.trim());
        reloadPublishers();
    }

    private void onAddCategory() {
        String name = JOptionPane.showInputDialog(this, "Category name:");
        if (name == null || name.trim().isEmpty()) return;

        service.addCategory(name.trim());
        reloadCategories();
    }

    private void onAddAuthor() {
        JTextField first = new JTextField();
        JTextField last = new JTextField();
        JTextField year = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
        p.add(new JLabel("First name:"));
        p.add(first);
        p.add(new JLabel("Last name:"));
        p.add(last);
        p.add(new JLabel("Birth year:"));
        p.add(year);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Author", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            int y = Integer.parseInt(year.getText().trim());
            service.addAuthor(first.getText().trim(), last.getText().trim(), y);
            reloadAuthors();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddBook() {
        try {
            String title = titleField.getText().trim();
            String isbn = isbnField.getText().trim();
            int year = (Integer) yearSpinner.getValue();

            Publisher publisher = (Publisher) publisherBox.getSelectedItem();
            Category category = (Category) categoryBox.getSelectedItem();

            List<Author> selectedAuthors = authorsList.getSelectedValuesList();
            if (selectedAuthors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select at least 1 author.");
                return;
            }

            service.addBook(title, isbn, year, publisher, category, new ArrayList<>(selectedAuthors));

            ok = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Add book error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
