package ee.ioc.cs.vsle.editor;

import java.io.*;

import javax.swing.*;

import ee.ioc.cs.vsle.vclass.*;

public class SchemeContainer implements ISchemeContainer {

    VPackage _package;
    String dir;
    Scheme scheme;

    public SchemeContainer( VPackage _package, String dir ) {
        this._package = _package;
        this.dir = dir;
        scheme = new Scheme( this, new ObjectList(), new ConnectionList() );
    }

    void loadScheme( File schemeFile ) {

        SchemeLoader schemeLoader = new SchemeLoader( _package );

        if ( schemeLoader.load( schemeFile ) ) {
            scheme = schemeLoader.getScheme( this );
        } else {
            JOptionPane.showMessageDialog( null, "Error loading scheme",
                    "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    @Override
    public ObjectList getObjectList() {
        return scheme.getObjectList();
    }

    @Override
    public VPackage getPackage() {
        return _package;
    }

    @Override
    public Scheme getScheme() {
        return scheme;
    }

    public void setScheme( Scheme scheme ) {
        this.scheme = scheme;
    }

    @Override
    public String getWorkDir() {
        return dir;
    }

    @Override
    public void registerRunner( long id ) {
    }

    @Override
    public void repaint() {
    }

    @Override
    public void unregisterRunner( long id ) {
    }

    @Override
    public String getSchemeName() {
        return _package.getSchemeClassName( null );
    }

}