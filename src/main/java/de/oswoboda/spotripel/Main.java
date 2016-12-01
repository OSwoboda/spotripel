package de.oswoboda.spotripel;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;

public class Main
{
	private final GrammaticalRelation nsubj = UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT;
	private final GrammaticalRelation nsubjpass = UniversalEnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT;
	private final GrammaticalRelation det = UniversalEnglishGrammaticalRelations.DETERMINER;
	private final GrammaticalRelation cop = UniversalEnglishGrammaticalRelations.COPULA;
	private final GrammaticalRelation dobj = UniversalEnglishGrammaticalRelations.DIRECT_OBJECT;
	private final GrammaticalRelation neg = UniversalEnglishGrammaticalRelations.NEGATION_MODIFIER;
	private final GrammaticalRelation nmod = UniversalEnglishGrammaticalRelations.NOMINAL_MODIFIER;
	
	private boolean stem = true;
	private SemanticGraph sg = null;
	
	private Sentence sent1 = new Sentence("John was the CEO of a company.");
	private Sentence sent2 = new Sentence("Dole wasn't defeated by Clinton. Dole was defeated by John, who won the competition.");
	private Sentence sent3 = new Sentence("Last week Tom visited Sarah at home, but she wasn't ill.");
	private Sentence sent4 = new Sentence("Sandra hasn't played football, yet.");
	private Sentence sent5 = new Sentence("The big baby isn't cute.");
	private Sentence sent6 = new Sentence("Today, it isn't raining.");
	private Sentence sent7 = new Sentence("Peter wasn't there, he was somewhere else.");
	private Sentence sent8 = new Sentence("Peter wasn't there, he was at home.");
	private Sentence sent9 = new Sentence("The door opened slowly.");
	
	private List<String> subject = new ArrayList<>();
	private List<String> predicate = new ArrayList<>();
	private List<String> object = new ArrayList<>();
	
	public void posTag() {
		Sentence sent = sent9;
		sg = sent.dependencyGraph(SemanticGraphFactory.Mode.BASIC);
		System.out.println(sg.toPOSList());
		for (SemanticGraphEdge edge : sg.findAllRelns(nsubj)) {
			IndexedWord target = edge.getTarget();
			
			add(subject, target);
			IndexedWord source = edge.getSource();
			String sourceTag = source.tag();
			
			if (sourceTag.contains("NN") || sourceTag.contains("JJ") || sourceTag.contains("RB")) {
				if (sourceTag.contains("NN")) {
					// if the source is a noun (NN*), its the object
					add(object, source);				
				} else {
					// if the source is an adjective (JJ*) or an adverb (RB*), then there is no object
					object.add("");
				}
				if (sg.hasChildWithReln(source, cop)) {
					predicate.add(negate(source)+stem(sg.getChildWithReln(source, cop)));
				}
			} else {
				// if the source is neither noun nor adjective nor adverb, its the predicate
				predicate.add(negate(source)+stem(source));
				
				if (sg.hasChildWithReln(source, dobj)) {
					add(object, sg.getChildWithReln(source, dobj));
				} else {
					object.add("");
				}
			}
		}
		for (SemanticGraphEdge edge : sg.findAllRelns(nsubjpass)) {
			IndexedWord target = edge.getTarget();
			IndexedWord source = edge.getSource();
			add(object, target);
			predicate.add(negate(source)+stem(source));
			if (sg.hasChildWithReln(source, nmod)) {
				add(subject, sg.getChildWithReln(source, nmod));
			}
		}
	}
	
	private String negate(IndexedWord word) {
		return (sg.hasChildWithReln(word, neg)) ? "¬" : "";
	}
	
	private String stem(IndexedWord word) {
		return (stem) ? Morphology.stemStatic(word.value(), word.tag()).value() : word.value();
	}
	
	private void add(List<String> list, IndexedWord word) {
		list.add((sg.hasChildWithReln(word, det)) ? sg.getChildWithReln(word, det).value()+" "+word.value() : word.value());
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (int i=0; i < subject.size(); ++i) {
			buf.append(subject.get(i)).append(" ");
			buf.append(predicate.get(i)).append(" ");
			buf.append(object.get(i)).append("\n");
		}
		return buf.toString();
	}
	
    public static void main(String[] args)
    {
        Main main = new Main();
        main.posTag();
        System.out.println(main);
    }
}
