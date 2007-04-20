import java.util.*;
import javax.microedition.io.*;

public class HTTPIOException extends Exception {
	private int httpCode;
	
	public int getHttpCode() {
		return this.httpCode;
	}
	
	public HTTPIOException(int httpCode) {
		super("Status Code: " + httpCode);
		this.httpCode = httpCode;
	}
	
	public HTTPIOException(int httpCode, String message) {
		super(message);
	}
}