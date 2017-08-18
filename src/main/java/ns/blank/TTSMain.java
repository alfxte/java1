package ns.blank;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

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
		/*LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(java.util.logging.Level.OFF);*/
		
		// TODO: will share this on stackoverflow
		PrintStream original = System.out;		
		System.setOut(new PrintStream(new OutputStream() {			
			
			@Override
			public void write(int b) throws IOException {			
				original.write(b);
			}
		}) {
			@Override
			public void println(String x) {				
				String caller = Thread.currentThread().getStackTrace()[2].getClassName();
				if (caller.contains("com.jsyn") || caller.contains("com.softsynth")) {
					return;
				}
				super.println(x);
			}
		});
		
		UnitStorage.prepare("./res/unit_storage");
		
		/*TextProcessor processor = new TextProcessor("PD phd h. m. abdul salam 1994 m150bisa", "./res/abbrev.txt");
		System.out.println(processor);
		
		SimpleTTS tts = new SimpleTTS(true);
		tts.loadSample("./res/unit_storage/abdul.wav");
		tts.loadSample("./res/unit_storage/ahmad.wav");		
		tts.addText(processor);
		tts.play();
		String[] sylls = Syllablelizer.toSyllable("infrastruktur");
		System.out.println(Arrays.toString(sylls));
		*/
		
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.setOnclick(() -> {
						// TODO: change to setText instead of creating new object each time
						SimpleTTS tts = new SimpleTTS(true);
						String text = window.textField.getText();
						TextProcessor processor = new TextProcessor(text, "./res/abbrev.txt");
						tts.addText(processor);
						tts.play();
						
						processor = null;
						tts = null;
						System.gc();
						
					});
					window.frame.setVisible(true);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		 

	}
	

}
