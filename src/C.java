import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
    	List<Library> libraries = copyLibraries();
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
        System.out.println("SCORE: " + score(result, nDays));
        printToFile(result);
    }
    
    public long score(Collection<Library> libraries, int nDays) {
    	int day = 0;
    	Set<Book> books = new HashSet<>();
    	for (Library l : libraries) {
    		day += l.signupTime;
    		int daysRemaining = Math.max(nDays - day, 0);
    		l.books.stream()
    				.limit(daysRemaining * l.booksPerDay)
    				.forEach(b -> books.add(b));
    	}
    	return books.stream()
    			.mapToLong(b -> b.score)
    			.sum();
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
//        new C("f_libraries_of_the_world.txt").solve();
//        new C("e_so_many_books.txt").solve();
//        new C("d_tough_choices.txt").solve();
        new C("c_incunabula.txt").solve();
//        new C("a_example.txt").solve();
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
