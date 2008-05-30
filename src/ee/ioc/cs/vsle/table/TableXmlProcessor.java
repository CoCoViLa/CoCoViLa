/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

/** 
 * Class provides methods for parsing table xml as well as saving tables, 
 * parse() method returns a map of tables by their ids.
 * 
 * @author pavelg
 */
public class TableXmlProcessor {

    private static final String XML_DOC_ROOT = "tables";
    private static final String XML_NS_URI = "cocovila";
    private DiagnosticsCollector collector = new DiagnosticsCollector();
    private File xmlFile;
    
    /**
     * @param tableFile
     */
    public TableXmlProcessor( File tableFile ) {

        if( tableFile == null ) {
            throw new TableException( "No file" );
        } 

        xmlFile = tableFile;
    }
    
    /**
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private Document getDocument() throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        factory.setNamespaceAware( true );

        Document document = factory.newDocumentBuilder().parse( xmlFile );
        
        validateDocument( document );
        
        return document;
    }
    
    /**
     * @param document
     * @throws SAXException
     * @throws IOException
     */
    private void validateDocument( Document document ) throws SAXException, IOException {
        
        URL url = FileFuncs.getResource( "table.xsd", true );
        if ( url == null ) {
            url = new URL( "http://www.cs.ioc.ee/~cocovila/dtd/table.xsd" );
        }
        
        /*
         * Use Schema and Validator objects instead (hate string-passing style)
         * 
         * factory.setValidating(true);
         * factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
         * factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", new InputSource( url.toString() ) );
        */
        
        // Create a SchemaFactory capable of understanding WXS schemas.
        SchemaFactory schemaFactory =
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // Load a WXS schema, represented by a Schema instance.
        Source schemaFile = new StreamSource( url.toString() );
        
        Schema schema = schemaFactory.newSchema(schemaFile);

        // Create a Validator object, which can be used to validate
        // an instance document.
        Validator validator = schema.newValidator();

        validator.setErrorHandler( new ErrorHandler() {

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
        
        // Validate the DOM tree.
        validator.validate( new DOMSource( document ) );
        
    }
    
    /**
     * @param table
     */
    public void save( Table table ) {
        
        try {
            Document document = null;
            
            if( !xmlFile.exists() ) {

                xmlFile.createNewFile();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.getDOMImplementation().createDocument( XML_NS_URI, XML_DOC_ROOT, null );

            } else {
                document = getDocument();
            }
            
            Element root = document.getDocumentElement();
            NodeList tables = root.getElementsByTagName( "table" );
            
            Node existingTableNode = null;
            
            for( int i = 0; i < tables.getLength(); i++ ) {
                
                if( table.getTableId().equals( tables.item( i ).getAttributes().getNamedItem( "id" ).getNodeValue() ) ) {

                    if( JOptionPane.showConfirmDialog( null, 
                            "Overwrite existing table \"" + table.getTableId() + "\" in "+ xmlFile.getName() + "?", 
                            "Overwrite table?", 
                            JOptionPane.OK_CANCEL_OPTION ) != JOptionPane.OK_OPTION ) {
                        return;
                    }
                    
                    existingTableNode = tables.item( i );
                    break;
                }
            }
            
            Node newTableNode = createTableNode( table );
            
            if( existingTableNode != null ) {
                root.replaceChild( existingTableNode, newTableNode );
            } else {
                root.appendChild( newTableNode );
            }
            
            //just in case
            validateDocument( document );
            
            // Serialize the document onto System.out
            TransformerFactory xformFactory = TransformerFactory.newInstance();  
            Transformer transformer = xformFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            Source input = new DOMSource(document);
            Result output = new StreamResult( new FileOutputStream( xmlFile ) );
            transformer.transform(input, output);
            
        } catch ( Exception e ) {
            collector.collectDiagnostic( e.getClass().getName() + " " + e.getMessage(), true );
            
        } finally {
            checkProblems( "Error saving table file " + xmlFile.getName() );
        }
        
    }
    
    private Node createTableNode( Table table ) {
        
        return null;
    }
    
    private void checkProblems( final String errorMess ) {
        
        if (collector.hasProblems()) {
            final boolean[] rv = new boolean[] { false };
            
            Runnable runnable = new Runnable() {
                public void run() {
                    rv[0] = DiagnosticsCollector.promptLoad(
                            Editor.getInstance(), collector,
                            errorMess,
                            xmlFile.getName() );
                }
            };
            
            if( SwingUtilities.isEventDispatchThread() ) {
                
                runnable.run();
                
            } else {
                try {
                    SwingUtilities.invokeAndWait( runnable );
                } catch (Exception e) {
                    db.p(e);
                }
            }
            
            if (!rv[0]) {
                throw new TableException( errorMess );
            }
        }
    }
    
    /**
     * @param tableFile absolute path to table.xml
     * @return Map<table id, table>
     */
    public Map<String, Table> parse() {
        
        Map<String, Table> tables = new LinkedHashMap<String, Table>();
        
        Document document = null;

        try {
            document = getDocument();
        } catch ( Exception e ) {
            collector.collectDiagnostic( e.getMessage(), true );
        }
        
        checkProblems( "Error parsing table file " + xmlFile.getName() );
        
        if ( document == null ) {
            throw new TableException( "Error parsing table file " + xmlFile.getName() );
        }

        Element root = document.getDocumentElement();
        
        NodeList list = root.getElementsByTagName( "table" );
        
        for (int i=0; i<list.getLength(); i++) {
            Table t =  createTable( (Element)list.item(i) );
            tables.put( t.getTableId(), t );
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

                        table.addDataCell( rowId, colId, 
                                ( cell.getChildNodes().getLength() == 0 ) 
                                    //<cell id="..."/> 
                                    ? null 
                                    //<cell id="...">text</cell>
                                    : cell.getTextContent() );
                    }
                }
            }
        }
    }
    
}
