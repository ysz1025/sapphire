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

package org.eclipse.sapphire.ui.def.internal;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.services.ReferenceService;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.forms.SectionDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SectionReferenceService extends ReferenceService<SectionDef>
{
    @Override
    protected SectionDef compute()
    {
        final String reference = context( Value.class ).text();
        return (SectionDef) context( ISapphireUiDef.class ).getPartDef( reference, true, SectionDef.class );
    }
    
}
