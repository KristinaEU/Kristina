package owlSpeak.usersimulatorCamResInfoSys;

import java.io.*;
import java.util.*;

import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.GPDialogueOptimizer;
import owlSpeak.engine.his.UserAction.UserActionType;
import owlSpeak.servlet.document.UsersimulatorDocument; 
import owlSpeak.servlet.OwlSpeakServlet; 
import usersimulatorCamResInfoSys.*;
import usersimulatorCamResInfoSys.dialogueacts.*;

public class UsersimulatorGPTraining {
	
	private static boolean printToConsole = true;
	private static int numDialogues = 4000;
	private static int numTurns = 30;
	private static String strategy = "GP-SARSA_D2Kernel_ny0.01_sigma5_gamma1_Policy4_eta3_R1_x2_EVAL_ohneLernen";
	protected static ServletEngine owlEngine; 
	
	private static SystemAction systemMove2systemAction(String move, Map<String, String> variables) {
		if (move.equals("move_sys_Open") || move.equals("move_sys_Hello"))
		{
			WelcomeMSGDialogueAct a = new WelcomeMSGDialogueAct(DialogueType.Welcomemsg);
			SystemAction s = new SystemAction(a);
			return s;
		}
		else if (move.equals("move_sys_Bye") || move.equals("move_sys_Abort"))
		{
			ByeDialogueAct a = new ByeDialogueAct(DialogueType.Bye);
			SystemAction s = new SystemAction(a);
			return s;
		}
		else if (move.contains("Confirm") && (!move.contains("Request"))) 
		{
			if (move.contains("Area"))
			{
				ConfirmDialogueAct a = new ConfirmDialogueAct(DialogueType.Confirm);
				String var = variables.get("var_Area");
				var = var.replace("_", " ");
				a.addSlotValue("area", var);
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Food"))
			{
				ConfirmDialogueAct a = new ConfirmDialogueAct(DialogueType.Confirm);
				String var = variables.get("var_Food");
				var = var.replace("_", " ");
				a.addSlotValue("food", var);
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Name"))
			{
				ConfirmDialogueAct a = new ConfirmDialogueAct(DialogueType.Confirm);
				String var = variables.get("var_Name");
				var = var.replace("_", " ");
				a.addSlotValue("name", var);
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Pricerange"))
			{
				ConfirmDialogueAct a = new ConfirmDialogueAct(DialogueType.Confirm);
				String var = variables.get("var_Pricerange");
				var = var.replace("_", " ");
				a.addSlotValue("pricerange", var);
				SystemAction s = new SystemAction(a);
				return s;
			}
			else
			{
				return null;
			}
		}
		else if (move.contains("Request") && (!move.contains("Confirm"))) 
		{
			if (move.contains("Area"))
			{
				RequestDialogueAct a = new RequestDialogueAct(DialogueType.Request);
				a.setSlot("area");
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Food"))
			{
				RequestDialogueAct a = new RequestDialogueAct(DialogueType.Request);
				a.setSlot("food");
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Name"))
			{
				RequestDialogueAct a = new RequestDialogueAct(DialogueType.Request);
				a.setSlot("name");
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Pricerange"))
			{
				RequestDialogueAct a = new RequestDialogueAct(DialogueType.Request);
				a.setSlot("pricerange");
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("More"))
			{
				ReqMoreDialogueAct a = new ReqMoreDialogueAct(DialogueType.Reqmore);
				SystemAction s = new SystemAction(a);
				return s;
			}
			else
			{
				return null;
			}
		}
		else if (move.contains("Request") && move.contains("Confirm"))
		{
			if (move.contains("Confirm_Area"))
			{
				if (move.contains("Request_Food"))
				{
					ConfreqDialogueAct a = new ConfreqDialogueAct(DialogueType.Confreq);
					String var = variables.get("var_Area");
					var = var.replace("_", " ");
					a.addSlotValue("area", var);
					a.setReq("food");
					SystemAction s = new SystemAction(a);
					return s;
				}
				else if (move.contains("Request_Pricerange"))
				{
					ConfreqDialogueAct a = new ConfreqDialogueAct(DialogueType.Confreq);
					String var = variables.get("var_Area");
					var = var.replace("_", " ");
					a.addSlotValue("area", var);
					a.setReq("pricerange");
					SystemAction s = new SystemAction(a);
					return s;
				}
				else
				{
					return null;
				}
			}
			else if (move.contains("Confirm_Food"))
			{
				if (move.contains("Request_Area"))
				{
					ConfreqDialogueAct a = new ConfreqDialogueAct(DialogueType.Confreq);
					String var = variables.get("var_Food");
					var = var.replace("_", " ");
					a.addSlotValue("food", var);
					a.setReq("area");
					SystemAction s = new SystemAction(a);
					return s;
				}
				else if (move.contains("Request_Pricerange"))
				{
					ConfreqDialogueAct a = new ConfreqDialogueAct(DialogueType.Confreq);
					String var = variables.get("var_Food");
					var = var.replace("_", " ");
					a.addSlotValue("food", var);
					a.setReq("pricerange");
					SystemAction s = new SystemAction(a);
					return s;
				}
				else
				{
					return null;
				}
			}
			else if (move.contains("Confirm_Pricerange"))
			{
				if (move.contains("Request_Food"))
				{
					ConfreqDialogueAct a = new ConfreqDialogueAct(DialogueType.Confreq);
					String var = variables.get("var_Pricerange");
					var = var.replace("_", " ");
					a.addSlotValue("pricerange", var);
					a.setReq("food");
					SystemAction s = new SystemAction(a);
					return s;
				}
				else if (move.contains("Request_Area"))
				{
					ConfreqDialogueAct a = new ConfreqDialogueAct(DialogueType.Confreq);
					String var = variables.get("var_Pricerange");
					var = var.replace("_", " ");
					a.addSlotValue("pricerange", var);
					a.setReq("area");
					SystemAction s = new SystemAction(a);
					return s;
				}
				else
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		else if (move.contains("Offer"))
		{
			OfferDialogueAct o = new OfferDialogueAct(DialogueType.Offer);
			o.addSlotValue("name", "golden_house"); //dummy 
			SystemAction s = new SystemAction(o);
			return s;
		}
		else if (move.contains("Tell"))
		{
			if (move.contains("Area"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("area", "centre"); //dummy
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Food"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("food", "italian"); //dummy
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Name"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("name", "golden_house"); //dummy
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Pricerange"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("pricerange", "cheap"); //dummy
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Address"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("address", "27 Baker Street"); //dummy 
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Phone"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("phone", "00441223458"); //dummy 
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Postcode"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("postcode", "CB5"); //dummy 
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Signature"))
			{
				InformDialogueAct a = new InformDialogueAct(DialogueType.Inform);
				a.addSlotValue("signature", "creativity"); //dummy 
				SystemAction s = new SystemAction(a);
				return s;
			}
			else if (move.contains("Alternative"))
			{
				OfferDialogueAct a = new OfferDialogueAct(DialogueType.Offer);
				a.addSlotValue("name", "anatolia"); //dummy 
				SystemAction s = new SystemAction(a);
				return s;
			}
			else
			{
				return null;
			}			
		}	
		else
		{
			return null;
		}
	}
	
	private static List<String> userAction2userMove(UserAction action) 
	{				
		List <DialogueAct> allActs = new ArrayList<DialogueAct>();
		allActs = action.getDialActList();
		List <String> allActions = new ArrayList<String>();
		
		for (DialogueAct current: allActs) allActions.add(current.toString());

		List<String> allMoves = new ArrayList<String>();
				
		for (String act: allActions) 
		{
			act = act.replace(" ", "_");
			
			String move = "move_user_";
			String semanticGroupName = "";
			String speak = "";
			String newSpeak = "";
				
			if (act.startsWith("Inform")) 
			{
				act = act.replace("Inform", "");
				act = act.replace("{", "");
				act = act.replace("}", "");
				String[] str = act.split(",");
				
				move = "";
				
				for (int i = 0; i < str.length; i++)    
				{
					String a = str[i];
					speak += a.substring(a.indexOf("=")+1);
					
					if (a.contains("name"))
					{
						semanticGroupName += "semGroup_Name";
						move += "move_user_Name";
					}
					else if (a.contains("area"))
					{
						semanticGroupName += "semGroup_Area";
						move += "move_user_Area";
					}
					else if (a.contains("food"))
					{
						semanticGroupName += "semGroup_Food";
						move += "move_user_Food";
					}
					else if (a.contains("pricerange"))
					{
						semanticGroupName += "semGroup_Pricerange";
						move += "move_user_Pricerange";
					}
					else
					{
						move = "user move does not exist";
						semanticGroupName = "user move does not exist";
						speak = "user move does not exist";
					}
						
					if (str.length > 1 && i != str.length-1)
					{
						speak += ",";
						semanticGroupName += ",";
						move += ",";
					}
				}
					
				if (move.contains(",")) // multislot user moves
				{
					if(move.contains("move_user_Area") && move.contains("move_user_Food") && move.contains("move_user_Name") && move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_4";
					}
					else if(move.contains("move_user_Area") && move.contains("move_user_Food") && move.contains("move_user_Name") && !move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_3afn";
					}
					else if(move.contains("move_user_Area") && move.contains("move_user_Food") && !move.contains("move_user_Name") && move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_3afp";
					}
					else if(move.contains("move_user_Area") && !move.contains("move_user_Food") && move.contains("move_user_Name") && move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_3anp";
					}
					else if(!move.contains("move_user_Area") && move.contains("move_user_Food") && move.contains("move_user_Name") && move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_3fnp";
					}
					else if(move.contains("move_user_Area") && move.contains("move_user_Food") && !move.contains("move_user_Name") && !move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_2af";
					}
					else if(move.contains("move_user_Area") && !move.contains("move_user_Food") && move.contains("move_user_Name") && !move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_2an";
					}
					else if(move.contains("move_user_Area") && !move.contains("move_user_Food") && !move.contains("move_user_Name") && move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_2ap";
					}
					else if(!move.contains("move_user_Area") && move.contains("move_user_Food") && move.contains("move_user_Name") && !move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_2fn";
					}
					else if(!move.contains("move_user_Area") && move.contains("move_user_Food") && !move.contains("move_user_Name") && move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_2fp";
					}
					else if(!move.contains("move_user_Area") && !move.contains("move_user_Food") && move.contains("move_user_Name") && move.contains("move_user_Pricerange"))
					{
						move = "move_user_multislot_2np";
					}
					else
					{
						move = "user move does not exist";
					}
						
					String[] arraySpeak = speak.split(",");
					String[] arraySemanticGroupName = semanticGroupName.split(",");
						
					for (int n = 0; n < arraySpeak.length; n++)
					{
						newSpeak += arraySemanticGroupName[n] + " " + arraySpeak[n] + " ";
					}
				}
				else // move with only one slot
				{
					newSpeak = semanticGroupName + " " + speak;
				}
				
				move += ":" + newSpeak;
			}
			else if (act.startsWith("Affirm"))
			{
				move += "Affirm_";
				if (act.contains("name"))	move += "Name";
				else if (act.contains("area")) move += "Area";
				else if (act.contains("food"))	move += "Food";
				else if (act.contains("pricerange")) move += "Pricerange";
				else move = "user move does not exist";
			}
			else if (act.startsWith("Deny"))
			{
				move += "Deny_";
				if (act.contains("name"))	move += "Name";
				else if (act.contains("area")) move += "Area";
				else if (act.contains("food"))	move += "Food";
				else if (act.contains("pricerange")) move += "Pricerange";
				else move = "user move does not exist";
			}
			else if (act.startsWith("Request"))
			{
				move += "Request_";
				if (act.contains("area")) move += "Area";
				else if (act.contains("food"))	move += "Food";
				else if (act.contains("name"))	move += "Name";
				else if (act.contains("pricerange")) move += "Pricerange";
				else if (act.contains("addr")) move += "Address";
				else if (act.contains("phone")) move += "Phone";
				else if (act.contains("postcode")) move += "Postcode";
				else if (act.contains("signature")) move += "Signature";
				else move = "user move does not exist";
			}
			else if (act.startsWith("Reqalts"))
			{
				move += "Request_Alternative";
			}
			else if (act.startsWith("Bye"))
			{
				move += "Bye";
			}
			else if (act.startsWith("Thankyou"))
			{
				move += "Thankyou";
			}
			else
			{
				move = "user move does not exist";
			}
			
			if (!move.equals("user move does not exist")) allMoves.add(move);
		}
		
		for (String m: allMoves)
		{
			if (m.matches("move_user_Area.+") && allMoves.contains("move_user_Affirm_Food"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Food");
				m = m.replace("move_user_Area", "move_user_Affirm_Food_And_Give_Area");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Area.+") && allMoves.contains("move_user_Affirm_Pricerange"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Pricerange");
				m = m.replace("move_user_Area", "move_user_Affirm_Pricerange_And_Give_Area");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Area.+") && allMoves.contains("move_user_Affirm_Name"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Name");
				m = m.replace("move_user_Area", "move_user_Affirm_Name_And_Give_Area");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Food.+") && allMoves.contains("move_user_Affirm_Area"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Area");
				m = m.replace("move_user_Food", "move_user_Affirm_Area_And_Give_Food");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Food.+") && allMoves.contains("move_user_Affirm_Pricerange"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Pricerange");
				m = m.replace("move_user_Food", "move_user_Affirm_Pricerange_And_Give_Food");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Food.+") && allMoves.contains("move_user_Affirm_Name"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Name");
				m = m.replace("move_user_Food", "move_user_Affirm_Name_And_Give_Food");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Pricerange.+") && allMoves.contains("move_user_Affirm_Area"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Area");
				m = m.replace("move_user_Pricerange", "move_user_Affirm_Area_And_Give_Pricerange");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Pricerange.+") && allMoves.contains("move_user_Affirm_Food"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Food");
				m = m.replace("move_user_Pricerange", "move_user_Affirm_Food_And_Give_Pricerange");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Pricerange.+") && allMoves.contains("move_user_Affirm_Name"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Name");
				m = m.replace("move_user_Pricerange", "move_user_Affirm_Name_And_Give_Pricerange");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Name.+") && allMoves.contains("move_user_Affirm_Area"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Area");
				m = m.replace("move_user_Name", "move_user_Affirm_Area_And_Give_Name");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Name.+") && allMoves.contains("move_user_Affirm_Food"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Food");
				m = m.replace("move_user_Name", "move_user_Affirm_Food_And_Give_Name");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Name.+") && allMoves.contains("move_user_Affirm_Pricerange"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Affirm_Pricerange");
				m = m.replace("move_user_Name", "move_user_Affirm_Pricerange_And_Give_Name");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Area.+") && allMoves.contains("move_user_Deny_Area"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Deny_Area");
				m = m.replace("move_user_Area", "move_user_Correct_Area");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Food.+") && allMoves.contains("move_user_Deny_Food"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Deny_Food");
				m = m.replace("move_user_Food", "move_user_Correct_Food");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Name.+") && allMoves.contains("move_user_Deny_Name"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Deny_Name");
				m = m.replace("move_user_Name", "move_user_Correct_Name");
				allMoves.add(m);
				break;
			}
			if (m.matches("move_user_Pricerange.+") && allMoves.contains("move_user_Deny_Pricerange"))
			{
				allMoves.remove(m);
				allMoves.remove("move_user_Deny_Pricerange");
				m = m.replace("move_user_Pricerange", "move_user_Correct_Pricerange");
				allMoves.add(m);
				break;
			}
		}
		
		// remove move_user_Thankyou if there is any other move
		if (allMoves.contains("move_user_Thankyou") && allMoves.size() > 1)
			allMoves.remove("move_user_Thankyou");
		
		return allMoves;
	}
	

	private static boolean compareGoals(UserGoal goalUS, String beliefSpace, String beliefSpaceGeneric) 
	{
		System.out.println("User Goal: " + goalUS);
			
		String[] bel = null;
		String[] belGen = null;
		int lenBel = 0;
		int lenBelGen = 0;
		ArrayList<String> belAll = new ArrayList<String>();

		if (!beliefSpace.contains("(all)"))
		{
			bel = beliefSpace.substring(beliefSpace.indexOf("semGroup")).split(";");
			belAll.addAll(Arrays.asList(bel));
			lenBel = bel.length;
		}

		if (beliefSpaceGeneric.contains("var"))
		{
			belGen = beliefSpaceGeneric.substring(beliefSpaceGeneric.indexOf("var")).split(";");
			belAll.addAll(Arrays.asList(belGen));
			lenBelGen = belGen.length;
		}
		
		if (belAll.isEmpty())
		{
			System.out.println("The System got nothing.");
			return false;
		}
		
		String[] arraySystem = belAll.toArray(new String[lenBel+lenBelGen]);
		System.out.println("The System got:");
		for (String s : arraySystem)
			System.out.println(s);
		
		Vector<Constraint> constraints = goalUS.getConstraints();
		Vector<Request> requests = goalUS.getRequests();
				
		int toCheck = constraints.size() + requests.size();
		int correct = 0;
		
		for (int i = 0; i < constraints.size(); i++)
		{
			String slot = constraints.get(i).getSlot();
			String valueUS = constraints.get(i).getValue();
			if (slot.equals("area"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("semGroup_Area="))
					{
						String valueSystem = s.substring(s.indexOf("=")+1);
						if (valueSystem.contains("(c)")) valueSystem = valueSystem.replace("(c)", "");
						valueSystem = valueSystem.replace("_", " ");
						valueUS = valueUS.replace("_", " ");
						if (valueUS.equalsIgnoreCase(valueSystem)) correct++;
					}
				}		
			}
			else if (slot.equals("food"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("semGroup_Food="))
					{
						String valueSystem = s.substring(s.indexOf("=")+1);
						if (valueSystem.contains("(c)")) valueSystem = valueSystem.replace("(c)", "");
						valueSystem = valueSystem.replace("_", " ");
						valueUS = valueUS.replace("_", " ");
						if (valueUS.equalsIgnoreCase(valueSystem)) correct++;
					}
				}
			}
			else if (slot.equals("name"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("semGroup_Name="))
					{
						String valueSystem = s.substring(s.indexOf("=")+1);
						if (valueSystem.contains("(c)")) valueSystem = valueSystem.replace("(c)", "");
						valueSystem = valueSystem.replace("_", " ");
						valueUS = valueUS.replace("_", " ");
						if (valueUS.equalsIgnoreCase(valueSystem)) correct++;
					}
				}
			}
			else if (slot.equals("pricerange"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("semGroup_Pricerange="))
					{
						String valueSystem = s.substring(s.indexOf("=")+1);
						if (valueSystem.contains("(c)")) valueSystem = valueSystem.replace("(c)", "");
						valueSystem = valueSystem.replace("_", " ");
						valueUS = valueUS.replace("_", " ");
						if (valueUS.equalsIgnoreCase(valueSystem)) correct++;
					}
				}
			}
		}
		
		for (int i = 0; i < requests.size(); i++)
		{
			String slot = requests.get(i).getSlot();
			if (slot.equals("area"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidArea=yes")) correct++;
				}
			}
			else if (slot.equals("food"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidFood=yes")) correct++;
				}
			}
			else if (slot.equals("name"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidName=yes")) correct++;
				}
			}
			else if (slot.equals("pricerange"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidPricerange=yes")) correct++;
				}
			}
			else if (slot.equals("addr"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidAddress=yes")) correct++;
				}
			}
			else if (slot.equals("phone"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidPhone=yes")) correct++;
				}
			}
			else if (slot.equals("postcode"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidPostcode=yes")) correct++;
				}
			}
			else if (slot.equals("signature"))
			{
				for (String s : arraySystem)
				{
					if (s.contains("var_SaidSignature=yes")) correct++;
				}
			}
		}
		
		if (toCheck == correct) return true;
		else return false;
	}	
	
	
	public static void main(String[] args) throws IOException {
		
		if(printToConsole) System.out.println("Starting...");
		
		System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
		ServletEngine owlEngine = new ServletEngine();
		String whereAmI = "http://peewit.e-technik.uni-ulm.de:8083/owlSpeak";
		final String user = "user";
		String id = OwlSpeakServlet.reset(owlEngine, whereAmI, user);
		
		// reset SummaryBeliefPoints and matrices
		OwlSpeakServlet.resetSBP(owlEngine);
		File my = new File("./conf/OwlSpeak/models/GP_PolicyModels/my.csv");
		if(my.exists()) 
		{
			my.delete();
			if (printToConsole) System.out.println("my deleted");
		} 
		File C = new File("./conf/OwlSpeak/models/GP_PolicyModels/C.csv");
		if(C.exists()) 
		{
			C.delete();
			if (printToConsole) System.out.println("C deleted");
		} 
		File K = new File("./conf/OwlSpeak/models/GP_PolicyModels/K.csv");
		if(K.exists()) 
		{
			K.delete();
			if (printToConsole) System.out.println("K deleted");
		} 
		
		// create new user simulator
		UserSimulator us = new UserSimulator("./conf/OwlSpeak/");
		
		// create new dialogue optimizer
		GPDialogueOptimizer gpdo = new GPDialogueOptimizer();
//		GPDialogueOptimizer_Agenda gpdo = new GPDialogueOptimizer_Agenda();
//		GPDialogueOptimizer_2 gpdo = new GPDialogueOptimizer_2();

		// create new data base result writer
		owlSpeak.usersimulatorCamResInfoSys.DBResultWriter.connect();
		owlSpeak.usersimulatorCamResInfoSys.DBResultWriter ldb = new owlSpeak.usersimulatorCamResInfoSys.DBResultWriter();
		ldb.createTable();
		ldb.createMetaTable();
		
		int fails = 0;

		for (int dialogueNumber = 1; dialogueNumber <= numDialogues; dialogueNumber++)
		{
			if (printToConsole) System.out.println("############### NEW DIALOGUE (" + dialogueNumber + ") ###############");
			
			// reset the usersimulator and get the usergoal of the new dialogue
			us.resetUS();
			if (printToConsole) System.out.println("UserGoal: " + us.getGoal());

			boolean exit = false;
			boolean success = false;
			int exchanges = 0;
			double reward = 0;
			
			SystemAction sysAct;
			UserAction userAct;
			
			List<String> allUserMoves = new ArrayList<String>();
			String userMove = "";
			String confidence = "0.9";    // TODO: muss von Matthias noch eingebaut werden
			String speak = "";
			
			// get the first system move
			UsersimulatorDocument systemMove = (UsersimulatorDocument) (OwlSpeakServlet.processRequest(whereAmI, null, user, "0", owlEngine));
			String move = systemMove.getSystemMove();
			String agenda = systemMove.getAgenda();
			Map<String, String> variables = systemMove.getVariables();
			Vector<String> allowedUserMovesList = systemMove.getAllowedUserMovesList();
			if (printToConsole) System.out.println("System move: " + move);
//			if (printToConsole) System.out.println("System move:");
//			if (printToConsole) System.out.println("\tmove: " + move);
//			if (printToConsole) System.out.println("\tagenda: " + agenda);
//			if (printToConsole) System.out.println("\tvariables: " + variables);
//			if (printToConsole) System.out.println("\tallowedUserMovesList: " + allowedUserMovesList);
			
			gpdo.newEpisode(systemMove);
			
//			if (dialogueNumber == 1)
//				gpdo.initialize(systemMove);
//			else
//				reward = gpdo.optimize(systemMove, user, false, false, false, true);
			
			while (exit == false)
			{
				confidence = Double.toString((Math.random() + 1) / 2) ;   // TODO: muss von Matthias noch eingebaut werden
				System.out.println("Confidence: " + confidence);
				
				if (exchanges >= numTurns)
				{
					exit = true;
					String beliefSpace = systemMove.getSbnp().getOntology().partitionDistributions[Settings.getuserpos(user)].getTopPartition().toString();
					String beliefSpaceGeneric = systemMove.getSbnp().getOntology().factory.getBeliefSpace(user+"BeliefspaceGeneric").toString();
					success = compareGoals(us.getGoal(), beliefSpace, beliefSpaceGeneric);
					if (success)
					{
						if (printToConsole) System.out.println("Dialogue successful.");
						reward = gpdo.optimise(systemMove, user, true, false);
					}
					else
					{
						fails++;
						if (printToConsole) System.out.println("Dialogue failed.");
						reward = gpdo.optimise(systemMove, user, false, true);
//						if (printToConsole) System.out.println("Dialogue aborted.");
//						reward = gpdo.optimize(systemMove, user, false, false, true);
					}
				}
				else
				{
					// create the system action out of the system move
					sysAct = systemMove2systemAction(move, variables);
					// get the next user action
					userAct = us.fromSDS(sysAct);	
					// create the user move out of the user action
					allUserMoves = userAction2userMove(userAct);
					
					if (printToConsole) System.out.println("All user moves: " + allUserMoves.toString());
					
					if (allUserMoves.isEmpty())     // no user move
					{
						if (printToConsole) System.out.println("No user move.");
						
						// get the next system move
						systemMove = (UsersimulatorDocument) (OwlSpeakServlet.processRequest(whereAmI, UserActionType.OOG, user, "0", owlEngine));
						move = systemMove.getSystemMove();
						agenda = systemMove.getAgenda();
						variables = systemMove.getVariables();
						allowedUserMovesList = systemMove.getAllowedUserMovesList();
						exchanges++;
						if (printToConsole) System.out.println("System move: " + move);
//						if (printToConsole) System.out.println("System move:");
//						if (printToConsole) System.out.println("\tmove: " + move);
//						if (printToConsole) System.out.println("\tagenda: " + agenda);
//						if (printToConsole) System.out.println("\tvariables: " + variables);
//						if (printToConsole) System.out.println("\tallowedUserMovesList: " + allowedUserMovesList);
						
						// optimize policy
						if (move.equals("move_sys_Abort"))
						{
							exit = true;
							String beliefSpace = systemMove.getSbnp().getOntology().partitionDistributions[Settings.getuserpos(user)].getTopPartition().toString();
							String beliefSpaceGeneric = systemMove.getSbnp().getOntology().factory.getBeliefSpace(user+"BeliefspaceGeneric").toString();
							success = compareGoals(us.getGoal(), beliefSpace, beliefSpaceGeneric);
							if (success)
							{
								if (printToConsole) System.out.println("Dialogue aborted but successful.");
								reward = gpdo.optimise(systemMove, user, true, false);
							}
							else
							{
								fails++;
								if (printToConsole) System.out.println("Dialogue aborted.");
								reward = gpdo.optimise(systemMove, user, false, false, true);
							}
						}
						else 
						{
							reward = gpdo.optimise(systemMove, user, false, false);
						}
					}
					
					else     // at least one user move 
					{
						userMove = allUserMoves.get(0);
						speak = "";
						
						if (userMove.contains(":"))     // user move with speak
						{
							String[] array = userMove.split(":");
							userMove = array[0];
							speak = array[1];
						}
						
						if (!allowedUserMovesList.contains(userMove))     // user move is not appropriate -> take next (if more are available)
						{
						    Iterator<String> itr = allUserMoves.iterator();
						    while ((!allowedUserMovesList.contains(userMove)) && itr.hasNext()) 
						    {
								userMove = itr.next();
								speak = "";
								
								if (userMove.contains(":"))
								{
									String[] array = userMove.split(":");
									userMove = array[0];
									speak = array[1];
								}
						    }
							
						    if (!allowedUserMovesList.contains(userMove))     // no appropriate user move
							{
						    	if (printToConsole) System.out.println("No appropriate user move.");
						    	
								// get the next system move
								systemMove = (UsersimulatorDocument) (OwlSpeakServlet.processRequest(whereAmI, UserActionType.OOG, user, "0", owlEngine));
								move = systemMove.getSystemMove();
								agenda = systemMove.getAgenda();
								variables = systemMove.getVariables();
								allowedUserMovesList = systemMove.getAllowedUserMovesList();
								exchanges++;
								if (printToConsole) System.out.println("System move: " + move);
//								if (printToConsole) System.out.println("System move:");
//								if (printToConsole) System.out.println("\tmove: " + move);
//								if (printToConsole) System.out.println("\tagenda: " + agenda);
//								if (printToConsole) System.out.println("\tvariables: " + variables);
//								if (printToConsole) System.out.println("\tallowedUserMovesList: " + allowedUserMovesList);
								
								// optimize policy
								if (move.equals("move_sys_Abort"))
								{
									exit = true;
									String beliefSpace = systemMove.getSbnp().getOntology().partitionDistributions[Settings.getuserpos(user)].getTopPartition().toString();
									String beliefSpaceGeneric = systemMove.getSbnp().getOntology().factory.getBeliefSpace(user+"BeliefspaceGeneric").toString();
									success = compareGoals(us.getGoal(), beliefSpace, beliefSpaceGeneric);
									if (success)
									{
										if (printToConsole) System.out.println("Dialogue aborted but successful.");
										reward = gpdo.optimise(systemMove, user, true, false);
									}
									else
									{
										fails++;
										if (printToConsole) System.out.println("Dialogue aborted.");
										reward = gpdo.optimise(systemMove, user, false, false, true);
									}
								}
								else 
								{
									reward = gpdo.optimise(systemMove, user, false, false);
								}
							}
						    else     // one of the user moves is appropriate
						    {
								if (printToConsole) System.out.println("User move: " + userMove);
								
								// get the next system move
								systemMove = (UsersimulatorDocument) (OwlSpeakServlet.processWork(whereAmI, agenda, userMove, confidence, speak, user, owlEngine));
								move = systemMove.getSystemMove();
								agenda = systemMove.getAgenda();
								variables = systemMove.getVariables();
								allowedUserMovesList = systemMove.getAllowedUserMovesList();
								exchanges++;
								if (printToConsole) System.out.println("System move: " + move);
//								if (printToConsole) System.out.println("System move:");
//								if (printToConsole) System.out.println("\tmove: " + move);
//								if (printToConsole) System.out.println("\tagenda: " + agenda);
//								if (printToConsole) System.out.println("\tvariables: " + variables);
//								if (printToConsole) System.out.println("\tallowedUserMovesList: " + allowedUserMovesList);
								
								// optimize policy
								if (userMove.equals("move_user_Bye"))
								{
									exit = true;
									String beliefSpace = systemMove.getSbnp().getOntology().partitionDistributions[Settings.getuserpos(user)].getTopPartition().toString();
									String beliefSpaceGeneric = systemMove.getSbnp().getOntology().factory.getBeliefSpace(user+"BeliefspaceGeneric").toString();
									success = compareGoals(us.getGoal(), beliefSpace, beliefSpaceGeneric);
									if (success)
									{
										if (printToConsole) System.out.println("Dialogue successful.");
										reward = gpdo.optimise(systemMove, user, true, false);
									}
									else
									{
										fails++;
										if (printToConsole) System.out.println("Dialogue failed.");
										reward = gpdo.optimise(systemMove, user, false, true);
									}
								}
								else if (move.equals("move_sys_Abort"))
								{
									exit = true;
									String beliefSpace = systemMove.getSbnp().getOntology().partitionDistributions[Settings.getuserpos(user)].getTopPartition().toString();
									String beliefSpaceGeneric = systemMove.getSbnp().getOntology().factory.getBeliefSpace(user+"BeliefspaceGeneric").toString();
									success = compareGoals(us.getGoal(), beliefSpace, beliefSpaceGeneric);
									if (success)
									{
										if (printToConsole) System.out.println("Dialogue aborted but successful.");
										reward = gpdo.optimise(systemMove, user, true, false);
									}
									else
									{
										fails++;
										if (printToConsole) System.out.println("Dialogue aborted.");
										reward = gpdo.optimise(systemMove, user, false, false, true);
									}
								}
								else 
								{
									reward = gpdo.optimise(systemMove, user, false, false);
								}
							}
						}
						
						else     // first user moves is appropriate
						{
							if (printToConsole) System.out.println("User move: " + userMove);
							
							// get the next system move
							systemMove = (UsersimulatorDocument) (OwlSpeakServlet.processWork(whereAmI, agenda, userMove, confidence, speak, user, owlEngine));
							move = systemMove.getSystemMove();
							agenda = systemMove.getAgenda();
							variables = systemMove.getVariables();
							allowedUserMovesList = systemMove.getAllowedUserMovesList();
							exchanges++;
							if (printToConsole) System.out.println("System move: " + move);
//							if (printToConsole) System.out.println("System move:");
//							if (printToConsole) System.out.println("\tmove: " + move);
//							if (printToConsole) System.out.println("\tagenda: " + agenda);
//							if (printToConsole) System.out.println("\tvariables: " + variables);
//							if (printToConsole) System.out.println("\tallowedUserMovesList: " + allowedUserMovesList);
														
							// optimize policy
							if (userMove.equals("move_user_Bye"))
							{
								exit = true;
								String beliefSpace = systemMove.getSbnp().getOntology().partitionDistributions[Settings.getuserpos(user)].getTopPartition().toString();
								String beliefSpaceGeneric = systemMove.getSbnp().getOntology().factory.getBeliefSpace(user+"BeliefspaceGeneric").toString();
								success = compareGoals(us.getGoal(), beliefSpace, beliefSpaceGeneric);
								if (success)
								{
									if (printToConsole) System.out.println("Dialogue successful.");
									reward = gpdo.optimise(systemMove, user, true, false);
								}
								else
								{
									fails++;
									if (printToConsole) System.out.println("Dialogue failed.");
									reward = gpdo.optimise(systemMove, user, false, true);
								}
							}
							else if (move.equals("move_sys_Abort"))
							{
								exit = true;
								String beliefSpace = systemMove.getSbnp().getOntology().partitionDistributions[Settings.getuserpos(user)].getTopPartition().toString();
								String beliefSpaceGeneric = systemMove.getSbnp().getOntology().factory.getBeliefSpace(user+"BeliefspaceGeneric").toString();
								success = compareGoals(us.getGoal(), beliefSpace, beliefSpaceGeneric);
								if (success)
								{
									if (printToConsole) System.out.println("Dialogue successful.");
									reward = gpdo.optimise(systemMove, user, true, false);
								}
								else
								{
									fails++;
									if (printToConsole) System.out.println("Dialogue aborted.");
									reward = gpdo.optimise(systemMove, user, false, false, true);
								}
							}
							else 
							{
								reward = gpdo.optimise(systemMove, user, false, false);
							}
						}
					}
				}
			}
			int s = 0;
			if (success) s = 1;
			
			// for evaluation:
//			reward = 0 - exchanges;
//			if (success) reward += 20;
			
			ldb.insertNewEntry(strategy, id, s, exchanges, OwlSpeakServlet.getQualityObject().getCurrentIQ(), reward);
			if (printToConsole) System.out.println("Exchanges: " + exchanges);
			if (printToConsole) System.out.println("Failed Dialogues: " + fails);
			if (printToConsole) System.out.println();
			
			id = OwlSpeakServlet.reset(owlEngine, whereAmI, user);
		}		
	}
}