package ns.blank;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class MainGUI {

	/*package*/ JFrame frame;
	/*package*/ JButton btnListen;
	/*package*/ JTextField textField;

	
	/**
	 * Create the application.
	 */
	public MainGUI() {
		initialize();
	}
	
	public void setOnclick(Runnable r) {
		btnListen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {			
				r.run();
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 48, 422, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnListen = new JButton("Listen");
		btnListen.setBounds(165, 105, 91, 23);		
		frame.getContentPane().add(btnListen);
	}
}
