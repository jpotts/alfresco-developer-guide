package com.someco.transformer;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Resource;
import net.sf.mpxj.Task;
import net.sf.mpxj.mpp.MPPReader;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;

public class ProjectToPlainText extends AbstractContentTransformer2 {

	public static final String MIMETYPE_PROJECT = "application/vnd.ms-project";
	
	@Override
	protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options)	throws Exception {

		Writer out = new BufferedWriter(new OutputStreamWriter(writer.getContentOutputStream()));
		
		ProjectFile mpp = new MPPReader().read (reader.getContentInputStream());
		ProjectHeader projectHeader = mpp.getProjectHeader();
		List<Task> listAllTasks = mpp.getAllTasks();
		List<Resource> listAllResources = mpp.getAllResources();
		
		out.write(projectHeader.getProjectTitle());
		for (Task task : listAllTasks) {
			out.write("ID:" + task.getID());
			out.write(" TASK:" + task.getName());
			if (task.getNotes() != null) out.write(" NOTES:" + task.getNotes());
			if (task.getContact() != null) out.write(" CONTACT:" + task.getContact());
			out.write("\r\n");
		}
		for (Resource resource : listAllResources) {
			out.write("RESOURCE:" + resource.getName());
			if (resource.getEmailAddress() != null) out.write(" EMAIL:" + resource.getEmailAddress());
			if (resource.getNotes() != null) out.write(" NOTES:" + resource.getNotes());
			out.write("\r\n");
		}

		out.flush();
		
		if (out != null) {
			out.close();
		}

	}

	public double getReliability(String sourceMimetype, String targetMimetype) {
		
		// only support MPP -> TEXT
		if (sourceMimetype.equals(ProjectToPlainText.MIMETYPE_PROJECT) &&
	            targetMimetype.equals(MimetypeMap.MIMETYPE_TEXT_PLAIN)) {
	            	return 1.0;
	    } else {
	    	return 0.0;
	    }

	}

	public boolean isTransformable(String sourceMimetype,
		String targetMimetype, TransformationOptions options) {
		return (getReliability(sourceMimetype, targetMimetype) == 1.0);
	}

}
