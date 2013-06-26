package www.funsumer.net.constants;

public class TweetInfo {
	private String Author;
	private String Article;
	private String Artime;
	private String ArticleFrom;
	private String Article_Like_Num;
	private String Article_Comment_Num;

	private String Authorpic;
	private String ArPic;

	private String Authorid;
	private String Articleid;

	private String Belong;
	private String Isparty;

	// PICTURE
	public String getAuthorpic() {
		return Authorpic;
	}

	public void setAuthorpic(String Authorpic) {
		this.Authorpic = "http://funsumer.net/" + Authorpic;
	}

	public String getArPic() {
		return ArPic;
	}

	public void setArPic(String ArPic) {
		this.ArPic = "http://funsumer.net/" + ArPic;
	}

	// TEXT
	public String getArticle() {
		return Article;
	}

	public void setArticle(String Article) {
		this.Article = Article;
	}

	public String getAuthor() {
		return Author;
	}

	public void setAuthor(String Author) {
		this.Author = Author;
	}

	public String getArticleFrom() {
		return ArticleFrom;
	}

	public void setArticleFrom(String ArticleFrom) {
		this.ArticleFrom = ArticleFrom;
	}

	public String getArtime() {
		return Artime;
	}

	public void setArtime(String Artime) {
		this.Artime = Artime;
	}
	
	public String getArticle_Like_Num() {
		return Article_Like_Num;
	}

	public void setArticle_Like_Num(String Article_Like_Num) {
		this.Article_Like_Num = Article_Like_Num;
	}

	public String getArticle_Comment_Num() {
		return Article_Comment_Num;
	}

	public void setArticle_Comment_Num(String Article_Comment_Num) {
		this.Article_Comment_Num = Article_Comment_Num;
	}

	// ID
	public String getAuthorid() {
		return Authorid;
	}

	public void setAuthorid(String Authorid) {
		this.Authorid = Authorid;
	}

	public String getArticleid() {
		return Articleid;
	}

	public void setArticleid(String Articleid) {
		this.Articleid = Articleid;
	}

	// ETC
	public String getBelong() {
		return Belong;
	}

	public void setBelong(String Belong) {
		this.Belong = Belong;
	}

	public String getIsparty() {
		return Isparty;
	}

	public void setIsparty(String Isparty) {
		this.Isparty = Isparty;
	}

}
