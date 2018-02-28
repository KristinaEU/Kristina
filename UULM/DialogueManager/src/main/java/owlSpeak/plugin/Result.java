package owlSpeak.plugin;

/**
 * The class that represents the results from the plugins.
 * @author Maximilian Grotz.
 */
public class Result {

	public String sFoundOntoKeyword;
	public String sFoundMoveKeyword;
	public String sUtterance;
	public String sRecordedOntoKeyword;
	public String sRecordedMoveKeyword;
	public String sYes;
	public String sNo;
	public String sCatch1;
	public String sCatch2;									

	public Result()
	{
	}
	
	public Result(String sFoundOntoKeyword, String sFoundMoveKeyword, String sUtterance, String sRecordedOntoKeyword, String sRecordedMoveKeyword, String sYes, String sNo, String sCatch1, String sCatch2)
	{
		this.sFoundOntoKeyword = sFoundOntoKeyword;
		this.sFoundMoveKeyword = sFoundMoveKeyword;
		this.sUtterance = sUtterance;
		this.sRecordedOntoKeyword = sRecordedOntoKeyword;
		this.sRecordedMoveKeyword = sRecordedMoveKeyword;
		this.sYes = sYes;
		this.sNo = sNo;
		this.sCatch1 = sCatch1;
		this.sCatch2 = sCatch2;
	}
}
