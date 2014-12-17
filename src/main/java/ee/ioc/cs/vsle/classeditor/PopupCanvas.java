package ee.ioc.cs.vsle.classeditor;


import java.awt.Dimension;
import java.awt.Graphics;

import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.VPackage;


public class PopupCanvas extends ClassCanvas{

	public PopupCanvas(VPackage _package, String workingDir) {
		super(_package, workingDir);
		initialize();
	}
	
	@Override
    protected void initialize() {
    	super.initialize();
    	drawAreaSize = new Dimension(100, 100);
        drawingArea.removeMouseListener(super.mListener);
        drawingArea.removeMouseMotionListener(super.mListener);
    }
	
	@Override
	public void paintComponent( Graphics g ){
		//stab
	}

	@Override
	public void setPosInfo( int x, int y ) {
		//stab
	}
	
	/*public void addObject( GObj obj ) {
		if(getObjects() != null){
			getObjects().add(obj);}
		else {
			objects = new ObjectList();
			getObjects().add(obj);
		}
		
    }

	public ObjectList getObjects() {
		return objects;
	}

	public void setObjects(ObjectList objects) {
		this.objects = objects;
	}   
	*/
	
}
