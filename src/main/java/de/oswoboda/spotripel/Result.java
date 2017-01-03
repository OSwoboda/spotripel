package de.oswoboda.spotripel;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.simple.Sentence;

public class Result {
	
	private Sentence sentence;
	private List<SPOTripel> tripel = new ArrayList<>();
	private SPOTripel currentTripel = new SPOTripel();
	public String posList;

	public Result(Sentence sentence) {
		this.sentence = sentence;
		SemanticGraph sg = sentence.dependencyGraph(SemanticGraphFactory.Mode.ENHANCED_PLUS_PLUS);
		posList = sg.toPOSList();
		tripel.add(currentTripel);
	}
	
	public Sentence getSentence() {
		return sentence;
	}
	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}
	
	public void addSubject(String subject) {
		if (currentTripel.hasSubject()) {
			currentTripel = new SPOTripel();
			tripel.add(currentTripel);
		}
		currentTripel.setSubject(subject);
	}
	
	public void addPredicate(String predicate) {
		if (currentTripel.hasPredicate()) {
			currentTripel = new SPOTripel();
			tripel.add(currentTripel);
		}
		currentTripel.setPredicate(predicate);
	}
	
	public void addObject(String object) {
		if (currentTripel.hasObject()) {
			currentTripel = new SPOTripel();
			tripel.add(currentTripel);
		}
		currentTripel.setObject(object);
	}
	
	public String toString() {
		StringBuilder csv = new StringBuilder();
		for (SPOTripel spotripel : tripel) {
			csv.append(spotripel.getSubject()).append(",");
			csv.append(spotripel.getPredicate()).append(",");
			csv.append(spotripel.getObject()).append("\n");
		}
		
		return csv.toString();
	}

}
