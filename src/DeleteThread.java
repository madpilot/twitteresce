import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;

public class DeleteThread extends Thread  {
	private Twitteresce parent;
	private TwitterAPI api;
	private int id;
	
	public DeleteThread(int id, Twitteresce parent) {
		this.id = id;
		this.parent = parent;
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
	}
		
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
		
	public void run() {
		this.parent.displayDefaultView();

		Display display = Display.getDisplay(this.parent);

		try {
			Alert deleting = new Alert("Please wait", "Deleting tweet...", null, AlertType.INFO);
			Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			deleting.setIndicator(gauge);
			deleting.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
			
			try {
				display.setCurrent(deleting);
			} catch(IllegalArgumentException iae) {
			
			}

			
			this.api.DestroyStatus(this.id);
			
			// Refresh the timeline
			this.parent.displayDefaultView();
			
			this.parent.timerThread = new Timer();
			
			if(this.parent.getSettings().getRefreshRate() == 0) {
				this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date());
			} else {
				this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date(), (long)(this.parent.getSettings().getRefreshRate() * 60 * 1000));
			}
		} catch(HTTPIOException hie) {
			// This will restore the last tweet screen ensuring the Alert is displayed.
			this.parent.displayDefaultView();
			
			Alert error = new Alert("Can't delete tweet", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			if(hie.getHttpCode() == HttpConnection.HTTP_FORBIDDEN || hie.getHttpCode() == HttpConnection.HTTP_UNAUTHORIZED)
			{
				error = new Alert("You aren't allowed to delete tweets", "It looks like your username or password is incorrect", null, AlertType.ERROR);
			}
			
			try {
				Display.getDisplay(this.parent).setCurrent(error, this.parent.getDefaultView().getDisplayable());
			} catch(IllegalArgumentException iae) {
				// Another thread has slipped in between the DisplayTwits call above and here.
			}
		} catch(Exception e) {
			this.parent.displayDefaultView();
			
			Alert error = new Alert("Can't delete the status", "Please try again later", null, AlertType.ERROR);
			
			try {
				Display.getDisplay(this.parent).setCurrent(error, this.parent.getDefaultView().getDisplayable());
			} catch(IllegalArgumentException iae) {
				
			}
		}
	}
}