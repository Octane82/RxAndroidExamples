package dkarelin.ru.rxjavaexample_1;


import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;



@StorIOSQLiteType(table = "tweets")
public class Tweet {

    // annotated fields should have package-level visibility
    @StorIOSQLiteColumn(name = "author")
    String author;

    @StorIOSQLiteColumn(name = "content")
    String content;

    // please leave default constructor with package-level visibility
    Tweet() {}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
