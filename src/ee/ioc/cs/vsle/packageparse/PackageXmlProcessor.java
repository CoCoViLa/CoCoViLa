/**
 * 
 */
package ee.ioc.cs.vsle.packageparse;

import java.awt.*;
import java.io.*;
import java.util.*;

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
    private static final int IMPLIED_ALPHA = 255;
    private static final int IMPLIED_COLOR = 0;
    private static final float IMPLIED_STROKE = 1.0f;
    private static final float IMPLIED_LINE = 0.0f;
    
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
        if(newClass.getComponentType() != PackageClass.ComponentType.SCHEME) {
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
        
        if(newClass.getComponentType() != PackageClass.ComponentType.SCHEME) {

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
        
        Port newPort = new Port( name, type, Integer.parseInt( x ), Integer.parseInt( y ), portConnection, strict, multi );
        
        //is id really needed?
        if( portNode.hasAttribute( ATR_ID ) )
            newPort.setId( portNode.getAttribute( ATR_ID ) );
        
        Element gr;
        //open
        if( ( gr = getElementByName( portNode, EL_OPEN ) ) != null 
                && ( gr = getElementByName( gr, EL_GRAPHICS )) != null ) {
            newPort.setOpenGraphics( getGraphicsParser().parse( gr ) );
        } else {
            ClassGraphics newGraphics = new ClassGraphics();
            newGraphics.addShape( new Oval( -4, -4, 8, 8, 12632256, true, 1.0f, 255, 0, true ) );
            newGraphics.addShape( new Oval( -4, -4, 8, 8, 0, false, 1.0f, 255, 0, true ) );
            newGraphics.setBounds( -4, -4, 8, 8 );
            newPort.setOpenGraphics( newGraphics );
        }
        
        //closed
        if( ( gr = getElementByName( portNode, EL_CLOSED ) ) != null 
                && ( gr = getElementByName( gr, EL_GRAPHICS )) != null ) {
            newPort.setClosedGraphics( getGraphicsParser().parse( gr ) );
        } else {
            ClassGraphics newGraphics = new ClassGraphics();
            newGraphics.addShape( new Oval( -4, -4, 8, 8, 0, true, 1.0f, 255, 0, true ) );
            newGraphics.setBounds( -4, -4, 8, 8 );
            newPort.setClosedGraphics( newGraphics );
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
        
        class Colorprops {
            int colour;
            int alpha;
            public Colorprops( int colour, int transparency ) {
                this.colour = colour;
                this.alpha = transparency;
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
        
        private Colorprops getColorProps( Element shape ) {
            return new Colorprops( 
                    shape.hasAttribute( ATR_COLOUR ) ? Integer.parseInt( shape.getAttribute( ATR_COLOUR ) ) : IMPLIED_COLOR,
                    shape.hasAttribute( ATR_TRANSPARENCY ) ? Integer.parseInt( shape.getAttribute( ATR_TRANSPARENCY ) ) : IMPLIED_ALPHA );
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
                    Colorprops cp = getColorProps( node );
                    shape =  new Rect( dim.x, dim.y, dim.width, dim.height, cp.colour, isShapeFilled( node ), lp.strokeWidth, cp.alpha, lp.lineType );
                    
                } else if ( EL_OVAL.equals( nodeName ) ) {

                    Dim dim =  getDim( node );
                    Lineprops lp = getLineProps( node );
                    Colorprops cp = getColorProps( node );
                    shape = new Oval( dim.x, dim.y, dim.width, dim.height, cp.colour, isShapeFilled( node ), lp.strokeWidth, cp.alpha, lp.lineType );
                    
                } else if ( EL_ARC.equals( nodeName ) ) {

                    Dim dim =  getDim( node );
                    Lineprops lp = getLineProps( node );
                    Colorprops cp = getColorProps( node );
                    int startAngle = Integer.parseInt( node.getAttribute( ATR_START_ANGLE ) );
                    int arcAngle = Integer.parseInt( node.getAttribute( ATR_ARC_ANGLE ) );
                    shape = new Arc( dim.x, dim.y, dim.width, dim.height,
                            startAngle, arcAngle, cp.colour, isShapeFilled( node ), lp.strokeWidth, cp.alpha, lp.lineType );
                    
                } else if ( EL_POLYGON.equals( nodeName ) ) {
                    Lineprops lp = getLineProps( node );
                    Colorprops cp = getColorProps( node );

                    Polygon polygon =  new Polygon( cp.colour, isShapeFilled( node ), lp.strokeWidth, cp.alpha, lp.lineType );
                    
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
                        FixedCoords fc = getFixedCoords( (Element)list.item( j ), width, height, null );
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
            Colorprops cp = getColorProps( textNode );
            String str = textNode.getAttribute( ATR_STRING );

            String fontName = textNode.getAttribute( ATR_FONTNAME );
            int fontStyle = Integer.parseInt( textNode.getAttribute( ATR_FONTSTYLE ) );
            int fontSize = Integer.parseInt( textNode.getAttribute( ATR_FONTSIZE ) );

            Font font = new Font( fontName, fontStyle, fontSize );

            FixedCoords fc = getFixedCoords( textNode, graphics.getBoundWidth(), graphics.getBoundHeight(), null );
            
            Text newText = new Text( fc.x, fc.y, font, new Color( cp.colour ), cp.alpha, str, isShapeFixed( textNode ) );
            newText.setFixedX( fc.fx );
            newText.setFixedY( fc.fy );
            
            return newText;
        }

        private Line makeLine( Element lineNode, ClassGraphics graphics ) {
            
            Lineprops lp = getLineProps( lineNode );
            Colorprops cp = getColorProps( lineNode );
            
            int w = graphics.getBoundWidth();
            int h = graphics.getBoundHeight();
            FixedCoords fc1 = getFixedCoords( lineNode, w, h, "1" );
            FixedCoords fc2 = getFixedCoords( lineNode, w, h, "2" );
            
            Line newLine = new Line( fc1.x, fc1.y, fc2.x, fc2.y, cp.colour, lp.strokeWidth, cp.alpha, lp.lineType );
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
        return PackageParser.ENTITY_RESOLVER;
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
    
    
    public static VPackage load(File f) {

        return new PackageXmlProcessor(f).parse();
    }

}
