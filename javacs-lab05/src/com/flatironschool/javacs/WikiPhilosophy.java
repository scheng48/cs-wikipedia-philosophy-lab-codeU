package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	private static class WikiChecker {
		private Elements paragraphs;

		public WikiChecker(Elements paragraphs) {
			this.paragraphs = paragraphs;
		}

		public Element findFirstLink() {
			// iterate through all paragraphs
			for (Element paragraph: paragraphs) {
				Element firstLink = findFirstLinkParagraph(paragraph);
				if (firstLink != null) {
					return firstLink;
				}
			}
			return null;
		}

		private Element findFirstLinkParagraph(Node root) {
			// creates tree of nodes 
			Iterable<Node> tree = new WikiNodeIterable(root);
			for (Node node: tree) {
				// not in parens???
				if (node instanceof TextNode) {
					if (isNotInParens((TextNode) node)) {
						continue;
					}
				}
				// not in italics??
				if (node instanceof Element) {
					if (isValid((Element)node)) {
						return (Element) node;
					}
				}
			}
			return null;
		}


		private boolean isValid (Element e) {
			if (isNotItalics(e)) {
				return true;
			}
			return false;
		}

		private boolean isNotItalics (Element e) {
			Elements parents = e.parents();
			for (Element element: parents) {
				if (element.tagName().equals("i") || element.tagName().equals("em")) {
					return false;
				}
			}
			return true;
		}

		private boolean isNotInParens(TextNode e) {
			int parensCounter = 0;
			String text = e.text();
			for (char c: text.toCharArray()) {
				if (c == '(') {
					parensCounter++;
				} else if (c == ')') {
					parensCounter--;
				}
			}
			if (parensCounter == 0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	final static WikiFetcher fetcher = new WikiFetcher();

	
	private static boolean wikiFinder(String beg, String end, int maxTries) throws IOException {
		List<String> visited = new ArrayList<String>();

		String url = beg;
		for(int i = 0; i < maxTries; i++) {
			Elements paragraphs = fetcher.fetchWikipedia(url); //gets paragraphs
			WikiChecker checker = new WikiChecker(paragraphs); //creates wikiChecker that can check paragraphs (Elements type)
			Element e = checker.findFirstLink(); //finding next link

			if (visited.contains(url)) {
				System.out.println("loop");
				return false;
			}

			visited.add(url); // adds current URL to list

			if (e == null) {
				System.out.println("dead end");
				return false;
			}
			if (url == end) {
				System.out.println("made it");
				return true;
			}
			url = e.attr("abs:href");
		}
		System.out.println("exceeded maximum tries");
		return false;
	}

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
        // some example code to get you started

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String goal = "https://en.wikipedia.org/wiki/Philosophy";

		if (wikiFinder(url, goal, 7)) {
			System.out.println("we did it reddit");
		}
	}
}
