import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class C {
    private String outputFile;

    private int nBooks;
    private int nLibraries;
    private int nDays;

    private List<Book> books;
    private List<Library> libraries;

    public C(String inputFile) throws IOException {
        System.out.println("Running " + C.class.getSimpleName() + " with " + inputFile);
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

        libraries = new LinkedList<>();
        for (int i = 0; i < nLibraries; i++) {
            tokens = integers(in.readLine());
            libraries.add(new Library(i, tokens[1], tokens[2]));
            tokens = integers(in.readLine());
            for (int j = 0; j < tokens.length; j++) {
                libraries.get(i).books.add(books.get(tokens[j]));
            }
            libraries.get(i).books.sort(Comparator.comparingInt(b -> -b.score));
        }
    }

    public void solve() throws IOException {
        List<Library> result = new ArrayList<>();
        int time = nDays;
        for (int i = 0; i < nLibraries; i++) {
            for (Library library : libraries) {
                library.computeValue(time);
            }
            libraries.sort((Comparator.comparingDouble(l -> -l.value)));

            Library lib = libraries.remove(0);
            lib.books.forEach(b -> b.taken = true);
            time -= lib.signupTime;
            result.add(lib);

            if (i % Math.max(1, nLibraries / 20) == 0) {
                System.out.println("Iteration " + i + " " + 100 * i / (double) nLibraries + "%");
            }
        }

        libraries = result;
        printToFile();
    }

    public void printToSyso() throws IOException {
        printSolution(libraries, System.out);
    }

    public void printToFile() throws IOException {
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            printSolution(libraries, out);
        }
    }

    public void printSolution(Collection<Library> libraries, OutputStream out) throws IOException {
        libraries.removeIf(l -> l.books.isEmpty());
        write(out, "" + libraries.size());
        for (Library library : libraries) {
            write(out, library.id + " " + library.books.size());
            write(out, library.books.stream().map(b -> "" + b.id).collect(Collectors.joining(" ")));
        }
    }

    private void write(OutputStream out, String line) throws IOException {
        out.write(line.getBytes());
        out.write('\n');
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
        C template = new C("f_libraries_of_the_world.txt");
        template.solve();
        template = new C("e_so_many_books.txt");
        template.solve();
//        template = new C("d_tough_choices.txt");
//        template.solve();
        template = new C("c_incunabula.txt");
        template.solve();
        template = new C("a_example.txt");
        template.solve();
    }


    public static class Book {
        int id;
        int score;
        boolean taken;

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

        double value;

        public Library(int id, int signupTime, int booksPerDay) {
            this.id = id;
            this.books = new ArrayList<>();
            this.signupTime = signupTime;
            this.booksPerDay = booksPerDay;
        }

        public void computeValue(int daysLeft) {
            books.removeIf(b -> b.taken);
            int sum = 0;
            daysLeft -= signupTime;
            for (int i = 0; i < Math.min(daysLeft * booksPerDay, books.size()); i++) {
                sum += books.get(i).score;
            }
            value = sum / (double) signupTime;
        }
    }
}