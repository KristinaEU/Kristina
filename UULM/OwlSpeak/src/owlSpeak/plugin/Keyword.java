package owlSpeak.plugin;

/**
 * The class that represents the keywords from the ontology and the recognized result.
 * @author Maximilian Grotz.
 */
public class Keyword {
	
	public String keyword;
	 
	/**
	 * The class that represents the MoveKeywords.
	 */
	public class MoveKeyword extends Keyword{
		public String moveName;
		public String ontoName;
		public String agendaName;
			
		public MoveKeyword(String keyword, String moveName, String ontoName, String agendaName){
			this.keyword = keyword;
			this.moveName = moveName;
			this.ontoName = ontoName;
			this.agendaName = agendaName;
		}
	}
	
	/**
	 * The class that represents the OntoKeywords.
	 */ 
	public class OntoKeyword extends Keyword{
		public String ontoName;
		public String ontoDomainName;
		 
		public OntoKeyword(String keyword, String ontoName, String ontoDomainName){
			this.keyword = keyword;
			this.ontoName = ontoName;
			this.ontoDomainName = ontoDomainName;
		}
	}

	/**
	 * The class that represents the RecKeywords from the external ercognizer.
	 */
	public class RecWord extends Keyword{
		public String conf;
		 
		public RecWord(String keyword, String conf){
			this.keyword = keyword;
			this.conf = conf;
		}
	}
	
	/**
	 * The class that represents the RecKeywords from the external ercognizer as a array.
	 */
	public class RecArray{
		public String[] keywordArr; 
		public String conf;

		public RecArray(String keywords, String conf){
			this.keywordArr = keywords.split(" ");
			this.conf = conf;
		}
	}
}
