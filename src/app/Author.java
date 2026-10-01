package app;



public class Author extends Person {
    private Integer birthYear;
    public Author(Long id, String firstName, String lastName, Integer birthYear) {
        super(id, firstName, lastName);
        this.birthYear = birthYear;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    @Override
    public String toString() {
        return getFullName();
    }

}
