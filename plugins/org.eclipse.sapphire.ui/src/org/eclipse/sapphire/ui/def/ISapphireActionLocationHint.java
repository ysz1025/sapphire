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

package org.eclipse.sapphire.ui.def;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "action location hint" )

public interface ISapphireActionLocationHint

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireActionLocationHint.class );
    
    // *** ReferenceEntityId ***
    
    @Label( standard = "reference entity ID" )
    @NonNullValue
    @PossibleValues( property = "../#/Id", invalidValueSeverity = IStatus.OK )
    
    ValueProperty PROP_REFERENCE_ENTITY_ID = new ValueProperty( TYPE, "ReferenceEntityId" );
    
    Value<String> getReferenceEntityId();
    void setReferenceEntityId( String value );
    
}