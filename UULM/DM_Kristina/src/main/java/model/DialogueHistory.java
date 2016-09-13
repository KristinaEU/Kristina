package model;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.semanticweb.owlapi.model.IRI;

import owlSpeak.Move;

public class DialogueHistory {
	public enum Participant {USER,SYSTEM};
	private static LinkedList<Pair<Move,Participant>> history = new LinkedList<Pair<Move,Participant>>();
	
	public static void add(Move move, Participant p){
		history.add(new ImmutablePair<Move,Participant>(move,p));
	}
	
	public static Move getLastUserMove(){
		Iterator<Pair<Move,Participant>> it = history.descendingIterator();
		while(it.hasNext()){
			Pair<Move,Participant> p = it.next();
			if(p.getRight()==Participant.USER){
				return p.getLeft();
			}
		}
		return null;
	}
	
	public static Move getLastSystemMove(){
		Iterator<Pair<Move,Participant>> it = history.descendingIterator();
		while(it.hasNext()){
			Pair<Move,Participant> p = it.next();
			if(p.getRight()==Participant.SYSTEM){
				return p.getLeft();
			}
		}
		return null;
	}
}
