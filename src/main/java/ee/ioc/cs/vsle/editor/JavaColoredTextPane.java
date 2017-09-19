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
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import ee.ioc.cs.vsle.util.syntax.*;

public class JavaColoredTextPane extends JTextPane {

	private Font m_font = new Font("Courier New", Font.PLAIN, 12);
	
	/**
     * the styled document that is the model for
     * the textPane
     */
    protected HighLightedDocument document = new HighLightedDocument();
    /**
     * A reader wrapped around the document
     * so that the document can be fed into
     * the lexer.
     */
    protected DocumentReader documentReader;
    /**
     * The lexer that tells us what colors different
     * words should be.
     */
    protected Lexer syntaxLexer;
    /**
     * A thread that handles the actual coloring.
     */
    protected Colorer colorer;
    /**
     * A lock for modifying the document, or for
     * actions that depend on the document not being
     * modified.
     */
    private Object doclock = new Object();
    
	public JavaColoredTextPane() {
		super();
		addCaretListener(new BracketMatcher());
		setStyledDocument(document);
		setCaretPosition(0);
        setMargin(new Insets(5,5,5,5));
        setOpaque(true);

//      Start the thread that does the coloring
        colorer = new Colorer();
        colorer.start();

        // Set up the hash table that contains the styles.
        initStyles();

        // create the new document.
        documentReader = new DocumentReader(document);

        syntaxLexer = new JavaLexer(documentReader);


        
        document.addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				colorer.resumeColoring();
			}

			public void removeUpdate(DocumentEvent e) {
				colorer.resumeColoring();
			}});
	}

	@Override
	public void updateUI()
	{
		super.updateUI();
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				setStyleBackground( getBackground() );
				colorAll();
			}
		});
	}
	
	public void append( String s ) {
		try {
			document.insertString( document.getLength(), s, getStyles("text") );
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void setFont(Font font) {
		m_font = font;
		
		if( styles == null ) {
			super.setFont( font );
		} else {
			initStyles();
			colorAll();
		}
	}
	
	public Font getFont() {
		return m_font;
	}
	/**
     * Run the Syntax Highlighting as a separate thread.
     * Things that need to be colored are messaged to the
     * thread and put in a list.
     */
    private class Colorer extends Thread {

    	private boolean isRunning = true;
        /**
         * Keep a list of places in the file that it is safe to restart the
         * highlighting.  This happens whenever the lexer reports that it has
         * returned to its initial state.  Since this list needs to be sorted
         * and we need to be able to retrieve ranges from it, it is stored in a
         * balanced tree.
         */
        private TreeSet<Integer> iniPositions = new TreeSet<Integer>();

        /**
         * As we go through and remove invalid positions we will also be finding
		 * new valid positions. 
		 * Since the position list cannot be deleted from and written to at the same
         * time, we will keep a list of the new positions and simply add it to the
         * list of positions once all the old positions have been removed.
         */
    	private Set<Integer> newPositions = new HashSet<Integer>();

        /**
         * A simple wrapper representing something that needs to be colored.
         * Placed into an object so that it can be stored in a Vector.
         */
        private class RecolorEvent {
            public int position;
            public int adjustment;
            public RecolorEvent(int position, int adjustment){
                this.position = position;
                this.adjustment = adjustment;
            }
        }

        /**
         * Vector that stores the communication between the two threads.
         */
        private volatile Queue<RecolorEvent> eventQueue = new LinkedList<RecolorEvent>();

        /**
         * The amount of change that has occurred before the place in the
         * document that we are currently highlighting (lastPosition).
         */
        private volatile int change = 0;

        /**
         * The last position colored
         */
        private volatile int lastPosition = -1;
        
        private volatile boolean asleep = false;

        /**
         * When accessing the vector, we need to create a critical section.
         * we will synchronize on this object to ensure that we don't get
         * unsafe thread behavior.
         */
        private Object lock = new Object();

        /**
         * Tell the Syntax Highlighting thread to take another look at this
         * section of the document.  It will process this as a FIFO.
         * This method should be done inside a doclock.
         */
        public synchronized void color(int position, int adjustment){
            // figure out if this adjustment effects the current run.
            // if it does, then adjust the place in the document
            // that gets highlighted.
            if (position < lastPosition){
                if (lastPosition < position - adjustment){
                    change -= lastPosition - position;
				} else {
                	change += adjustment;
                }
            }
            synchronized(lock){
                eventQueue.add(new RecolorEvent(position, adjustment));
                if (asleep){
//                	System.err.println("notifying");
                	notifyAll();
                }
            }
        }

        /**
         * The colorer runs forever and may sleep for long
         * periods of time.  It should be interrupted every
         * time there is something for it to do.
         */
        public void run(){
        	
        	Thread.currentThread().setName( "Colorer" );
        	
            int position = -1;
            int adjustment = 0;
            // if we just finish, we can't go to sleep until we
            // ensure there is nothing else for us to do.
            // use try again to keep track of this.
            boolean tryAgain = false;
            while ( isRunning ){
//            	System.err.println("running");
                synchronized(lock){
                    if (!eventQueue.isEmpty()){
                        RecolorEvent re = eventQueue.poll();
                        position = re.position;
                        adjustment = re.adjustment;
                    } else {
                        tryAgain = false;
                        position = -1;
                        adjustment = 0;
                    }
                }
                if (position != -1){
                    SortedSet<Integer> workingSet;
                    Iterator<Integer> workingIt;
                    int startRequest = position;
                    int endRequest = position + ( ( adjustment >= 0 ) ? adjustment : -adjustment );
                    int dp;
                    int dpStart = 0;
                    int dpEnd = 0;

                    // find the starting position.  We must start at least one
                    // token before the current position
                    try {
                        // all the good positions before
                        workingSet = iniPositions.headSet(startRequest);
                        // the last of the stuff before
                        dpStart = workingSet.last();
                    } catch (NoSuchElementException x){
                        // if there were no good positions before the requested start,
                        // we can always start at the very beginning.
                        dpStart = 0;
                    }

                    // if stuff was removed, take any removed positions off the list.
                    if (adjustment < 0){
                        workingSet = iniPositions.subSet(startRequest, endRequest);
                        workingIt = workingSet.iterator();
                        while (workingIt.hasNext()){
                            workingIt.next();
                            workingIt.remove();
                        }
                    }

                    // adjust the positions of everything after the insertion/removal.
                    workingSet = iniPositions.tailSet(startRequest);
                    workingIt = workingSet.iterator();
                    SortedSet<Integer> adjusted = new TreeSet<Integer>();
                    while (workingIt.hasNext()){
                    	adjusted.add( workingIt.next() + adjustment );
                    	workingIt.remove();
                    }
                    iniPositions.addAll(adjusted);
                    
                    // now go through and highlight as much as needed
                    workingSet = iniPositions.tailSet(dpStart);
                    workingIt = workingSet.iterator();
                    dp = 0;
                    if (workingIt.hasNext()){
                        dp = workingIt.next();
                    }
                    try {
                        Token t;
                        boolean done = false;
                        dpEnd = dpStart;
                        synchronized (doclock){
                            // we are playing some games with the lexer for efficiency.
                            // we could just create a new lexer each time here, but instead,
                            // we will just reset it so that it thinks it is starting at the
                            // beginning of the document but reporting a funny start position.
                            // Reseting the lexer causes the close() method on the reader
                            // to be called but because the close() method has no effect on the
                            // DocumentReader, we can do this.
                            syntaxLexer.reset(documentReader, 0, dpStart, 0);
                            // After the lexer has been set up, scroll the reader so that it
                            // is in the correct spot as well.
                            documentReader.seek(dpStart);
                            // we will highlight tokens until we reach a good stopping place.
                            // the first obvious stopping place is the end of the document.
                            // the lexer will return null at the end of the document and wee
                            // need to stop there.
                            t = syntaxLexer.getNextToken();
                        }
                        newPositions.add(dpStart);
                        while (!done && t != null){
                            // this is the actual command that colors the stuff.
                            // Color stuff with the description of the style matched
                            // to the hash table that has been set up ahead of time.
                            synchronized (doclock){
                                if (t.getCharEnd() <= document.getLength()){
                                    document.setCharacterAttributes(
                                        t.getCharBegin() + change,
                                        t.getCharEnd()-t.getCharBegin(),
                                        getStyles(t.getDescription()),
                                        true
                                    );              
                                    //System.out.println(t.getDescription());
                                    // record the position of the last bit of text that we colored
                                    dpEnd = t.getCharEnd();
                                }
                                lastPosition = (t.getCharEnd() + change);
                            }
                            // The other more complicated reason for doing no more highlighting
                            // is that all the colors are the same from here on out anyway.
                            // We can detect this by seeing if the place that the lexer returned
                            // to the initial state last time we highlighted is the same as the
                            // place that returned to the initial state this time.
                            // As long as that place is after the last changed text, everything
                            // from there on is fine already.
                            if (t.getState() == Token.INITIAL_STATE){
                                //System.out.println(t);
                                // look at all the positions from last time that are less than or
                                // equal to the current position
                                while (dp != 0 && dp <= t.getCharEnd()){
                                    if (dp == t.getCharEnd() && dp >= endRequest){
                                        // we have found a state that is the same
										done = true;
                                        dp = 0;
                                    } else if (workingIt.hasNext()){
                                        // didn't find it, try again.
                                        dp = workingIt.next();
                                    } else {
                                        // didn't find it, and there is no more info from last
                                        // time.  This means that we will just continue
                                        // until the end of the document.
                                        dp = 0;
                                    }
                                }
                                // so that we can do this check next time, record all the
                                // initial states from this time.
                                newPositions.add(dpEnd);
                            }
                            synchronized (doclock){
                                t = syntaxLexer.getNextToken();
                            }
                        }

                        // remove all the old initial positions from the place where
                        // we started doing the highlighting right up through the last
                        // bit of text we touched.
                        workingIt = iniPositions.subSet(dpStart, dpEnd).iterator();
                        while (workingIt.hasNext()){
                            workingIt.next();
                            workingIt.remove();
                        }
                        
                        // Remove all the positions that are after the end of the file.:
                        workingIt = iniPositions.tailSet(document.getLength()).iterator();
                        while (workingIt.hasNext()){
                            workingIt.next();
                            workingIt.remove();
                        }

                        // and put the new initial positions that we have found on the list.
                        iniPositions.addAll(newPositions);
                        newPositions.clear();
                        
                        /*workingIt = iniPositions.iterator();
                        while (workingIt.hasNext()){
                            System.out.println(workingIt.next());
                        }
                        
                        System.out.println("Started: " + dpStart.getPosition() + " Ended: " + dpEnd.getPosition());*/
                    } catch (IOException x){
                    }
                    synchronized (doclock){
                        lastPosition = -1;
                        change = 0;
                    }
                    // since we did something, we should check that there is
                    // nothing else to do before going back to sleep.
                    tryAgain = true;
                }                
                asleep = true;
                if (!tryAgain){
                	synchronized ( this ){
                		try {
//                			System.err.println( "sleeping after " + ( System.currentTimeMillis() - time) + " ms of work :)");
                			wait();
//                			System.err.println( "waking" );
                			time = System.currentTimeMillis();
                		} catch (InterruptedException x){
                		}
                	}
                    
                }
                asleep = false;
            }
            
        }
        long time = 0;
        void resumeColoring() {
        	synchronized (doclock){
        		doclock.notifyAll();
        	}
        }

		public boolean isRunning() {
			return isRunning;
		}

		public void setRunning(boolean isRunning) {
			this.isRunning = isRunning;
			if (asleep){
				this.interrupt();
			}
		}
    }

    /**
     * Color or recolor the entire document
     */
    public void colorAll(){
    	if( document != null ) {
    		color(0, document.getLength());
    	}
    }

    /**
     * Color a section of the document.
     * The actual coloring will start somewhere before
     * the requested position and continue as long
     * as needed.
     *
     * @param position the starting point for the coloring.
     * @param adjustment amount of text inserted or removed
     *    at the starting point.
     */
    public void color(int position, int adjustment){
        colorer.color(position, adjustment);
    }
    
    /**
     * A hash table containing the text styles.
     * Simple attribute sets are hashed by name (String)
     */
    private Hashtable<String, SimpleAttributeSet> styles = new Hashtable<String, SimpleAttributeSet>();

    /**
     * retrieve the style for the given type of text.
     *
     * @param styleName the label for the type of text ("tag" for example) 
	 *      or null if the styleName is not known.
     * @return the style
     */
    private SimpleAttributeSet getStyles(String styleName){
        return styles.get(styleName);
    }

    /**
     * Create the styles and place them in the hash table.
     */
    private void initStyles(){
        SimpleAttributeSet style;

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("body", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.blue);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("tag", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.blue);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("endtag", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("reference", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, new Color(0xB03060)/*Color.maroon*/);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("name", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, new Color(0xB03060)/*Color.maroon*/);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, true);
        styles.put("value", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("text", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.blue);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("reservedWord", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("identifier", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, new Color(0xB03060)/*Color.maroon*/);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("literal", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, new Color(0x000080)/*Color.navy*/);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("separator", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("operator", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.green.darker());
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("comment", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, new Color(0xA020F0).darker()/*Color.purple*/);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("preprocessor", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("whitespace", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.red);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("error", style);

        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.orange);
        StyleConstants.setBold(style, m_font.isBold());
        StyleConstants.setItalic(style, false);
        styles.put("unknown", style);
        
        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.GREEN.darker());
        StyleConstants.setBold(style, true);
        StyleConstants.setItalic(style, false);
        styles.put("specComment", style);
        
        style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, m_font.getFamily());
        StyleConstants.setFontSize(style, m_font.getSize());
        StyleConstants.setForeground(style, Color.blue);
        StyleConstants.setBold(style, true);
        StyleConstants.setItalic(style, false);
        styles.put("specReservedWord", style);
        
        setStyleBackground( getBackground() );
    }

    private void setStyleBackground( Color bg ) {
    	for( SimpleAttributeSet style : styles.values() ) {
    		StyleConstants.setBackground( style, bg );
    	}
    }
    
    /**
     * Just like a DefaultStyledDocument but intercepts inserts and
     * removes to color them.
     */
    private class HighLightedDocument extends DefaultStyledDocument {
    	
    	private boolean isBeingHighlighted = false;
    	
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            synchronized (doclock){
                super.insertString(offs, str, a);
                color(offs, str.length());
                documentReader.update(offs, str.length());
            }
        }

		public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        	synchronized (doclock){
        		super.replace(offset, length, text, attrs);
        	}
		}

		public void remove(int offs, int len) throws BadLocationException {
            synchronized (doclock){
                super.remove(offs, len);
                color(offs, -len);
                documentReader.update(offs, -len);
            }
        }
        
        protected void fireUndoableEditUpdate(UndoableEditEvent e) {
        	if( !isBeingHighlighted ) {
        		super.fireUndoableEditUpdate(e);
        	}
        }
        
        public void setCharacterAttributes(int offset, int length, AttributeSet s, boolean replace) {
        	isBeingHighlighted = true;
//        	db.p("setCharacterAttributes: " 
//        			+ "offset " + offset + "\n"
//        			+ "length " + length + "\n"
//        			+ "s " + s + "\n"
//        			+ "replace " + replace + "\n");
        	synchronized (doclock){
        		super.setCharacterAttributes(offset, length, s, replace);
        	}
        	isBeingHighlighted= false;
        }
    }

    public void destroy() {
    	if( colorer != null && colorer.isRunning() ) {
    		colorer.setRunning( false );
    		colorer = null;
    	}
    	
    	if( document != null ) {
    		document = null;
    	}
    	
    	if( documentReader != null ) {
    		documentReader = null;
    	}
    	
    	if( syntaxLexer != null ) {
    		syntaxLexer = null;
    	}
    }
    
    public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane areaScrollPane = new JScrollPane(new JavaColoredTextPane());
		frame.getContentPane().add( areaScrollPane );
		frame.setSize( 500, 350 );
		frame.setVisible( true );
	}
    

    /**
     * A class to support highlighting of parenthesis.  To use it, add it as a
     * caret listener to your text component.
     * 
     * It listens for the location of the dot.  If the character before the dot
     * is a close paren, it finds the matching start paren and highlights both
     * of them.  Otherwise it clears the highlighting.
     *
     * This object can be shared among multiple components.  It will only
     * highlight one at a time.
     **/
    static class BracketMatcher implements CaretListener
    {
    	/** The tags returned from the highlighter, used for clearing the
            current highlight. */
    	Object start, end;

    	/** The last highlighter used */
    	Highlighter highlighter;

    	/** Used to paint good parenthesis matches */
    	Highlighter.HighlightPainter goodPainter;

    	/** Used to paint bad parenthesis matches */
    	Highlighter.HighlightPainter badPainter;

    	/** Highlights using a good painter for matched parens, and a bad
            painter for unmatched parens */
    	BracketMatcher(Highlighter.HighlightPainter goodHighlightPainter,
    			Highlighter.HighlightPainter badHighlightPainter)
    			{
    		this.goodPainter = goodHighlightPainter;
    		this.badPainter = badHighlightPainter;
    			}

    	/** A BracketMatcher with the default highlighters (cyan and magenta) */
    	BracketMatcher()
    	{
    		this(new DefaultHighlighter.DefaultHighlightPainter(Color.cyan),
    				new DefaultHighlighter.DefaultHighlightPainter(Color.magenta));
    	}

    	public void clearHighlights()
    	{
    		if(highlighter != null) {
    			if(start != null)
    				highlighter.removeHighlight(start);
    			if(end != null)
    				highlighter.removeHighlight(end);
    			start = end = null;
    			highlighter = null;
    		}
    	}

    	/** Returns the character at position p in the document*/
    	public static char getCharAt(Document doc, int p) 
    	throws BadLocationException
    	{
    		return doc.getText(p, 1).charAt(0);
    	}

    	/** Returns the position of the matching parenthesis (bracket,
    	 * whatever) for the character at paren.  It counts all kinds of
    	 * brackets, so the "matching" parenthesis might be a bad one.  For
    	 * this demo, we're not going to take quotes or comments into account
    	 * since that's not the point.
    	 * 
    	 * It's assumed that paren is the position of some parenthesis
    	 * character
    	 * 
    	 * @return the position of the matching paren, or -1 if none is found 
    	 **/
    	public static int findMatchingParen(Document d, int paren) 
    	throws BadLocationException
    	{
    		int parenCount = 1;
    		int i = paren-1;
    		for(; i >= 0; i--) {
    			char c = getCharAt(d, i);
    			switch(c) {
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
    			if(parenCount == 0)
    				break;
    		}
    		return i;
    	}

    	/** Called whenever the caret moves, it updates the highlights */
    	public void caretUpdate(CaretEvent e)
    	{
    		clearHighlights();
    		JTextComponent source = (JTextComponent) e.getSource();
    		highlighter = source.getHighlighter();
    		Document doc = source.getDocument();
    		if(e.getDot() == 0) {
    			return;
    		}

    		// The character we want is the one before the current position
    		int closeParen = e.getDot()-1;
    		try {
    			char c = getCharAt(doc, closeParen);
    			if(c == ')' ||
    					c == ']' ||
    					c == '}') {
    				int openParen = findMatchingParen(doc, closeParen);
    				if(openParen >= 0) {
    					char c2 = getCharAt(doc, openParen);
    					if((c2 == '(' && c == ')') ||
    							(c2 == '{' && c == '}') ||
    							(c2 == '[' && c == ']')) {
    						start = highlighter.addHighlight(openParen,
    								openParen+1,
    								goodPainter);
    						end = highlighter.addHighlight(closeParen,
    								closeParen+1,
    								goodPainter);
    					}
    					else {
    						start = highlighter.addHighlight(openParen,
    								openParen+1,
    								badPainter);
    						end = highlighter.addHighlight(closeParen,
    								closeParen+1,
    								badPainter);
    					}
    				}
    				else {
    					end = highlighter.addHighlight(closeParen,
    							closeParen+1,
    							badPainter);		
    				}

    			}
    		}
    		catch(BadLocationException ex) {
    			throw new Error(ex);
    		}
    	}

    }
}

/**
 * A reader interface for an abstract document.  Since
 * the syntax highlighting packages only accept Stings and
 * Readers, this must be used.
 * Since the close() method does nothing and a seek() method
 * has been added, this allows us to get some performance
 * improvements through reuse.  It can be used even after the
 * lexer explicitly closes it by seeking to the place that
 * we want to read next, and reseting the lexer.
 */
class DocumentReader extends Reader {

    /**
     * Modifying the document while the reader is working is like
     * pulling the rug out from under the reader.  Alerting the
     * reader with this method (in a nice thread safe way, this
     * should not be called at the same time as a read) allows
     * the reader to compensate.
     */
    public void update(int position, int adjustment){
        if (position < this.position){
            if (this.position < position - adjustment){
                this.position = position;
            } else {
                this.position += adjustment;
            }
        }
    }

    /**
     * Current position in the document. Incremented
     * whenever a character is read.
     */
    private long position = 0;

    /**
     * Saved position used in the mark and reset methods.
     */
    private long mark = -1;

    /**
     * The document that we are working with.
     */
    private AbstractDocument document;

    /**
     * Construct a reader on the given document.
     *
     * @param document the document to be read.
     */
    public DocumentReader(AbstractDocument document){
        this.document = document;
    }

    /**
     * Has no effect.  This reader can be used even after
     * it has been closed.
     */
    public void close() {
    }

    /**
     * Save a position for reset.
     *
     * @param readAheadLimit ignored.
     */
    public void mark(int readAheadLimit){
        mark = position;
    }

    /**
     * This reader support mark and reset.
     *
     * @return true
     */
    public boolean markSupported(){
        return true;
    }

    /**
     * Read a single character.
     *
     * @return the character or -1 if the end of the document has been reached.
     */
    public int read(){
        if (position < document.getLength()){
            try {
                char c = document.getText((int)position, 1).charAt(0);
                position++;
                return c;
            } catch (BadLocationException x){
                return -1;
            }
        }
		return -1;
    }

    /**
     * Read and fill the buffer.
     * This method will always fill the buffer unless the end of the document is reached.
     *
     * @param cbuf the buffer to fill.
     * @return the number of characters read or -1 if no more characters are available in the document.
     */
    public int read(char[] cbuf){
        return read(cbuf, 0, cbuf.length);
    }

    /**
     * Read and fill the buffer.
     * This method will always fill the buffer unless the end of the document is reached.
     *
     * @param cbuf the buffer to fill.
     * @param off offset into the buffer to begin the fill.
     * @param len maximum number of characters to put in the buffer.
     * @return the number of characters read or -1 if no more characters are available in the document.
     */
    public int read(char[] cbuf, int off, int len){
        if (position < document.getLength()){
            int length = len;
            if (position + length >= document.getLength()){
                length = document.getLength() - (int)position;
            }
            if (off + length >= cbuf.length){
                length = cbuf.length - off;
            }
            try {
                String s = document.getText((int)position, length);
                position += length;
                for (int i=0; i<length; i++){
                    cbuf[off+i] = s.charAt(i);
                }
                return length;
            } catch (BadLocationException x){
                return -1;
            }
        }
		return -1;
    }

    /**
     * @return true
     */
    public boolean ready() {
        return true;
    }

    /**
     * Reset this reader to the last mark, or the beginning of the document if a mark has not been set.
     */
    public void reset(){
        if (mark == -1){
            position = 0;
        } else {
            position = mark;
        }
        mark = -1;
    }

    /**
     * Skip characters of input.
     * This method will always skip the maximum number of characters unless
     * the end of the file is reached.
     *
     * @param n number of characters to skip.
     * @return the actual number of characters skipped.
     */
    public long skip(long n){
        if (position + n <= document.getLength()){
            position += n;
            return n;
        }
		long oldPos = position;
		position = document.getLength();
		return (document.getLength() - oldPos);
    }

    /**
     * Seek to the given position in the document.
     *
     * @param n the offset to which to seek.
     */
    public void seek(long n){
        if (n <= document.getLength()){
            position = n;
        } else {
            position = document.getLength();
        }
    }
}



