package org.alfresco.web.bean.generator;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.ui.common.ComponentConstants;
import org.alfresco.web.ui.repo.component.property.PropertySheetItem;
import org.alfresco.web.ui.repo.component.property.UIProperty;
import org.alfresco.web.ui.repo.component.property.UIPropertySheet;

import com.someco.util.Constants;

public class StoplightGenerator extends TextFieldGenerator {	

	@Override
	public UIComponent generate(FacesContext context, String id) {
		UIComponent component = context.getApplication().createComponent(ComponentConstants.JAVAX_FACES_OUTPUT);
		component.setRendererType(Constants.STOPLIGHT_DISPLAY_RENDERER);
		FacesHelper.setupComponentId(context, component, id);
		return component;
	}

   @Override
   @SuppressWarnings("unchecked")
   protected UIComponent createComponent(FacesContext context, UIPropertySheet propertySheet, PropertySheetItem item) {
	   UIComponent component = null;

	   if (item instanceof UIProperty) {
		   if (!propertySheet.inEditMode()) {
			   component = generate(context, item.getName());
		   } else {
			   // if the field has the list of values constraint 
			   // and it is editable a SelectOne component is 
			   // required otherwise create the standard edit component
			   ListOfValuesConstraint constraint = getListOfValuesConstraint(context, propertySheet, item);
		         
			   PropertyDefinition propDef = this.getPropertyDefinition(context,propertySheet.getNode(), item.getName());
		         
			   if (constraint != null && item.isReadOnly() == false &&
					propDef != null && propDef.isProtected() == false) {
				   component = context.getApplication().createComponent(UISelectOne.COMPONENT_TYPE);
				   FacesHelper.setupComponentId(context, component, item.getName());
		            
				   // create the list of choices
				   UISelectItems itemsComponent = (UISelectItems)context.getApplication().createComponent("javax.faces.SelectItems");
		            
				   List<SelectItem> items = new ArrayList<SelectItem>(3);
				   List<String> values = constraint.getAllowedValues();
				   for (String value : values) {
					   Object obj = null;
		               
					   // we need to setup the list with objects of the correct type
					   if (propDef.getDataType().getName().equals(DataTypeDefinition.INT)) {
						   obj = Integer.valueOf(value);
					   } else if (propDef.getDataType().getName().equals(DataTypeDefinition.LONG)) {
						   obj = Long.valueOf(value);
					   } else if (propDef.getDataType().getName().equals(DataTypeDefinition.DOUBLE)) {
						   obj = Double.valueOf(value);
					   } else if (propDef.getDataType().getName().equals(DataTypeDefinition.FLOAT)) {
						   obj = Float.valueOf(value);
					   } else {
						   obj = value;
					   }
		               
					   items.add(new SelectItem(obj, value));
				   }
		            
				   itemsComponent.setValue(items);
		            
				   // add the items as a child component				   
				   component.getChildren().add(itemsComponent);
			   } else {
				   // use the standard component in edit mode
				   component = super.generate(context, item.getName());
			   }			   
		   }
	   }
	   return component;
   }

}
