package app;


import java.time.LocalDate;
public class Member extends Person{

    private String email;
    private LocalDate registrationDate;
    private MemberCard card;    //relatie 1-1


    public Member(Long id, String firstName, String lastName,
                  String email, LocalDate registrationDate) {
        super(id, firstName, lastName);
        this.email = email;
        this.registrationDate = registrationDate;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    public MemberCard getCard(){
        return card;
    }
    public void setCard(MemberCard card){
        this.card = card;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + getId() +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
