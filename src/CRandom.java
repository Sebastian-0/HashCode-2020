import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CRandom {
    private static final Map<String, Long> BEST_SCORES = new HashMap<>();

    static {
        BEST_SCORES.put("a_example.txt", 21L);
        BEST_SCORES.put("b_read_on.txt", 5_855_900L);
        BEST_SCORES.put("c_incunabula.txt", 5_689_822L);
        BEST_SCORES.put("d_tough_choices.txt", 5_028_010L);
        BEST_SCORES.put("e_so_many_books.txt", 4976322L);
        BEST_SCORES.put("f_libraries_of_the_world.txt", 5345656L);
    }

    private String inputFileName;
    private String outputFile;

    private int nBooks;
    private int nLibraries;
    private int nDays;

    private List<Book> books;
    private List<Library> libraries;

    public CRandom(String inputFile) throws IOException {
        inputFileName = inputFile;
        System.out.println("Running " + CRandom.class.getSimpleName() + " with " + inputFile);
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
        int session = 0;
        while (true) {
            List<Library> libraries = copyLibraries();
            List<Library> result = new ArrayList<>();
            int time = nDays;
            for (int i = 0; i < nLibraries; i++) {
                for (Library library : libraries) {
                    library.computeValue(time, session);
                }
                libraries.sort((Comparator.comparingDouble(l -> -l.value)));

                int offset = 0;
                while (Math.random() <= 0.5 && libraries.size() > offset+1) {
                    offset++;
                }

                Library lib = libraries.remove(offset);

                for (Book b : lib.books) {
                    b.taken = session;
                }
                time -= lib.signupTime;
                result.add(lib);

                if (time <= 0) {
                    break;
                }

//                if (i % Math.max(1, nLibraries / 20) == 0) {
//                    System.out.println("Iteration " + i + " " + 100 * i / (double) nLibraries + "%");
//                }
            }

            // Test
            long score = score(result, nDays);

            if (score > BEST_SCORES.get(inputFileName)) {
                System.out.println("New best: " + score);
                BEST_SCORES.put(inputFileName, score);
                printToFile(result);
            }

            session++;
        }
    }

    private List<Library> copyLibraries() {
        List<Library> libs = new ArrayList<>();
        for (Library library : libraries) {
            Library copy = new Library(library.id, library.signupTime, library.booksPerDay);
            copy.books.addAll(library.books);
            libs.add(copy);
        }
        return libs;
    }

    public long score(Collection<Library> libraries, int nDays) {
        int day = 0;
        Set<Book> books = new HashSet<>();
        for (Library l : libraries) {
            day += l.signupTime;
            long daysRemaining = Math.max(nDays - day, 0);
            l.books.stream()
                    .limit(daysRemaining * l.booksPerDay)
                    .forEach(b -> books.add(b));
        }
        return books.stream()
                .mapToLong(b -> b.score)
                .sum();
    }

    public void printToSyso() throws IOException {
        printSolution(libraries, System.out);
    }

    public void printToFile(List<Library> libraries) throws IOException {
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
//        CRandom template = new CRandom("f_libraries_of_the_world.txt");
//        template.solve();
//        CRandom template = new CRandom("e_so_many_books.txt");
//        template.solve();
//        template = new C("d_tough_choices.txt");
//        template.solve();
        CRandom template = new CRandom("c_incunabula.txt");
        template.solve();
//        template = new CRandom("a_example.txt");
//        template.solve();
    }


    public static class Book {
        int id;
        int score;
        int taken;

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

        public void computeValue(int daysLeft, int currentSession) {
            books.removeIf(b -> b.taken == currentSession);
            int sum = 0;
            daysLeft -= signupTime;
            for (int i = 0; i < Math.min(daysLeft * booksPerDay, books.size()); i++) {
                sum += books.get(i).score;
            }
            value = sum / (double) signupTime;
        }
    }
}
