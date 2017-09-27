package de.oswoboda.spotripel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;

public class SPOBuilder {

	private List<Result> results = new ArrayList<>();

	private final GrammaticalRelation nsubj = UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT;
	private final GrammaticalRelation csubj = UniversalEnglishGrammaticalRelations.CLAUSAL_SUBJECT;
	private final GrammaticalRelation nsubjpass = UniversalEnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT;
	private final GrammaticalRelation csubjpass = UniversalEnglishGrammaticalRelations.CLAUSAL_PASSIVE_SUBJECT;
	private final GrammaticalRelation det = UniversalEnglishGrammaticalRelations.DETERMINER;
	private final GrammaticalRelation cop = UniversalEnglishGrammaticalRelations.COPULA;
	private final GrammaticalRelation dobj = UniversalEnglishGrammaticalRelations.DIRECT_OBJECT;
	private final GrammaticalRelation neg = UniversalEnglishGrammaticalRelations.NEGATION_MODIFIER;

	private boolean stem = true;
	private SemanticGraph sg = null;

	public SPOBuilder add(String text) {
		Result result = new Result(new Sentence(text));
		results.add(result);
		sg = result.getSentence().dependencyGraph(SemanticGraphFactory.Mode.ENHANCED_PLUS_PLUS);
		for (SemanticGraphEdge edge : Stream.concat(sg.findAllRelns(nsubj).stream(), sg.findAllRelns(csubj).stream())
				.collect(Collectors.toList())) {
			IndexedWord target = edge.getTarget();

			result.addSubject(addDeterminer(target));
			IndexedWord source = edge.getSource();
			String sourceTag = source.tag();

			if (sourceTag.contains("NN") || sourceTag.contains("JJ") || sourceTag.contains("RB")) {
				if (sourceTag.contains("NN")) {
					// if the source is a noun (NN*), its the object
					result.addObject(addDeterminer(source));
				} else {
					// if the source is an adjective (JJ*) or an adverb (RB*),
					// then there is no object
				}
				if (sg.hasChildWithReln(source, cop)) {
					result.addPredicate(negate(source) + stem(sg.getChildWithReln(source, cop)));
				}
			} else {
				// if the source is neither noun nor adjective nor adverb, its
				// the predicate
				result.addPredicate(negate(source) + stem(source));

				if (sg.hasChildWithReln(source, dobj)) {
					result.addObject(addDeterminer(sg.getChildWithReln(source, dobj)));
				}
			}
		}
		for (SemanticGraphEdge edge : Stream
				.concat(sg.findAllRelns(nsubjpass).stream(), sg.findAllRelns(csubjpass).stream())
				.collect(Collectors.toList())) {
			IndexedWord target = edge.getTarget();
			IndexedWord source = edge.getSource();
			result.addObject(addDeterminer(target));
			result.addPredicate(negate(source) + stem(source));
			Set<IndexedWord> children = sg.getChildrenWithRelns(source,
					UniversalEnglishGrammaticalRelations.getNmods());
			if (!children.isEmpty()) {
				if (children.size() == 1) {
					result.addSubject(addDeterminer(children.iterator().next()));
				}
			}
		}

		return this;
	}

	private String negate(IndexedWord word) {
		return (sg.hasChildWithReln(word, neg)) ? "¬" : "";
	}

	private String stem(IndexedWord word) {
		return (stem) ? Morphology.stemStatic(word.value(), word.tag()).value() : word.value();
	}

	private String addDeterminer(IndexedWord word) {
		return (sg.hasChildWithReln(word, det) ? sg.getChildWithReln(word, det).value() + " " + word.value()
				: word.value());
	}

	public String toString() {
		StringBuilder csv = new StringBuilder();
		for (Result result : results) {
			csv.append("\n").append(result.getSentence().text()).append("\n");
			csv.append(result.posList).append("\n");
			csv.append(result);
		}

		return csv.toString();
	}

}
