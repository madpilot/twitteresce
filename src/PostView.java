import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class PostView implements View, CommandListener {
	private Command cmdSend;
	private Command cmdCancel;
	
	private Twitteresce parent;
	private TextBox textBox;
	
	public PostView(Twitteresce parent) {
		this.parent = parent;
		cmdSend = new Command("Send", Command.ITEM, 1);
		cmdCancel = new Command("Cancel", Command.BACK, 1);
		
		textBox = new TextBox("Enter your message", "", 140, TextField.ANY);
	}
	
	public void display(String initial) {
		this.textBox.setString(initial);
				
		textBox.addCommand(cmdSend);
		textBox.addCommand(cmdCancel);
		
		textBox.setCommandListener(this);
		
		Display.getDisplay(this.parent).setCurrent(textBox);
	}
	
	public void display() {
		this.display("");
	}
	
	public void commandAction(Command c, Displayable s) {
		if(c == cmdSend) {
			TextBox textBox = (TextBox)s;
			UpdateThread updateThread = new UpdateThread(textBox.getString(), this.parent);
			updateThread.start();
		} else if (c == cmdCancel) {
			this.parent.displayDefaultView();
		}
	}
	
	public Displayable getDisplayable() {
		return this.textBox;
	}
}