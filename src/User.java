import org.kxml2.kdom.*;

public class User {
	private int id;
	private String name;
	private String screenName;
	private String location;
	private String description;
	private String profileImageURL;
	private String url;
	private boolean protect;
	private Status status;
	
	public User() {
	
	}
	
	public User(int id, String name, String screenName, String location, String description, String profileImageURL, boolean protect, Status status) {
		this.id = id;
		this.name = name;
		this.screenName = screenName;
		this.location = location;
		this.description = description;
		this.profileImageURL = profileImageURL;
		this.url = url;
		this.protect = protect;
		this.status = status;
	}
	
	public User(Element element) {
		for(int i = 0; i < element.getChildCount(); i++) {
			if(element.getType(i) == Node.ELEMENT) {
				Element child = (Element)element.getChild(i);
			
				if(child.getName().equals("id")) {
					this.setID(Integer.parseInt(child.getText(0)));
				} 
				else if(child.getName().compareTo("name") == 0) 
				{
					this.setName(child.getText(0));
				} 
				else if(child.getName().compareTo("screen_name") == 0) 
				{
					this.setScreenName(child.getText(0));
				} 
				else if(child.getName().compareTo("location") == 0) 
				{
					this.setLocation(child.getText(0));
				} 
				else if(child.getName().compareTo("description") == 0) 
				{
					this.setDescription(child.getText(0));
				} 
				else if(child.getName().compareTo("profile_image_url") == 0) 
				{
					this.setProfileImageURL(child.getText(0));
				} 
				else if(child.getName().compareTo("url") == 0) 
				{
					this.setURL(child.getText(0));
				} 
				else if(child.getName().compareTo("protect") == 0) 
				{
					this.setProtect(child.getText(0) == "true");
				} 
				else if(child.getName().compareTo("status") == 0) 
				{
					this.setStatus(new Status(child));
				}
			}
		}
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getScreenName() {
		return this.screenName;
	}
	
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getProfileImageURL() {
		return this.profileImageURL;
	}
	
	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}
	
	public String getURL() {
		return this.url;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
	
	public boolean getProtect() {
		return this.protect;
	}
	
	public void setProtect(boolean protect) {
		this.protect = protect;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
}