package app;

import java.time.LocalDate;

public class MemberCard implements Identifiable<Long> {

    private Long id;
    private Member member;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expirationDate;

    public MemberCard(Long id, Member member, String cardNumber,
                      LocalDate issueDate, LocalDate expirationDate) {
        this.id = id;
        this.member = member;
        this.cardNumber = cardNumber;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return "MemberCard{" +
                "cardNumber='" + cardNumber + '\'' +
                ", member=" + member.getFullName() +
                '}';
    }
}

