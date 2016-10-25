package model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.semanticweb.owlapi.model.IRI;

import owlSpeak.Move;
import owlSpeak.kristina.KristinaMove;

public class DialogueHistory {
	public enum Participant {
		USER, SYSTEM
	};

	private static LinkedList<Pair<KristinaMove, Participant>> history = new LinkedList<Pair<KristinaMove, Participant>>();

	public static void add(KristinaMove move, Participant p){
		if (!move.getDialogueAction().equals(DialogueAction.INCOMPREHENSIBLE)
				&& !move.getDialogueAction().equals(DialogueAction.UNKNOWN)
				&& !move.getDialogueAction().equals(DialogueAction.CALM_DOWN)
				&& !move.getDialogueAction().equals(DialogueAction.CHEER_UP)
				&& !move.getDialogueAction().equals(DialogueAction.CONSOLE)
				&& !move.getDialogueAction().equals(DialogueAction.EMPTY)
				&& !move.getDialogueAction().equals(DialogueAction.MISSING_COMPREHENSION)
				&& !move.getDialogueAction().equals(DialogueAction.PERSONAL_APOLOGISE)
				&& !move.getDialogueAction().equals(DialogueAction.REPEAT)
				&& !move.getDialogueAction().equals(DialogueAction.REPHRASE)
				&& !move.getDialogueAction().equals(DialogueAction.SHARE_JOY)
				&& !move.getDialogueAction().equals(DialogueAction.SIMPLE_APOLOGISE)
				&& !(p.equals(Participant.SYSTEM) && (move.getText().equals("I don't know.") || move.getText().equals("I don't now about that.")|| move.getText().equals("Are you still there?"))))
		history.add(new ImmutablePair<KristinaMove,Participant>(move,p));
	}

	public static KristinaMove getLastUserMove() {
		Iterator<Pair<KristinaMove, Participant>> it = history
				.descendingIterator();
		while (it.hasNext()) {
			Pair<KristinaMove, Participant> p = it.next();

			if (p.getRight() == Participant.USER) {
				return p.getLeft();
			}
		}
		return null;
	}

	public static KristinaMove getPreviousUserMove() {
		Iterator<Pair<KristinaMove, Participant>> it = history
				.descendingIterator();
		boolean start = false;
		while (it.hasNext()) {
			Pair<KristinaMove, Participant> p = it.next();
			if (p.getRight() == Participant.SYSTEM) {
				start = true;
			}
			if (start && p.getRight() == Participant.USER) {
				return p.getLeft();
			}
		}
		return null;
	}

	public static KristinaMove getLastSystemMove() {
		Iterator<Pair<KristinaMove, Participant>> it = history
				.descendingIterator();
		while (it.hasNext()) {
			Pair<KristinaMove, Participant> p = it.next();
			if (p.getRight() == Participant.SYSTEM) {
				return p.getLeft();
			}
		}
		return null;
	}

	public static List<KristinaMove> getLastUserMoves() {
		Iterator<Pair<KristinaMove, Participant>> it = history
				.descendingIterator();
		LinkedList<KristinaMove> result = new LinkedList<KristinaMove>();
		boolean collect = false;
		while (it.hasNext()) {
			Pair<KristinaMove, Participant> p = it.next();
			if (p.getRight() == Participant.USER) {
				collect = true;
				result.addLast(p.getLeft());
			} else if (collect && p.getRight() == Participant.SYSTEM) {
				return result;
			}
		}
		return null;
	}

	public static List<KristinaMove> getLastSystemMoves() {
		Iterator<Pair<KristinaMove, Participant>> it = history
				.descendingIterator();
		LinkedList<KristinaMove> result = new LinkedList<KristinaMove>();
		boolean collect = false;
		while (it.hasNext()) {
			Pair<KristinaMove, Participant> p = it.next();
			if (p.getRight() == Participant.SYSTEM) {
				collect = true;
				result.addLast(p.getLeft());
			} else if (collect && p.getRight() == Participant.USER) {
				return result;
			}
		}
		return null;
	}

	public static void restart(String user) {
		history.removeAll(history);
	}
}
