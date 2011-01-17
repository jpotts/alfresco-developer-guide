package com.someco.action.evaluator;

import javax.faces.context.FacesContext;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.web.action.ActionEvaluator;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;

import com.someco.model.SomeCoModel;

public class InterviewSetupEvaluator implements ActionEvaluator {
	
	private static final long serialVersionUID = -7356371395117066842L;

	public boolean evaluate(Node node) {

		FacesContext fc = FacesContext.getCurrentInstance();
		DictionaryService dd = Repository.getServiceRegistry(fc).getDictionaryService();
		
		if (dd.isSubClass(node.getType(), SomeCoModel.TYPE_SC_RESUME)) {
			return true;
		} else {
			return false;		
		}
	}
	
	public boolean evaluate(Object obj) {
		if (obj instanceof Node) {
			return evaluate((Node)obj);
		} else {
			// if you don't give me a Node, I don't know how to evaluate
			return false;
		}
	}

}
