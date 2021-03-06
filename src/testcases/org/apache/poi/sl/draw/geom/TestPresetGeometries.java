/*
 *  ====================================================================
 *    Licensed to the Apache Software Foundation (ASF) under one or more
 *    contributor license agreements.  See the NOTICE file distributed with
 *    this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0
 *    (the "License"); you may not use this file except in compliance with
 *    the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * ====================================================================
 */
package org.apache.poi.sl.draw.geom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class TestPresetGeometries {
    @Test
    public void testRead(){
        Map<String, CustomGeometry> shapes = PresetGeometries.getInstance();
        assertEquals(187, shapes.size());

        for(String name : shapes.keySet()) {
            CustomGeometry geom = shapes.get(name);
            Context ctx = new Context(geom, new Rectangle2D.Double(0, 0, 100, 100), new IAdjustableShape() {
                @Override
                public Guide getAdjustValue(String presetName) {
                    return null;
                }
            });
            for(Path p : geom){
                Path2D path = p.getPath(ctx);
                assertNotNull(path);
            }
        }
        
        // we get the same instance on further calls
        assertTrue(shapes == PresetGeometries.getInstance());
    }

    // helper methods to adjust list of presets for other tests
    public static void clearPreset() {
        // ensure that we are initialized
        assertNotNull(PresetGeometries.getInstance());
        
        // test handling if some presets are not found
        PresetGeometries._inst.clear();
    }
    
    public static void resetPreset() {
        PresetGeometries._inst = null;
    }

    @Ignore("problem solved? Turn back on if this debugging is still in process.")
    @Test
    public void testCheckXMLParser() throws Exception{
        // Gump reports a strange error because of an unavailable XML Parser, let's try to find out where 
        // this comes from
        // 
        Enumeration<URL> resources = this.getClass().getClassLoader().getResources("META-INF/services/javax.xml.stream.XMLEventFactory");
        printURLs(resources);
        resources = ClassLoader.getSystemResources("META-INF/services/javax.xml.stream.XMLEventFactory");
        printURLs(resources);
        resources = ClassLoader.getSystemResources("org/apache/poi/sl/draw/geom/presetShapeDefinitions.xml");
        printURLs(resources);
    }

    private void printURLs(Enumeration<URL> resources) {
        while(resources.hasMoreElements()) {
            URL url = resources.nextElement();
            System.out.println("URL: " + url);
        }
    }
}
