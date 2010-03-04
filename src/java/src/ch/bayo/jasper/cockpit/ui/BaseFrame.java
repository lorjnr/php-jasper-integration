package ch.bayo.jasper.cockpit.ui;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;

public class BaseFrame extends JFrame {
	
	public BaseFrame(String caption) {
		super(caption);
		addWindowListener(new WindowClosingAdapter(exitOnClose()));
	}
	
	public void showFrame() {

		setSize(600, 490);
		
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    
	    // Determine the new location of the window
	    int w = getSize().width;
	    int h = getSize().height;
	    int x = (dim.width-w)/2;
	    int y = (dim.height-h)/2;
	    
	    // Move the window
	    setLocation(x, y);

		setVisible(true);
	}
	
	protected boolean exitOnClose() {
		return false;
	}
	
	void doAfterClose() {
		//do nothing
	}

}
