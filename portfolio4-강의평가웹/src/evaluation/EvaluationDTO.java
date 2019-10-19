package evaluation;

public class EvaluationDTO {
	private int evaluationID;
	private String userID;
	private String lectureName;
	private String professorName;
	private int lectureYear;
	private String semesterDivide;
	private String lectureDivide;
	private String evaluationTitle;
	private String evaluationContent;
	private String totalScore;
	private String lectureFaculty;
	private String creditProportion;
	private String reportVolume;
	private int likeCount;
	public int getEvaluationID() {
		return evaluationID;
	}
	public void setEvaluationID(int evaluationID) {
		this.evaluationID = evaluationID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getLectureName() {
		return lectureName;
	}
	public void setLectureName(String lectureName) {
		this.lectureName = lectureName;
	}
	public String getProfessorName() {
		return professorName;
	}
	public void setProfessorName(String professorName) {
		this.professorName = professorName;
	}
	public int getLectureYear() {
		return lectureYear;
	}
	public void setLectureYear(int lectureYear) {
		this.lectureYear = lectureYear;
	}
	public String getSemesterDivide() {
		return semesterDivide;
	}
	public void setSemesterDivide(String semesterDivide) {
		this.semesterDivide = semesterDivide;
	}
	
	public String getLectureDivide() {
		return lectureDivide;
	}
	public void setLectureDivide(String lectureDivide) {
		this.lectureDivide = lectureDivide;
	}
	public String getEvaluationTitle() {
		return evaluationTitle;
	}
	public void setEvaluationTitle(String evaluationTitle) {
		this.evaluationTitle = evaluationTitle;
	}
	public String getEvaluationContent() {
		return evaluationContent;
	}
	public void setEvaluationContent(String evaluationContent) {
		this.evaluationContent = evaluationContent;
	}
	public String getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(String totalScore) {
		this.totalScore = totalScore;
	}
	public String getLectureFaculty() {
		return lectureFaculty;
	}
	public void setLectureFaculty(String lectureFaculty) {
		this.lectureFaculty = lectureFaculty;
	}
	public String getCreditProportion() {
		return creditProportion;
	}
	public void setCreditProportion(String creditProportion) {
		this.creditProportion = creditProportion;
	}
	public String getReportVolume() {
		return reportVolume;
	}
	public void setReportVolume(String reportVolume) {
		this.reportVolume = reportVolume;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public EvaluationDTO() {
		
	}
	public EvaluationDTO(int evaluationID, String userID, String lectureName, String professorName, int lectureYear,
			String semesterDivide, String lectureDivide,String evaluationTitle, String evaluationContent, String totalScore,
			String lectureFaculty, String creditProportion, String reportVolume, int likeCount) {
		super();
		this.evaluationID = evaluationID;
		this.userID = userID;
		this.lectureName = lectureName;
		this.professorName = professorName;
		this.lectureYear = lectureYear;
		this.semesterDivide = semesterDivide;
		this.evaluationTitle = evaluationTitle;
		this.evaluationContent = evaluationContent;
		this.totalScore = totalScore;
		this.lectureFaculty = lectureFaculty;
		this.creditProportion = creditProportion;
		this.reportVolume = reportVolume;
		this.likeCount = likeCount;
	}
	
}
