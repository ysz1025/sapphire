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

package org.eclipse.sapphire;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PropertyBinding implements Disposable
{
    private Property property;

    public void init( final Property property )
    {
        if( property == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.property = property;
    }
    
    public Property property()
    {
        return this.property;
    }
    
    @Override
    public void dispose()
    {
        // The default implementation doesn't do anything.
    }

}
