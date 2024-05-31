package ReadBookAssignment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PageProcessor {
    private int pageNumber;
    private Set<String> excludeWords;

    public PageProcessor(int pageNumber, Set<String> excludeWords) {
        this.pageNumber = pageNumber;
        this.excludeWords = excludeWords;
    }

//    public Map<String, Set<Integer>> processPage(String filePath) throws IOException {
//        Map<String, Set<Integer>> pageWords = new HashMap<>();
//        List<String> lines = Files.readAllLines(Paths.get(filePath));
//        for (String line : lines) {
//            // Split by whitespace or any non-word character, preserving special symbols
////        	 String[] words = line.toLowerCase().split("[^\\w表\-/\"]+");
//            String[] words = line.toLowerCase().split("[^\\w表\-\"]+");
//            for (String word : words) {
//                // Remove unwanted characters from the start and end
////            	word = word.replaceAll("^[^\\w表\-/\"]+|[^\\w表\-/\"]+$", "");
//                word = word.replaceAll("^[^\\w表\-\"]+|[^\\w表\-\"]+$", "");
//                if (!word.isEmpty() && !excludeWords.contains(word) && !word.matches("^\\d+$") && !word.matches(".*[a-zA-Z].*\\d.*")  ) {
//                    pageWords.computeIfAbsent(word, k -> new HashSet<>()).add(pageNumber); // Use page number 1 for simplicity
//                }
//            }
//        }
//        return pageWords;
//    }
//    
    
    
    
    public Map<String, Set<Integer>> processPage(String filePath) throws IOException {
        Map<String, Set<Integer>> pageWords = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        
        for (String line : lines) {
            // Split by whitespace or any non-word character, preserving special symbols
            String[] words = line.toLowerCase().split("(?<=\\W)|(?=\\W)");
            
            for (int i = 0; i < words.length; i++) {
                String word = words[i].trim();
                
                // Remove unwanted characters from the start and end of words
                word = word.replaceAll("^[^\\w表\-\"]+|[^\\w表\-\"]+$", "");

                // Check if the word is not empty and is not in the exclude list
                if (!word.isEmpty() && !excludeWords.contains(word) && !word.matches("^\\d+$") && !word.matches(".*[a-zA-Z].*\\d.*")) {
                    // Check if the current word is a symbol
                    if (word.matches("^[\\W&&[^_]]+$")) {
                        pageWords.computeIfAbsent(word, k -> new HashSet<>()).add(pageNumber);
                    } else {
                        // Check if the previous word was a symbol
                        if (i > 0) {
                            String prevWord = words[i - 1].trim();
                            prevWord = prevWord.replaceAll("^[^\\w表\-\"]+|[^\\w表\-\"]+$", "");

                            if (prevWord.matches("^[\\W&&[^_]]+$")) {
                                pageWords.computeIfAbsent(prevWord + word, k -> new HashSet<>()).add(pageNumber);
                            } else {
                                pageWords.computeIfAbsent(word, k -> new HashSet<>()).add(pageNumber);
                            }
                        } else {
                            pageWords.computeIfAbsent(word, k -> new HashSet<>()).add(pageNumber);
                        }
                    }
                }
            }
        }
        return pageWords;
    }

  





}
