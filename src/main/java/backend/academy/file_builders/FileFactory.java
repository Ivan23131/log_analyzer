package backend.academy.file_builders;

public class FileFactory {
    public static FileBuilder createFileBuilder(String type) {
        if (type.equals("markdown")) {
            return new MarkdownBuilder();
        } else {
            return new AsciiDocBuilder();
        }
    }

    private FileFactory() {

    }
}
