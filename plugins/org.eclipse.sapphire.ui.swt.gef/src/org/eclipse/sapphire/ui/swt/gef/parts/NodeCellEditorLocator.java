/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.sapphire.ui.swt.gef.parts;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

final public class NodeCellEditorLocator implements CellEditorLocator {

	private TextFigure textFigure;
	private DiagramConfigurationManager manager;

	public NodeCellEditorLocator(DiagramConfigurationManager manager, TextFigure textFigure) {
		this.manager = manager;
		setLabel(textFigure);
	}

	public void relocate(CellEditor celleditor) {
		double zoom = manager.getDiagramEditor().getZoomLevel();
		Rectangle labelRect = textFigure.getClientArea();
		Rectangle parentRect = textFigure.getAvailableArea();

		labelRect.x = parentRect.x;
		labelRect.width = parentRect.width;
		// zoom
		labelRect.width = (int) (labelRect.width * zoom);
		
		Text text = (Text) celleditor.getControl();
		Point size = text.computeSize(-1, -1);
		// 
		size.x = Math.min(size.x, labelRect.width);
		if (text.getText().length() == 0) {
			size.x = 10;
		}
		
		// center the cell editor
		int offset = 0;
		switch (textFigure.getHorizontalAlignment()) {
		case SWT.RIGHT:
			if (size.x < labelRect.width) {
				offset = labelRect.width - size.x;
			}
			break;
		case SWT.LEFT:
			break;
		default:
			if (size.x < labelRect.width) {
				offset = (labelRect.width - size.x + 1) / 2;
			}
			break;
		}

		size.x = Math.min(size.x, labelRect.width);
		
		textFigure.translateToAbsolute(labelRect);
		
		text.setBounds(labelRect.x + offset, labelRect.y, size.x, size.y);
	}
	
	protected TextFigure getLabel() {
		return textFigure;
	}

	protected void setLabel(TextFigure textFigure) {
		this.textFigure = textFigure;
	}

}