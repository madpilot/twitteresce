import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class ReadTweetView implements View, CommandListener {
	private Command cmdBack;
	
	private Twitteresce parent;
	private Status status;
	
	private Form form;
	
	public ReadTweetView(Twitteresce parent, Status status) {
		this.parent = parent;
		this.status = status;
		
		cmdBack = new Command("Back", Command.BACK, 1);
		form = new Form("Update from " + this.status.getUser().getScreenName());
	}
	
	public void display() {
		StringItem tweet = new StringItem(null, this.status.getText(), Item.PLAIN);
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