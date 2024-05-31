package ReadBookAssignment;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WordIndexer {
    private List<String> pages;
    private String excludeWordsFile;
    private Map<String, Set<Integer>> index;

    public WordIndexer(List<String> pages, String excludeWordsFile) {
        this.pages = pages;
        this.excludeWordsFile = excludeWordsFile;
        this.index = new TreeMap<>();
    }

    public Set<String> readExcludeWords() throws IOException {
        Set<String> excludeWords = new HashSet<>();
        List<String> lines = Files.readAllLines(Paths.get(excludeWordsFile));
        for (String line : lines) {
            excludeWords.add(line.trim().toLowerCase());
        }
        return excludeWords;
    }

    public void processPages() throws IOException {
        Set<String> excludeWords = readExcludeWords();
        for (int i = 0; i < pages.size(); i++) {
            PageProcessor processor = new PageProcessor(i + 1, excludeWords);
            Map<String, Set<Integer>> pageWords = processor.processPage(pages.get(i));
            for (Map.Entry<String, Set<Integer>> entry : pageWords.entrySet()) {
                index.merge(entry.getKey(), entry.getValue(), (oldSet, newSet) -> {    //using lambda expression
                    oldSet.addAll(newSet);
                    return oldSet;
                });
            }
        }
    }

//    public void writeIndex(String outputFile) throws IOException {
//        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
//            writer.write("Word : Page Numbers");
//            writer.newLine();
//            writer.write("-------------------");
//            writer.newLine();
//            for (Map.Entry<String, Set<Integer>> entry : index.entrySet()) {
//                writer.write(entry.getKey() + " : " + String.join(",", 
//                    entry.getValue().stream().map(String::valueOf).sorted().toArray(String[]::new)));    //:: scope variable
//                writer.newLine();
//            }
//        }
//    }
    
    public void writeIndex(String outputFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
            writer.write("Word : Page Numbers");
            writer.newLine();
            writer.write("-------------------");
            writer.newLine();

            // Create a list to store the entries
            List<Map.Entry<String, Set<Integer>>> sortedEntries = new ArrayList<>(index.entrySet());

            // Sort the entries to prioritize words first
            Collections.sort(sortedEntries, (entry1, entry2) -> {
                String key1 = entry1.getKey();
                String key2 = entry2.getKey();
                boolean isWord1 = key1.matches("[a-zA-Z]+");
                boolean isWord2 = key2.matches("[a-zA-Z]+");

                // Prioritize words over symbols and combinations
                if (isWord1 && !isWord2) {
                    return -1; // Word comes before symbol or combination
                } else if (!isWord1 && isWord2) {
                    return 1; // Symbol or combination comes after word
                } else {
                    return key1.compareToIgnoreCase(key2); // Sort alphabetically otherwise
                }
            });

            // Write sorted entries to the output file
            for (Map.Entry<String, Set<Integer>> entry : sortedEntries) {
                writer.write(entry.getKey() + " : " + String.join(",", 
                    entry.getValue().stream().map(String::valueOf).sorted().toArray(String[]::new)));
                writer.newLine();
            }
        }
    }


//    public void printIndex() {
//        System.out.println("Word : Page Numbers");
//        System.out.println("-------------------");
//        
//        for (Map.Entry<String, Set<Integer>> entry : index.entrySet()) {
//            System.out.println(entry.getKey() + " : " + String.join(",", 
//                entry.getValue().stream().map(String::valueOf).sorted().toArray(String[]::new)));
//        }
//    }

    public void printIndex() {
        System.out.println("Word : Page Numbers");
        System.out.println("-------------------");

        // Create a list to store the entries
        List<Map.Entry<String, Set<Integer>>> sortedEntries = new ArrayList<>(index.entrySet());

        // Sort the entries to prioritize words first
        Collections.sort(sortedEntries, (entry1, entry2) -> {
            String key1 = entry1.getKey();
            String key2 = entry2.getKey();
            boolean isWord1 = key1.matches("[a-zA-Z]+");
            boolean isWord2 = key2.matches("[a-zA-Z]+");

            // Prioritize words over symbols and combinations
            if (isWord1 && !isWord2) {
                return -1; // Word comes before symbol or combination
            } else if (!isWord1 && isWord2) {
                return 1; // Symbol or combination comes after word
            } else {
                return key1.compareToIgnoreCase(key2); // Sort alphabetically otherwise
            }
        });

        // Print sorted entries to the console
        for (Map.Entry<String, Set<Integer>> entry : sortedEntries) {
            System.out.println(entry.getKey() + " : " + String.join(",", 
                entry.getValue().stream().map(String::valueOf).sorted().toArray(String[]::new)));
        }
    }


    public static void main(String[] args) {
        try {
            List<String> pages = Arrays.asList(
                "C:\\Users\\IT Tech\\Downloads\\Page1.txt", 
                "C:\\Users\\IT Tech\\Downloads\\Page2.txt", 
                "C:\\Users\\IT Tech\\Downloads\\Page3.txt"
            );
            String excludeWordsFile = "C:\\Users\\IT Tech\\Downloads\\exclude-words.txt";
            String outputFile = "C:\\Users\\IT Tech\\Downloads\\index.txt";

            WordIndexer indexer = new WordIndexer(pages, excludeWordsFile);
            indexer.processPages();
            indexer.writeIndex(outputFile);
            indexer.printIndex();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
