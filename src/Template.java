import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import hashcode.D.Book;
import hashcode.D.Library;

public class Template {
    private String outputFile;

    private int nBooks;
    private int nLibraries;
    private int nDays;

    private List<Book> books;
    private List<Library> libraries;

    public Template(String inputFile) throws IOException {
        System.out.println("Running " + Template.class.getSimpleName() + " with " + inputFile);
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

    public void solve() throws IOException {

        printToFile();
    }
    
    public static long score(Collection<Library> libraries, int nDays) {
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
        Template template = new Template("input_file");
        template.solve();
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
