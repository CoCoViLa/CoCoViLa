package ee.ioc.cs.vsle.util;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class SystemUtils {

    private static final Logger logger = LoggerFactory.getLogger(SystemUtils.class);

    /**
     * Upon platform, use OS-specific methods for opening the URL in required
     * browser.
     * 
     * @param url - URL to be opened in a browser. Capable of browsing local
     *                documentation as well if path is given with file://
     */
    public static void openInBrowser( String url, Component parent ) {
        try {
            // Check if URL is defined, otherwise there is no reason for opening
            // the browser in the first place.
            if ( url != null && url.trim().length() > 0 ) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            logger.debug(null, e);

            StringBuilder msg = new StringBuilder();
            msg.append("A browser could not be launched for opening ");
            msg.append("the documentation web page.");

            String exMsg = e.getMessage();
            if (exMsg != null) {
                msg.append('\n');
                msg.append(exMsg);
            }

            msg.append("\nThe documentation can still be found by ");
            msg.append("browsing to the following URL:\n");
            msg.append(url);

            JOptionPane.showMessageDialog(parent,
                    msg.toString(),
                    "Error opening documentation",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
