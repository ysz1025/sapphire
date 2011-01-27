/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - created common ISapphireDocumentation base type
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.internal.DocumentationRefMethods;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

@Label( standard = "documentation reference" )
@GenerateImpl

public interface ISapphireDocumentationRef

    extends ISapphireDocumentation
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireDocumentationRef.class );
    
    // *** Id ***
    
    @Label( standard = "ID" )
    @NonNullValue
    @XmlBinding( path = "id" )
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<String> getId();
    void setId( String id );
    
    // *** Method : resolve ***
    
    @DelegateImplementation( DocumentationRefMethods.class )
    
    ISapphireDocumentationDef resolve();

}
