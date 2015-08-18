/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.sl.draw;

import static org.apache.poi.sl.draw.Drawable.DRAW_FACTORY;

import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.text.AttributedString;

import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.Notes;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;

public class DrawFactory {
    protected static final ThreadLocal<DrawFactory> defaultFactory = new ThreadLocal<DrawFactory>();

    /**
     * Set a custom draw factory for the current thread.
     * This is a fallback, for operations where usercode can't set a graphics context.
     * Preferably use the rendering hint {@link Drawable#DRAW_FACTORY} to set the factory.
     *
     * @param factory
     */
    public static void setDefaultFactory(DrawFactory factory) {
        defaultFactory.set(factory);
    }

    public static DrawFactory getInstance(Graphics2D graphics) {
        // first try to find the factory over the rendering hint
        DrawFactory factory = null;
        boolean isHint = false;
        if (graphics != null) {
            factory = (DrawFactory)graphics.getRenderingHint(DRAW_FACTORY);
            isHint = (factory != null);
        }
        // secondly try the thread local default
        if (factory == null) {
            factory = defaultFactory.get();
        }
        // and at last, use the default factory
        if (factory == null) {
            factory = new DrawFactory();
        }
        if (graphics != null && !isHint) {
            graphics.setRenderingHint(DRAW_FACTORY, factory);
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    public Drawable getDrawable(Shape shape) {
        if (shape instanceof TextBox) {
            return getDrawable((TextBox<? extends TextParagraph<? extends TextRun>>)shape);
        } else if (shape instanceof FreeformShape) {
            return getDrawable((FreeformShape<? extends TextParagraph<? extends TextRun>>)shape);
        } else if (shape instanceof TextShape) {
            return getDrawable((TextShape<? extends TextParagraph<? extends TextRun>>)shape);
        } else if (shape instanceof GroupShape) {
            return getDrawable((GroupShape<? extends Shape>)shape);
        } else if (shape instanceof PictureShape) {
            return getDrawable((PictureShape)shape);
        } else if (shape instanceof Background) {
            return getDrawable((Background)shape);
        } else if (shape instanceof ConnectorShape) {
            return getDrawable((ConnectorShape)shape);
        } else if (shape instanceof TableShape) {
            return getDrawable((TableShape)shape);
        } else if (shape instanceof Slide) {
            return getDrawable((Slide<? extends Shape, ? extends SlideShow, ? extends Notes<?,?>>)shape);
        } else if (shape instanceof MasterSheet) {
            return getDrawable((MasterSheet<? extends Shape, ? extends SlideShow>)shape);
        } else if (shape instanceof Sheet) {
            return getDrawable((Sheet<? extends Shape, ? extends SlideShow>)shape);
        } else if (shape.getClass().isAnnotationPresent(DrawNotImplemented.class)) {
            return new DrawNothing<Shape>(shape);
        }
        
        throw new IllegalArgumentException("Unsupported shape type: "+shape.getClass());
    }

    public <T extends Slide<? extends Shape, ? extends SlideShow, ? extends Notes<?,?>>> DrawSlide<T> getDrawable(T sheet) {
        return new DrawSlide<T>(sheet);
    }

    public <T extends Sheet<? extends Shape, ? extends SlideShow>> DrawSheet<T> getDrawable(T sheet) {
        return new DrawSheet<T>(sheet);
    }

    public <T extends MasterSheet<? extends Shape, ? extends SlideShow>> DrawMasterSheet<T> getDrawable(T sheet) {
        return new DrawMasterSheet<T>(sheet);
    }

    public <T extends TextBox<? extends TextParagraph<?>>> DrawTextBox<T> getDrawable(T shape) {
        return new DrawTextBox<T>(shape);
    }

    public <T extends FreeformShape<? extends TextParagraph<? extends TextRun>>> DrawFreeformShape<T> getDrawable(T shape) {
        return new DrawFreeformShape<T>(shape);
    }

    public <T extends ConnectorShape> DrawConnectorShape<T> getDrawable(T shape) {
        return new DrawConnectorShape<T>(shape);
    }
    
    public <T extends TableShape> DrawTableShape<T> getDrawable(T shape) {
        return new DrawTableShape<T>(shape);
    }
    
    public <T extends TextShape<? extends TextParagraph<? extends TextRun>>> DrawTextShape<T> getDrawable(T shape) {
        return new DrawTextShape<T>(shape);
    }

    public <T extends GroupShape<? extends Shape>> DrawGroupShape<T> getDrawable(T shape) {
        return new DrawGroupShape<T>(shape);
    }
    
    public <T extends PictureShape> DrawPictureShape<T> getDrawable(T shape) {
        return new DrawPictureShape<T>(shape);
    }
    
    public <T extends TextRun> DrawTextParagraph<T> getDrawable(TextParagraph<T> paragraph) {
        return new DrawTextParagraph<T>(paragraph);
    }

    public <T extends Background> DrawBackground<T> getDrawable(T shape) {
        return new DrawBackground<T>(shape);
    }
    
    public DrawTextFragment getTextFragment(TextLayout layout, AttributedString str) {
        return new DrawTextFragment(layout, str);
    }
    
    public DrawPaint getPaint(PlaceableShape shape) {
        return new DrawPaint(shape);
    }
}