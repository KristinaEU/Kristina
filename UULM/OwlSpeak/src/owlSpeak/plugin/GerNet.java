package owlSpeak.plugin;

import germanet.GermaNet;
import germanet.Synset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;

import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.plugin.Keyword.MoveKeyword;
import owlSpeak.plugin.Keyword.OntoKeyword;
import owlSpeak.plugin.Keyword.RecWord;
import owlSpeak.servlet.OwlDocument;
import owlSpeak.servlet.OwlSpeakServlet;

/**
 * Class GerNet: Plugin to handle a NOMATCH using GermaNet
 * @author Max Grotz
 * @version 1.0
 */
public class GerNet {

	public GermaNet gNet;
	
	public GerNet(){
		String sGerNetPath = Settings.homePath+"\\Plugins\\GermaNet\\CodeBase";
		File file = new File(sGerNetPath);
		try {
			gNet = new GermaNet(file, true);
		} catch (FileNotFoundException e) {
			System.err.println("GermaNet: CodeBase not found");
			e.printStackTrace();
		} catch (XMLStreamException e) {
			System.err.println("GermaNet: XML-Input error");
			e.printStackTrace();
		}
	}
	
	/**
	 * this function is used to perform a comparison with an amount of RecWords and Keywords extended by the use of GermaNet 
	 * @param owlEngine the actual owlEngine
	 * @param user the actual user
	 * @param whereAmI the netadress of the servlet
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @return the filled document
	 */
	public OwlDocument performGermaNet(ServletEngine owlEngine, String user, String whereAmI, String noInputCounter){
		
		Vector<RecWord> vRecWord = ExtRecog.outputRecWord("l", "");
		Vector<Result> vResult = new Vector<Result>();
		Vector<Synset> vSynsets = new Vector<Synset>();
		
		Vector<ExtSynset> vExtSynsets = new Vector<ExtSynset>();
		Vector<ExtSynset> vExtSynsetsToSort = new Vector<ExtSynset>();
		Vector<OntoKeyword> vOntoKeywords = KeywordLists.generateOntoKeywordsList(owlEngine, user);
		Vector<MoveKeyword> vMoveKeywords;
		LinkedList<ExtSynset> llMaster = new LinkedList<ExtSynset>();
		
		String sFoundOntoKey;
		String sFoundMoveKey;
		
		
	
		for (int i = 0; i < vOntoKeywords.size(); i++) {
			OntoKeyword actOntoKeyword = vOntoKeywords.elementAt(i);
			vSynsets = getSynsets(actOntoKeyword.keyword);
			
			for (int j = 0; j < vSynsets.size(); j++) {
				ExtSynset temp = new ExtSynset(vSynsets.elementAt(j), actOntoKeyword);
				vExtSynsetsToSort.add(temp);
			}
		}
		
		// *** sorting the elements of vExtSynsetsToSort into llMaster depending on the hierarchy in GermaNet
		llMaster.add(vExtSynsetsToSort.firstElement());
		for (int j = 1; j < vExtSynsetsToSort.size(); j++) {
			boolean added = false;
			added = vExtSynsetsToSort.elementAt(j).sortList(llMaster, vExtSynsetsToSort.elementAt(j), vExtSynsetsToSort, j);
			if (!added) llMaster.add(vExtSynsetsToSort.elementAt(j));
		}
		
		// *** filling the elements of llMaster with Hyponyms and put them on a vector ***
		for (int i = 0; i < llMaster.size(); i++) {
			llMaster.get(i).fillTreeHyponyms();
			vExtSynsets.add(llMaster.get(i));
		}

		// *** smoothing down the tree of ExtSynsets.vExtHyponyms to a single vector***
		Vector<ExtSynset> vAllHynonyms = ExtSynset.getAllHyponyms(vExtSynsets);

		// *** performing search ***
		for (int k = 0; k < vRecWord.size(); k++) {
			for (int i = 0; i < vAllHynonyms.size(); i++) {
			ExtSynset actExtSynset = vAllHynonyms.elementAt(i);
			boolean added = false;
				for (int j = 0; j < actExtSynset.synset.getAllOrthForms().size(); j++) {
					boolean bContainsOnto = false;
					if (vRecWord.elementAt(k).keyword.toLowerCase().contains(actExtSynset.synset.getAllOrthForms().get(j).toLowerCase())){
						OntoKeyword foundOntoKeyword = (OntoKeyword) actExtSynset.keyword;
						sFoundOntoKey = foundOntoKeyword.ontoName;
						for (int n = 0; n < vResult.size(); n++) {
							if ((vResult.elementAt(n).sFoundOntoKeyword.equalsIgnoreCase(sFoundOntoKey))) bContainsOnto = true;
						}	
						if (!bContainsOnto){
							System.out.println(vRecWord.elementAt(k).keyword + " found");	
							vMoveKeywords = KeywordLists.generateMoveKeywordsList(owlEngine, user, sFoundOntoKey);
							for (int l=0; l < vRecWord.size(); l++){
								for (int m=0; m < vMoveKeywords.size(); m++){
									Result rFound = new Result();
									if (vRecWord.elementAt(l).keyword.equals(vMoveKeywords.elementAt(m).keyword)){		
										sFoundMoveKey = vMoveKeywords.elementAt(m).moveName;
										boolean bContains = false;
										for (int n = 0; n < vResult.size(); n++) {
											if ((vResult.elementAt(n).sFoundMoveKeyword.equalsIgnoreCase(sFoundMoveKey)) && vResult.elementAt(n).sFoundOntoKeyword.equalsIgnoreCase(sFoundOntoKey)) bContains = true;
										}	
										if (!bContains){
											rFound.sFoundOntoKeyword = sFoundOntoKey;
											rFound.sFoundMoveKeyword = sFoundMoveKey;
											rFound.sRecordedOntoKeyword = vRecWord.elementAt(k).keyword;
											rFound.sRecordedMoveKeyword = vRecWord.elementAt(l).keyword;
											vResult.add(rFound);
											added = true;
										}
									}
									if (added) break;
								}
								if (added) break;
							}
							if (added) break;
						}
						if (added) break;
					}
				}
			}
		}
		
		
		
		// *** Logger - Output ***
		String log = "| ";
		for (int i = 0; i < vResult.size(); i++) {
			log = log + "Onto: " + vResult.elementAt(i).sFoundOntoKeyword + " / Move: " + vResult.elementAt(i).sFoundMoveKeyword + " / Recog: " + vResult.elementAt(i).sRecordedOntoKeyword + " / " + vResult.elementAt(i).sRecordedMoveKeyword + " | ";    
		} 
		OwlSpeakServlet.logger.logp(Level.INFO, "GermaNet-Plugin: ", vResult.size()+" Matches found", log);
		
		// *** Creating answer-String in case of a Nomatch with the result of the Spotting ***
		if (vResult.isEmpty()){
			String sDraft = new String();
			OwlSpeakOntology onto = owlEngine.systemCore.findOntology("system"); 
			Agenda uttAgenda = onto.factory.getAgenda("Plugin_Nomatch_ag");
			Iterator <Move> moveIt=uttAgenda.getHas().iterator();
			while(moveIt.hasNext()){
				Iterator<OWLLiteral> olIt = moveIt.next().getUtterance().getUtteranceString().iterator();
				while(olIt.hasNext()){
					sDraft = sDraft+" "+(olIt.next().getLiteral());
				}
			}
			System.out.println("NO RESULT");
			OwlDocument VDoc = owlEngine.buildOutput(whereAmI, "NOMATCH", sDraft, user);
			return VDoc;
		}

		// *** yes-no-grammars ***
		OwlSpeakOntology systemOnto = owlEngine.systemCore.findOntology("system"); 
		String sYes = new String();
		Move tempMove_yes = systemOnto.factory.getMove("yes_mv");
		Iterator<OWLLiteral> olIt_yes = tempMove_yes.getGrammar().getGrammarString().iterator();
		while(olIt_yes.hasNext()){
			sYes = sYes+" "+(olIt_yes.next().getLiteral());
		}
		String sNo = new String();
		Move tempMove_No = systemOnto.factory.getMove("no_mv");
		Iterator<OWLLiteral> olIt_No = tempMove_No.getGrammar().getGrammarString().iterator();
		while(olIt_No.hasNext()){
			sNo = sNo+" "+(olIt_No.next().getLiteral());
		}
		// *** catches-utterances ***
		String sCatch1 = new String();
		Move tempMove_Catch1 = systemOnto.factory.getMove("catch1_mv");
		Iterator<OWLLiteral> olIt_Catch1 = tempMove_Catch1.getUtterance().getUtteranceString().iterator();
		while(olIt_Catch1.hasNext()){
			sCatch1 = sCatch1+" "+(olIt_Catch1.next().getLiteral());
		}
		String sCatch2 = new String();
		Move tempMove_Catch2 = systemOnto.factory.getMove("catch2_mv");
		Iterator<OWLLiteral> olIt_Catch2 = tempMove_Catch2.getUtterance().getUtteranceString().iterator();
		while(olIt_Catch2.hasNext()){
			sCatch2 = sCatch2+" "+(olIt_Catch2.next().getLiteral());
		}
		
		// *** Creating answer-String with the result of the Spotting, if more than one ***
		String sDraft = new String();
		Agenda uttAgenda = systemOnto.factory.getAgenda("Plugin_Found_Keyword_ag");
		Iterator <Move> moveIt=uttAgenda.getHas().iterator();
		while(moveIt.hasNext()){
			Iterator<OWLLiteral> olIt = moveIt.next().getUtterance().getUtteranceString().iterator();
			while(olIt.hasNext()){
				sDraft = sDraft+" "+(olIt.next().getLiteral());
			}
		}
		for (int i = 0; i < vResult.size(); i++) {
			String sUtterance = sDraft.replace("%moveKeyword%", vResult.elementAt(i).sRecordedMoveKeyword).replace("%ontoKeyword%", vResult.elementAt(i).sRecordedOntoKeyword);
			vResult.elementAt(i).sUtterance = sUtterance;
			vResult.elementAt(i).sYes = sYes;
			vResult.elementAt(i).sNo = sNo;
			vResult.elementAt(i).sCatch1 = sCatch1;
			vResult.elementAt(i).sCatch2 = sCatch2;
		}

		OwlDocument VDoc = owlEngine.buildPluginAlternatives(vResult, whereAmI, user);
		return VDoc;
		
	}
	
	/**
	 * this function returns synsets
	 * @param input - the String for which the synset should be returned 
	 * @return a vector with {@link Synset}s
	 */
	public Vector<Synset> getSynsets(String input) {
		List<Synset> lSynset = gNet.getSynsets(input);
		Vector<Synset> vReturn = new Vector<Synset>();
		vReturn.addAll(lSynset);
		return vReturn;
	}
}









//	  ***********************
//	  *** for further use ***
//	  ***********************
	
//	/**
//	 * this function returns synons
//	 * @param input - the word for which the synons should be returned 
//	 * @return a vector {@link String} containing synons 
//	 */
//	public Vector <String> getSynons(String input){
//		Vector<String> vSynons = new Vector<String>();
//		List <Synset> lSynset = gNet.getSynsets(input);
//		List <String> lString;
//		for (int i = 0; i<lSynset.size(); i++){
//			lString = lSynset.get(i).getAllOrthForms();
//			for (int j = 0; j<lString.size(); j++){
//				if(!vSynons.contains(lString.get(j).toLowerCase())){
//					vSynons.add(lString.get(j).toLowerCase());
//				}
//			}
//		}
//		
//		return vSynons;
//	}
//	
//	/**
//	 * this function returns synons as a {@link Vector} of {@link OntoKeyword}s for a {@link Vector} with {@link OntoKeyword}s
//	 * @param input - a {@link Vector} with {@link OntoKeyword}s
//	 * @return a {@link Vector} containing synons as {@link OntoKeyword}s 
//	 */
//	public Vector<OntoKeyword> getOntoSynons(Vector<OntoKeyword> input){
//		Keyword x = new Keyword();
//		Vector<OntoKeyword> output = new Vector<OntoKeyword>();
//		for (int i = 0; i < input.size(); i++) {
//			List <Synset> lSynset = gNet.getSynsets(input.elementAt(i).keyword);
//			for (int j = 0; j < lSynset.size(); j++) {
//				List <String> lString = lSynset.get(j).getAllOrthForms();
//				for (int k = 0; k < lString.size(); k++) {
//					OntoKeyword temp = x.new OntoKeyword(lString.get(k), input.elementAt(i).ontoName, input.elementAt(i).ontoDomainName); 	
//					for (int l = 0; l < output.size(); l++) {
//						if (output.elementAt(l).keyword != temp.keyword){
//							output.add(temp);							
//						}
//					}
//				}
//			}
//		}
//		return output;
//	}
//	
//	/**
//	 * this function returns synons as a Vector of MoveKeywords for a Vector with MoveKeywords 
//	 * @param input - a {@link Vector} with {@link MoveKeyword}s
//	 * @return a {@link Vector} containing synons as {@link MoveKeyword}s 
//	 */
//	public Vector<MoveKeyword> getMoveSynons(Vector<MoveKeyword> input){
//		Keyword x = new Keyword();
//		Vector<MoveKeyword> output = new Vector<MoveKeyword>();
//		for (int i = 0; i < input.size(); i++) {
//			List <Synset> lSynset = gNet.getSynsets(input.elementAt(i).keyword);
//			for (int j = 0; j < lSynset.size(); j++) {
//				List <String> lString = lSynset.get(j).getAllOrthForms();
//				for (int k = 0; k < lString.size(); k++) {
//					MoveKeyword temp = x.new MoveKeyword(lString.get(k), input.elementAt(i).moveName, input.elementAt(i).ontoName, input.elementAt(i).agendaName); 	
//					for (int l = 0; l < output.size(); l++) {
//						if (output.elementAt(l).keyword != temp.keyword){
//							output.add(temp);							
//						}
//					}
//				}
//			}
//		}
//		return output;
//	}
//	
//	/**
//	 * this function returns hyperonyms
//	 * @param input - the word for which the hyperonyms should be returned 
//	 * @return a vector with {@link Synset}s which contains the hyperonyms
//	 */
//	public Vector<Synset> getHyperonyms(String input) {
//		List<Synset> lSynset = gNet.getSynsets(input);
//		Vector<Synset> vHyperonymy = new Vector<Synset>();
//		for (int i = 0; i < lSynset.size(); i++) {
//			vHyperonymy.addAll(lSynset.get(i).getRelatedSynsets(ConRel.hyperonymy));
//		}
//		return vHyperonymy;
//	}
//	
//	/**
//	 * this function returns hyperonyms
//	 * @param input - the {@link Synset} for which the hyperonyms should be returned 
//	 * @return a vector with {@link Synset}s which contains the hyperonyms
//	 */
//	public Vector<Synset> getHyperonyms(Synset input) {
//		Vector<Synset> vReturn = new Vector<Synset>();
//		vReturn.addAll(input.getRelatedSynsets(ConRel.hyperonymy));
//		return vReturn;
//	}
//	
//	/**
//	 * this function returns hyponyms
//	 * @param input - the word for which the hyponyms should be returned 
//	 * @return a vector with {@link Synset}s which contains the hyponyms
//	 */
//	public Vector<Synset> getHyponyms(String input) {
//		List<Synset> lSynset = gNet.getSynsets(input);
//		Vector<Synset> vHyponyms = new Vector<Synset>();
//		for (int i = 0; i < lSynset.size(); i++) {
//			vHyponyms.addAll(lSynset.get(i).getRelatedSynsets(ConRel.hyponymy));
//		}
//		return vHyponyms;
//	}
//	
//	/**
//	 * this function returns hyponyms
//	 * @param input - the {@link Synset} for which the Hyponyms should be returned 
//	 * @return a vector with {@link Synset}s which contains the hyponyms
//	 */
//	public Vector<Synset> getHyponyms(Synset input) {
//		Vector<Synset> vReturn = new Vector<Synset>();
//		vReturn.addAll(input.getRelatedSynsets(ConRel.hyponymy));
//		return vReturn;
//	}
//	
//	/**
//	 * this function returns hyponyms
//	 * @param id - the id for which the {@link Synset} should be returned 
//	 * @return a {@link Synset}
//	 */
//	public Synset getSynset(int id){
//		Synset temp = gNet.getSynsetByID(id);
//		return temp;
//	}

	
	
	
	

