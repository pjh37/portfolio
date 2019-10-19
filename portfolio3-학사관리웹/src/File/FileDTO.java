package File;

public class FileDTO {
	private String pid;
	private String fileName;
	private String fileRealName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public void setPid(String pid) {
		this.pid=pid;
	}
	public String getPid() {
		return pid;
	}
	public FileDTO(String pid, String fileName,String fileRealName) {//fileName
		super();
		this.fileName = fileName;
		this.fileRealName = fileRealName;
		this.pid=pid;
	}
}
