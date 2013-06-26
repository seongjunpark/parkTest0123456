package www.funsumer.net.constants;

public class FriendInfo {
	private String Fname;
	private String Fpic;
	private String Fid;

	// PICTURE
	public String getFpic() {
		return Fpic;
	}

	public void setFpic(String Fpic) {
		this.Fpic = "http://funsumer.net/" + Fpic;
	}
	
	// TEXT
	public String getFname() {
		return Fname;
	}

	public void setFname(String Fname) {
		this.Fname = Fname;
	}
	
	// ID
	public String getFid() {
		return Fid;
	}

	public void setFid(String Fid) {
		this.Fid = Fid;
	}
	
}
