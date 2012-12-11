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

package org.eclipse.sapphire.ui.swt.gef.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeFactoryModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;
import org.eclipse.sapphire.ui.swt.gef.policies.ShapeFactoryLayoutEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.presentation.ContainerShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapePresentation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeFactoryEditPart extends ShapeEditPart
{
	public ShapeFactoryEditPart(DiagramConfigurationManager configManager) 
	{
    	super(configManager);    	
    }

	@Override
	protected IFigure createFigure() 
	{
		return null;
	}
	
	@Override
	public IFigure getContentPane() 
	{
		ShapeFactoryModel model = (ShapeFactoryModel)getModel();
		ShapePresentation parentPresentation = getParentContainer(model.getShapePresentation());
		IFigure parentFigure = parentPresentation.getFigure();
		return parentFigure;
	}
	
	@Override
	protected List<ShapeModel> getModelChildren() 
	{
		List<ShapeModel> returnedModelChildren = new ArrayList<ShapeModel>();
		ShapeFactoryModel containerModel = getCastedModel();
		returnedModelChildren.addAll(containerModel.getChildren());
		return returnedModelChildren;
	}

	@Override
	protected void addChildVisual(EditPart childEditPart, int index) 
	{
		IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
		
		ShapeModel shapeModel = (ShapeModel)childEditPart.getModel();
		ShapePresentation shapePresentation = shapeModel.getShapePresentation();
		ContainerShapePresentation parentPresentation = getParentContainer(shapePresentation);
		Object layoutConstraint = ShapeUtil.getLayoutConstraint(shapePresentation, 
				parentPresentation.getLayout());
		IFigure parentFigure = parentPresentation.getFigure();
		parentFigure.add(child, layoutConstraint, index);
		
	}
	
	@Override
	protected void createEditPolicies() 
	{
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ShapeFactoryLayoutEditPolicy());
	}
	
	public ShapeFactoryModel getCastedModel() 
	{
		return (ShapeFactoryModel)getModel();
	}
	
	public void propertyChange(PropertyChangeEvent evt) 
	{
		String prop = evt.getPropertyName();
		if (prop.equals(ShapeFactoryModel.SHAPE_ADD))
		{
			refreshChildren();
		}
		else if (prop.equals(ShapeFactoryModel.SHAPE_DELETE))
		{
			refreshChildren();
		}
		else if (prop.equals(ShapeFactoryModel.SHAPE_REORDER))
		{
			refreshChildren();
		}
		
	}	
	
}
