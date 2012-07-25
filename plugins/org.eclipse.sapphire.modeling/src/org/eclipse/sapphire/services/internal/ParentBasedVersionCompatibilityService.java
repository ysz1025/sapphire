/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;
import org.eclipse.sapphire.services.VersionCompatibilityAggregationService;
import org.eclipse.sapphire.services.VersionCompatibilityService;

/**
 * Implementation of VersionCompatibilityService that derives its behavior from version compatibility of the containing
 * element's parent property.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ParentBasedVersionCompatibilityService extends VersionCompatibilityService
{
    private VersionCompatibilityAggregationService parentVersionCompatibilityService;
    private Listener parentVersionCompatibilityServiceListener;
    
    @Override
    protected void initVersionCompatibilityService()
    {
        final IModelElement element = context( IModelElement.class );
        final IModelParticle parentModelParticle = element.parent();
        
        final IModelElement parentModelElement;
        
        if( parentModelParticle instanceof IModelElement )
        {
            parentModelElement = (IModelElement) parentModelParticle;
        }
        else
        {
            parentModelElement = (IModelElement) parentModelParticle.parent();
        }
        
        this.parentVersionCompatibilityService = parentModelElement.service( element.getParentProperty(), VersionCompatibilityAggregationService.class );
        
        this.parentVersionCompatibilityServiceListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                refresh();
            }
        };
        
        this.parentVersionCompatibilityService.attach( this.parentVersionCompatibilityServiceListener );
    }
    
    @Override
    protected Data compute()
    {
        return this.parentVersionCompatibilityService.data();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.parentVersionCompatibilityService != null )
        {
            this.parentVersionCompatibilityService.detach( this.parentVersionCompatibilityServiceListener );
        }
    }
    
    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            final IModelElement element = context.find( IModelElement.class );
            final IModelParticle parentModelParticle = element.parent();
            
            if( parentModelParticle != null )
            {
                final IModelElement parentModelElement;
                
                if( parentModelParticle instanceof IModelElement )
                {
                    parentModelElement = (IModelElement) parentModelParticle;
                }
                else
                {
                    parentModelElement = (IModelElement) parentModelParticle.parent();
                }
                
                return parentModelElement.service( element.getParentProperty(), VersionCompatibilityAggregationService.class ) != null;
            }
            
            return false;
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            return new ParentBasedVersionCompatibilityService();
        }
    }
    
}
