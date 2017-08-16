package ns.blank;

import java.io.IOException;

import com.jsyn.data.FloatSample;

public class TTSMain {		
	
	public static void printInfo(FloatSample sample) {
		System.out.println("Sample has: channels  = " + sample.getChannelsPerFrame());
        System.out.println("            frames    = " + sample.getNumFrames());
        System.out.println("            rate      = " + sample.getFrameRate());
        System.out.println("            loopStart = " + sample.getSustainBegin());
        System.out.println("            loopEnd   = " + sample.getSustainEnd());
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {		
		
		
		UnitStorage.prepare("./res/unit_storage/");
		
		TextProcessor processor = new TextProcessor("PD phd h. m. abdul salam 1994 m150bisa", "./res/abbrev.txt");
		System.out.println(processor);
		
		SimpleTTS tts = new SimpleTTS(true);
		/*tts.loadSample("./res/unit_storage/words/abdul.wav");
		tts.loadSample("./res/unit_storage/words/ahmad.wav");*/		
		tts.addText(processor);
		tts.play();		
		
		

	}
}
