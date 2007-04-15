import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class TwitteresceThread extends Thread  {
	private Twitteresce parent;
	private TwitterAPI api;
	private int refresh;
	private String mode;
	private Display display;
	private boolean running;
	
	public TwitteresceThread(Display display, int refresh, String mode, boolean running, Twitteresce parent) {
		this.parent = parent;
		this.api = new TwitterAPI("myles@madpilot.com.au", "Ffe3wtEt");
		this.display = display;
		this.refresh = refresh;
		this.mode = mode;
		this.running = running;
	}
	
	public TwitteresceThread(Display display, String mode, Twitteresce parent) {
		this.parent = parent;
		this.api = new TwitterAPI("myles@madpilot.com.au", "Ffe3wtEt");
		this.display = display;
		this.refresh = 0;
		this.mode = mode;
		this.running = false;
	}
	
	public String getMode() {
		return this.mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public void Retrieve() {
		if(this.mode == "friends") {
			try {
				Vector statuses = this.api.FriendsTimeLine();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayTwits(statuses, "Twitteresce - Friends");
			} catch(HTTPIOException hie) {
				
			} catch(IOException ioe) {
			
			}
		} else if (this.mode == "public") {
			try {
				Vector statuses = this.api.PublicTimeLine();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayTwits(statuses, "Twitteresce - Public");
			} catch(HTTPIOException hie) {
				
			} catch(IOException ioe) {
			
			}
		}
	}
		
	public void run() {
		// Run once, then loop if needed.
		Retrieve();
		
		while(running) {
			Retrieve();
			try {
				this.sleep(this.refresh * 1000 * 60);
			} catch (InterruptedException ie) {
			
			}
		}
	}
}