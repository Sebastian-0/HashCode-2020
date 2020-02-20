import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Info {
    private String outputFile;

    private int nBooks;
    private int nLibraries;
    private int nDays;

    private List<Book> books;
    private List<Library> libraries;

    public Info(String inputFile) throws IOException {
        System.out.println("Running " + Info.class.getSimpleName() + " with " + inputFile);
        BufferedReader in = new BufferedReader(new FileReader("input/" + inputFile));
        outputFile = "output/" + inputFile;

        int[] tokens = integers(in.readLine());
        nBooks = tokens[0];
        nLibraries = tokens[1];
        nDays = tokens[2];

        books = new ArrayList<>();
        tokens = integers(in.readLine());
        for (int i = 0; i < nBooks; i++) {
            books.add(new Book(i, tokens[i]));
        }

        libraries = new ArrayList<>();
        for (int i = 0; i < nLibraries; i++) {
            tokens = integers(in.readLine());
            libraries.add(new Library(i, tokens[1], tokens[2]));
            tokens = integers(in.readLine());
            for (int j = 0; j < tokens.length; j++) {
                libraries.get(i).books.add(books.get(tokens[j]));
            }
        }
    }

    public void analyze() {
        int bookMinValue = Integer.MAX_VALUE;
        int bookMaxValue = Integer.MIN_VALUE;
        for (Book book : books) {
            bookMinValue = Math.min(bookMinValue, book.score);
            bookMaxValue = Math.max(bookMaxValue, book.score);
        }
        int[] bookValues = new int[bookMaxValue - bookMinValue + 1];
        for (Book book : books) {
            bookValues[book.score - bookMinValue] += 1;
        }

        System.out.println(String.format("Book value range [%d, %d]", bookMinValue, bookMaxValue));
        System.out.println(Arrays.stream(bookValues).mapToObj(Integer::toString).collect(Collectors.joining(" ")));

        int libraryMinValue = Integer.MAX_VALUE;
        int libraryMaxValue = Integer.MIN_VALUE;
        for (Library library : libraries) {
            libraryMinValue = Math.min(libraryMinValue, library.books.size());
            libraryMaxValue = Math.max(libraryMaxValue, library.books.size());
        }
        int[] libraryValues = new int[libraryMaxValue - libraryMinValue + 1];
        for (Library library : libraries) {
            libraryValues[library.books.size() - libraryMinValue] += 1;
        }

        System.out.println(String.format("Library sizes range [%d, %d]", libraryMinValue, libraryMaxValue));
        System.out.println(Arrays.stream(libraryValues).mapToObj(Integer::toString).collect(Collectors.joining(" ")));
    }

    public int[] integers(String nums) {
        String[] tokens = nums.split(" ");
        int[] result = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = integer(tokens[i]);
        }
        return result;
    }

    public int integer(String num) {
        return Integer.parseInt(num);
    }

    public static void main(String[] args) throws IOException {
        Info template = new Info("b_read_on.txt");
        template.analyze();
    }


    public static class Book {
        int id;
        int score;

        public Book(int id, int score) {
            this.id = id;
            this.score = score;
        }
    }

    public static class Library {
        int id;
        List<Book> books;
        int signupTime;
        int booksPerDay;

        public Library(int id, int signupTime, int booksPerDay) {
            this.id = id;
            this.books = new ArrayList<>();
            this.signupTime = signupTime;
            this.booksPerDay = booksPerDay;
        }
    }
}
