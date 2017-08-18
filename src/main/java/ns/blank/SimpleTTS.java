package ns.blank;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.List;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

import ns.blank.Word.EWordType;

class SimpleTTS {
	
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

	public void addText(TextProcessor processor) {		
		
		for (int c = 0; c < processor.words.size() ; c++) {
			Word word = processor.words.get(c);
			
			//INFO: rule 2
			if (applyRule2(word.str, EWordType.DIPHONE)) {
				System.out.println(getClass().getSimpleName() + ">> rule2 applied to : " + word);				
			}
			
			//INFO: rule 5
			else if (applyRule5(word.str)) {
				System.out.println(getClass().getSimpleName() + ">> rule5 applied to : " + word);				
			}
			
			// failed to apply rule2 and rule5
			// will use rule 1
			else {
				//INFO: rule 1
				word.type = EWordType.MONOPHONE;
				try {
					Word res = processor.applyRule1(word, "./res/abbrev.txt");
					if (res != null) { // reach here, means rule1 got applied
						if (res.str.contains("_")) { //it means the abbreviation not available on abbrev.txt
							List<Word> list = Word.toWordList(Arrays.asList(res.str.split("_")), EWordType.MONOPHONE);
							// replace the current position word with the splited word
							processor.words.remove(c);
							processor.words.addAll(c, list);							
							c += list.size()-1; // increment the counter based on the new list size							
							list.forEach((w) ->  applyRule2(w.str, w.type)); 
						} else {
							processor.words.set(c, res);
						}
						System.out.println(getClass().getSimpleName() + ">> rule1 applied to : " + word);						
						continue;
					}			
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}
			
			
			// add pause for each word
			loadSample("./res/unit_storage/_.wav");
			
			
			
		}
		
	}
	
	/*package*/ boolean applyRule2(String str, EWordType type) {
		
		/*if (UnitStorage.sAllUnit.contains(str + ".wav")) {			
			loadSample("./res/unit_storage/" + str + ".wav");
			return true;				
		}*/
		
		try {
			switch (type) {
			case MONOPHONE:
				loadSample("./res/unit_storage/monophone/" + str + ".wav");
				return true;

			case DIPHONE:
				loadSample("./res/unit_storage/diphone/" + str + ".wav");
				break;
			}
			
		} catch (Exception e) {
			return false;
		}
		
		return false;		
	}
	
	/*package*/ boolean applyRule5(String str) {
		String[] sylls = Syllablelizer.toSyllable(str);
		if (sylls == null) {
			// unable to syllablelize,(fail) 
			return false;
		}
		for (String syll : sylls) {
			if (!applyRule2(syll, EWordType.DIPHONE)) {
				System.out.println(getClass().getSimpleName() + ">> [!!not-found] syllable: " + syll);
			}
		}				
		
		// success
		return true;
		
	}
	
	
}