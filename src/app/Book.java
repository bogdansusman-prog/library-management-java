package app;


import java.util.ArrayList;
import java.util.List;

/*
Carte (tabel books)
 */
public class Book implements Identifiable<Long>, Borrowable, Comparable<Book> {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishYear;
    private Publisher publisher;
    private Category category;
    private List<Author> authors = new ArrayList<>();
    private boolean available = true;

    public Book(Long id, String title, String isbn, Integer publishYear,
                Publisher publisher, Category category) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publishYear = publishYear;
        this.publisher = publisher;
        this.category = category;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void addAuthor(Author author) {
        authors.add(author);
    }

    @Override
    public int compareTo(Book other) {
        int byTitle = this.title.compareToIgnoreCase(other.title);
        if (byTitle != 0) return byTitle;
        return this.publishYear.compareTo(other.publishYear);
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", year=" + publishYear +
                ", publisher=" + publisher +
                ", category=" + category +
                '}';
    }
}
