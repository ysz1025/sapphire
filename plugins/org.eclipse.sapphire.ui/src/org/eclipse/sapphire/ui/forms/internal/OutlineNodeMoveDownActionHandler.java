/******************************************************************************
 * Copyright (c) 2016 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.forms.internal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.forms.MasterDetailsContentNodePart;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class OutlineNodeMoveDownActionHandler extends OutlineNodeMoveActionHandler
{
    public static final String ID = "Sapphire.Outline.Move.Down";
    
    public OutlineNodeMoveDownActionHandler()
    {
        setId( ID );
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    protected Object run( final Presentation context )
    {
        final MasterDetailsContentNodePart node = (MasterDetailsContentNodePart) getPart();
        final Element element = node.getModelElement();
        final ElementList<Element> list = (ElementList<Element>) element.parent();
        
        list.moveDown( element );
        
        return null;
    }
    
    @Override
    protected boolean computeEnabledState()
    {
        boolean enabled = super.computeEnabledState();
        
        if( enabled )
        {
            final Element element = getModelElement();
            final ElementList<?> list = (ElementList<?>) element.parent();
            enabled = ( list.indexOf( element ) < ( list.size() - 1 ) );
        }
        
        return enabled;
    }

}
