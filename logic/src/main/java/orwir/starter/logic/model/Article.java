package orwir.starter.logic.model;


import java.io.Serializable;
import java.util.Date;

public class Article implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String content;
    private Date date;

    public Article(Long id, String title, String description, String content, Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

}
