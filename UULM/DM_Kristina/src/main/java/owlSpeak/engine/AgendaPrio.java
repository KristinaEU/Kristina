package owlSpeak.engine;

import java.util.Iterator;
import java.util.Random;

import owlSpeak.Agenda;
import owlSpeak.History;
import owlSpeak.OSFactory;
import owlSpeak.WorkSpace;
/**
 * The class that encapsulates the calculation of the Agenda priority
 * @author Dan Denich
 *
 */
public class AgendaPrio {
	/**
	 * creates an History object containing an Agenda for the WorkSpace 
	 * @param agenda the Agenda to add
	 * @param workspace the WorkSpace for which the history object should be
	 * @param factory the factory which should be used
	 * @param time the "time" in the controller  - will be written to AddTime
	 */ 
	public static void addAgendaPrio(Agenda agenda, WorkSpace workspace, OSFactory factory, int time){
		Iterator <History> histIt=factory.getAllHistoryInstances().iterator();
		History hist;
		int prio = agenda.getPriority();
		while(histIt.hasNext()){
			hist = histIt.next();
			if (  (hist.getForAgenda().getFullName().equals(agenda.getFullName()))&& (workspace.getFullName().equals(hist.getInWorkspace().getFullName()))   ){
			    setHistPrio(hist, prio);
				return;
			}
		}
		Random test = new Random();	
		hist = factory.createHistory("History"+test.nextInt());
		hist.setForAgenda(agenda,agenda);
		hist.setAgendaPriority(0,prio);
		hist.setInWorkspace(workspace,workspace);
		hist.setAddTime(0,time);
	}	
	/**
	 * sets the proctime in an History object containing an Agenda for the WorkSpace 
	 * @param agenda the Agenda for which the proctime should be written
	 * @param workspace the WorkSpace for which the history object should be
	 * @param factory the factory which should be used
	 * @param time the "time" in the controller - will be written to the proctime 
	 */ 
	public static void setAgendaProctime(Agenda agenda, WorkSpace workspace, OSFactory factory, int time){
		Iterator <History> histIt=factory.getAllHistoryInstances().iterator();
		History hist;
		while(histIt.hasNext()){
			hist = histIt.next();
			if (  (hist.getForAgenda().getFullName().equals(agenda.getFullName()))&& (workspace.getFullName().equals(hist.getInWorkspace().getFullName()))   ){
				hist.setProcTime(hist.getProcTime(),time);
				return;
			}
		}
	}	
	/**
	 * returns the AddTime in an History object containing an Agenda for the WorkSpace 
	 * @param agenda the Agenda for which the AddTime should be returned
	 * @param workspace the WorkSpace for which the history object should be
	 * @param factory the factory which should be used
	 * @return the AddTime of the Agenda
	 */ 
	public static int getAgendaAddTime(Agenda agenda, WorkSpace workspace, OSFactory factory){
		int result = 0;
		Iterator <History> histIt=factory.getAllHistoryInstances().iterator();
		History hist;
		while(histIt.hasNext()){
			hist = histIt.next();
			if (  (hist.getForAgenda().getFullName().equals(agenda.getFullName()))&& (workspace.getFullName().equals(hist.getInWorkspace().getFullName()))   ){
			    result = hist.getAddTime();
			    return result;
			}
		}
		return result;
	}
	/**
	 * returns the Priority in an History object containing an Agenda for the WorkSpace 
	 * @param agenda the Agenda for which the Priority should be returned
	 * @param workspace the WorkSpace for which the history object should be
	 * @param factory the factory which should be used
	 * @param core the Core object 
	 * @param actualtime the "time" in the controller which will be used in dynamic calculations
	 * @return the Priority of the Agenda
	 */ 
	public static int calculateAgendaPrio(Agenda agenda, WorkSpace workspace, OSFactory factory, Core core, int actualtime){
		int prio = getAgendaPrio(agenda, workspace, factory, core); 
		if(Settings.matchagenda==0){
			return  prio;
		}else 
		if(Settings.matchagenda==1){
			int deltat = (actualtime-getAgendaAddTime(agenda, workspace, factory));		
			prio = prio + (deltat*prio/Settings.matchagendapriofactor);
		}
		
		return prio;
	}
	/**
	 * sets the Priority of a given History object
	 * @param hist the History object
	 * @param prio the value that should be written in Priority 
	 */ 
	public static void setHistPrio(History hist, int prio){
		hist.setAgendaPriority(hist.getAgendaPriority(),prio);
	}	
	/**
	 * returns the Priority in an History object containing an Agenda for the WorkSpace 
	 * @param agenda the Agenda of which the Priority should be returned
	 * @param workspace the WorkSpace for which the history object should be
	 * @param factory the factory which should be used
	 * @param core the Core object
	 * @return the Priority of the Agenda 
	 */ 
	public static int getAgendaPrio(Agenda agenda, WorkSpace workspace, OSFactory factory, Core core){
		int value = agenda.getPriority();
		Iterator <History> histIt=factory.getAllHistoryInstances().iterator();
		History hist;
		while(histIt.hasNext()){
			hist = histIt.next();
			if (  (hist.getForAgenda().getFullName().equals(agenda.getFullName()))&& (workspace.getFullName().equals(hist.getInWorkspace().getFullName()))   ){
				value=hist.getAgendaPriority();
			}
		}
		return value;
	}	
	/**
	 * sets the Priority in an History object containing an Agenda for the WorkSpace 
	 * @param agenda the Agenda for which the Priority should be written
	 * @param workspace the WorkSpace for which the history object should be
	 * @param factory the factory which should be used
	 * @param core the Core object
	 * @param prio the value that should be written to Priority 
	 */ 
	public static void setAgendaPrio(Agenda agenda, WorkSpace workspace, OSFactory factory, Core core, int prio){
		Iterator <History> histIt=factory.getAllHistoryInstances().iterator();
		History hist;
		while(histIt.hasNext()){
			hist = histIt.next();
			if (  (hist.getForAgenda().getFullName().equals(agenda.getFullName()))&& (workspace.getFullName().equals(hist.getInWorkspace().getFullName()))   ){
				hist.setAgendaPriority(hist.getAgendaPriority(),prio);
			}
		}		
	}	
}
