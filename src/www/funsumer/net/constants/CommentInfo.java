package www.funsumer.net.constants;

public class CommentInfo {
	private String Comment_Pic;

	private String Comment_Name;
	private String Comment_Time;
	private String Comment_Info;
	
	private String Comment_ID;
	
	// PICTURE
	public String getComment_Pic() {
		return Comment_Pic;
	}

	public void setComment_Pic(String Comment_Pic) {
		this.Comment_Pic = "http://funsumer.net/" + Comment_Pic;
	}

	// TEXT
	public String getComment_Name() {
		return Comment_Name;
	}

	public void setComment_Name(String Comment_Name) {
		this.Comment_Name = Comment_Name;
	}
	
	public String getComment_Time() {
		return Comment_Time;
	}

	public void setComment_Time(String Comment_Time) {
		this.Comment_Time = Comment_Time;
	}
	
	public String getComment_Info() {
		return Comment_Info;
	}

	public void setComment_Info(String Comment_Info) {
		this.Comment_Info = Comment_Info;
	}
	
	// ID
	public String getComment_ID() {
		return Comment_ID;
	}

	public void setComment_ID(String Comment_ID) {
		this.Comment_ID = Comment_ID;
	}
}
