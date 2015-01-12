/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.validation.internal;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ReferenceValidationService

    extends ModelPropertyValidationService<ReferenceValue<?,?>>
    
{
    @Override
    public Status validate()
    {
        final ReferenceValue<?,?> value = target();
        
        if( value.resolve() == null && value.getText() != null )
        {
            final ValueProperty property = value.getProperty();
            final String label = property.getLabel( true, CapitalizationType.NO_CAPS, false );
            final String str = value.getText();
            final String msg = NLS.bind( Resources.message, label, str );
            return Status.createErrorStatus( msg );
        }
        
        return Status.createOkStatus();
    }
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            return ( property instanceof ValueProperty && property.hasAnnotation( Reference.class ) && property.hasAnnotation( MustExist.class ) );
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new ReferenceValidationService();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String message;
        
        static
        {
            initializeMessages( ReferenceValidationService.class.getName(), Resources.class );
        }
    }
    
}