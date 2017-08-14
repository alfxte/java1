package ns.blank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

public class TTSMain {	
	
	static class Utils {
		public static String getFullPath(String relativePath) {
			return Paths.get(relativePath).toAbsolutePath().normalize().toString();			
		}
	}
	
	static class UnitStorage {
		private static HashSet<String> sAllUnit;
		public static void prepare(String unitStorageDir) {
			File dir = new File(Utils.getFullPath(unitStorageDir));			
			sAllUnit = new HashSet<>(Arrays.asList(dir.list()));
		}
	}
	
	static class Word {
		
		public static List<Word> convertToWord(List<String> list) {
			ArrayList<Word> words = new ArrayList<>();
			list.forEach((str) -> {
				words.add(new Word(str, true));
			}); 
			return words;
		}
		
		public static List<Word> toWord(String[] arr) {
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
	
	static class TextProcessor {		
		
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
		
		private ArrayList<Word> mWords;		
		private HashMap<String, String> mMap;		
		
		public TextProcessor(String text, String rules1Files) {
			mWords = new ArrayList<>();
			mWords.addAll(Word.toWord(text.split(" "))); // split by space			
			for (int c = 0; c < mWords.size(); c++) {
				Word word = mWords.get(c);
				if (!word.shouldProcess) {
					continue;
				}
				
				word.str = word.str.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
				
				// rule 1
				if (word.str.length() < 3) {
					try {
						mWords.set(c, applyRule1(word, rules1Files));						
					} catch (IOException e) {					
						e.printStackTrace();
					}
					System.out.println("rule1 applied to : " + word);
					continue;
				}
				
				// rule 2
				if (mMap.containsKey(word.str)) {
					System.out.println("rule2 applied to : " + word);
					continue;
				}
				
				// rule 3
				try {
					mWords.set(c, applyRule3(word));
					System.out.println("rule3 applied to : " + word);
					continue;
				}  catch(NumberFormatException | NullPointerException e) { 
			        // do nothing here
					// It means the string wasn't a number
					// then, do check with the next rule
			    } 
				
				//rule 4
				if (word.str.matches(".*\\d.*")) {
					//System.out.println("rule4 applied to : " + word);
					List<Word> res = applyRule4(word.str);					
					if (res != null) {
						// remove current elem, changed to sliced form, then decrement the coutner to redo current data
						mWords.remove(c);						
						mWords.addAll(c, res);						
						c--;
					}					
										
					continue;
				}
				
				
				
			}
			
			
		}
		
		private Word applyRule1(Word word, String rulesFiles) throws FileNotFoundException, IOException {			
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
			if (retval == null) {
				retval = ""; 
				// get char in string except the last one
				char[] dst = new char[word.str.length()-1];
				word.str.getChars(0, dst.length, dst, 0); 
				// insert '_' between char
				for (char c : dst) {
					retval += (char)c + "_";					
				}
				retval += word.str.charAt(dst.length); // append the last char
			}
			return  new Word(retval, true);			
			
		}
		
		public static Word applyRule3(Word word) throws NumberFormatException, NullPointerException {			
			word.str = convertIntToStringRep(Integer.parseInt(word.str));
			return word;
		}
		
		private static String convertIntToStringRep(int n) {
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
		
		
		public static List<Word> applyRule4(String text) {
			String[] split = text.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
			// remove current elem, changed to sliced form, then decrement the coutner to redo current
			
			List<Word> slicedWord = Word.toWord(split);
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
	        for (Word w : mWords ) {	        	
	            joiner.add(w.str);
	        }	        
	        return joiner.toString();			 
		}
	}

	
	static class SimpleTTS {
		
		private Synthesizer mSynth;
		private VariableRateDataReader mPlayer;		
		
		public SimpleTTS (boolean isStereo) {
			mSynth = JSyn.createSynthesizer();
			mPlayer = createSamplePlayer(mSynth, isStereo);
			mSynth.start();
		}
		
		private VariableRateDataReader createSamplePlayer(Synthesizer synth, boolean isStereo) {
			
			VariableRateDataReader samplePlayer;	
			LineOut lineOut;
			synth.add(lineOut = new LineOut());
			
			if (isStereo) {
				synth.add(samplePlayer = new VariableRateStereoReader());
	            samplePlayer.output.connect(0, lineOut.input, 0);
	            samplePlayer.output.connect(1, lineOut.input, 1);
			} else {
				synth.add(samplePlayer = new VariableRateMonoReader());
	            samplePlayer.output.connect(0, lineOut.input, 0);
			}
			
			lineOut.start();
			return samplePlayer;
		}
		
		public void loadSample(String relativeFile) {

	        // Load the sample and display its properties.
	        SampleLoader.setJavaSoundPreferred(false);
	        String curDir = Utils.getFullPath(relativeFile);
	        FloatSample sample = null;
			try {
				sample = SampleLoader.loadFloatSample(new File(curDir));
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			mPlayer.rate.set(sample.getFrameRate());
	        mPlayer.dataQueue.queue(sample);
		}
		
		public void play() {	        

	        try {
	        	do {
		            mSynth.sleepFor(1.0);
		        } while (mPlayer.dataQueue.hasMore());
				mSynth.sleepFor(0.5);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
	        mSynth.stop();
		}
	}
	
	
	public static void printInfo(FloatSample sample) {
		System.out.println("Sample has: channels  = " + sample.getChannelsPerFrame());
        System.out.println("            frames    = " + sample.getNumFrames());
        System.out.println("            rate      = " + sample.getFrameRate());
        System.out.println("            loopStart = " + sample.getSustainBegin());
        System.out.println("            loopEnd   = " + sample.getSustainEnd());
	}
	
	
	
	
	

	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		/*SimpleTTS tts = new SimpleTTS(true);
		tts.loadSample("./res/unit_storage/words/abdul.wav");
		tts.loadSample("./res/unit_storage/words/ahmad.wav");
		tts.play();*/
		
		UnitStorage.prepare("./res/unit_storage/");
		
		TextProcessor processor = new TextProcessor("PD h. m. abdul 1994 324 m150bisa", "./res/abbrev.txt");
		System.out.println(processor);
		
		

	}
}
