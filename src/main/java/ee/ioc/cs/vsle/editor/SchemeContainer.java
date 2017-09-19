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

    public void loadScheme( File schemeFile ) {

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
