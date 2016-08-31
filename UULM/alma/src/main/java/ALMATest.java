import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.apache.xmlbeans.XmlException;

import de.affect.manage.AffectManager;
import de.affect.manage.event.AffectUpdateEvent;
import de.affect.manage.event.AffectUpdateListener;
import de.affect.xml.AffectInputDocument.AffectInput;
import de.affect.xml.AffectInputDocument.AffectInput.Character;
import de.affect.xml.AffectInputDocument.AffectInput.PAD;
import de.affect.xml.AffectOutputDocument.AffectOutput.CharacterAffect;

public class ALMATest implements AffectUpdateListener{
	
	public static AffectManager fAM = null;
	  // ALMA configuration files
	  private static String sALMACOMP = "./src/main/resources/conf/AffectComputation.aml";
	  private static String sALMADEF = "./src/main/resources/conf/CharacterDefinition.aml";
	  // ALMA mode: 
	  //     false - output on console 
	  //     true - graphical user interface CharacterBuilder
	  //            NOTE: No runtime windows (defined in AffectComputation or
	  //                  AffectDefinition will be displayed!)
	  private static final boolean sGUIMode = false;
	  // Console logging 
	  public static Logger log = Logger.getLogger("Alma");
	  
	  public ALMATest(){
		  try {
		      fAM = new AffectManager(sALMACOMP, sALMADEF, sGUIMode);
		      fAM.addAffectUpdateListener(this);
		    } catch (IOException io) {
		      log.info("Error during ALMA initialisation");
		      io.printStackTrace();
		      System.exit(-1);
		    } catch (XmlException xmle) {
		      log.info("Error in ALMA configuration");
		      xmle.printStackTrace();
		      System.exit(-1);
		    }
	  }
	
	public static void main(String[] args){
		ALMATest test = new ALMATest();
		double a = 0.5;
		double d = 0.1;
		double p = -0.1;
		double intensity = 0.8;
		String character = "Anne";
		
		AffectInput aiInput = AffectInput.Factory.newInstance();

	    // Building the Character element
	    Character perfCharacter = Character.Factory.newInstance();
	    perfCharacter.setName(character);
	    aiInput.setCharacter(perfCharacter);

	    PAD pad =PAD.Factory.newInstance();
	    pad.setPleasure((new Double(p)).doubleValue());
	    pad.setArousal((new Double(a)).doubleValue());
	    pad.setDominance((new Double(d)).doubleValue());
	    pad.setIntensity((new Double(intensity)).doubleValue());
	    pad.setDescription("test");
	    aiInput.setPAD(pad);
	    fAM.processSignal(aiInput);
	    fAM.stopAll();
	}

	@Override
	public void update(AffectUpdateEvent arg0) {
		for (Iterator<CharacterAffect> it = arg0.getUpdate().getAffectOutput().getCharacterAffectList().iterator(); it.hasNext();) {
			CharacterAffect c = it.next();
			if(c.getName().equals("Anne")){
				System.out.println("Update: ");
				System.out.println(c.getMood().getPleasure()+" "+c.getMood().getArousal()+" "+c.getMood().getDominance());
			}
		}
	}

}
