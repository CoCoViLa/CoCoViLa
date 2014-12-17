package ee.ioc.cs.vsle.iconeditor;

import static ee.ioc.cs.vsle.graphics.Shape.*;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.graphics.Image;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.vclass.PackageClass.ComponentType;

public class ClassImport {
	ArrayList <String> pc;
	ArrayList <IconClass>icons;
	ShapeGroup shapeList;
	IconClass classIcon;
	ArrayList <IconPort> ports;
	ArrayList <ClassField> fields;
	ClassField field;
	String path;
/*
 * Takes the package file and gives a list of class names and a list of classes information
 */
	public ClassImport(File file,  ArrayList <String> packageClasses, ArrayList <IconClass> icons){
		
		this.pc = packageClasses;
		this.icons = icons;
		DefaultHandler handler = new ClassHandler();
		icons.clear();
		packageClasses.clear();
		SAXParserFactory factory = SAXParserFactory.newInstance();

		path = file.getParentFile().getAbsolutePath();
		
		factory.setValidating(true);
		
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	class ClassHandler extends DefaultHandler {
		boolean inClass = false;
		boolean inName = false;
		boolean inGraphics = false;
		boolean inPort = false;
		boolean inField = false;
		boolean inDesc = false;
		boolean inIcon = false;
		boolean isRelation = false;
		private StringBuilder charBuf = new StringBuilder(); 
		
		
		@Override
		public InputSource resolveEntity(String publicId, String systemId) {
			InputSource is = null;
			if (systemId != null && systemId.endsWith("dtd")) {
				is = new InputSource( FileFuncs.getResource( RuntimeProperties.PACKAGE_DTD, false ).toString() );
			}
			return is;
		}
	
		@Override
		public void startElement(String namespaceURI, String lName, String qName,
			 Attributes attrs) {
			String element = qName;
			
			int x, y, w, h, col; // x, y, width, height, colour 
			int x2, y2;
			float st = 1; // stroke
			float lt = 0; // linetype
			int tr = 255; //transparency
			String strVal;
			int startAngle, arcAngle;
			boolean filled, fixed, strict, multi;
			String name = null;
			String type = null;
			String value = null;
			String fontName = null;
			String fontStyle = null;
			int fontSize = 10;
							
			if(element.equals("class")){
				inClass = true;
				classIcon = new IconClass();
				ports = new ArrayList<IconPort>();
				fields = new ArrayList<ClassField>();
				shapeList = new ShapeGroup();
                type = attrs.getValue("type");
                classIcon.setComponentType( ComponentType.getType( type ) );
			} else if(element.equals("name") && inClass){
				inName = true;
			} else if(element.equals("description") && inClass){
				inDesc = true;
			} else if(element.equals("graphics")){
				inGraphics = true;
			} else if(element.equals("icon")){
				inIcon = true;
			} else if (element.equals("rect")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				col = Integer.parseInt(attrs.getValue("colour"));
				filled = Boolean.parseBoolean(attrs.getValue("filled"));
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Float.parseFloat(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Float.parseFloat(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Rect rect = new Rect(x, y, w, h, createColor( col, tr ), filled, st, lt);
				rect.setFixed(fixed);
				shapeList.add(rect);
			}else if(element.equals("oval")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				col = Integer.parseInt(attrs.getValue("colour"));
				filled = Boolean.parseBoolean(attrs.getValue("filled"));
				
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Float.parseFloat(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Float.parseFloat(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Oval oval = new Oval(x, y, w, h, createColor( col, tr ), filled, st, lt);
				oval.setFixed(fixed);
				shapeList.add(oval);
			}else if(element.equals("line")){
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				
				x = getCoordinate(attrs.getValue("x1"));
				y = getCoordinate(attrs.getValue("y1"));
				x2 = getCoordinate(attrs.getValue("x2"));
				y2 = getCoordinate(attrs.getValue("y2"));
				
				col = Integer.parseInt(attrs.getValue("colour"));
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Float.parseFloat(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Float.parseFloat(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				
				
				Line line = new Line(x, y, x2, y2, createColor( col, tr ), st, lt);
				line.setFixed(fixed);
				shapeList.add(line);
			}else if(element.equals("arc")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				col = Integer.parseInt(attrs.getValue("colour"));
				filled = Boolean.parseBoolean(attrs.getValue("filled"));
				
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Float.parseFloat(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Float.parseFloat(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				
				
				startAngle = Integer.parseInt(attrs.getValue("startAngle"));
				arcAngle = Integer.parseInt(attrs.getValue("arcAngle"));
				
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Arc arc = new Arc(x, y, w, h, startAngle, arcAngle, createColor( col, tr ), filled, st, lt);
				arc.setFixed(fixed);
				shapeList.add(arc);
			}else if (element.equals("bounds")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				BoundingBox b = new BoundingBox(x, y, w, h);
				classIcon.boundingbox = b;
				shapeList.add(b);
			}else if(element.equals("dot")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				col = Integer.parseInt(attrs.getValue("colour"));
				st = Float.parseFloat(attrs.getValue("stroke"));
				tr = Integer.parseInt(attrs.getValue("transparency"));
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Dot dot = new Dot(x, y, createColor( col, tr ), st);
				dot.setFixed(fixed);
				shapeList.add(dot);
			}else if(element.equals("text")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				col = Integer.parseInt(attrs.getValue("colour"));
				fontSize = Integer.parseInt(attrs.getValue("fontsize"));
				fontName = attrs.getValue("fontname");
				fontStyle = attrs.getValue("fontstyle");
				
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				String textStr = attrs.getValue("string");
				
				Font font = null;

				if (fontStyle.equalsIgnoreCase("0"))
					font = new Font(fontName, Font.PLAIN, fontSize);
				else if (fontStyle.equalsIgnoreCase("1"))
					font = new Font(fontName, Font.BOLD, fontSize);
				else if (fontStyle.equalsIgnoreCase("2")) font = new Font(fontName, Font.ITALIC, fontSize);
				if (font != null) {
				    Text text;
				    if (inField) {
				        text = new Text(x, y, font, createColor( col, tr ), "*".concat(field.getName()));
				    } else {
				        text = new Text(x, y, font, createColor( col, tr ), textStr);
				    }
				    shapeList.add(text);
				}
				
			} else if(element.equals("image")) { 
			    x = getCoordinate(attrs.getValue("x"));
                y = getCoordinate(attrs.getValue("y"));
                fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
                String path = attrs.getValue( "path" );
                
                String fullPath;
                
                if ( !ClassImport.this.path.endsWith(File.separator)
                        && !path.startsWith(File.separator)) {
                    fullPath = ClassImport.this.path + File.separator
                            + path;
                } else {
                    fullPath = ClassImport.this.path + path;
                }
                
                fullPath = FileFuncs.preparePathOS( fullPath );
                
                Image newImg = new Image( x, y, fullPath, path, fixed );
                
                shapeList.add(newImg);
                
			} else if(element.equals("port")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				name = attrs.getValue("name");
				boolean isAreaConn = Boolean.parseBoolean(attrs.getValue("isAreaConn"));
				strict= Boolean.parseBoolean(attrs.getValue("strict"));
				multi = Boolean.parseBoolean(attrs.getValue( "multi" ) );
				IconPort port = new IconPort(name, x, y, isAreaConn, strict, multi);
                port.setType(attrs.getValue("type"));
				ports.add(port);
			}else if (element.equals("field")){
			    inField = true;
				name = attrs.getValue("name");
				type = attrs.getValue("type");
				value = attrs.getValue("value");
				field = new ClassField(name);
				
				field.setType(type);
				field.setValue(value);
				
			}
			
		}

		@Override
		public void endElement(String namespaceURI, String sName, String qName) {
			String element = qName;
			if (element.equals("class")){
				inClass = false;
				classIcon.shapeList = shapeList;
				classIcon.ports = ports;
				classIcon.fields = fields;
				icons.add(classIcon);
			}
			if(element.equals("name") && inClass){
				String name = charBuf.toString();
				pc.add(name);
				classIcon.setName(name);
				inName = false;
			}
			if(element.equals("graphics")){
				inGraphics = false;
			}
			if(element.equals("field")){
			    fields.add(field);
			    inField = false;
			}
			if(element.equals("description") && inClass){
				classIcon.setDescription(charBuf.toString());
				inDesc = false;
			}
			if(element.equals("icon")){
				classIcon.setIconName(charBuf.toString());
				inIcon = false;
			}
			charBuf.delete(0, charBuf.length());
		}
		
		@Override
		public void characters(char[] ch, int start, int length) {
			charBuf.append(ch, start, length);
		}

		/*
		 * Removes either r or f or both from the end of the
		 * coordinate
		 */
		public int getCoordinate(String c) {
			while (c.length() > 1 && (((c.charAt(c.length() - 1)) == 'f') || ((c.charAt(c.length() - 1)) == 'r'))){
				c = c.substring(0,c.length() - 1);
			}
			return Integer.parseInt(c);
		}
	
	}

}
