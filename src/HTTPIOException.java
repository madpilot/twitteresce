import java.util.*;
import javax.microedition.io.*;

public class HTTPIOException extends Exception {
	private int httpCode;
	
	public HTTPIOException(int httpCode) {
		super("Status Code: " + httpCode);
	}
	
	public HTTPIOException(int httpCode, String message) {
		super(message);
	}
}