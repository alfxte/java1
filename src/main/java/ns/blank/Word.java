package ns.blank;

import java.util.ArrayList;
import java.util.List;

class Word {
	
	enum EWordType {
		MONOPHONE,
		DIPHONE,
		TRIPHONE
	}
	
	public static List<Word> toWordList(List<String> list, EWordType wordtype) {
		ArrayList<Word> words = new ArrayList<>();
		list.forEach((str) -> {
			words.add(new Word(str, true, wordtype));
		}); 
		return words;
	}
	
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
	EWordType type = EWordType.DIPHONE;
	
	public Word(String word, boolean shouldProcess, EWordType wordType) {
		this.str = word;
		this.shouldProcess = shouldProcess;
		this.type = wordType;
	}
	
	public Word(String word, boolean shouldProcess) {
		this.str = word;
		this.shouldProcess = shouldProcess;			
	}
	
	public Word(String word) {
		this.str = word;
	}
	
	public boolean isDiphone() {
		return type == EWordType.DIPHONE;
	}
	
	public boolean isMonophone() {
		return type == EWordType.MONOPHONE;
	}
	
	public boolean isTriphone() {
		return type == EWordType.TRIPHONE;
	}
	
	@Override
	public String toString() {		
		return str + " (proc = " + shouldProcess + ")";
	}
	
}

