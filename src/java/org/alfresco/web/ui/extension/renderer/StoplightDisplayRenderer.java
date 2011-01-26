package org.alfresco.web.ui.extension.renderer;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.renderer.BaseRenderer;

public class StoplightDisplayRenderer extends BaseRenderer {

	public boolean isMultiple(UIComponent component) {
		return false;
	}	

	public boolean getRendersChildren() {
		return false;
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (component.isRendered() == false) return;

		ResponseWriter out = context.getResponseWriter();

		// render the field
		if(isMultiple(component)) {
			out.write(Utils.encode("Multiple value property fields are not supported."));
		} else {
			
			String value = (String)((ValueHolder)component).getValue();
			String stoplightImageRef = null;
	        if(value == null) {
	        	stoplightImageRef = "/alfresco/someco/images/icons/stoplight-disable.png";
	        	value = "";
	        } else if (value.toLowerCase().equals("red")){
	        	stoplightImageRef = "/alfresco/someco/images/icons/stoplight-red.png";
	        } else if (value.toLowerCase().equals("yellow")) {
	        	stoplightImageRef = "/alfresco/someco/images/icons/stoplight-yellow.png";
	        } else if (value.toLowerCase().equals("green")) {
	        	stoplightImageRef = "/alfresco/someco/images/icons/stoplight-green.png";
	        } else {
	        	stoplightImageRef = "/alfresco/someco/images/icons/stoplight-disable.png";
	        	value = "";
	        }
	                
	    	// display stoplight image tag
	        out.write("<div>");
	        out.write("<img src=\"" + stoplightImageRef + "\" alt=\"" + value + "\"/>");
	        out.write("</div>");	        	
		}		
	}
	
	public void decode(FacesContext context, UIComponent component) {
		if (Utils.isComponentDisabledOrReadOnly(component)) {
			return;
		}
	}
}
