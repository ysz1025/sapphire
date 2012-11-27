/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.PropertyEvent;
import org.eclipse.sapphire.ui.diagram.shape.def.ImageDef;
import org.eclipse.sapphire.ui.diagram.shape.def.RectangleDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeFactoryCaseDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeFactoryDef;
import org.eclipse.sapphire.ui.diagram.shape.def.TextDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeFactoryPart extends ShapePart 
{
	private ShapeFactoryDef shapeFactoryDef;
	private IModelElement modelElement;	
	private ListProperty modelProperty;
	private String propertyName;
	private List<ShapePart> children;
	private Listener shapePropertyListener;
	private List<JavaType> javaTypes;
	
	@Override
    protected void init()
    {
        super.init();
        this.modelElement = getModelElement();
        this.shapeFactoryDef = (ShapeFactoryDef)super.definition;
        this.children = new ArrayList<ShapePart>();
        this.javaTypes = new ArrayList<JavaType>();
        
        for (ShapeFactoryCaseDef shapeCase : this.shapeFactoryDef.getShapeFactoryCases())
        {
        	this.javaTypes.add(shapeCase.getType().resolve());
        }
        
        this.propertyName = this.shapeFactoryDef.getProperty().getContent();
        this.modelProperty = (ListProperty)resolve(this.modelElement, propertyName);
        ModelElementList<?> list = this.modelElement.read(this.modelProperty);
        for( IModelElement listEntryModelElement : list )
        {
        	ShapeFactoryCaseDef shapeFactoryCase = getShapeFactoryCase(listEntryModelElement);
        	ShapeDef shapeDef = shapeFactoryCase.getShape().element();
        	ShapePart childShapePart = createShapePart(shapeDef, listEntryModelElement);
        	if (childShapePart != null)
        	{
        		this.children.add(childShapePart);
        	}
        }
        
        // Add listeners
        this.shapePropertyListener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
            {
                handleModelPropertyChange( event );
            }
        };
        this.modelElement.attach(this.shapePropertyListener, this.propertyName);
        
    }
	
	public List<ShapePart> getChildren()
	{
		return this.children;
	}
	
	@Override
    public List<ShapePart> getActiveChildren()
    {
    	return this.children;
    }	
	
	public List<JavaType> getSupportedTypes()
	{
		return this.javaTypes;
	}
	    
    public ShapePart newShape(JavaType javaType)
    {
    	ModelElementList<?> list = this.modelElement.read(this.modelProperty);
    	final Class cl = javaType.artifact();
    	IModelElement element = list.insert(cl);
    	return getShapePart(element);
    }
	
    public ModelElementList<IModelElement> getModelElementList()
    {
    	ModelElementList<IModelElement> list = this.modelElement.read(this.modelProperty);
    	return list;
    }
    
    public void moveChild(ShapePart childPart, int newIndex)
    {
    	ModelElementList<IModelElement> list = this.modelElement.read(this.modelProperty);
    	int oldIndex = list.indexOf(childPart.getLocalModelElement());
    	this.modelElement.detach(this.shapePropertyListener, this.propertyName);
    	if (oldIndex < newIndex)
    	{
    		for (int i = oldIndex; i < newIndex; i++)
    		{
    			list.moveDown(childPart.getLocalModelElement());
    		}
    	}
    	else
    	{
    		for (int i = newIndex; i < oldIndex; i++)
    		{
    			list.moveUp(childPart.getLocalModelElement());
    		}
    	}
    	this.modelElement.attach(this.shapePropertyListener, this.propertyName);
    	this.children.remove(childPart);
    	this.children.add(newIndex, childPart);
    	getNodePart().reorderShapes(this);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        this.modelElement.detach(this.shapePropertyListener, this.propertyName);
    }
    
    private ShapePart getShapePart(IModelElement element)
    {
        List<ShapePart> shapeParts = getChildren();
        for (ShapePart shapePart : shapeParts)
        {
            if (shapePart.getLocalModelElement().equals(element))
            {
                return shapePart;
            }
        }
        return null;
    }

    private ShapeFactoryCaseDef getShapeFactoryCase(IModelElement listEntryModelElement)
	{
        for (ShapeFactoryCaseDef shapeFactoryCase : this.shapeFactoryDef.getShapeFactoryCases())
        {
        	JavaType javaType = shapeFactoryCase.getType().resolve();
        	Class<?> cl = javaType.artifact();
        	if (cl.isAssignableFrom(listEntryModelElement.getClass()))
        	{
        		return shapeFactoryCase;
        	}
        			
        }
		return null;
	}
	
    private ShapePart createShapePart(ShapeDef shapeDef, IModelElement modelElement)
    {
    	ShapePart shapePart = null;
    	if (shapeDef instanceof TextDef)
    	{
	        shapePart = new TextPart();
    	}
    	else if (shapeDef instanceof ImageDef)
    	{
    		shapePart = new ImagePart();
    	}
    	else if (shapeDef instanceof RectangleDef)
    	{
    		shapePart = new RectanglePart();
    	}
    	else if (shapeDef instanceof ShapeFactoryDef)
    	{
    		shapePart = new ShapeFactoryPart();
    	}
    	if (shapePart != null)
    	{
    		shapePart.init(this, modelElement, shapeDef, Collections.<String,String>emptyMap());
    		shapePart.setActive(true);
    	}
    	return shapePart;
    }
    
    private void handleModelPropertyChange(final PropertyEvent event)
    {
    	final IModelElement element = event.element();
    	final ModelProperty property = event.property();
    	ModelElementList<IModelElement> newList = (ModelElementList<IModelElement>)element.read(property);
    	
    	List<ShapePart> children = getChildren();
		List<IModelElement> oldList = new ArrayList<IModelElement>(children.size());
		for (ShapePart shapePart : children)
		{
			oldList.add(shapePart.getLocalModelElement());
		}
    	
    	List<IModelElement> deletedShapes = ListUtil.ListDiff(oldList, newList);
    	List<IModelElement> newShapes = ListUtil.ListDiff(newList, oldList);
    	DiagramNodePart nodePart = this.getNodePart();
    	if (deletedShapes.isEmpty() && newShapes.isEmpty() && ListUtil.ListDiffers(oldList, newList))
    	{
    		// List has been re-ordered
    		List<ShapePart> newChildren = new ArrayList<ShapePart>();
    		for (IModelElement listEle : newList)
    		{
    			ShapePart shapePart = getShapePart(listEle);
    			newChildren.add(shapePart);
    		}
    		this.children.clear();
    		this.children.addAll(newChildren);
    		nodePart.reorderShapes(this);
    	}
    	else
    	{
			for (IModelElement deletedShape : deletedShapes)
			{
				ShapePart shapePart = getShapePart(deletedShape);
				if (shapePart != null)
				{
					shapePart.dispose();
					this.children.remove(shapePart);
					nodePart.deleteShape(shapePart);
				}
			}    	    	
			for (IModelElement newShape : newShapes)
			{
	        	ShapeFactoryCaseDef shapeFactoryCase = getShapeFactoryCase(newShape);
	        	ShapeDef shapeDef = shapeFactoryCase.getShape().element();
				
		    	ShapePart shapePart = createShapePart(shapeDef, newShape);
		    	this.children.add(shapePart);
		    	nodePart.addShape(shapePart);
			}
    	}    	
    }
	
}
