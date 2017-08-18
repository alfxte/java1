package ns.blank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import ns.blank.Word.EWordType;

class TextProcessor {		
	
	public static final String[] units = {
            "", "satu", "dua", "tiga", "empat", "lima", "enam", "tujuh",
            "lapan", "sembilan", "sepuluh", "sebelas", "dua belas", "tiga belas", "empat belas",
            "lima belas", "enam belas", "tujuh belas", "lapan belas", "sembilan belas"
    };
	
	public static final String[] tens = {
            "",        // 0
            "",        // 1
            "dua puluh",  // 2
            "tiga puluh",  // 3
            "empat puluh",   // 4
            "lima puluh",   // 5
            "enam puluh",   // 6
            "tujuh puluh", // 7
            "lapan puluh",  // 8
            "sembilan puluh",   // 9
            "seratus",   // 10
            "seribu"   // 11
    };
	
	/*package*/ ArrayList<Word> words;		
	private HashMap<String, String> mMap;		
	
	public TextProcessor(String text, String rules1Files) {
		words = new ArrayList<>();
		words.addAll(Word.toWordList(text.split(" "))); // split by space			
		for (int c = 0; c < words.size(); c++) {
			Word word = words.get(c);
			if (!word.shouldProcess) {
				continue;
			}
			
			word.str = word.str.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
			
			//INFO: rule 1			
			try {
				Word res1 = applyRule1(word, rules1Files);
				if (res1 != null) { // reach here, means rule1 got applied
					if (res1.str.contains("_")) { //it means the abbreviation not available on abbrev.txt
						List<Word> list = Word.toWordList(Arrays.asList(res1.str.split("_")));
						list.forEach((w) -> w.type = EWordType.MONOPHONE);
						// replace the current position word with the splited word
						words.remove(c);
						words.addAll(c, list);						
						c += list.size()-1; // increment the counter based on the new list size
					} else {
						words.set(c, res1);
					}
					System.out.println(getClass().getSimpleName() + ">> rule1 applied to : " + word);
					continue;
				}				
			} catch (IOException e) {					
				e.printStackTrace();
			}			
			
			
			//INFO: rule 3
			try {					
				List<Word> list = applyRule3(word); // if success, means `word` variable is number  
				words.remove(c);
				words.addAll(c, list);
				c += list.size()-1; // increment the counter based on the new list size
				System.out.println(getClass().getSimpleName() + ">> rule3 applied to : " + word);
				continue;
			}  catch(NumberFormatException | NullPointerException e) { 
		        // do nothing here
				// It means the string wasn't a number
				// then, do check with the next rule
		    } 
			
			//INFO: rule 4
			if (word.str.matches(".*\\d.*")) {
				//System.out.println("rule4 applied to : " + word);
				List<Word> res = applyRule4(word.str);					
				if (res != null) {
					// remove current elem, changed to sliced form, then decrement the counter to redo current data
					words.remove(c);						
					words.addAll(c, res);						
					c--;
				}										
				continue;
			}				
			
		}
		
		
	}
	/*package*/ Word applyRule1(Word word, String rulesFiles) throws FileNotFoundException, IOException {			
	
		if (mMap == null) {
			mMap = new HashMap<String, String>();
			String path = Utils.getFullPath(rulesFiles);
			try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
				String line = null;
				while((line = reader.readLine()) != null) {
					String[] split = line.split("\t");						
					mMap.put(split[0], split[1]);						
				}
			}				
		}			 			
		String retval = mMap.get(word.str);
		// not found
		if (retval == null) {
			// is available on unit storage?
			if (UnitStorage.sDiphoneUnit.contains(word.str+".wav")) {
				retval = word.str;
			} 
			// TODO: not really sure
			// should we user the length of the text as the trigger or not?
			else if (word.str.length() < 3 || word.isMonophone())  { 			
				retval = ""; 
				// get char in string except the last one
				char[] dst = new char[word.str.length()-1];
				word.str.getChars(0, dst.length, dst, 0); 
				// insert '_' between char
				for (char c : dst) {
					retval += (char)c + "_";					
				}
				retval += word.str.charAt(dst.length); // append the last char
			} else {
				// means rule 1 not applicable for the text
				return null;
			}
			
		}
		return new Word(retval, true);			
		
	}
	
	/*package*/ List<Word> applyRule3(Word word) throws NumberFormatException, NullPointerException {			
		String res = convertIntToStringRep(Integer.parseInt(word.str));			
		return Word.toWordList(res.split(" "));
	}
	
	private String convertIntToStringRep(int n) {
		if (n < 0) {
			return "minus " + convertIntToStringRep(-n);
		}
		
		if (n < 20) {
		    return units[n];
		}
		
		if(n == 100) {
		    return tens[10];
		}
		
		if(n == 1000) {
		    return tens[11];
		}
		
		if (n < 100) {
		    return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
		}
		
		if (n < 200) {
		    return tens[10] + ((n % 100 != 0) ? " " : "") + convertIntToStringRep(n % 100); 
		}
		
		if (n < 1000) {
		    return units[n / 100] + " ratus" + ((n % 100 != 0) ? " " : "") + convertIntToStringRep(n % 100);
		}
		
		if (n < 2000) {
		    return tens[11] + ((n % 100 != 0) ? " " : "") + convertIntToStringRep(n % 100);
		}
		
		if (n < 1000000) {
		    return convertIntToStringRep(n / 1000) + " ribu" + ((n % 1000 != 0) ? " " : "") + convertIntToStringRep(n % 1000);
		}
		
		if (n < 1000000000) {
		    return convertIntToStringRep(n / 1000000) + " juta" + ((n % 1000000 != 0) ? " " : "") + convertIntToStringRep(n % 1000000);
		}
		
		return convertIntToStringRep(n / 1000000000) + " bilion"  + ((n % 1000000000 != 0) ? " " : "") + convertIntToStringRep(n % 1000000000);
	}
	
	
	/*package*/ List<Word> applyRule4(String text) {
		String[] split = text.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		// remove current elem, changed to sliced form, then decrement the coutner to redo current
		
		List<Word> slicedWord = Word.toWordList(split);
		for (Word w : slicedWord) {
			if (w.str.length() < 3) {
				w.shouldProcess = false;
			}
		}
		
		return slicedWord;
	}
	
	 
	 
	@Override
	public String toString() {			
		/*String s = "";
		for (String text : mText) {
			s += text;
		}*/			
		StringJoiner joiner = new StringJoiner(" || ");
        for (Word w : words ) {	        	
            joiner.add(w.str);
        }	        
        return joiner.toString();			 
	}
}