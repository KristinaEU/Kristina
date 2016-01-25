package owlSpeak.plugin;

import germanet.ConRel;
import germanet.Synset;

import java.util.LinkedList;
import java.util.Vector;

/**
 * The class that represents the extended Synsets.
 * @author Maximilian Grotz
 */
public class ExtSynset {

	
	public Synset synset;
	public Vector<ExtSynset> vExtHyperonyms;
	public Vector<ExtSynset> vExtHyponyms;
	public Vector<Synset> vSynlist;
	public Keyword keyword;
	public static int iStep = 1;
	public static String sAdded = "";
	
	public ExtSynset(){}
	public ExtSynset(Synset synset) {
		this.synset = synset; 
	}
	public ExtSynset(Synset synset, Keyword keyword) {
		this.synset = synset;
		this.keyword = keyword;
	}
	
	public static Vector<ExtSynset> getAllHyponyms(Vector<ExtSynset> vExt){
		Vector<ExtSynset> hypos = new Vector<ExtSynset>();
		for (int i = 0; i < vExt.size(); i++) {
			Vector<ExtSynset> temp = vExt.elementAt(i).listHyponym();
			
			for (int j = 0; j < temp.size(); j++) {
				boolean contain = false;
				for (int k = 0; k < hypos.size(); k++) {
					if(hypos.elementAt(k).synset.getId() == temp.elementAt(j).synset.getId()) contain = true; 
				}
				if (!contain) hypos.add(temp.elementAt(j));
			}

		}
		return hypos;
	}
	
	/**
	 * this function returns all Hyponyms of vExtSynsets of current object
	 * @return a vector with Hyponyms
	 */	
	public Vector <ExtSynset> listHyponym(){
		Vector <ExtSynset> temp = new Vector <ExtSynset>();
		getHyponym(temp);
		return temp;
	}

	/**
	 * this function adds the current object to the given vector if it does not contains it already
	 * @param syns vector with ExtSynsets, where the current object should be added
	 */
	public void getHyponym(Vector <ExtSynset> syns){
		for(int i = 0; i < syns.size();  i++){
			if (this.synset.getId() == syns.elementAt(i).synset.getId()) return;
		}
		syns.add(this);
		for(int i = 0; i < vExtHyponyms.size();  i++){
			vExtHyponyms.elementAt(i).getHyponym(syns);
		}
	}

	/**
	 * this function fills up the elements of the object with hyponyms
	 */
	public void fillTreeHyponyms(){
		vExtHyponyms = new Vector<ExtSynset>();
		Vector<Synset> vSynset = new Vector<Synset>();
		vSynset.addAll(this.synset.getRelatedSynsets(ConRel.hyponymy));
		
		for (int i = 0; i < vSynset.size(); i++) {
			ExtSynset temp = new ExtSynset(vSynset.elementAt(i), this.keyword);
			vExtHyponyms.add(temp);
		}
		for (int i = 0; i < vExtHyponyms.size(); i++) {
			vExtHyponyms.elementAt(i).fillTreeHyponyms();
		}
	}

	/**
	 * this function sorts a vector with ExtSynsets in a LinkedList depending on the hierarchy of GermaNet
	 * @param llMaster LinkedList, which should be sorted
	 * @param next next Elemtent, which should be sorted in
	 * @param vToCompare vector with ExtSynsets, which sould be compared for sorting in
	 * @param iPosOfActual number of recursive runs
	 * @return true a element was added
	 */
	public boolean sortList(LinkedList<ExtSynset> llMaster, ExtSynset next, Vector<ExtSynset> vToCompare, int iPosOfActual){
		boolean bAdded = false;
		ExtSynset actual = vToCompare.elementAt(iPosOfActual);
		Vector<Synset> vHyperSyn = new Vector<Synset>();
		Vector<ExtSynset> vHyperExt = new Vector<ExtSynset>();
		vHyperSyn.addAll(next.synset.getRelatedSynsets(ConRel.hyperonymy));
		for (int j = 0; j < vHyperSyn.size(); j++) {
			ExtSynset temp = new ExtSynset(vHyperSyn.elementAt(j), actual.keyword);
			vHyperExt.add(temp);
		}

		for (int i = 0; i < vHyperExt.size(); i++) {
			for (int j = 0; j < llMaster.size(); j++) {
				if (vHyperExt.elementAt(i).synset.getId() == llMaster.get(j).synset.getId()){
					llMaster.add(j, actual);
					bAdded = true;
					return bAdded;
				} 
			}
			bAdded = sortList(llMaster, vHyperExt.elementAt(i), vToCompare, iPosOfActual);
		}
		return bAdded;
	}

	
//	  ************************************
//	  *** experimental for further use ***
//	  ************************************
//	
	
	
//	/**
//	 * The class that represents the extended Synsets.
//	 */
//	class MultiExtSynset{
//		public Vector<ExtSynset> vExtSynsets;
//		public Vector<Synset> synlist;
//		public Keyword keyword;
//		
//		public MultiExtSynset(){
//			vExtSynsets=new Vector<ExtSynset>();
//		}
//		public MultiExtSynset(Vector<Synset> vSynsets){
//			this();
//			for (int i = 0; i < vSynsets.size(); i++) {
//				vExtSynsets.add(new ExtSynset(vSynsets.elementAt(i)));
//			}
//		}
//		public MultiExtSynset(Vector<Synset> vSynsets, Keyword keyword){
//			this();
//			this.keyword = keyword;
//			for (int i = 0; i < vSynsets.size(); i++) {
//				vExtSynsets.add(new ExtSynset(vSynsets.elementAt(i), keyword));
//			}
//		}
//		
//		  ***********************
//		  *** for further use ***
//		  ***********************
//		
//		/**
//		 * this function fills up vExtSynsets of itself with hyponyms
//		 */
//		public void fillTreeHyponyms(){
//			for (int i = 0; i < vExtSynsets.size(); i++){
//				 vExtSynsets.elementAt(i).fillTreeHyponyms();
//			}
//		}
//		
//		/**
//		 * this function fills up vExtSynsets of current object with hyponyms without duplicates from the given vector 
//		 * @param vMEScompares vector which should be filled
//		 */
//		public void fillTreeHyponymsWithoutDublicates(Vector<MultiExtSynset> vMEScompares){
//			Vector <MultiExtSynset> vMultiTemp = new Vector<ExtSynset.MultiExtSynset>();
//			vMultiTemp.addAll(vMEScompares);
//			vMultiTemp.remove(this);
//			for (int i = 0; i < this.vExtSynsets.size(); i++) {
//				this.vExtSynsets.elementAt(i).fillTreeHyponymsWithoutDublicates(vMultiTemp);
//			}
//			
//		}
//		
//		/**
//		 * this function returns all Hyperonyms of vExtSynsets of current object
//		 * @return a vector with Hyperonyms
//		 */		
//		public Vector <Synset> listHyperonyms(){
//			Vector <Synset> temp = new Vector <Synset>();
//			for (int i = 0; i < vExtSynsets.size(); i++){
//				vExtSynsets.elementAt(i).getHyperonyms(temp);
//			}
//			return temp;
//		}
//		
//		/**
//		 * this function returns all Hyponyms of vExtSynsets of current object
//		 * @return a vector with Hyponyms
//		 */		
//		public Vector <ExtSynset> listHyponym(){
//			Vector <ExtSynset> temp = new Vector <ExtSynset>();
//			for (int i = 0; i < vExtSynsets.size(); i++){
//				vExtSynsets.elementAt(i).getHyponym(temp);
//			}
//			return temp;
//		}
//		
//	}
	
	
//	/**
//	 * this function fills up vExtHyperonyms of itself with Hyperonyms
//	 */
//	public void fillTreeHyperonyms(){
//		vExtHyperonyms = new Vector<ExtSynset>();
//		Vector<Synset> vSynset = new Vector<Synset>();
//		vSynset.addAll(this.synset.getRelatedSynsets(ConRel.hyperonymy));
//		
//		for (int i = 0; i < vSynset.size(); i++) {
//			ExtSynset temp = new ExtSynset(vSynset.elementAt(i));
//			vExtHyperonyms.add(temp);
//		}
//		if (iStep > 0){
//			for (int i = 0; i < vExtHyperonyms.size(); i++) {
//				iStep--;
//				vExtHyperonyms.elementAt(i).fillTreeHyperonyms();
//			}
//		}
//	}
//
//	/**
//	 * this function fills up the elements of the object with hyponyms
//	 * @param vMultiTemp the vector, which should be filled
//	 */
//	public void fillTreeHyponymsWithoutDublicates(Vector<MultiExtSynset> vMultiTemp){
//		// *** Abort, if the synset is one of the keyword-synsets ***
//		for (int i = 0; i < vMultiTemp.size(); i++) {
//			for (int j = 0; j < vMultiTemp.elementAt(i).vExtSynsets.size(); j++) {
//				if (this.synset.getId() == vMultiTemp.elementAt(i).vExtSynsets.elementAt(j).synset.getId()) return;
//			}
//		}
//		
//		vExtHyponyms = new Vector<ExtSynset>();
//		Vector<Synset> vSynset = new Vector<Synset>();
//		vSynset.addAll(this.synset.getRelatedSynsets(ConRel.hyponymy));
//		for (int j = 0; j < vSynset.size(); j++) {
//			ExtSynset temp = new ExtSynset(vSynset.elementAt(j), this.keyword);
//			vExtHyponyms.add(temp);
//		}
//		for (int j = 0; j < vExtHyponyms.size(); j++) {
//			this.vExtHyponyms.elementAt(j).fillTreeHyponymsWithoutDublicates(vMultiTemp);
//		}
//	}
//	
//	/**
//	 * this function returns all Hyperonyms of vExtSynsets of current object
//	 * @return a vector with Hyperonyms
//	 */	
//	public Vector <Synset> listHyperonyms(){
//		Vector <Synset> temp = new Vector <Synset>();
//		getHyperonyms(temp);
//		return temp;
//	}
//	
//	/**
//	 * this function is the recursive part of "public Vector <Synset> listHyperonyms()"
//	 * @param syns a vector with Synsets
//	 */
//	public void getHyperonyms(Vector<Synset> syns){
//		for(int i = 0; i < syns.size();  i++){
//			if (synset.getId() == syns.elementAt(i).getId()) return;
//		}
//		syns.add(synset);
//		for(int i = 0; i < vExtHyperonyms.size();  i++){
//			vExtHyperonyms.elementAt(i).getHyperonyms(syns);
//		}
//	}
	
}


