package ee.ioc.cs.vsle.packageparse;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import ee.ioc.cs.vsle.common.xml.*;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.graphics.Image;
import ee.ioc.cs.vsle.graphics.Polygon;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.graphics.Text;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * This class allows to parse and read packages and also to write into a package
 * Uses DOM for handling XML
 * 
 * @author pavelg
 */
public class PackageXmlProcessor extends AbstractXmlProcessor {

    private static final String ATR_ARC_ANGLE = "arcAngle";
    private static final String ATR_START_ANGLE = "startAngle";
    private static final String EL_PAINTER = "painter";
    private static final String EL_ICON = "icon";
    private static final String ATR_Y2 = "y2";
    private static final String ATR_Y1 = "y1";
    private static final String ATR_X2 = "x2";
    private static final String ATR_X1 = "x1";
    private static final String ATR_LINETYPE = "linetype";
    private static final String ATR_FONTSIZE = "fontsize";
    private static final String ATR_FONTSTYLE = "fontstyle";
    private static final String ATR_FONTNAME = "fontname";
    private static final String VAL_F = "f";
    private static final String VAL_RF = "rf";
    private static final String ATR_STRING = "string";
    private static final String EL_BOUNDS = "bounds";
    private static final String ATR_TRANSPARENCY = "transparency";
    private static final String ATR_STROKE = "stroke";
    private static final String ATR_FILLED = "filled";
    private static final String ATR_COLOUR = "colour";
    private static final String ATR_HEIGHT = "height";
    private static final String ATR_WIDTH = "width";
    private static final String EL_RECT = "rect";
    private static final String EL_OVAL = "oval";
    private static final String EL_ARC = "arc";
    private static final String EL_LINE = "line";
    private static final String EL_TEXT = "text";
    private static final String EL_IMAGE = "image";
    private static final String ATR_PATH = "path";
    private static final String ATR_FIXED = "fixed";
    private static final String ATR_DESCRIPTION = "description";
    private static final String EL_DESCRIPTION = "description";
    private static final String ATR_VALUE = "value";
    private static final String EL_FIELD = "field";
    private static final String EL_POINT = "point";
    private static final String EL_POLYGON = "polygon";
    private static final String EL_FIELDS = "fields";
    private static final String EL_CLOSED = "closed";
    private static final String EL_KNOWN = "known";
    private static final String EL_DEFAULT = "default";
    private static final String EL_OPEN = "open";
    private static final String ATR_STRICT = "strict";
    private static final String ATR_MULTI = "multi";
    private static final String ATR_PORT_CONNECTION = "portConnection";
    private static final String ATR_Y = "y";
    private static final String VAL_TRUE = "true";
    private static final String ATR_X = "x";
    private static final String ATR_NAME = "name";
    private static final String EL_NAME = "name";
    private static final String ATR_ID = "id";
    private static final String EL_PORT = "port";
    private static final String EL_PORTS = "ports";
    private static final String ATR_SHOW_FIELDS = "showFields";
    private static final String EL_GRAPHICS = "graphics";
    private static final String VAL_RELATION = "relation";
    private static final String ATR_TYPE = "type";
    private static final String EL_CLASS = "class";
    private static final String EL_PACKAGE = "package";
    private static final String ATR_STATIC = "static";
    private static final String ATR_NATURE = "nature";
    private static final String ATR_HIDDEN = "hidden";
    
    /**
     * The default values
     */
    private static final Color IMPLIED_COLOR = new Color(0);
    private static final float IMPLIED_STROKE = 1.0f;
    private static final float IMPLIED_LINE = 0.0f;
    
    public static final EntityResolver ENTITY_RESOLVER = new EntityResolver() {

        @Override
        public InputSource resolveEntity( String publicId, String systemId ) {
            if ( systemId != null && systemId.endsWith( "dtd" ) ) {
                URL url = FileFuncs.getResource( RuntimeProperties.PACKAGE_DTD, true );
                if ( url != null ) {
                    return new InputSource( url.toString() );
                }
                //if unable to find dtd in local fs, try getting it from web
                return new InputSource( RuntimeProperties.SCHEMA_LOC + RuntimeProperties.PACKAGE_DTD );
            }
            return null;
        }

    };
    /**
     * @param packageFile
     */
    public PackageXmlProcessor( File packageFile ) {
        
        super(packageFile, null, "package", RuntimeProperties.PACKAGE_DTD);
    }
    
    @Override
    public VPackage parse() {
        
        if( RuntimeProperties.isLogDebugEnabled() )
            db.p( "Starting parsing package: " + xmlFile.getAbsolutePath() );
        
        long startParsing = System.currentTimeMillis();
        VPackage pack = null;
        try {
            Document document = getDocument();
            Element root = document.getDocumentElement();
            
            pack = new VPackage( xmlFile.getAbsolutePath() );
            
            Node name = root.getElementsByTagName( EL_NAME ).item( 0 );
            pack.setName( name.getTextContent() );
            Node descr = root.getElementsByTagName( EL_DESCRIPTION ).item( 0 );
            pack.setDescription( descr.getTextContent() );
            
            NodeList list = root.getElementsByTagName( EL_CLASS );

            for (int i=0; i<list.getLength(); i++) {
                PackageClass pc = parseClass( (Element)list.item( i ) );
                pack.getClasses().add( pc );
                if( pc.getPainterName() != null )
                    pack.setPainters( true );
            }
            
            if( RuntimeProperties.isLogDebugEnabled() )
                db.p( "Package parsing finished in " + ( System.currentTimeMillis() - startParsing ) + "ms.\n");
        } catch ( Exception e ) {
            collector.collectDiagnostic( e.getMessage(), true );
            if(RuntimeProperties.isLogDebugEnabled()) {
                e.printStackTrace();
            }
        } 
        
        try {
            checkProblems( "Error parsing package file " + xmlFile.getName() );
        } catch( Exception e ) {
            return null;
        }
        
        return pack;
    }
    
    private Element getElementByName( Element root, String name ) {
        return (Element)root.getElementsByTagName( name ).item( 0 );
    }
    
    private PackageClass parseClass( Element classNode ) {
        PackageClass newClass = new PackageClass();
        
        newClass.setComponentType( PackageClass.ComponentType.getType( classNode.getAttribute( ATR_TYPE ) ) );
        newClass.setStatic( Boolean.parseBoolean( classNode.getAttribute( ATR_STATIC ) ) );
        newClass.setName( getElementByName( classNode, EL_NAME ).getTextContent() );
        newClass.setDescription( getElementByName( classNode, EL_DESCRIPTION ).getTextContent() );
        newClass.setIcon( getElementByName( classNode, EL_ICON ).getTextContent() );
        
        //parse all variables declared in the corresponding specification
        if( newClass.getComponentType().hasSpec() ) {
            try {
                Collection<ClassField> specFields = SpecParser.getFields( getPath(), newClass.getName(), ".java" );
                newClass.setSpecFields( specFields );
            } catch ( IOException e ) {
                collector.collectDiagnostic( "Class " + newClass.getName() + " specified in package does not exist." );
            } catch ( SpecParseException e ) {
                collector.collectDiagnostic( "Unable to parse the specification of class " + newClass.getName() );
            }
        }
        
        //Graphics
        Element grNode = getElementByName( classNode, EL_GRAPHICS );
        newClass.addGraphics( getGraphicsParser().parse( grNode
                /*, newClass.getComponentType() == ComponentType.REL*/ ) );
        
        Element painter;
        if( (painter = getElementByName( grNode, EL_PAINTER ) ) != null ) {
            newClass.setPainterName( painter.getTextContent() );
        }
        
        //Ports
        NodeList ports = classNode.getElementsByTagName( EL_PORT );
        for ( int i = 0; i < ports.getLength(); i++ ) {
            parsePort( newClass, (Element)ports.item( i ) );
        }

        //Fields
        NodeList fields = classNode.getElementsByTagName( EL_FIELD );
        for ( int i = 0; i < fields.getLength(); i++ ) {
            parseField( newClass, (Element)fields.item( i ) );
        }
        
        return newClass;
    }
    
    private void parseField( PackageClass newClass, Element fieldNode ) {
        String name = fieldNode.getAttribute( ATR_NAME );
        String type = fieldNode.getAttribute( ATR_TYPE );
        
        ClassField newField;
        
        if( newClass.getComponentType().hasSpec() ) {
            if ( name.indexOf( "." ) > -1 ) {
                //TODO - temporarily do not dig into hierarchy
                int idx = name.indexOf( "." );
                String root = name.substring( 0, idx );

                if ( newClass.getSpecField( root ) == null ) {
                    collector.collectDiagnostic( "Field " + root + " in class " + newClass.getName()
                            + " is not declared in the specification, variable " + type + " " + name + " ignored " );
                    return;
                }

                newField = new ClassField( name, type );
                newClass.addSpecField( newField );
            } else {
                newField = newClass.getSpecField( name );

                if ( newField == null ) {

                    collector.collectDiagnostic( "Field " + type + " " + name + " in class " + newClass.getName()
                            + " is not declared in the specification" );
                    return;
                } else if ( !newField.getType().equals( type ) ) {

                    collector.collectDiagnostic( "Field " + type + " " + name + " in class " + newClass.getName()
                            + " does not match the field declared in the specification: " + newField.getType() + " "
                            + newField.getName() );
                    return;
                }
            }
        } else {
            newField = new ClassField( name, type );
            newClass.addSpecField( newField );
        }

        newField.setValue( fieldNode.hasAttribute( ATR_VALUE ) ? fieldNode.getAttribute( ATR_VALUE ) : null );
        newField.setDescription( fieldNode.getAttribute( ATR_DESCRIPTION ) );
        newField.setHidden( Boolean.parseBoolean( fieldNode.getAttribute( ATR_HIDDEN ) ) );
        
        String nature = fieldNode.getAttribute( ATR_NATURE );
        if ( "input".equals( nature ) )
            newField.setInput( true );
        else if ( "goal".equals( nature ) )
            newField.setGoal( true );

        newClass.addField( newField );
        
        Element gr;
        //known
        if( ( gr = getElementByName( fieldNode, EL_KNOWN ) ) != null 
                && ( gr = getElementByName( gr, EL_GRAPHICS )) != null ) {
            newField.setKnownGraphics( getGraphicsParser().parse( gr ) );
        }
        //default
        if( ( gr = getElementByName( fieldNode, EL_DEFAULT ) ) != null 
                && ( gr = getElementByName( gr, EL_GRAPHICS )) != null ) {
            newField.setDefaultGraphics( getGraphicsParser().parse( gr ) );
        }
    }

    private void parsePort( PackageClass newClass, Element portNode ) {
        String name = portNode.getAttribute( ATR_NAME );
        String type = portNode.getAttribute( ATR_TYPE );
        String x = portNode.getAttribute( ATR_X );
        String y = portNode.getAttribute( ATR_Y );
        String portConnection = portNode.getAttribute( ATR_PORT_CONNECTION );
        String strict = portNode.getAttribute( ATR_STRICT );
        String multi = portNode.getAttribute( ATR_MULTI );  
        
        ClassField cf = newClass.getSpecField( name );
        
        if( newClass.getComponentType().hasSpec() ) {

            if ( name.indexOf( "." ) > -1 ) {
                //TODO - temporarily do not dig into hierarchy
                int idx = name.indexOf( "." );
                String root = name.substring( 0, idx );

                if ( newClass.getSpecField( root ) == null ) {
                    collector.collectDiagnostic( "Field " + root + " in class " + newClass.getName()
                            + " is not declared in the specification, variable " + type + " " + name + " ignored " );
                    return;
                }

                newClass.addSpecField( new ClassField( name, type ) );
            } else if ( !TypeUtil.TYPE_THIS.equalsIgnoreCase( name ) ) {
                if ( cf == null ) {

                    collector.collectDiagnostic( "Port " + type + " " + name + " in class " + newClass.getName()
                            + " does not have the corresponding field in the specification" );
                } else if ( !cf.getType().equals( type )
                        //type may be declared as "alias", however cf.getType() returns e.g. "double[]", ignore it
                        && !( cf.isAlias() && TypeUtil.TYPE_ALIAS.equals( type )) ) {

                    collector.collectDiagnostic( "Port " + type + " " + name + " in class " + newClass.getName()
                            + " does not match the field declared in the specification: " + cf.getType() + " " + cf.getName() );
                }
            }
        }
        
        Port newPort = new Port( name, type, 
                Integer.parseInt( x ), 
                Integer.parseInt( y ), 
                portConnection, 
                Boolean.parseBoolean( strict ), 
                Boolean.parseBoolean( multi ) );
        
        if( portNode.hasAttribute( ATR_ID ) )
            newPort.setId( portNode.getAttribute( ATR_ID ) );
        
        Element gr;
        //open
        if( ( gr = getElementByName( portNode, EL_OPEN ) ) != null 
                && ( gr = getElementByName( gr, EL_GRAPHICS )) != null ) {
            newPort.setOpenGraphics( getGraphicsParser().parse( gr ) );
        }
        
        //closed
        if( ( gr = getElementByName( portNode, EL_CLOSED ) ) != null 
                && ( gr = getElementByName( gr, EL_GRAPHICS )) != null ) {
            newPort.setClosedGraphics( getGraphicsParser().parse( gr ) );
        }
        
        newClass.addPort( newPort );
    }

    private GraphicsParser graphicsParser;
    
    private GraphicsParser getGraphicsParser() {
        if( graphicsParser == null )
            graphicsParser = new GraphicsParser();
        return graphicsParser;
    }
    
    private class GraphicsParser {
    
        class Dim {
            int x, y, width, height;
            public Dim( int x, int y, int width, int height ) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }
        }
        
        class Lineprops {
            float strokeWidth, lineType;
            public Lineprops( float strokeWidth, float lineType ) {
                this.strokeWidth = strokeWidth;
                this.lineType = lineType;
            }
            
        }
        
        class FixedCoords {
            int x, fx, y, fy;
            public FixedCoords( int x, int fx, int y, int fy ) {
                this.x = x;
                this.fx = fx;
                this.y = y;
                this.fy = fy;
            }
        }
        
        private Dim getDim( Element shape ) {
            return new Dim(
                    Integer.parseInt( shape.getAttribute( ATR_X ) ),
                    Integer.parseInt( shape.getAttribute( ATR_Y ) ),
                    Integer.parseInt( shape.getAttribute( ATR_WIDTH ) ),
                    Integer.parseInt( shape.getAttribute( ATR_HEIGHT ) ));
        }
        
        private Lineprops getLineProps( Element shape ) {
            return new Lineprops( 
                    shape.hasAttribute( ATR_STROKE ) ? Float.parseFloat( shape.getAttribute( ATR_STROKE ) ) : IMPLIED_STROKE,
                    shape.hasAttribute( ATR_LINETYPE ) ? Float.parseFloat( shape.getAttribute( ATR_LINETYPE ) ) : IMPLIED_LINE );
        }
        
        private Color getColor( Element shape ) {
            Color color;
            if( shape.hasAttribute( ATR_COLOUR ) ) {
                String s = shape.getAttribute( ATR_COLOUR );
                if( s.indexOf( ',' ) > -1 ) {
                    String[] rgb = s.split( "," );
                    color = new Color(
                            Integer.parseInt( rgb[0] ),
                            Integer.parseInt( rgb[1] ),
                            Integer.parseInt( rgb[2] ) );
                } else {
                    color = new Color( Integer.parseInt( s ) );
                }
            } else
                color = IMPLIED_COLOR;
            
            if( shape.hasAttribute( ATR_TRANSPARENCY ) ) {
                int alpha = Integer.parseInt( shape.getAttribute( ATR_TRANSPARENCY ) );
                if( alpha < 255 )
                    return new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha );
            }
            
            return color;
        }
        
        private boolean isShapeFilled( Element shape ) {
            return Boolean.parseBoolean( shape.getAttribute( ATR_FILLED ) );
        }
        
        private boolean isShapeFixed( Element shape ) {
            return Boolean.parseBoolean( shape.getAttribute( ATR_FIXED ) );
        }
        
        private FixedCoords getFixedCoords( Element shape, int width, int height, String suffix ) {
            if( suffix == null ) suffix = "";
            // parse the coordinates and check if they are fixed or reverse fixed
            String val = shape.getAttribute( ATR_X + suffix );
            int x, y, fixedX = 0, fixedY = 0;
            if ( val.endsWith( VAL_RF ) ) {
                x = width;
                fixedX = x - Integer.parseInt( val.substring( 0, val.length() - 2 ) );
            } else if ( val.endsWith( VAL_F ) ) {
                x = Integer.parseInt( val.substring( 0, val.length() - 1 ) );
                fixedX = -1;
            } else {
                x = Integer.parseInt( val );
            }
            val = shape.getAttribute( ATR_Y + suffix );
            if ( val.endsWith( VAL_RF ) ) {
                y = height;
                fixedY = y - Integer.parseInt( val.substring( 0, val.length() - 2 ) );
            } else if ( val.endsWith( VAL_F ) ) {
                y = Integer.parseInt( val.substring( 0, val.length() - 1 ) );
                fixedY = -1;
            } else {
                y = Integer.parseInt( val );
            }
            
            return new FixedCoords( x, fixedX, y, fixedY );
        }
        
        private ClassGraphics parse( Element grNode/*, boolean isRelation*/ ) {
            ClassGraphics newGraphics = new ClassGraphics();
            newGraphics.setShowFields( Boolean.parseBoolean( grNode.getAttribute( ATR_SHOW_FIELDS ) ) );
            //newGraphics.setRelation( isRelation );
            
            NodeList list = grNode.getChildNodes();
            for ( int k = 0; k < list.getLength(); k++ ) {
                if( list.item( k ).getNodeType() != Node.ELEMENT_NODE ) continue;
                Element node = (Element)list.item( k );
                String nodeName = node.getNodeName();

                Shape shape = null;
                if ( EL_BOUNDS.equals( nodeName ) ) {
                    Dim dim =  getDim( node );
                    newGraphics.setBounds( dim.x, dim.y, dim.width, dim.height );
                    continue;
                } else if ( EL_LINE.equals( nodeName ) ) {
                    
                    shape = makeLine( node, newGraphics );
                    
                } else if ( EL_RECT.equals( nodeName ) ) {

                    Dim dim =  getDim( node );
                    Lineprops lp = getLineProps( node );
                    shape =  new Rect( dim.x, dim.y, dim.width, dim.height, getColor( node ), isShapeFilled( node ), lp.strokeWidth, lp.lineType );
                    
                } else if ( EL_OVAL.equals( nodeName ) ) {

                    Dim dim =  getDim( node );
                    Lineprops lp = getLineProps( node );
                    shape = new Oval( dim.x, dim.y, dim.width, dim.height, getColor( node ), isShapeFilled( node ), lp.strokeWidth, lp.lineType );
                    
                } else if ( EL_ARC.equals( nodeName ) ) {

                    Dim dim =  getDim( node );
                    Lineprops lp = getLineProps( node );
                    int startAngle = Integer.parseInt( node.getAttribute( ATR_START_ANGLE ) );
                    int arcAngle = Integer.parseInt( node.getAttribute( ATR_ARC_ANGLE ) );
                    shape = new Arc( dim.x, dim.y, dim.width, dim.height,
                            startAngle, arcAngle, getColor( node ), isShapeFilled( node ), lp.strokeWidth, lp.lineType );
                    
                } else if ( EL_POLYGON.equals( nodeName ) ) {
                    Lineprops lp = getLineProps( node );

                    Polygon polygon = new Polygon( getColor( node ), isShapeFilled( node ), lp.strokeWidth, lp.lineType );
                    
                    //points
                    NodeList points = node.getElementsByTagName( EL_POINT );
                    int pointCount = points.getLength();
                    // arrays of polygon points
                    int[] xs = new int[pointCount];
                    int[] ys = new int[pointCount];
                    // arrays of FIXED information about polygon points
                    int[] fxs = new int[pointCount];
                    int[] fys = new int[pointCount];
                    int width = newGraphics.getBoundWidth();
                    int height = newGraphics.getBoundHeight();
                    
                    for ( int j = 0; j < pointCount; j++ ) {
                        FixedCoords fc = getFixedCoords( (Element)points.item( j ), width, height, null );
                        xs[j] = fc.x;
                        fxs[j] = fc.fx;
                        ys[j] = fc.y;
                        fys[j] = fc.fy;
                    }
                    
                    polygon.setPoints( xs, ys, fxs, fys );
                    shape = polygon;
                    
                } else if ( EL_IMAGE.equals( nodeName ) ) {
                    
                    Dim dim =  getDim( node );
                    //image path should be relative to the package xml
                    String imgPath = node.getAttribute( ATR_PATH );
                    String fullPath = FileFuncs.preparePathOS( getPath() + imgPath );
                    shape = new Image( dim.x, dim.y, 
                            fullPath, imgPath, isShapeFixed( node ) );
                    
                } else if ( EL_TEXT.equals( nodeName ) ) {
                    shape = makeText( node, newGraphics );
                    /*
                     * if (str.equals("*self")) newText.name = "self"; else if
                     * (str.equals("*selfWithName")) newText.name = "selfName";
                     */
                }
                
                if( shape != null )
                    newGraphics.addShape( shape );
            }
            
            return newGraphics;
        }

        private Text makeText( Element textNode, ClassGraphics graphics ) {
            String str = textNode.getAttribute( ATR_STRING );

            String fontName = textNode.getAttribute( ATR_FONTNAME );
            int fontStyle = Integer.parseInt( textNode.getAttribute( ATR_FONTSTYLE ) );
            int fontSize = Integer.parseInt( textNode.getAttribute( ATR_FONTSIZE ) );

            Font font = new Font( fontName, fontStyle, fontSize );

            FixedCoords fc = getFixedCoords( textNode, graphics.getBoundWidth(), graphics.getBoundHeight(), null );
            
            Text newText = new Text( fc.x, fc.y, font, getColor( textNode ), str, isShapeFixed( textNode ) );
            newText.setFixedX( fc.fx );
            newText.setFixedY( fc.fy );
            
            return newText;
        }

        private Line makeLine( Element lineNode, ClassGraphics graphics ) {
            
            Lineprops lp = getLineProps( lineNode );
            
            int w = graphics.getBoundWidth();
            int h = graphics.getBoundHeight();
            FixedCoords fc1 = getFixedCoords( lineNode, w, h, "1" );
            FixedCoords fc2 = getFixedCoords( lineNode, w, h, "2" );
            
            Line newLine = new Line( fc1.x, fc1.y, fc2.x, fc2.y, getColor( lineNode ), lp.strokeWidth, lp.lineType );
            newLine.setFixedX1( fc1.fx );
            newLine.setFixedX2( fc2.fx );
            newLine.setFixedY1( fc1.fy );
            newLine.setFixedY2( fc2.fy );
            
            return newLine;
        }

    }
    
    @Override
    protected void reportError( String message ) {
        throw new PackageParsingException(message);
    }

    @Override
    protected EntityResolver getEntityResolver() {
        return ENTITY_RESOLVER;
    }

    @Override
    protected DocumentBuilderFactory getDocBuilderFactory() {
        DocumentBuilderFactory factory = super.getDocBuilderFactory();
        factory.setValidating( true );
        return factory;
    }
    
    @Override
    protected DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        DocumentBuilder builder = super.getDocBuilder();
        builder.setErrorHandler( getErrorHandler() );
        return builder;
    }
    
    @Override
    protected void validateDocument( Document document ) throws SAXException,
            IOException {
    }
    
    public void addPackageClass( PackageClass pClass ) {
        
        Document doc;
        try {
            doc = getDocument();
        } catch ( Exception e ) {
            e.printStackTrace();
            return;
        }
        
        String name = pClass.getName();

        //check if such class exists and remove duplicates
        Element rootEl = doc.getDocumentElement();
        NodeList classEls = rootEl.getElementsByTagName( EL_CLASS );
        for( int i = 0; i < classEls.getLength(); i++ ) {
            Element nameEl = getElementByName( (Element)classEls.item( i ), EL_NAME );
            if( name.equals( nameEl.getTextContent() ) ) {
                rootEl.removeChild( classEls.item( i ) );
            }
        }
        
        Element classNode = doc.createElement( EL_CLASS );
        doc.getDocumentElement().appendChild( classNode );
        classNode.setAttribute( ATR_TYPE, PackageClass.ComponentType.SCHEME.getXmlName() );
        classNode.setAttribute( ATR_STATIC, "false" );
        
        Element className = doc.createElement( EL_NAME );
        className.setTextContent( name );
        classNode.appendChild( className );
        
        Element desrc = doc.createElement( EL_DESCRIPTION );
        desrc.setTextContent( pClass.getDescription() );
        classNode.appendChild( desrc );
        
        Element icon = doc.createElement( EL_ICON );
        icon.setTextContent( pClass.getIcon() );
        classNode.appendChild( icon );
        
        //graphics
        classNode.appendChild( generateGraphicsNode( doc, pClass.getGraphics() ) );
        
        //ports
        List<Port> ports = pClass.getPorts();
        if( !ports.isEmpty() ) {
            Element portsEl = doc.createElement( EL_PORTS );
            classNode.appendChild( portsEl );
            
            for( Port port : ports ) {
                portsEl.appendChild( generatePortNode( doc, port ) );
            }
        }
        
        //fields
        Collection<ClassField> fields = pClass.getFields();
        if( !fields.isEmpty() ) {
            Element fieldsEl = doc.createElement( EL_FIELDS );
            classNode.appendChild( fieldsEl );
            
            for( ClassField cf : fields ) {
                fieldsEl.appendChild( generateFieldNode( doc, cf ) );
            }
        }
        
        //write
        try {
            writeDocument( doc, new FileOutputStream( xmlFile ) );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }
    
    private Node generateFieldNode( Document doc, ClassField field ) {
        Element fieldEl = doc.createElement( EL_FIELD );
        
        fieldEl.setAttribute( ATR_NAME, field.getName() );
        fieldEl.setAttribute( ATR_TYPE, field.getType() );
        
        if( field.isInput() )
            fieldEl.setAttribute( ATR_NATURE, "input" );
        else if( field.isGoal() )
            fieldEl.setAttribute( ATR_NATURE, "goal" );
        
        if( field.getValue() != null )
            fieldEl.setAttribute( ATR_VALUE, field.getValue() );
        
        return fieldEl;
    }

    private Element generatePortNode( Document doc, Port port ) {
        Element portEl = doc.createElement( EL_PORT );
        
        portEl.setAttribute( ATR_NAME, port.getName() );
        portEl.setAttribute( ATR_TYPE, port.getType() );
        portEl.setAttribute( ATR_X, Integer.toString( port.getX() ) );
        portEl.setAttribute( ATR_Y, Integer.toString( port.getY() ) );
        portEl.setAttribute( ATR_PORT_CONNECTION, port.isArea() ? "area" : "" );
        portEl.setAttribute( ATR_STRICT, Boolean.toString( port.isStrict() ) );
        portEl.setAttribute( ATR_MULTI, Boolean.toString( port.isMulti() ) );
        
        return portEl;
    }
    
    private Element generateGraphicsNode( Document doc, ClassGraphics gr ) {
        
        Element graphicsEl = doc.createElement( EL_GRAPHICS );
        
        Element bounds = doc.createElement( EL_BOUNDS );
        graphicsEl.appendChild( bounds );
        bounds.setAttribute( ATR_X, Integer.toString( gr.getBoundX() ) );
        bounds.setAttribute( ATR_Y, Integer.toString( gr.getBoundY() ) );
        bounds.setAttribute( ATR_WIDTH, Integer.toString( gr.getBoundWidth() ) );
        bounds.setAttribute( ATR_HEIGHT, Integer.toString( gr.getBoundHeight() ) );
        
        for( Shape shape : gr.getShapes() ) {
            
            if( shape instanceof Rect ) {
                Rect rect = (Rect)shape;
                Element rectEl = doc.createElement( EL_RECT );
                graphicsEl.appendChild( rectEl );
                rectEl.setAttribute( ATR_X, Integer.toString( rect.getX() ) );
                rectEl.setAttribute( ATR_Y, Integer.toString( rect.getY() ) );
                rectEl.setAttribute( ATR_WIDTH, Integer.toString( rect.getWidth() ) );
                rectEl.setAttribute( ATR_HEIGHT, Integer.toString( rect.getHeight() ) );
                rectEl.setAttribute( ATR_COLOUR, Integer.toString( rect.getColor().getRGB() ) );
                rectEl.setAttribute( ATR_FILLED, Boolean.toString( rect.isFilled() ) );
                rectEl.setAttribute( ATR_FIXED, Boolean.toString( rect.isFixed() ) );
                rectEl.setAttribute( ATR_STROKE, Float.toString( rect.getStroke().getLineWidth() ) );
                rectEl.setAttribute( ATR_LINETYPE, Float.toString( rect.getLineType() ) );
                rectEl.setAttribute( ATR_TRANSPARENCY, Integer.toString( rect.getTransparency() ) );
            } else if( shape instanceof Text ) {
                Text text = (Text)shape;
                Element textEl = doc.createElement( EL_TEXT );
                textEl.setAttribute( ATR_STRING, text.getText() );
                textEl.setAttribute( ATR_X, Integer.toString( text.getX() ) );
                textEl.setAttribute( ATR_Y, Integer.toString( text.getY() ) );
                textEl.setAttribute( ATR_FONTNAME, text.getFont().getName() );
                textEl.setAttribute( ATR_FONTSIZE, Integer.toString( text.getFont().getSize() ) );
                textEl.setAttribute( ATR_FONTSTYLE, Integer.toString( text.getFont().getStyle() ) );
                textEl.setAttribute( ATR_TRANSPARENCY, Integer.toString( text.getTransparency() ) );
                textEl.setAttribute( ATR_COLOUR, Integer.toString( text.getColor().getRGB() ) );
                graphicsEl.appendChild( textEl );
            } //TODO handle the rest of shapes
        }
        
        return graphicsEl;
    }
    
    public static VPackage load(File f) {
        PackageXmlProcessor pxp = new PackageXmlProcessor(f);
        VPackage pack = pxp.parse();
        pxp.collector = null;
        pxp.xmlFile = null;
        pxp.ERROR_HANDLER = null;
        
        return pack;
    }
    
}
