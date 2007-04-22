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
	
	public void display(String initial) {
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
			TextBox textBox = (TextBox)s;
			UpdateThread updateThread = new UpdateThread(textBox.getString(), this.parent);
			updateThread.start();
			
		} else if (c == cmdCancel) {
			(Display.getDisplay(this.parent)).setCurrent(current.getDisplayable());
			this.parent.setDefaultView(current);
			
			this.parent.timerThread = new Timer();
			// Don't need to refresh right now, just later
			if(this.parent.getSettings().getRefreshRate() != 0) {
				this.parent.timerThread.schedule(new TwitteresceThread(this.parent), (long)(this.parent.getSettings().getRefreshRate() * 60 * 1000));
			}
		}
	}
	
	public Displayable getDisplayable() {
		return this.textBox;
	}
}