/******************************************************************************
 * Copyright (c) 2011 Oracle and Accenture
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Kamesh Sampath - [355751] General improvement of XML root binding API    
 ******************************************************************************/

package org.eclipse.sapphire.ui.form.editors.masterdetails.state;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a> 
 */

@XmlBinding( path = "master-details-editor-page-state" )
@GenerateImpl

public interface IMasterDetailsEditorPageState extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IMasterDetailsEditorPageState.class );
    
    // *** ContentOutlineState ***
    
    @Type( base = IContentOutlineState.class )
    @XmlBinding( path = "content-outline" )

    ImpliedElementProperty PROP_CONTENT_OUTLINE_STATE = new ImpliedElementProperty( TYPE, "ContentOutlineState" );
    
    IContentOutlineState getContentOutlineState();
    
}
