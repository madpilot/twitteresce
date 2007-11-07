/**
 * Java Class that allocs access to the Twitter API
 *
 * @author Myles Eftos
 */
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import org.kxml2.kdom.*;
import org.kxml2.io.*;

public class TwitterAPI {
	private String username;
	private String password;
	
	public TwitterAPI() {
		this.username = "";
		this.password = "";
	}
	
	public TwitterAPI(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
		
	private Vector Get(String url, boolean authenticate) throws HTTPIOException, IOException {
		HttpConnection c = null;
		InputStream is = null;
		InputStreamReader reader = null;
		KXmlParser parser = null;
		Document document = null;
		Vector returnCollection = new Vector();
		
		int responseCode = 0;
		
		c = (HttpConnection) Connector.open(url);
		
		if(authenticate) {
			c.setRequestProperty("Authorization", "Basic " + Base64.Encode(this.username + ":" + this.password));
		}
		responseCode = c.getResponseCode();
		
		if (responseCode != HttpConnection.HTTP_OK) {
			throw new HTTPIOException(responseCode);
		}
		
		is = c.openInputStream();
		reader = new InputStreamReader(is);
		
		// We should now have the XML at this point, so we can send it to the parser
		try {
			parser = new KXmlParser();
			parser.setInput(reader);
		
			document = new Document();
			document.parse(parser);
		} catch (org.xmlpull.v1.XmlPullParserException xppe) {
			throw new IOException();
		}
			
		// Let's iterate.
		Element root = document.getRootElement();
		
		if (root.getName().compareTo("statuses") == 0) {
			for(int i = 0; i < root.getChildCount(); i++) 
			{
				if(root.getType(i) == Node.ELEMENT) {
					Element child1 = (Element)root.getChild(i);
					
					if(child1.getName().compareTo("status") == 0)
					{
						returnCollection.addElement(new Status(child1));
					}
				}
			}
		} else if (root.getName().compareTo("users") == 0) {
			for(int i = 0; i < root.getChildCount(); i++) 
			{
				if(root.getType(i) == Node.ELEMENT) {
					Element child1 = (Element)root.getChild(i);
					
					if(child1.getName().compareTo("user") == 0)
					{
						returnCollection.addElement(new User(child1));
					}
				}
			}
		} else if (root.getName().compareTo("direct-messages") == 0) {
			for(int i = 0; i < root.getChildCount(); i++) 
			{
				if(root.getType(i) == Node.ELEMENT) {
					Element child1 = (Element)root.getChild(i);
					
					if(child1.getName().compareTo("direct_message") == 0)
					{
						returnCollection.addElement(new DirectMessage(child1));
					}
				}
			}
		}
		
		root = null;
		reader.close();
		
			
		document = null;
		parser = null;
		reader = null;
		
		try {
			c.close();
		} catch (IOException ioe) {
		
		}
		
		return returnCollection;
	}
	
	private void Post(String url, String data, boolean authenticate) throws HTTPIOException, IOException {
		HttpConnection c = null;
		InputStream is = null;
		OutputStream os = null;
		
		int responseCode = 0;
		
		c = (HttpConnection) Connector.open(url);
		if(authenticate) {
			c.setRequestProperty("Authorization", "Basic " + Base64.Encode(this.username + ":" + this.password));
		}
		
		c.setRequestProperty("X-Twitter-Client", "Twitteresce");
		//c.setRequestProperty("X-Twitter-Client-Version", getAppProperty("MIDlet-Version").toString());
		c.setRequestProperty("X-Twitter-Client-URL", "http://www.madpilot.com.au/twitteresce/meta.xml");
		
		c.setRequestMethod(HttpConnection.POST);
		
		os = c.openOutputStream();
		os.write(data.getBytes());
		os.flush();
		
		responseCode = c.getResponseCode();
		
		if (responseCode != HttpConnection.HTTP_OK) {
			throw new HTTPIOException(responseCode);
		}
	}
	
	public Vector PublicTimeLine() throws HTTPIOException, IOException {
		return Get("http://twitter.com/statuses/public_timeline.xml", false);
	}
	
	public Vector FriendsTimeLine() throws HTTPIOException, IOException {
		return Get("http://twitter.com/statuses/friends_timeline.xml", true);
	}
	
	public Vector UserTimeLine() throws HTTPIOException, IOException {
		return Get("http://twitter.com/statuses/user_timeline.xml", true);
	}
	
	public Vector UserTimeLine(int id) throws HTTPIOException, IOException {
		return Get("http://twitter.com/statuses/user_timeline.xml", true);
	}
	
	public Vector ShowStatus(int id) throws HTTPIOException, IOException {
		return Get("http://twitter.com/status/show/" + id + ".xml", true);
	}
	
	public void Update(String message) throws HTTPIOException, IOException {
		Post("http://twitter.com/statuses/update.xml?status=" + URLEncoder.encode(message), "&source=twitteresce", true);
	}
	
	public void DestroyStatus(int id) throws HTTPIOException, IOException {
		Get("http://twitter.com/statuses/destroy/" + id + ".xml", true);
	}
	
	public Vector Friends() throws HTTPIOException, IOException {
		return Get("http://twitter.com/users/friends.xml", true);
	}
	
	public Vector Followers() throws HTTPIOException, IOException {
		return Get("http://twitter.com/users/followes.xml", true);
	}
	
	public Vector Featured() throws HTTPIOException, IOException {
		return Get("http://twitter.com/users/featured.xml", true);
	}
	
	public Vector ShowUser(int id) throws HTTPIOException, IOException {
		return Get("http://twitter.com/users/show/" + id + ".xml", true);
	}
	
	public Vector DirectMessages() throws HTTPIOException, IOException {
		return Get("http://twitter.com/direct_messages.xml", true);
	}
	
	public Vector SentDirectMessages() throws HTTPIOException, IOException {
		return Get("http://twitter.com/direct_messages/sent.xml", true);
	}
	
	public void NewDirectMessage(int id, String message) throws HTTPIOException, IOException {
		Post("http://twitter.com/direct_messages/new.xml&user=" + id + "&text=" + URLEncoder.encode(message), "&source=twitteresce", true);
	}
	
	public void NewDirectMessage(String username, String message) throws HTTPIOException, IOException {
		Post("http://twitter.com/direct_messages/new.xml&user=" + username + "&text=" + URLEncoder.encode(message), "&source=twitteresce", true);
	}
	
	public void DestroyDirectMessage(int id) throws HTTPIOException, IOException {
		Get("http://twitter.com/direct_messages/destroy/" + id + ".xml", true);
	}
}