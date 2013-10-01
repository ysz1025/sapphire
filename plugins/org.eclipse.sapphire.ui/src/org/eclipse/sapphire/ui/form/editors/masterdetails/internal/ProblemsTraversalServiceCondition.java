/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.form.editors.masterdetails.internal;

import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPagePart;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProblemsTraversalServiceCondition extends ServiceCondition
{
    @Override
    public boolean applicable( final ServiceContext context )
    {
        return ( context.find( SapphirePart.class ) instanceof MasterDetailsEditorPagePart );
    }
    
}