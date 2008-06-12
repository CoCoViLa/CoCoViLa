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

    private static final String TBL_ELEM_ENTRY = "entry";
    private static final String TBL_ELEM_RULE = "rule";
    private static final String TBL_ELEM_ROOT = "table";
    private static final String TBL_ATTR_VALUE = "value";
    private static final String TBL_ATTR_COND = "cond";
    private static final String TBL_ELEM_VAR = "var";
    private static final String TBL_ATTR_VAR = "var";
    private static final String TBL_ATTR_TYPE = "type";
    private static final String TBL_ELEM_DATA = "data";
    private static final String TBL_ELEM_VRULES = "vrules";
    private static final String TBL_ELEM_HRULES = "hrules";
    private static final String TBL_ELEM_OUTPUT = "output";
    private static final String TBL_ELEM_INPUT = "input";
    private static final String TBL_ATTR_ID = "id";
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
     * Parses and validates table xml document
     * 
     * @return document
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
     * Validates table structure
     * 
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
     * Creates new table xml document with corresponding namespace
     * 
     * @return document
     * @throws ParserConfigurationException
     */
    private Document createNewDocument() throws ParserConfigurationException {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.getDOMImplementation().createDocument( XML_NS_URI, XML_DOC_ROOT, null );
    }
    
    /**
     * Saves the table in xml format
     * 
     * @param table
     */
    public void save( Table table, boolean askOnOverwrite ) {
        
        try {
            Document document;
            
            if( !xmlFile.exists() ) {
                xmlFile.createNewFile();
            } 
            
            if( xmlFile.length() == 0 ) {
                document = createNewDocument();
            } else {
                
                try {
                    document = getDocument();
                } catch( Exception e ) {
                    
                    if( JOptionPane.showConfirmDialog( null,
                            "Unable to parse table content, overwrite " + xmlFile.getName() + "?", 
                            "Error reading table file", 
                            JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.OK_OPTION ) {
                        
                        collector.clearMessages();
                        
                        document = createNewDocument();
                    } else {
                        return;
                    }
                }
            }
            
            Element root = document.getDocumentElement();
            NodeList tables = root.getElementsByTagNameNS( XML_NS_URI, TBL_ELEM_ROOT );
            
            Node existingTableNode = null;
            
            for( int i = 0; i < tables.getLength(); i++ ) {
                
                if( table.getTableId().equals( tables.item( i ).getAttributes().getNamedItem( TBL_ATTR_ID ).getNodeValue() ) ) {

                    if( askOnOverwrite && JOptionPane.showConfirmDialog( null, 
                            "Overwrite existing table \"" + table.getTableId() + "\" in "+ xmlFile.getName() + "?", 
                            "Overwrite table?", 
                            JOptionPane.OK_CANCEL_OPTION ) != JOptionPane.OK_OPTION ) {
                        return;
                    }
                    
                    existingTableNode = tables.item( i );
                    
                    break;
                }
            }
            
            Node newTableNode = createTableNode( table, document );
            
            if( existingTableNode != null ) {
                root.replaceChild( newTableNode, existingTableNode );
            } else {
                root.appendChild( newTableNode );
            }
            
            //just in case
            validateDocument( document );
            
            if( !collector.hasProblems() ) {
                //transform and save into file
                TransformerFactory xformFactory = TransformerFactory.newInstance();  
                Transformer transformer = xformFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT,"yes");
                Source input = new DOMSource(document);
                Result output = new StreamResult( new FileOutputStream( xmlFile ) );
                transformer.transform(input, output);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            collector.collectDiagnostic( e.getClass().getName() + " " + e.getMessage(), true );
            
        } finally {
            checkProblems( "Error saving table file " + xmlFile.getName() );
        }
        
    }
    
    /**
     * Creates table node
     * 
     * @param table
     * @param document
     * @return
     */
    private Node createTableNode( Table table, Document document ) {
        
        Element tableNode = document.createElementNS( XML_NS_URI, TBL_ELEM_ROOT );
        tableNode.setAttribute( TBL_ATTR_ID, table.getTableId() );
        
        //save input vars
        Element inputNode = document.createElementNS( XML_NS_URI,TBL_ELEM_INPUT );
        tableNode.appendChild( inputNode );
        
        for( TableField input : table.getInputFields() ) {
            
            inputNode.appendChild( createVarNode( input, document ) );
        }
        
        //save output var
        Element outputNode = document.createElementNS( XML_NS_URI,TBL_ELEM_OUTPUT );
        tableNode.appendChild( outputNode );
        outputNode.appendChild( createVarNode( table.getOutputField(), document ) );
        
        //save rules
        Element hRulesNode = document.createElementNS( XML_NS_URI,TBL_ELEM_HRULES );
        createRuleNodesAndAppend( table.getHRules(), document, hRulesNode );
        tableNode.appendChild( hRulesNode );
        
        Element vRulesNode = document.createElementNS( XML_NS_URI,TBL_ELEM_VRULES );
        createRuleNodesAndAppend( table.getVRules(), document, vRulesNode );
        tableNode.appendChild( vRulesNode );
        
        //save data table
        tableNode.appendChild( createDataNode( table, document ) );
        
        return tableNode;
    }
    
    /**
     * Creates var node
     * 
     * @param field
     * @param doc
     * @return
     */
    private Node createVarNode( TableField field, Document doc ) {
        Element var = doc.createElementNS( XML_NS_URI,TBL_ELEM_VAR );
        var.setAttribute( TBL_ATTR_ID, field.getId() );
        var.setAttribute( TBL_ATTR_TYPE, field.getType() );
        
        return var;
    }
    
    /**
     * Creates and appends rule nodes
     * 
     * @param rules
     * @param doc
     * @param rulesNode
     */
    private void createRuleNodesAndAppend( List<Rule> rules, Document doc, Node rulesNode ) {
        
        for ( Rule rule : rules ) {
            Element ruleNode = doc.createElementNS( XML_NS_URI,TBL_ELEM_RULE );
            ruleNode.setAttribute( TBL_ATTR_COND, rule.getConditionString() );
            ruleNode.setAttribute( TBL_ATTR_VALUE, TypeUtil.toTokenString( rule.getValue() ) );
            ruleNode.setAttribute( TBL_ATTR_VAR, rule.getField().getId() );
            
            //entries
            for ( Integer entry : rule.getEntries() ) {
                Element entryNode = doc.createElementNS( XML_NS_URI,TBL_ELEM_ENTRY );
                entryNode.setAttribute( TBL_ATTR_ID, entry.toString() );
                ruleNode.appendChild( entryNode );
            }
            
            rulesNode.appendChild( ruleNode );
        }
    }
    
    /**
     * Creates a node for the data table
     * 
     * @param tbl
     * @param doc
     * @return
     */
    private Node createDataNode( Table tbl, Document doc ) {
        
        Element dataNode = doc.createElementNS( XML_NS_URI,TBL_ELEM_DATA );
        
        List<Integer> rowIds = tbl.getOrderedRowIds();
        List<Integer> colIds = tbl.getOrderedColumnIds();
        
        for ( int i = 0, rowCount = rowIds.size(); i < rowCount; i++ ) {
            
            Element rowNode = doc.createElementNS( XML_NS_URI,"row" );
            rowNode.setAttribute( TBL_ATTR_ID, rowIds.get( i ).toString() );
            
            for ( int j = 0, colCount = colIds.size(); j < colCount; j++ ) {
                
                Element cellNode = doc.createElementNS( XML_NS_URI,"cell" );
                cellNode.setAttribute( TBL_ATTR_ID, colIds.get( j ).toString() );
                
                Object value = tbl.getCellValueAt( i, j );
                
                if( value != null ) {
                    cellNode.setTextContent( value.toString() );
                }
                
                rowNode.appendChild( cellNode );
            }
            
            dataNode.appendChild( rowNode );
        }
        
        return dataNode;
    }
    
    /**
     * Checks if there are any problems
     * 
     * @param errorMess
     */
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
     * Parses the table xml from a given file
     * 
     * @param tableFile absolute path to table.xml
     * @return Map<table id, table>
     */
    public Map<String, Table> parse() {
        
        Map<String, Table> tables = new LinkedHashMap<String, Table>();
        
        try {
            Document document = getDocument();

            Element root = document.getDocumentElement();

            NodeList list = root.getElementsByTagNameNS( XML_NS_URI, TBL_ELEM_ROOT );

            for (int i=0; i<list.getLength(); i++) {
                Table t =  createTable( (Element)list.item(i) );
                tables.put( t.getTableId(), t );
            }

        } catch ( Exception e ) {
            collector.collectDiagnostic( e.getMessage(), true );
        } finally {
            checkProblems( "Error parsing table file " + xmlFile.getName() );
        }
        
        return tables;
    }
    
    /**
     * Iterates through nodes and parses each element using corresponding methods
     *  
     * @param tableRoot Element
     * @return Table object
     * @throws TableException
     */
    private Table createTable( Element tableRoot ) throws TableException {
        
        Table table = new Table( tableRoot.getAttribute( TBL_ATTR_ID ) );
        
        NodeList nodes = tableRoot.getChildNodes();
        
        for (int i=0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if( TBL_ELEM_INPUT.equals( node.getNodeName() ) ) {
                
                table.addInputFields( parseVariables( node.getChildNodes() ) );
                
            } else if ( TBL_ELEM_OUTPUT.equals( node.getNodeName() )  ) {
                
                table.setOutputField( parseVariables( node.getChildNodes() ).iterator().next() );

            } else if ( TBL_ELEM_HRULES.equals( node.getNodeName() )  ) {
                
                table.addHRules( parseRules( node.getChildNodes(), table ) );
                
            } else if ( TBL_ELEM_VRULES.equals( node.getNodeName() )  ) {
                
                table.addVRules( parseRules( node.getChildNodes(), table ) );
                
            } else if ( TBL_ELEM_DATA.equals( node.getNodeName() )  ) {
                
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
    private Set<TableField> parseVariables( NodeList list ) {

        Set<TableField> vars = new LinkedHashSet<TableField>();
        
        for ( int j = 0; j < list.getLength(); j++ ) {
            Node var = list.item( j );
            if ( var.getNodeType() == Node.ELEMENT_NODE ) {
                NamedNodeMap attrs = var.getAttributes();
                
                vars.add( new TableField( attrs.getNamedItem( TBL_ATTR_ID ).getNodeValue(), attrs.getNamedItem( TBL_ATTR_TYPE ).getNodeValue() ) );
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
    private Set<Rule> parseRules( NodeList ruleNodes, Table table ) throws TableException {

        Set<Rule> rules = new LinkedHashSet<Rule>();
        
        for ( int j = 0; j < ruleNodes.getLength(); j++ ) {
            Node ruleNode = ruleNodes.item( j );
            if ( ruleNode.getNodeType() == Node.ELEMENT_NODE ) {
                NamedNodeMap attrs = ruleNode.getAttributes();
                
                Rule rule;
                
                try {
                    rule = Rule.createRule( 
                            table.getInput( attrs.getNamedItem( TBL_ATTR_VAR ).getNodeValue() ), 
                            attrs.getNamedItem( TBL_ATTR_COND ).getNodeValue(), 
                            attrs.getNamedItem( TBL_ATTR_VALUE ).getNodeValue() );
                    
                } catch ( Exception e ) {
                    throw new TableException( e );
                }
                
                rules.add( rule );
                
                NodeList entries = ruleNode.getChildNodes();
                
                for ( int k = 0; k < entries.getLength(); k++ ) {
                    Node entry = entries.item( k );
                    
                    if( entry.getNodeType() == Node.ELEMENT_NODE ) {
                        NamedNodeMap eattrs = entry.getAttributes();
                        rule.addEntry( Integer.parseInt( eattrs.getNamedItem( TBL_ATTR_ID ).getNodeValue() ) );
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
    private void parseData( NodeList dataNodes, Table table ) throws TableException {

        for ( int j = 0; j < dataNodes.getLength(); j++ ) {
            Node row = dataNodes.item( j );
            if ( row.getNodeType() == Node.ELEMENT_NODE ) {
                NamedNodeMap attrs = row.getAttributes();
                int rowId = Integer.parseInt( attrs.getNamedItem( TBL_ATTR_ID ).getNodeValue() );
                NodeList cells = row.getChildNodes();
                
                for ( int k = 0; k < cells.getLength(); k++ ) {
                    Node cell = cells.item( k );
                    
                    if( cell.getNodeType() == Node.ELEMENT_NODE ) {
                        NamedNodeMap cellAttrs = cell.getAttributes();
                        int colId = Integer.parseInt( cellAttrs.getNamedItem( TBL_ATTR_ID ).getNodeValue() );

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
