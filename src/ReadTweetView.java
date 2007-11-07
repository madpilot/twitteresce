import java.lang.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class ReadTweetView implements View, CommandListener {
	private Command cmdBack;
	private Command cmdPostTweet;
	private Command cmdPostAtUser;
	private Command cmdDirectMessage;
	private Command cmdDeleteTweet;
	
	private Twitteresce parent;
	private Status status;
	
	private Form form;
	
	// Use this so store the current screen, so we can restore it on cancel
	View current;
	
	public ReadTweetView(Twitteresce parent, Status status) {
		current = parent.getDefaultView();
		
		this.parent = parent;
		this.status = status;
		
		cmdPostTweet = new Command("Post Tweet", Command.ITEM, 1);
		cmdPostAtUser = new Command("Post at User", Command.ITEM, 2);
		cmdDirectMessage = new Command("Direct Message User", Command.ITEM, 3);
		cmdDeleteTweet = new Command("Delete Tweet", Command.ITEM, 4);
		
		cmdBack = new Command("Back", Command.BACK, 1);
		form = new Form("Update from " + this.status.getUser().getScreenName());
		this.parent.setDefaultView(this);
	}
	
	public boolean interruptible() {
		return false;
	}
	
	public void display() {
		String timeString = "";
		long diff = ((new Date()).getTime() - this.status.getCreatedAt().getTime()) / 1000;
		
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
		
		StringItem tweet = new StringItem(null, this.status.getText(), Item.PLAIN);
		form.deleteAll();
		
		form.append(tweet);
	
		form.addCommand(cmdPostTweet);
		form.addCommand(cmdPostAtUser);
		form.addCommand(cmdDirectMessage);
		
		if(this.status.getUser().getScreenName().equals(this.parent.getSettings().getUsername())) {
			form.addCommand(cmdDeleteTweet);
		}
		form.addCommand(cmdBack);
			
		form.setCommandListener(this);

		Display.getDisplay(this.parent).setCurrent(form);
	}
	
	public void commandAction(Command c, Displayable s) {
		if(c == cmdPostTweet) {
			PostView postView = new PostView(this.parent);
			postView.display();
		} else if (c == cmdPostAtUser) {
			PostView postView = new PostView(this.parent);
			postView.display("@" + status.getUser().getScreenName() + ": ");
		} else if (c == cmdDirectMessage) {			
			PostView postView = new PostView(this.parent);
			postView.display("D " + status.getUser().getScreenName() + " ");
		} else if (c == cmdDeleteTweet) {
			this.parent.setDefaultView(current);
			DeleteThread deleteThread = new DeleteThread(status.getID(), this.parent);
			deleteThread.start();
			this.parent.displayDefaultView();
		} else if (c == cmdBack) {
			this.parent.setDefaultView(current);
			this.parent.displayDefaultView();
			
			//this.parent.timerThread = new Timer();
			// Don't need to refresh right now, just later
			//if(this.parent.getSettings().getRefreshRate() != 0) {
			//	this.parent.timerThread.schedule(new TwitteresceThread(this.parent), (long)(this.parent.getSettings().getRefreshRate() * 60 * 1000));
			//}
		}
	}
	
	public Displayable getDisplayable() {
		return this.form;
	}
}