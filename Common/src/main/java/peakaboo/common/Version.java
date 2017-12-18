package peakaboo.common;

public class Version {

	public final static boolean release = false;
	public final static boolean rc = false;
	public final static boolean beta = false;
	
	public final static String devReleaseDescription = "Development Version";
	public final static String rcReleaseDescription = "Release Candidate";
	public final static String releaseDescription = release ? "" : rc ? rcReleaseDescription : devReleaseDescription;
	
	public final static String titleReleaseDescription = (release ? "" : " [") + releaseDescription + (release ? "" : "]");
	
	
	public final static int versionNoMajor = 5;
	public final static int versionNoMinor = 0;
	public final static int versionNoPoint = 0;
	public final static int rcNo = 0;
	public final static int betaNo = 0;
	
	public final static String buildDate = "2017-12-15";
	public final static String longVersionNo =  
			release ? versionNoMajor + "." + versionNoMinor + "." + versionNoPoint : 
			rc 		? versionNoMajor + "." + versionNoMinor + "." + versionNoPoint + " RC" + rcNo : 
			versionNoMajor + "." + versionNoMinor + "." + versionNoPoint + "dev";
	public final static String logo = (release) ? "icon" : rc ? "devicon" : "devicon";
	public final static String icon = (release) ? "icon" : rc ? "devicon" : "devicon";
	public final static String splash = (release) ? "splash" : rc ? "devsplash" : "devsplash";
	public final static String program_name = "Peakaboo";
	public final static String title = program_name + " " + (release ? versionNoMajor : longVersionNo) + titleReleaseDescription;
	
	
}
