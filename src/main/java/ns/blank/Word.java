package ns.blank;

import java.util.ArrayList;
import java.util.List;

class Word {
	
	public static List<Word> toWordList(List<String> list) {
		ArrayList<Word> words = new ArrayList<>();
		list.forEach((str) -> {
			words.add(new Word(str, true));
		}); 
		return words;
	}
	
	public static List<Word> toWordList(String[] arr) {
		ArrayList<Word> words = new ArrayList<>();			
		for (String str : arr) {
			words.add(new Word(str, true));
		}
		return words;
	}
	
	String str = "";
	boolean shouldProcess = true;		
	
	public Word(String word, boolean shouldProcess) {
		this.str = word;
		this.shouldProcess = shouldProcess;			
	}
	
	public Word(String word) {
		this.str = word;
	}
	
	@Override
	public String toString() {		
		return str + " (proc = " + shouldProcess + ")";
	}
	
}

