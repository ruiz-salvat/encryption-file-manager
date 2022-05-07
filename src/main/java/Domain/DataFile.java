package Domain;

public class DataFile {

    private static DataFile instance;
    private String name;
    private String content;

    public DataFile(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public static void initialize() {
        instance = new DataFile(null, null);
    }

    public static DataFile getInstance() {
        return instance;
    }

    public static void setNewDataFile(String name, String content) {
        instance = new DataFile(name, content);
    }

    public static boolean isSet() {
        return instance.name != null && instance.content != null;
    }

    public static String getName() {
        return instance.name;
    }

    public static String getContent() {
        return instance.content;
    }

    @Override
    public String toString() {
        return "DataFile{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
