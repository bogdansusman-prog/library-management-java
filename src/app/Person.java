package app;

public abstract class Person implements Identifiable<Long> {
    protected Long id;
    private String firstName;
    private String lastName;

    public Person(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }
}
