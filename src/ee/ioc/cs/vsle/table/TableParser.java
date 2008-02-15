package ee.ioc.cs.vsle.table;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;


/**
 * Class provides static methods for parsing table xml, 
 * parse() method takes table file name as input 
 * and returns a set of tables by their ids.
 */
class TableParser {

    //TODO add functionality for saving tables modified via GUI
    
    /**
     * @param tableFile absolute path to table.xml
     * @return Map<table id, table>
     */
    static Map<String, Table> parse( final File tableFile ) {
        
        Map<String, Table> tables = new HashMap<String, Table>();
        
        if( tableFile == null || !tableFile.exists() ) {
            return tables;
        }
        
        final DiagnosticsCollector collector = new DiagnosticsCollector();
        
        Document document = null;
        
        try {
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

            URL url = FileFuncs.getResource( "table.xsd", true );
            if ( url == null ) {
                url = new URL( "http://www.cs.ioc.ee/~cocovila/dtd/table.xsd" );
            }
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", new InputSource( url.toString() ) );
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            builder.setErrorHandler( new ErrorHandler() {

                public void error(SAXParseException exception) {
                    
                    collector.collectDiagnostic( "Error: " + exception.getMessage(), true );
                }

                public void fatalError(SAXParseException exception) {
                    
                    collector.collectDiagnostic( "Fatal Error: " + exception.getMessage(), true );
                }

                public void warning(SAXParseException exception) {
                    
                    collector.collectDiagnostic( "Warning: " +exception.getMessage(), false );
                }
            } );
            
            document = builder.parse( tableFile );
            
        } catch ( ParserConfigurationException e ) {
            collector.collectDiagnostic( "ParserConfigurationException: " + e.getMessage(), true );
        } catch ( IOException e ) {
            collector.collectDiagnostic( "IOException: " + e.getMessage(), true );
        } catch ( SAXException e ) {
            collector.collectDiagnostic( "SAXException: " + e.getMessage(), true );
        }
        
        if( collector.hasProblems() 
                && !DiagnosticsCollector.promptLoad( Editor.getInstance(), collector, 
                        "Error parsing table file " + tableFile.getName(), tableFile.getName() ) || document == null ) {
            
            throw new TableException( "Error parsing table file " + tableFile.getName() );
        }
        
        Element root = document.getDocumentElement();
        
        NodeList list = root.getElementsByTagName( "table" );
        
        for (int i=0; i<list.getLength(); i++) {
            Table t =  createTable( (Element)list.item(i) );
            tables.put( t.getId(), t );
         }
        
        return tables;
    }
    
    /**
     * Iterates through nodes and parses each element using corresponding method
     *  
     * @param tableRoot Element
     * @return Table object
     * @throws TableException
     */
    private static Table createTable( Element tableRoot ) throws TableException {
        
        Table table = new Table( tableRoot.getAttribute( "id" ) );
        
        NodeList nodes = tableRoot.getChildNodes();
        
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if( "input".equals( node.getNodeName() ) ) {
                
                table.addInputFields( parseVariables( node.getChildNodes() ) );
                
            } else if ( "output".equals( node.getNodeName() )  ) {
                
                table.setOutputField( parseVariables( node.getChildNodes() ).iterator().next() );

            } else if ( "hrules".equals( node.getNodeName() )  ) {
                
                table.addHRules( parseRules( node.getChildNodes(), table ) );
                
            } else if ( "vrules".equals( node.getNodeName() )  ) {
                
                table.addVRules( parseRules( node.getChildNodes(), table ) );
                
            } else if ( "data".equals( node.getNodeName() )  ) {
                
                parseData( node.getChildNodes(), table );
            }
         }
        
        return table;
    }
    
    /**
     * Parses variables (inputs and outputs)
     * 
     * @param list
     * @return
     */
    private static Set<TableField> parseVariables( NodeList list ) {

        Set<TableField> vars = new LinkedHashSet<TableField>();
        
        for ( int j = 0; j < list.getLength(); j++ ) {
            Node var = list.item( j );
            if ( var.getNodeType() == Node.ELEMENT_NODE ) {
                NamedNodeMap attrs = var.getAttributes();
                
                vars.add( new TableField( attrs.getNamedItem( "id" ).getNodeValue(), attrs.getNamedItem( "type" ).getNodeValue() ) );
            }
        }
        
        return vars;
    }

    /**
     * Parses rules (horizontal and vertical)
     * 
     * @param ruleNodes
     * @param table
     * @return
     * @throws TableException
     */
    private static Set<Rule> parseRules( NodeList ruleNodes, Table table ) throws TableException {

        Set<Rule> rules = new LinkedHashSet<Rule>();
        
        for ( int j = 0; j < ruleNodes.getLength(); j++ ) {
            Node ruleNode = ruleNodes.item( j );
            if ( ruleNode.getNodeType() == Node.ELEMENT_NODE ) {
                NamedNodeMap attrs = ruleNode.getAttributes();
                
                Rule rule;
                
                try {
                    rule = Rule.createRule( 
                            table.getInput( attrs.getNamedItem( "var" ).getNodeValue() ), 
                            attrs.getNamedItem( "cond" ).getNodeValue(), 
                            attrs.getNamedItem( "value" ).getNodeValue() );
                    
                } catch ( Exception e ) {
                    throw new TableException( e );
                }
                
                rules.add( rule );
                
                NodeList entries = ruleNode.getChildNodes();
                
                for ( int k = 0; k < entries.getLength(); k++ ) {
                    Node entry = entries.item( k );
                    
                    if( entry.getNodeType() == Node.ELEMENT_NODE ) {
                        NamedNodeMap eattrs = entry.getAttributes();
                        rule.addEntry( Integer.parseInt( eattrs.getNamedItem( "id" ).getNodeValue() ) );
                    }
                }
            }
        }
        
        return rules;
    }
    
    /**
     * Parses data table
     * 
     * @param dataNodes
     * @param table
     * @throws TableException
     */
    private static void parseData( NodeList dataNodes, Table table ) throws TableException {

        for ( int j = 0; j < dataNodes.getLength(); j++ ) {
            Node row = dataNodes.item( j );
            if ( row.getNodeType() == Node.ELEMENT_NODE ) {
                NamedNodeMap attrs = row.getAttributes();
                int rowId = Integer.parseInt( attrs.getNamedItem( "id" ).getNodeValue() );
                NodeList cells = row.getChildNodes();
                
                for ( int k = 0; k < cells.getLength(); k++ ) {
                    Node cell = cells.item( k );
                    
                    if( cell.getNodeType() == Node.ELEMENT_NODE ) {
                        NamedNodeMap cellAttrs = cell.getAttributes();
                        int colId = Integer.parseInt( cellAttrs.getNamedItem( "id" ).getNodeValue() );
                        table.addDataCell( new Table.DataCell( rowId, colId, cell.getTextContent() ) );
                    }
                }
            }
        }
    }

    /**
     * Main method for testing
     * 
     * @param args
     */
    public static void main( String[] args ) {
        
//      try {
//        Map<String, Table> tables = parse( "table.xml" );
        
//        Table t = tables.get( "multiplication" );
//        
//        Random r = new Random();
//        
//        int x = 1 + r.nextInt( 9 );
//        int y = 1 + r.nextInt( 9 );
//        
//            
//            Object o =  t.queryTable( new Object[] { x, y } );
//            
//            System.out.println( x + " * " + y + " = " + o );
//            
//        } catch ( TableException e ) {
//            e.printStackTrace();
//        }
        
        try {
            
            Map<String, Table> tables = parse( new File( "table_test.xml" ) );
            
            Table t = tables.get( "test" );
            
            Object o =  t.queryTable( new Object[] { 5, 30, true } );
            
            System.out.println( "From table: " + o );
            
        } catch ( TableException e ) {
            System.err.println( e.getMessage() );
//            e.printStackTrace();
        }
    }
}
