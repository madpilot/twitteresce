import java.util.Date;
import org.kxml2.kdom.*;

public class Status {
	private int id;
	private String text;
	private Date createdAt;
	private User user;
	
	public Status() {
	
	}
	
	public Status(int id, String text, Date createdAt, User user) {
		this.id = id;
		this.text = text;
		this.createdAt = createdAt;
		this.user = user;
	}
	
	public Status(Element element) {
		for(int i = 0; i < element.getChildCount(); i++) {
			if(element.getType(i) == Node.ELEMENT) {
				Element child = (Element)element.getChild(i);
			
				if(child.getName().compareTo("id") == 0) 
				{
					this.setID(Integer.parseInt(child.getText(0)));
				} 
				else if(child.getName().compareTo("text") == 0) 
				{
					this.setText(child.getText(0));
				} 
				else if(child.getName().compareTo("created_at") == 0) 
				{
					//this.text = child.getText(0);
				} 
				else if(child.getName().compareTo("user") == 0) 
				{
					this.setUser(new User(child));
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
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Date getCreatedAt() {
		return this.createdAt;
	}
	
	public void setText(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}