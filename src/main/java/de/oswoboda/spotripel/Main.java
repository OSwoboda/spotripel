package de.oswoboda.spotripel;

import java.io.FileNotFoundException;

import edu.stanford.nlp.simple.Sentence;

public class Main {

	private static String sent1 = "John was the CEO of a company.";
	private static String sent2 = "Dole wasn't defeated by Clinton. Dole was defeated by John, who won the competition.";
	private static String sent3 = "Last week Tom visited Sarah at home, but she wasn't ill.";
	private static String sent4 = "Sandra hasn't played football, yet.";
	private static String sent5 = "The big baby isn't cute.";
	private static String sent6 = "Today, it isn't raining.";
	private static String sent7 = "Peter wasn't there, he was somewhere else.";
	private static String sent8 = "Peter wasn't there, he was at home.";
	private static String sent9 = "The door opened slowly.";

	public static void main(String[] args) throws FileNotFoundException {
		// SPOBuilder builder = new SPOBuilder();
		// File dir = new File("src/main/resources/AlbertJG_2011_bearbeitet/");
		// for (File file : dir.listFiles()) {
		// Scanner s = new Scanner(file);
		// while (s.hasNextLine()) {
		// builder.add(s.nextLine());
		// }
		// }
		// System.out.println(builder.toString());
		SPOBuilder builder = new SPOBuilder();
		builder.add("To read is easier than to write.");
		System.out.println(builder.toString());
	}
}
