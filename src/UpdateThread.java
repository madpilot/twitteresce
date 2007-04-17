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
			Alert uploading = new Alert("Please wait", "Sending new update...", null, AlertType.INFO);
			Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			uploading.setIndicator(gauge);
			uploading.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
			display.setCurrent(uploading);
			
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
		/*
		} catch(HTTPIOException hie) {
			Alert error = new Alert("Can't send the update", "Please try again later", null, AlertType.ERROR);
			
			if(hie.getHttpCode() == HttpConnection.HTTP_FORBIDDEN) {
				error = new Alert("You aren't allowed to send updates", "Please check your username and password", null, AlertType.ERROR);
			} else if(hie.getHttpCode() == HttpConnection.HTTP_INTERNAL_ERROR) {
				error = new Alert("Can't send the update", "The server is down, please try again later", null, AlertType.ERROR);
			}
			display.setCurrent(error);
		} catch(IOException ioe) {
			Alert error = new Alert("Can't send the update", "Please try again later", null, AlertType.ERROR);
			display.setCurrent(error);
		*/
		} catch(Exception e) {
			Alert error = new Alert("Oops!", "Unknown error, please contact the dev team.", null, AlertType.ERROR);
			display.setCurrent(error);
		}
	}
}