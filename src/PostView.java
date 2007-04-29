import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class PostView implements View, CommandListener {
	private Command cmdSend;
	private Command cmdCancel;
	
	private Twitteresce parent;
	private TextBox textBox;
	
	// Use this so store the current screen, so we can restore it on cancel
	View current;
	
	public PostView(Twitteresce parent) {
		current = parent.getDefaultView();
		
		this.parent = parent;
		cmdSend = new Command("Send", Command.ITEM, 1);
		cmdCancel = new Command("Cancel", Command.BACK, 1);
		
		textBox = new TextBox("Enter your message", "", 140, TextField.ANY);
	}
	
	public boolean interruptible() {
		return false;
	}
	
	public void display(String initial) {
		this.parent.setCurrentView(this);
		this.textBox.setString(initial);
				
		textBox.addCommand(cmdSend);
		textBox.addCommand(cmdCancel);
		
		textBox.setCommandListener(this);
		
		Display.getDisplay(this.parent).setCurrent(textBox);
		this.parent.setDefaultView(this);
	}
	
	public void display() {
		this.display("");
	}
	
	public void commandAction(Command c, Displayable s) {
		if(c == cmdSend) {
			this.parent.setDefaultView(this.current);
			TextBox textBox = (TextBox)s;
			
			UpdateThread updateThread = new UpdateThread(textBox.getString(), this.parent);
			updateThread.start();
			
		} else if (c == cmdCancel) {
			this.parent.setDefaultView(current);
			this.parent.displayDefaultView();
		}
	}
	
	public Displayable getDisplayable() {
		return this.textBox;
	}
}