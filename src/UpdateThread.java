import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class UpdateThread extends Thread  {
	private Twitteresce parent;
	private TwitterAPI api;
	private String message;
	
	public UpdateThread(String message) {
		this.message = message;
		this.parent = parent;
		this.api = new TwitterAPI("myles@madpilot.com.au", "Ffe3wtEt");
	}
		
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
		
	public void run() {
		// Run once, then loop if needed.
		try {
			this.api.Update(this.message);
		} catch(HTTPIOException hie) {
				
		} catch(IOException ioe) {
		
		}
	}
}