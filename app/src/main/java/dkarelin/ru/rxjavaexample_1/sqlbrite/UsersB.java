package dkarelin.ru.rxjavaexample_1.sqlbrite;

/**
 * POJO Sqlite brite
 */

public class UsersB {

    private String id;
    private String name;
    private String age;
    private String info;


    public UsersB(String id, String name, String age, String info) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
