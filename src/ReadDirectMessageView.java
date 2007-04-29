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
		String timeString = "";
		long diff = ((new Date()).getTime() - this.message.getCreatedAt().getTime()) / 1000;
		
		if(diff < 60) {
			timeString = diff + " second" + (diff == 1 ? "" : "s") + " ago.";
		} else if (diff < (60 * 60)) {
			long mins = diff / 60;
			timeString = "About " + mins + " minute" + (mins == 1 ? "" : "s") + " ago.";
		} else if (diff < (24 * 60 * 60)) {
			long hours = diff / (60 * 60);
			timeString = "About " + hours + " hour" + (hours == 1 ? "" : "s") + " ago.";
		} else {
			long days = diff / (24 * 60 * 60);
			timeString = "About " + days + " day" + (days == 1 ? "" : "s") + " ago.";
		}
		
		this.parent.setCurrentView(this);
		StringItem tweet = new StringItem(null, this.message.getText() + ". " + timeString, Item.PLAIN);
		form.deleteAll();
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