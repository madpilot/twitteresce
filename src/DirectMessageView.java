import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class DirectMessageView implements View, CommandListener {
	private Command cmdReadMessage;
	private Command cmdDirectMessage;
	private Command cmdTimeline;
	private Command cmdRefresh;
	private Command cmdBack;
	
	private Twitteresce parent;
	
	private Vector messages;
	
	private List list;
	
	private View current;
	
	public DirectMessageView(Twitteresce parent) {
		this.current = parent.getDefaultView();
		this.parent = parent;
		
		cmdReadMessage = new Command("Read", Command.ITEM, 1);
		cmdDirectMessage = new Command("Post Direct Message", Command.ITEM, 2);
		cmdRefresh = new Command("Refresh", Command.ITEM, 4);
		cmdBack = new Command("Back", Command.EXIT, 1);
		
		this.messages = new Vector();
		this.list = new List("Direct Messages", Choice.IMPLICIT);
		
		this.list.addCommand(cmdReadMessage);
		this.list.addCommand(cmdDirectMessage);
		this.list.addCommand(cmdRefresh);
		this.list.addCommand(cmdBack);
	}
	
	public boolean interruptible() {
		return true;
	}
	
	public void display() {
		this.parent.setCurrentView(this);
		this.display(this.messages);
	}
	
	public void display(Vector messages) {
		Display display = Display.getDisplay(this.parent);
		
		// First we will check to see if it there are new tweets
		int newTweets = 0;
		
		if(this.messages == null) {
			newTweets = messages.size();
		} else {
			if(!messages.isEmpty()) {
				if(this.messages.size() != messages.size()) {
					newTweets = Math.max(0, messages.size() - this.messages.size());
				} else {
					if(this.messages.isEmpty()) {
						newTweets = messages.size();
					} else {
						Enumeration e = messages.elements();
						while(e.hasMoreElements() && ((DirectMessage)e.nextElement()).getID() != ((DirectMessage)this.messages.firstElement()).getID()) {
							newTweets++;
						}
					}
				}
			}
		}
		
		this.messages = messages;
		
		String title = "Twitteresce - Direct Messages";
	
		int lastIndex = 0;
		if(list != null) {
			lastIndex = list.getSelectedIndex();
		}
		
		list = new List(title, Choice.IMPLICIT);
		list.setFitPolicy(Choice.TEXT_WRAP_ON);
		
		for(int i = 0; i < messages.size(); i++) {
			DirectMessage message = (DirectMessage)messages.elementAt(i);
			list.append(message.getSenderScreenName() + ": " + message.getText(), null);
		}
		
		list.setSelectCommand(cmdReadMessage);
		
		this.list.addCommand(cmdReadMessage);
		this.list.addCommand(cmdDirectMessage);
		this.list.addCommand(cmdRefresh);
		this.list.addCommand(cmdBack);
		
		list.setCommandListener(this);
		
		if(newTweets == 0 && lastIndex > 0) {
			list.setSelectedIndex(lastIndex, true);
		}
		
		if(this.parent.getCurrentView().interruptible()) {
			display.setCurrent(list);
			this.parent.setDefaultView(this);
			
			if(newTweets > 0) {
				Alert newTweetAlert = new Alert("New Messages", "There are " + newTweets + " new messages", null, AlertType.INFO);
				try {
					display.setCurrent(newTweetAlert, list);
				} catch(IllegalArgumentException iae) {
				
				}
			}
		}
	}
	
	public void commandAction(Command c, Displayable s) {
		// Just close the app - it's still open, but hidden
		if (c == cmdBack) 
		{
			this.parent.setDefaultView(this.current);
			Display display = Display.getDisplay(this.parent);
			this.parent.displayDefaultView();
	
			/*
			Alert loading = new Alert("Please wait", "Retrieving current tweets...", null, AlertType.INFO);
			Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			loading.setIndicator(gauge);
			loading.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
			
			if(this.parent.getSettings().getRefreshRate() != 0) {
				this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date());
			} else {
				this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date(), (long)(this.parent.getSettings().getRefreshRate() * 60 * 1000));
			}
			
			display.setCurrent(loading);
			*/
		}
		// Read the selected tweet
		else if (c == cmdReadMessage)
		{
			DirectMessage message = (DirectMessage)messages.elementAt(list.getSelectedIndex());
				
			ReadDirectMessageView readDirectMessage = new ReadDirectMessageView(this.parent, message);
			readDirectMessage.display();
		}
		// Refresh the tweet list
		else if (c == cmdRefresh) 
		{
			this.parent.timerThread.cancel();
			this.parent.timerThread = new Timer();
			
			// Refresh the display again by creating and running a new thread
			if(this.parent.getSettings().getRefreshRate() == 0) {
				this.parent.timerThread.schedule(new DirectMessageThread(this.parent), new Date());
			} else {
				this.parent.timerThread.schedule(new DirectMessageThread(this.parent), new Date(), (long)(this.parent.getSettings().getRefreshRate() * 60 * 1000));
			}
		}
		// Post a message at a user
		else if (c == cmdDirectMessage) 	
		{
			PostView postView = new PostView(this.parent);
			
			if(list.getSelectedIndex() == -1) {
				postView.display("D ");
			} else {
				DirectMessage message = (DirectMessage)messages.elementAt(list.getSelectedIndex());
				postView.display("D " + message.getSenderScreenName() + " ");
			}
		}
	}
	
	public Displayable getDisplayable() {
		return this.list;
	}
}