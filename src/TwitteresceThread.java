import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class TwitteresceThread extends Thread  {
	private Twitteresce parent;
	private TwitterAPI api;
	
	private Display display;
		
	public TwitteresceThread(Display display, Twitteresce parent) {
		this.parent = parent;
		
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
		this.display = display;
	}
	
	public void Retrieve() {
		if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_FRIENDS) {
			try {
				Vector statuses = this.api.FriendsTimeLine();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayTwits(statuses, "Twitteresce - Friends");
			} catch(HTTPIOException hie) {
				
			} catch(IOException ioe) {
			
			}
		} else if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_PUBLIC) {
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
		
		while(this.parent.getSettings().getAutomatic()) {
			Retrieve();
			try {
				this.sleep(this.parent.getSettings().getRefreshRate() * 1000 * 60);
			} catch (InterruptedException ie) {
			
			}
		}
	}
}