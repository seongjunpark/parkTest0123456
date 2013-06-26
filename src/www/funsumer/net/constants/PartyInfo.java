package www.funsumer.net.constants;

public class PartyInfo {
	private String PAR_IMAGES;
	private String BDPNAME;
	private String Pid;

	// PICTURE
	public String getPAR_IMAGES() {
		return PAR_IMAGES;
	}

	public void setPAR_IMAGES(String PAR_IMAGES) {
		this.PAR_IMAGES = "http://funsumer.net/" + PAR_IMAGES;
	}

	// TEXT
	public String getBDPNAME() {
		return BDPNAME;
	}

	public void setBDPNAME(String BDPNAME) {
		this.BDPNAME = BDPNAME;
	}
	
	// ID
	public String getPid() {
		return Pid;
	}

	public void setPid(String Pid) {
		this.Pid = Pid;
	}
	
}
