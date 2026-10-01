package ui;

import app.LibraryService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;


public class AddMemberDialog extends JDialog {
    private boolean ok = false;
    private final JTextField firstName = new JTextField(15);
    private final JTextField lastName = new JTextField(15);
    private final JTextField email = new JTextField(15);
    private final JTextField regDate = new JTextField(15);


    public AddMemberDialog(JFrame parent, LibraryService service) {
        super(parent, "Add Member", true);

        JPanel p = new JPanel(new GridLayout(5, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(new JLabel("First name:"));
        p.add(firstName);

        p.add(new JLabel("Last name:"));
        p.add(lastName);

        p.add(new JLabel("Email:"));
        p.add(email);

        p.add(new JLabel("Registration (YYYY-MM-DD):"));
        regDate.setText(LocalDate.now().toString());
        p.add(regDate);

        JButton addBtn = new JButton("Add");
        JButton cancelBtn = new JButton("Cancel");

        addBtn.addActionListener(e -> {
            try {
                service.addMember(
                        firstName.getText().trim(),
                        lastName.getText().trim(),
                        email.getText().trim(),
                        LocalDate.parse(regDate.getText().trim())
                );
                ok = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        p.add(addBtn);
        p.add(cancelBtn);

        setContentPane(p);
        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isOk() {
        return ok;
    }
}
