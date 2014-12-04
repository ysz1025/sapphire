/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.renderers.swt;

import static org.eclipse.sapphire.ui.SapphirePropertyEditor.HINT_ASSIST_CONTRIBUTORS;
import static org.eclipse.sapphire.ui.SapphirePropertyEditor.HINT_SUPPRESS_ASSIST_CONTRIBUTORS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.SapphireImageCache;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ActionsSectionAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.DefaultValueInfoAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.InfoSectionAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.MinMaxInfoAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ProblemsAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ProblemsSectionAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDialog;
import org.eclipse.sapphire.ui.assist.internal.ResetActionsAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ShowInSourceActionAssistContributor;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public final class CompactListTextAssistDecorator
{
    private static final List<PropertyEditorAssistContributor> SYSTEM_CONTRIBUTORS
        					= new ArrayList<PropertyEditorAssistContributor>();
    
    static
    {
        SYSTEM_CONTRIBUTORS.add( new InfoSectionAssistContributor() );
        SYSTEM_CONTRIBUTORS.add( new DefaultValueInfoAssistContributor() );
        SYSTEM_CONTRIBUTORS.add( new MinMaxInfoAssistContributor() );
        SYSTEM_CONTRIBUTORS.add( new ProblemsSectionAssistContributor() );
        SYSTEM_CONTRIBUTORS.add( new ActionsSectionAssistContributor() );
        SYSTEM_CONTRIBUTORS.add( new ResetActionsAssistContributor() );
        SYSTEM_CONTRIBUTORS.add( new ShowInSourceActionAssistContributor() );
    }
    
    private final SapphirePropertyEditor parentPropertyEditor;
    private final SapphireRenderingContext context;
    private final Label control;
    private final ModelProperty property;
    private PropertyEditorAssistContext assistContext;
    private IStatus problem;
    private boolean mouseOverEditorControl;
    private EditorControlMouseTrackListener mouseTrackListener;
    
    private CompactTextBinding binding;
    
    public CompactListTextAssistDecorator( final SapphirePropertyEditor propertyEditor,
    									   final ModelProperty property,
                                           final SapphireRenderingContext context,
                                           final Composite parent )
    {
        this.parentPropertyEditor = propertyEditor;
        this.context = context;
        this.property = property;
        this.mouseOverEditorControl = false;
        this.mouseTrackListener = new EditorControlMouseTrackListener();
        
        this.control = new Label( parent, SWT.NONE );
        this.context.adapt( this.control );
        
        this.control.addMouseListener
        (
            new MouseAdapter()
            {
                @Override
                public void mouseUp( final MouseEvent event )
                {
                    openAssistDialog();
                }
            }
        );
        
        this.control.addMouseTrackListener
        (
            new EditorControlMouseTrackListener()
            {
                @Override
                public void mouseEnter( MouseEvent event )
                {
                    super.mouseEnter( event );
                    refreshImageAndCursor();
                }

                @Override
                public void mouseHover( final MouseEvent event )
                {
                    // Suppress default behavior.
                }
            }
        );
        
        refresh();
    }
    
    public CompactTextBinding getBinding() {
		return this.binding;
	}

	public void setBinding(CompactTextBinding binding) {
		this.binding = binding;
	}

	public Label getControl()
    {
        return this.control;
    }
    
    public SapphireRenderingContext getUiContext()
    {
        return this.context;
    }
    
    public Shell getShell()
    {
        return this.context.getShell();
    }
    
    public void addEditorControl( final Control control )
    {
        if( control instanceof Composite )
        {
            for( Control child : ( (Composite) control ).getChildren() )
            {
                addEditorControl( child );
            }
        }
        
        control.addMouseTrackListener( this.mouseTrackListener );
    }
    
    public void removeEditorControl( final Control control )
    {
        if( control instanceof Composite )
        {
            for( Control child : ( (Composite) control ).getChildren() )
            {
                removeEditorControl( child );
            }
        }
        
        control.removeMouseTrackListener( this.mouseTrackListener );
    }

    public void openAssistDialog()
    {
        if( this.assistContext != null && ! this.assistContext.isEmpty() )
        {
            final Rectangle bounds = this.control.getBounds();
            Point position = this.control.getParent().toDisplay( new Point( bounds.x, bounds.y ) );
            position = new Point( position.x + bounds.width + 4, position.y + 2 );
            
            final PropertyEditorAssistDialog dialog 
                = new PropertyEditorAssistDialog( getShell(), position, this.assistContext );
            
            dialog.open();
        }
    }
    
    public void refresh()
    {
    	final IModelElement element = getBinding() != null ? getBinding().getModelElement() : null;
        
        final boolean enabled 
            = ( element == null ? false : element.isPropertyEnabled( this.property ) );
        
        if( enabled )
        {
            if( this.property instanceof ValueProperty )
            {
                final Value<?> value = element.read( (ValueProperty) this.property );
                this.problem = value.validate();
            }
            else if( this.property instanceof ListProperty )
            {
                final ModelElementList<?> list = element.read( (ListProperty) this.property );
                this.problem = list.validate();
            }
            else
            {
                throw new IllegalStateException( this.property.getClass().getName() );
            }
            
	        final SapphirePropertyEditor propertyEditor = element != null ? this.parentPropertyEditor.getChildPropertyEditor( element, this.property ) : null;
            this.assistContext = element != null ? new PropertyEditorAssistContext( propertyEditor, this.context ) : null;
            
            final List<PropertyEditorAssistContributor> contributors 
                = new ArrayList<PropertyEditorAssistContributor>( SYSTEM_CONTRIBUTORS );
            
            contributors.add( new ProblemsAssistContributor( this.problem ) );
            

            Collection<String> contributorsToSuppress = propertyEditor != null ? propertyEditor.getRenderingHint( HINT_SUPPRESS_ASSIST_CONTRIBUTORS, Collections.<String>emptyList() ) : null;
            for( String id : contributorsToSuppress )
            {
                for( Iterator<PropertyEditorAssistContributor> itr = contributors.iterator(); itr.hasNext(); )
                {
                    final PropertyEditorAssistContributor contributor = itr.next();
                    
                    if( contributor.getId().equals( id ) )
                    {
                        itr.remove();
                        break;
                    }
                }
            }
            
            Collection<Class<?>> additionalContributors = propertyEditor.getRenderingHint( HINT_ASSIST_CONTRIBUTORS, Collections.<Class<?>>emptyList() );
            for( Class<?> cl : additionalContributors )
            {
                try
                {
                    contributors.add( (PropertyEditorAssistContributor) cl.newInstance() );
                }
                catch( Exception e )
                {
                    SapphireUiFrameworkPlugin.log( e );
                }
            }

            Collections.sort
            ( 
                contributors, 
                new Comparator<PropertyEditorAssistContributor>()
                {
                    public int compare( final PropertyEditorAssistContributor c1,
                                        final PropertyEditorAssistContributor c2 )
                    {
                        return ( c1.getPriority() - c2.getPriority() ); 
                    }
                }
            );
            
            for( PropertyEditorAssistContributor c : contributors )
            {
                c.contribute( this.assistContext );
            }
            
            if( this.assistContext.isEmpty() )
            {
                this.assistContext = null;
            }
            else
            {
                final int valResultSeverity = this.problem.getSeverity();
                
                if( valResultSeverity != Status.ERROR && valResultSeverity != Status.WARNING && valResultSeverity != Status.INFO )
                {
                    this.problem = null;
                }
            }
        }
        else
        {
            this.assistContext = null;
            this.problem = null;
        }

        refreshImageAndCursor();
    }
    
    private void refreshImageAndCursor()
    {
        if( this.control.isDisposed() ) 
        {
            return;
        }
        
        final SapphireImageCache imageCache = this.parentPropertyEditor.getImageCache();
        
        if( this.assistContext != null )
        {
            if( this.problem != null )
            {
                final int severity = this.problem.getSeverity();
                final String fieldDecorationId;
                
                switch( severity )
                {
                    case Status.ERROR:
                    {
                        fieldDecorationId = FieldDecorationRegistry.DEC_ERROR;
                        break;
                    }
                    case Status.WARNING:
                    {
                        fieldDecorationId = FieldDecorationRegistry.DEC_WARNING;
                        break;
                    }
                    default:
                    {
                        fieldDecorationId = FieldDecorationRegistry.DEC_INFORMATION;
                        break;
                    }
                }
                
                final FieldDecoration fieldDecoration
                    = FieldDecorationRegistry.getDefault().getFieldDecoration( fieldDecorationId );
            
                this.control.setImage( fieldDecoration.getImage() );
            }
            else
            {
                if( this.mouseOverEditorControl )
                {
                    this.control.setImage( imageCache.getImage( SapphireImageCache.DECORATOR_ASSIST ) );
                }
                else
                {
                    this.control.setImage( imageCache.getImage( SapphireImageCache.DECORATOR_ASSIST_FAINT ) );
                }
            }
            
            this.control.setVisible( true );
            this.control.setCursor( Display.getCurrent().getSystemCursor( SWT.CURSOR_HAND ) );
        }
        else
        {
            this.control.setVisible( false );
            this.control.setImage( imageCache.getImage( SapphireImageCache.DECORATOR_BLANK ) );
            this.control.setCursor( null );
        }
    }
    
    private class EditorControlMouseTrackListener
    
        extends MouseTrackAdapter
        
    {
        @Override
        public void mouseEnter( final MouseEvent event )
        {
            CompactListTextAssistDecorator.this.mouseOverEditorControl = true;
        }
        
        @Override
        public void mouseHover( final MouseEvent event )
        {
            refreshImageAndCursor();
        }

        @Override
        public void mouseExit( final MouseEvent event )
        {
            CompactListTextAssistDecorator.this.mouseOverEditorControl = false;
            performedDelayedImageRefresh();
        }
        
        private void performedDelayedImageRefresh()
        {
            final Runnable op = new Runnable()
            {
                public void run()
                {
                    refreshImageAndCursor();
                }
            };
            
            final Thread thread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep( 250 );
                    }
                    catch( InterruptedException e ) {}
                    
                    Display.getDefault().asyncExec( op );
                }
            };
            
            thread.start();
        }
    };

}