/**
 * 
 */
package ee.ioc.cs.vsle.editor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * http://forum.java.sun.com/thread.jspa?forumID=256&threadID=333276
 */
class SyntaxDocument extends DefaultStyledDocument {


    private static final long		serialVersionUID	= 1L;

	private static final String		SPEC_START			= "/*@";
	private static final String		SPEC_END			= "@*/";

	private DefaultStyledDocument	doc;
	private Element					rootElement;

	private boolean					multiLineComment;
	private MutableAttributeSet		normal;
	private MutableAttributeSet		keyword;
	private MutableAttributeSet		spec;
	private MutableAttributeSet		comment;
	private MutableAttributeSet		quote;

	private Set<String>				keywords;
	private Set<String>				spec_keywords;

	private Font					m_font				= new Font("Courier New", Font.PLAIN, 12);

	public SyntaxDocument() {
		doc = this;
		rootElement = doc.getDefaultRootElement();
		putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

		initStyles();

		spec_keywords = new HashSet<String>();
		spec_keywords.add("specification");
		spec_keywords.add(SPEC_START);
		spec_keywords.add(SPEC_END);

		keywords = new HashSet<String>();
		keywords.add("abstract");
		keywords.add("boolean");
		keywords.add("break");
		keywords.add("byte");
		keywords.add("byvalue");
		keywords.add("case");
		keywords.add("cast");
		keywords.add("catch");
		keywords.add("char");
		keywords.add("class");
		keywords.add("const");
		keywords.add("continue");
		keywords.add("default");
		keywords.add("do");
		keywords.add("double");
		keywords.add("else");
		keywords.add("extends");
		keywords.add("false");
		keywords.add("final");
		keywords.add("finally");
		keywords.add("float");
		keywords.add("for");
		keywords.add("future");
		keywords.add("generic");
		keywords.add("goto");
		keywords.add("if");
		keywords.add("implements");
		keywords.add("import");
		keywords.add("inner");
		keywords.add("instanceof");
		keywords.add("int");
		keywords.add("interface");
		keywords.add("long");
		keywords.add("native");
		keywords.add("new");
		keywords.add("null");
		keywords.add("operator");
		keywords.add("outer");
		keywords.add("package");
		keywords.add("private");
		keywords.add("protected");
		keywords.add("public");
		keywords.add("rest");
		keywords.add("return");
		keywords.add("short");
		keywords.add("static");
		keywords.add("super");
		keywords.add("switch");
		keywords.add("synchronized");
		keywords.add("this");
		keywords.add("throw");
		keywords.add("throws");
		keywords.add("transient");
		keywords.add("true");
		keywords.add("try");
		keywords.add("var");
		keywords.add("void");
		keywords.add("volatile");
		keywords.add("while");
	}

	private void initStyles() {
		normal = new SimpleAttributeSet();
		StyleConstants.setForeground(normal, Color.black);
		setFontForStyle(normal);

		comment = new SimpleAttributeSet();
		StyleConstants.setForeground(comment, Color.green.darker());
		StyleConstants.setItalic(comment, true);
		setFontForStyle(comment);

		keyword = new SimpleAttributeSet();
		StyleConstants.setForeground(keyword, Color.blue);
		setFontForStyle(keyword);

		quote = new SimpleAttributeSet();
		StyleConstants.setForeground(quote, new Color(0xB03060));
		setFontForStyle(quote);

		spec = new SimpleAttributeSet();
		StyleConstants.setForeground(spec, Color.green);
		StyleConstants.setBold(spec, true);
		setFontForStyle(spec);
	}

	private void setFontForStyle(MutableAttributeSet style) {
		if (m_font != null) {
			StyleConstants.setFontFamily(style, m_font.getFamily());
			StyleConstants.setFontSize(style, m_font.getSize());
			StyleConstants.setBold(style, m_font.isBold());
			StyleConstants.setItalic(style, m_font.isItalic());
		}
	}

	public void setFont(Font font) {
		m_font = font;
		initStyles();

		try {
			processChangedLines(0, getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void fireUndoableEditUpdate( UndoableEditEvent e ) {
	    if( ( e.getEdit() instanceof AbstractDocument.DefaultDocumentEvent ) ) {
	        AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent)e.getEdit();
	        
	        if( event.getType() != DocumentEvent.EventType.CHANGE ) {
	            super.fireUndoableEditUpdate( e );
	        }
	    }
	}
	    
	/*
	 * Override to apply syntax highlighting after the document has been updated
	 */
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		// if (str.equals("{"))
		// str = addMatchingBrace(offset);

		super.insertString(offset, str, a);
		processChangedLines(offset, str.length());
	}

	// /*
	// *
	// */
	// protected String addMatchingBrace(int offset) throws BadLocationException
	// {
	// StringBuffer whiteSpace = new StringBuffer();
	// int line = rootElement.getElementIndex( offset );
	// int i = rootElement.getElement(line).getStartOffset();
	//
	// while (true)
	// {
	// String temp = doc.getText(i, 1);
	//
	// if (temp.equals(" ") || temp.equals("\t"))
	// {
	// whiteSpace.append(temp);
	// i++;
	// }
	// else
	// break;
	// }
	//
	// return "{" + whiteSpace.toString() + /*"\t\n" + whiteSpace.toString() +*/
	// "}";
	// }

	/*
	 * Override to apply syntax highlighting after the document has been updated
	 */
	public void remove(int offset, int length) throws BadLocationException {
		super.remove(offset, length);
		processChangedLines(offset, 0);
	}

	/*
	 * Determine how many lines have been changed, then apply highlighting to
	 * each line
	 */
	public void processChangedLines(int offset, int length) throws BadLocationException {
		String content = doc.getText(0, doc.getLength());

		// The lines affected by the latest document update

		int startLine = rootElement.getElementIndex(offset);
		int endLine = rootElement.getElementIndex(offset + length);

		// Make sure all comment lines prior to the start line are commented
		// and determine if the start line is still in a multi line comment

		setMultiLineComment(commentLinesBefore(content, startLine));

		// Do the actual highlighting

		for (int i = startLine; i <= endLine; i++) {
			applyHighlighting(content, i);
		}

		// Resolve highlighting to the next end multi line delimiter

		if (isMultiLineComment())
			commentLinesAfter(content, endLine);
		else
			highlightLinesAfter(content, endLine);
	}

	/*
	 * Highlight lines when a multi line comment is still 'open' (ie. matching
	 * end delimiter has not yet been encountered)
	 */
	private boolean commentLinesBefore(String content, int line) {
		int offset = rootElement.getElement(line).getStartOffset();

		// Start of comment not found, nothing to do

		if (lastIndexOf(content, SPEC_START, offset - 3) > -1) {
			return false;
		}

		int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);

		if (startDelimiter < 0)
			return false;

		// Matching start/end of comment found, nothing to do

		int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);

		if (endDelimiter < offset & endDelimiter != -1)
			return false;

		// End of comment not found, highlight the lines

		doc.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, comment, false);
		return true;
	}

	/*
	 * Highlight comment lines to matching end delimiter
	 */
	private void commentLinesAfter(String content, int line) {
		int offset = rootElement.getElement(line).getEndOffset();

		// End of comment not found, nothing to do

		if (indexOf(content, SPEC_END, offset) > -1) {
			return;
		}

		int endDelimiter = indexOf(content, getEndDelimiter(), offset);

		if (endDelimiter < 0)
			return;

		// Matching start/end of comment found, comment the lines

		int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);

		if (startDelimiter < 0 || startDelimiter <= offset) {
			doc.setCharacterAttributes(offset, endDelimiter - offset + 1, comment, false);
		}
	}

	/*
	 * Highlight lines to start or end delimiter
	 */
	private void highlightLinesAfter(String content, int line) throws BadLocationException {
		int offset = rootElement.getElement(line).getEndOffset();

		// Start/End delimiter not found, nothing to do

		int startDelimiter = indexOf(content, getStartDelimiter(), offset);
		int endDelimiter = indexOf(content, getEndDelimiter(), offset);

		if (startDelimiter < 0)
			startDelimiter = content.length();

		if (endDelimiter < 0)
			endDelimiter = content.length();

		int delimiter = Math.min(startDelimiter, endDelimiter);

		if (delimiter < offset)
			return;

		// Start/End delimiter found, reapply highlighting

		int endLine = rootElement.getElementIndex(delimiter);

		for (int i = line + 1; i < endLine; i++) {
			Element branch = rootElement.getElement(i);
			Element leaf = doc.getCharacterElement(branch.getStartOffset());
			AttributeSet as = leaf.getAttributes();

			if (as.isEqual(comment))
				applyHighlighting(content, i);
		}
	}

	/*
	 * Parse the line to determine the appropriate highlighting
	 */
	private void applyHighlighting(String content, int line) throws BadLocationException {
		int startOffset = rootElement.getElement(line).getStartOffset();
		int endOffset = rootElement.getElement(line).getEndOffset() - 1;

		int lineLength = endOffset - startOffset;
		int contentLength = content.length();

		if (endOffset >= contentLength)
			endOffset = contentLength - 1;

		// check for multi line comments
		// (always set the comment attribute for the entire line)

		if (endingMultiLineComment(content, startOffset, endOffset) || isMultiLineComment()
				|| startingMultiLineComment(content, startOffset, endOffset)) {
			doc.setCharacterAttributes(startOffset, endOffset - startOffset + 1, comment, false);
			return;
		}

		// set normal attributes for the line, including newline
		doc.setCharacterAttributes(startOffset, lineLength + 1, normal, true);

		// check for single line comment

		int index = content.indexOf(getSingleLineDelimiter(), startOffset);

		if ((index > -1) && (index < endOffset)) {
			doc.setCharacterAttributes(index, endOffset - index + 1, comment, false);
			endOffset = index - 1;
		}

		// check for tokens

		checkForTokens(content, startOffset, endOffset);
	}

	/*
	 * Does this line contain the start delimiter
	 */
	private boolean startingMultiLineComment(String content, int startOffset, int endOffset)
			throws BadLocationException {
		if (indexOf(content, SPEC_START, startOffset) > -1) {
			return false;
		}

		int index = indexOf(content, getStartDelimiter(), startOffset);

		if ((index < 0) || (index > endOffset))
			return false;

		setMultiLineComment(true);
		return true;
	}

	/*
	 * Does this line contain the end delimiter
	 */
	private boolean endingMultiLineComment(String content, int startOffset, int endOffset) throws BadLocationException {
		if (indexOf(content, SPEC_END, startOffset) > -1)
			return false;

		int index = indexOf(content, getEndDelimiter(), startOffset);

		if ((index < 0) || (index > endOffset))
			return false;

		setMultiLineComment(false);
		return true;
	}

	/*
	 * We have found a start delimiter and are still searching for the end
	 * delimiter
	 */
	private boolean isMultiLineComment() {
		return multiLineComment;
	}

	private void setMultiLineComment(boolean value) {
		multiLineComment = value;
	}

	/*
	 * Parse the line for tokens to highlight
	 */
	private void checkForTokens(String content, int startOffset, int endOffset) {
		while (startOffset <= endOffset) {
			// skip the delimiters to find the start of a new token

			while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
				if (startOffset < endOffset)
					startOffset++;
				else
					return;
			}

			// Extract and process the entire token

			if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1)))
				startOffset = getQuoteToken(content, startOffset, endOffset);
			else
				startOffset = getOtherToken(content, startOffset, endOffset);
		}
	}

	/*
	 * 
	 */
	private int getQuoteToken(String content, int startOffset, int endOffset) {
		String quoteDelimiter = content.substring(startOffset, startOffset + 1);
		String escapeString = getEscapeString(quoteDelimiter);

		int index;
		int endOfQuote = startOffset;

		// skip over the escape quotes in this quote

		index = content.indexOf(escapeString, endOfQuote + 1);

		while ((index > -1) && (index < endOffset)) {
			endOfQuote = index + 1;
			index = content.indexOf(escapeString, endOfQuote);
		}

		// now find the matching delimiter

		index = content.indexOf(quoteDelimiter, endOfQuote + 1);

		if ((index < 0) || (index > endOffset))
			endOfQuote = endOffset;
		else
			endOfQuote = index;

		doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);

		return endOfQuote + 1;
	}

	/*
	 * 
	 */
	private int getOtherToken(String content, int startOffset, int endOffset) {
		int endOfToken = startOffset + 1;

		while (endOfToken <= endOffset) {
			if (isDelimiter(content.substring(endOfToken, endOfToken + 1)))
				break;

			endOfToken++;
		}

		String token = content.substring(startOffset, endOfToken);

		if (isKeyword(token)) {
			doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keyword, false);
		} else if (isSpecKeyword(token)) {
			doc.setCharacterAttributes(startOffset, endOfToken - startOffset, spec, false);
		}

		return endOfToken + 1;
	}

	/*
	 * Assume the needle will the found at the start/end of the line
	 */
	private int indexOf(String content, String needle, int offset) {
		int index;

		while ((index = content.indexOf(needle, offset)) != -1) {
			String text = getLine(content, index).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;

			offset = index + 1;
		}

		return index;
	}

	/*
	 * Assume the needle will the found at the start/end of the line
	 */
	private int lastIndexOf(String content, String needle, int offset) {
		int index;

		while ((index = content.lastIndexOf(needle, offset)) != -1) {
			String text = getLine(content, index).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;

			offset = index - 1;
		}

		return index;
	}

	private String getLine(String content, int offset) {
		int line = rootElement.getElementIndex(offset);
		Element lineElement = rootElement.getElement(line);
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		return content.substring(start, end - 1);
	}

	/*
	 * Override for other languages
	 */
	protected boolean isDelimiter(String character) {
		String operands = ";:{}()[]+-/%<=>!&|^~*";

		return (Character.isWhitespace(character.charAt(0)) || operands.indexOf(character) != -1);
	}

	/*
	 * Override for other languages
	 */
	protected boolean isQuoteDelimiter(String character) {
		String quoteDelimiters = "\"'";

		return (quoteDelimiters.indexOf(character) >= 0);
	}

	/*
	 * Override for other languages
	 */
	protected boolean isKeyword(String token) {
		return keywords.contains(token);
	}

	protected boolean isSpecKeyword(String token) {
		return spec_keywords.contains(token);
	}

	/*
	 * Override for other languages
	 */
	protected String getStartDelimiter() {
		return "/*";
	}

	/*
	 * Override for other languages
	 */
	protected String getEndDelimiter() {
		return "*/";
	}

	/*
	 * Override for other languages
	 */
	protected String getSingleLineDelimiter() {
		return "//";
	}

	/*
	 * Override for other languages
	 */
	protected String getEscapeString(String quoteDelimiter) {
		return "\\" + quoteDelimiter;
	}

	public static JEditorPane createEditor() {

		EditorKit editorKit = new StyledEditorKit() {
			public Document createDefaultDocument() {
				return new SyntaxDocument();
			}
		};

		JEditorPane edit = new JEditorPane() {

			public Font getFont() {
				if (getDocument() != null && getDocument() instanceof SyntaxDocument) {
					return ((SyntaxDocument) getDocument()).m_font;
				}
				return super.getFont();
			}

			public void setFont(Font font) {
				super.setFont(font);
				if (getDocument() != null) {
					((SyntaxDocument) getDocument()).setFont(font);
				}
			}

		};
		edit.setEditorKitForContentType("text/java", editorKit);
		edit.setContentType("text/java");
		edit.addCaretListener(new BracketMatcher(edit));
		return edit;
	}

	/**
	 * A class to support highlighting of parenthesis. To use it, add it as a
	 * caret listener to your text component.
	 * 
	 * It listens for the location of the dot. If the character before the dot
	 * is a close paren, it finds the matching start paren and highlights both
	 * of them. Otherwise it clears the highlighting.
	 * 
	 * This object can be shared among multiple components. It will only
	 * highlight one at a time.
	 */
	private static class BracketMatcher implements CaretListener {
		/**
		 * The tags returned from the highlighter, used for clearing the current
		 * highlight.
		 */
		Object							start, end;
		int								p1, p2;

		/** The last highlighter used */
		Highlighter						highlighter;

		/** Used to paint good parenthesis matches */
		Highlighter.HighlightPainter	goodPainter;

		/** Used to paint bad parenthesis matches */
		Highlighter.HighlightPainter	badPainter;

		/**
		 * Highlights using a good painter for matched parens, and a bad painter
		 * for unmatched parens
		 */
		BracketMatcher(Highlighter.HighlightPainter goodHighlightPainter,
				Highlighter.HighlightPainter badHighlightPainter) {
			this.goodPainter = goodHighlightPainter;
			this.badPainter = badHighlightPainter;
		}

		/** A BracketMatcher with the default highlighters (cyan and magenta) */
		BracketMatcher(final JTextComponent parent) {
			this(new DefaultHighlighter.DefaultHighlightPainter(Color.cyan),
					new DefaultHighlighter.DefaultHighlightPainter(Color.magenta));

			parent.addKeyListener(new KeyAdapter() {

				public void keyPressed(KeyEvent e) {
					if ((e.getModifiersEx() == (KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK))) {
						if (KeyEvent.getKeyText(e.getKeyCode()).equals("P")) {
							if (p1 > -1 && p2 > -1) {
								int position = parent.getCaretPosition();

								if (position == p1) {
									parent.setCaretPosition(p2);
								} else if (position == p2) {
									parent.setCaretPosition(p1);
								}
							}
						}
					}
				}
			});
		}

		public void clearHighlights() {
			if (highlighter != null) {
				if (start != null)
					highlighter.removeHighlight(start);
				if (end != null)
					highlighter.removeHighlight(end);
				start = end = null;
				highlighter = null;
				p1 = p2 = -1;
			}
		}

		/** Returns the character at position p in the document */
		public static char getCharAt(Document doc, int p) throws BadLocationException {
			return doc.getText(p, 1).charAt(0);
		}

		/**
		 * Returns the position of the matching parenthesis (bracket, whatever)
		 * for the character at paren. It counts all kinds of brackets, so the
		 * "matching" parenthesis might be a bad one. For this demo, we're not
		 * going to take quotes or comments into account since that's not the
		 * point.
		 * 
		 * It's assumed that paren is the position of some parenthesis character
		 * 
		 * @return the position of the matching paren, or -1 if none is found
		 */
		public static int findMatchingParen(Document d, int paren, boolean forward) throws BadLocationException {
			int parenCount = 1;
			int i;
			if (forward) {
				i = paren + 1;
				for (; i < d.getLength(); i++) {
					char c = getCharAt(d, i);
					switch (c) {
					case '(':
					case '{':
					case '[':
						parenCount++;
						break;
					case ')':
					case '}':
					case ']':
						parenCount--;
						break;
					}
					if (parenCount == 0)
						break;
				}
			} else {
				i = paren - 1;
				for (; i >= 0; i--) {
					char c = getCharAt(d, i);
					switch (c) {
					case ')':
					case '}':
					case ']':
						parenCount++;
						break;
					case '(':
					case '{':
					case '[':
						parenCount--;
						break;
					}
					if (parenCount == 0)
						break;
				}
			}
			return i;
		}

		/** Called whenever the caret moves, it updates the highlights */
		public void caretUpdate(CaretEvent e) {
			clearHighlights();
			JTextComponent source = (JTextComponent) e.getSource();
			highlighter = source.getHighlighter();
			Document doc = source.getDocument();
			if (e.getDot() == 0) {
				return;
			}

			// The character we want is the one before the current position
			int startParenth = e.getDot() - 1;
			try {
				char c = getCharAt(doc, startParenth);
				if (c == ')' || c == ']' || c == '}') {
					int openParen = findMatchingParen(doc, startParenth, false);
					if (openParen >= 0) {
						char c2 = getCharAt(doc, openParen);
						if ((c2 == '(' && c == ')') || (c2 == '{' && c == '}') || (c2 == '[' && c == ']')) {
							start = highlighter.addHighlight(openParen, openParen + 1, goodPainter);
							end = highlighter.addHighlight(startParenth, startParenth + 1, goodPainter);
							p1 = openParen + 1;
							p2 = startParenth + 1;
						} else {
							start = highlighter.addHighlight(openParen, openParen + 1, badPainter);
							end = highlighter.addHighlight(startParenth, startParenth + 1, badPainter);
						}
					} else {
						end = highlighter.addHighlight(startParenth, startParenth + 1, badPainter);
					}

				} else if (c == '(' || c == '[' || c == '{') {
					int closeParen = findMatchingParen(doc, startParenth, true);
					if (closeParen >= 0) {
						char c2 = getCharAt(doc, closeParen);
						if ((c2 == ')' && c == '(') || (c2 == '}' && c == '{') || (c2 == ']' && c == '[')) {
							start = highlighter.addHighlight(closeParen, closeParen + 1, goodPainter);
							end = highlighter.addHighlight(startParenth, startParenth + 1, goodPainter);
							p1 = closeParen + 1;
							p2 = startParenth + 1;
						} else {
							start = highlighter.addHighlight(closeParen, closeParen + 1, badPainter);
							end = highlighter.addHighlight(startParenth, startParenth + 1, badPainter);
						}
					} else {
						end = highlighter.addHighlight(startParenth, startParenth + 1, badPainter);
					}

				}
			} catch (BadLocationException ex) {
				throw new Error(ex);
			}
		}

	}

	// public static void main(String a[])
	// {
	//
	// EditorKit editorKit = new StyledEditorKit()
	// {
	// public Document createDefaultDocument()
	// {
	// return new SyntaxDocument();
	// }
	// };
	//
	// final JEditorPane edit = new JEditorPane();
	// edit.setEditorKitForContentType("text/java", editorKit);
	// edit.setContentType("text/java");
	// edit.setEditorKit(new StyledEditorKit());
	// edit.setDocument(new SyntaxDocument());

	// JButton button = new JButton("Load SyntaxDocument.java");
	// button.addActionListener( new ActionListener()
	// {
	// public void actionPerformed(ActionEvent e)
	// {
	// try
	// {
	// FileInputStream fis = new FileInputStream( "SyntaxDocument.java" );
	// // FileInputStream fis = new FileInputStream(
	// "C:\\Java\\jdk1.4.1\\src\\javax\\swing\\JComponent.java" );
	// edit.read( fis, null );
	// edit.requestFocus();
	// }
	// catch(Exception e2) {}
	// }
	// });

	// JFrame frame = new JFrame("Syntax Highlighting");
	// frame.getContentPane().add( new JScrollPane(edit) );
	// frame.getContentPane().add(button, BorderLayout.SOUTH);
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.setSize(800,300);
	// frame.setVisible(true);
	// }
}
