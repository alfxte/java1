package ns.blank;

import java.io.File;
import java.io.IOException;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

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
		
		for (Word word : processor.mWords) {
			
			//INFO: rule 2
			if (applyRule2(word.str)) {
				System.out.println(getClass().getSimpleName() + ">> rule2 applied to : " + word);					
				continue;
			}
			
			//INFO: rule 5
			else {
				System.out.println(getClass().getSimpleName() + ">> rule2 applied to : " + word);
				applyRule5(word.str);
			}
			
			
			// add pause for each word
			loadSample("./res/unit_storage/_.wav");
			
			
			
		}
		
	}
	
	private boolean applyRule2(String str) {
		if (UnitStorage.sAllUnit.contains(str + ".wav")) {				
			loadSample("./res/unit_storage/" + str + ".wav");
			return true;				
		}
		
		return false;
	}
	
	private void applyRule5(String str) {
		String[] sylls = Syllablelizer.toSyllable(str);
		for (String syll : sylls) {
			if (!applyRule2(syll)) {
				System.out.println("[!!not-found] " + syll);
			}
		}				
		
	}
	
	
}