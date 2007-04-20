import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;

public class UpdateThread extends Thread  {
	private Twitteresce parent;
	private TwitterAPI api;
	private String message;
	
	public UpdateThread(String message, Twitteresce parent) {
		this.message = message;
		this.parent = parent;
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
	}
		
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
		
	public void run() {
		// Run once, then loop if needed.
		Display display = Display.getDisplay(this.parent);

		try {
			Alert posting = new Alert("Please wait", "Posting new tweet...", null, AlertType.INFO);
			Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			posting.setIndicator(gauge);
			posting.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
			
			try {
				display.setCurrent(posting);
			} catch(IllegalArgumentException iae) {
			
			}

			
			this.api.Update(this.message);
			
			if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_FRIENDS) 
			{						
				Vector statuses = this.api.FriendsTimeLine();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayTwits(statuses, "Twitteresce - Friends");
			} 
			else if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_PUBLIC) 
			{
				Vector statuses = this.api.PublicTimeLine();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayTwits(statuses, "Twitteresce - Public");
			}
			else if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_DIRECT) 
			{
				Vector messages = this.api.DirectMessages();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayDirectMessages(messages);
			}
		} catch(HTTPIOException hie) {
			// This will restore the last tweet screen ensuring the Alert is displayed.
			
			this.parent.DisplayTwits();
			
			Alert error = new Alert("Can't post tweet", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			if(hie.getHttpCode() == HttpConnection.HTTP_FORBIDDEN || hie.getHttpCode() == HttpConnection.HTTP_UNAUTHORIZED)
			{
				error = new Alert("You aren't allowed to post tweets", "It looks like your username or password is incorrect", null, AlertType.ERROR);
			}
			
			try {
				display.setCurrent(error, this.parent.list);
			} catch(IllegalArgumentException iae) {
				// Another thread has slipped in between the DisplayTwits call above and here.
			}
		} catch(IOException ioe) {
			this.parent.DisplayTwits();
			
			Alert error = new Alert("Can't send the update", "Please try again later", null, AlertType.ERROR);
			
			try {
				display.setCurrent(error, this.parent.list);
			} catch(IllegalArgumentException iae) {
				
			}
		} catch(Exception e) {
			this.parent.DisplayTwits();
			
			Alert error = new Alert("Oops!", "Unknown error, please contact the dev team.", null, AlertType.ERROR);
			
			try {
				display.setCurrent(error, this.parent.list);
			} catch(IllegalArgumentException iae) {
				
			}
		}
	}
}