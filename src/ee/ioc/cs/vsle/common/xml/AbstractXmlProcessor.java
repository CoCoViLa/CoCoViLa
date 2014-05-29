/**
 * 
 */
package ee.ioc.cs.vsle.common.xml;

import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.xml.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;

import org.w3c.dom.*;
import org.w3c.dom.ls.*;
import org.xml.sax.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public abstract class AbstractXmlProcessor implements DiagnosticsCollector.Diagnosable {

    protected DiagnosticsCollector collector = new DiagnosticsCollector();
    protected File xmlFile;
    protected final String XML_DOC_ROOT;
    protected final String XML_NS_URI;
    protected final String XML_SCHEMA;
    protected ErrorHandler ERROR_HANDLER = new ErrorHandler() {

        @Override
        public void error(SAXParseException exception) {
            collector.collectDiagnostic( "Error: " + exception.getMessage(), true );
        }

        @Override
        public void fatalError(SAXParseException exception) {
            collector.collectDiagnostic( "Fatal Error: " + exception.getMessage(), true );
        }

        @Override
        public void warning(SAXParseException exception) {
            collector.collectDiagnostic( "Warning: " + exception.getMessage(), false );
        }
    };
    
    protected AbstractXmlProcessor(File xmlFile, String ns, String root, String schemaFileName) {
        
        if( xmlFile == null ) {
            reportError( "No file" );
        } 

        this.xmlFile = xmlFile;
        
        XML_NS_URI = ns;
        XML_DOC_ROOT = root;
        XML_SCHEMA = schemaFileName;
    }
    
    /**
     * Parses and validates xml document
     * 
     * @return document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    protected Document getDocument() throws ParserConfigurationException, SAXException, IOException {

        Document document = getDocBuilder().parse( xmlFile );
        validateDocument( document );
        return document;
    }
    
    protected DocumentBuilderFactory getDocBuilderFactory() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( XML_NS_URI != null );
        return factory;
    }
    
    protected DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        DocumentBuilder builder = getDocBuilderFactory().newDocumentBuilder();
        builder.setEntityResolver( getEntityResolver() );
        return builder;
    }
    
    protected String getPath() {
        return xmlFile.getParent() + File.separator;
    }
    
    protected abstract EntityResolver getEntityResolver();
    
    protected ErrorHandler getErrorHandler() {
        return ERROR_HANDLER;
    }
    
    /**
     * Validates the structure
     * 
     * @param document
     * @throws SAXException
     * @throws IOException
     */
    protected void validateDocument( Document document ) throws SAXException, IOException {
        
        URL url = FileFuncs.getResource( XML_SCHEMA, true );
        if ( url == null ) {
            url = new URL( RuntimeProperties.SCHEMA_LOC + XML_SCHEMA );
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

        validator.setErrorHandler( getErrorHandler() );
        
        // Validate the DOM tree.
        validator.validate( new DOMSource( document ) );
        
    }
    
    /**
     * Creates new xml document with corresponding namespace
     * 
     * @return document
     * @throws ParserConfigurationException
     */
    protected Document createNewDocument() throws ParserConfigurationException {
        
        return getDocBuilderFactory().newDocumentBuilder().getDOMImplementation().createDocument( XML_NS_URI, XML_DOC_ROOT, null );
    }
    
    protected abstract Object parse();
    
    /**
     * Returns the list of diagnostic messages generated.
     * @return diagnostic messages
     */
    @Override
    public DiagnosticsCollector getDiagnostics() {
        return collector;
    }
    
    /**
     * Checks if there are any problems
     * 
     * @param errorMess
     */
    protected void checkProblems( final String errorMess ) {
        
        if (collector.hasProblems()) {
            final boolean[] rv = new boolean[] { false };
            
            Runnable runnable = new Runnable() {
                @Override
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
                reportError(errorMess);
            }
        }
    }
    
    protected abstract void reportError(String message);
    
    protected boolean writeDocument( Document document, OutputStream out ) {
        
        try {
            //Q: why bother? A: DOM3 L/S API should be faster
            if ( ( document.getFeature( "Core", "3.0" ) != null ) 
                    && ( document.getFeature( "LS", "3.0" ) != null ) ) {

                DOMImplementationLS domLS  = (DOMImplementationLS) ( document.getImplementation() ).getFeature( "LS", "3.0" );

                LSOutput lso = domLS.createLSOutput();
                lso.setSystemId( XML_SCHEMA );
                lso.setByteStream( out );

                LSSerializer lss = domLS.createLSSerializer();
                lss.getDomConfig().setParameter( "format-pretty-print", true );
                lss.write( document, lso );
                
            } else {
                
                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer trans = transfac.newTransformer();
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, XML_SCHEMA);
                trans.transform(new DOMSource(document), new StreamResult(out));
            }
            /* this is also fast, but deprecated by xerces
                OutputFormat format = new OutputFormat(document, "UTF-8", true);
                format.setIndenting(true);
                format.setDoctype( null, XML_SCHEMA );
                XMLSerializer serializer = new XMLSerializer(out, format);
                serializer.asDOMSerializer();
                serializer.serialize(document);
             */
        } catch ( Exception e ) {
            return false;
        }  finally {
            try {
                out.close();
            } catch (IOException e) {
                // ignore
            }
        }
        
        return true;
    }
}
