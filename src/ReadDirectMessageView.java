import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class ReadDirectMessageView implements View, CommandListener {
	private Command cmdBack;
	
	private Twitteresce parent;
	private DirectMessage message;
	private Form form;
	
	public ReadDirectMessageView(Twitteresce parent, DirectMessage message) {
		this.parent = parent;
		this.message = message;
		
		cmdBack = new Command("Back", Command.BACK, 1);
		this.form = new Form("Direct message from " + this.message.getSenderScreenName());
	}
	
	public boolean interruptible() {
		return false;
	}
	
	public void display() {
		this.parent.setCurrentView(this);
		StringItem tweet = new StringItem(null, this.message.getText(), Item.PLAIN);
		form.append(tweet);
	
		form.addCommand(cmdBack);
	
		form.setCommandListener(this);

		Display.getDisplay(this.parent).setCurrent(form);
	}
	
	public void commandAction(Command c, Displayable s) {
		if (c == cmdBack) {
			this.parent.displayDefaultView();
		}
	}
	
	public Displayable getDisplayable() {
		return this.form;
	}
}