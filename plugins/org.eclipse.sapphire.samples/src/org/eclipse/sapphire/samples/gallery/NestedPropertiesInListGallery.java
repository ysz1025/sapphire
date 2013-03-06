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

package org.eclipse.sapphire.samples.gallery;

import java.math.BigDecimal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface NestedPropertiesInListGallery extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( NestedPropertiesInListGallery.class );
    
    // *** Employees ***
    
    interface Employee extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( Employee.class );
        
        // *** Name ***
        
        interface Name extends IModelElement
        {
            ModelElementType TYPE = new ModelElementType( Name.class );
            
            // *** First ***
            
            @Label( standard = "first name" )
            @Required
            
            ValueProperty PROP_FIRST_NAME = new ValueProperty( TYPE, "First" );
            
            Value<String> getFirst();
            void setFirst( String value );
            
            // *** Last ***
            
            @Label( standard = "last name" )
            @Required
            
            ValueProperty PROP_LAST_NAME = new ValueProperty( TYPE, "Last" );
            
            Value<String> getLast();
            void setLast( String value );
        }
        
        @Type( base = Name.class )
        @Label( standard = "name" )
        
        ImpliedElementProperty PROP_NAME = new ImpliedElementProperty( TYPE, "Name" );
        
        Name getName();
        
        // *** Location ***
        
        interface Location extends IModelElement
        {
            ModelElementType TYPE = new ModelElementType( Location.class );
            
            // *** City ***
            
            @Label( standard = "city" )
            @Required
            
            ValueProperty PROP_CITY = new ValueProperty( TYPE, "City" );
            
            Value<String> getCity();
            void setCity( String value );
            
            // *** Country ***
            
            @Label( standard = "country" )
            @Required
            
            ValueProperty PROP_COUNTRY = new ValueProperty( TYPE, "Country" );
            
            Value<String> getCountry();
            void setCountry( String value );
        }
        
        @Type( base = Location.class )
        @Label( standard = "location" )
        
        ImpliedElementProperty PROP_LOCATION = new ImpliedElementProperty( TYPE, "Location" );
        
        Location getLocation();
        
        // *** Salary ***
        
        @Type( base = BigDecimal.class )
        @Label( standard = "salary" )
        @Required
        
        ValueProperty PROP_SALARY = new ValueProperty( TYPE, "Salary" );
        
        Value<BigDecimal> getSalary();
        void setSalary( String value );
        void setSalary( BigDecimal value );
    }
    
    @Type( base = Employee.class )
    @Label( standard = "employees" )
    
    ListProperty PROP_EMPLOYEES = new ListProperty( TYPE, "Employees" );
    
    ModelElementList<Employee> getEmployees();

}
