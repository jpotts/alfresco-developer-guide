package com.someco.extracter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.metadata.AbstractMappingMetadataExtracter;
import org.alfresco.service.cmr.repository.ContentReader;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;

public class EnhancedPdfExtracter extends AbstractMappingMetadataExtracter {
	
	    private static final String KEY_AUTHOR = "author";
	    private static final String KEY_TITLE = "title";
	    private static final String KEY_SUBJECT = "subject";
	    private static final String KEY_CREATED = "created";
	    private static final String KEY_KEYWORDS = "keywords";
	    
	    public static String[] SUPPORTED_MIMETYPES = new String[] {MimetypeMap.MIMETYPE_PDF };

	    public EnhancedPdfExtracter() {
	        super(new HashSet<String>(Arrays.asList(SUPPORTED_MIMETYPES)));
	    }
	    
	    @Override
	    public Map<String, Serializable> extractRaw(ContentReader reader) throws Throwable
	    {
	        Map<String, Serializable> rawProperties = newRawMap();
	        
	        PDDocument pdf = null;
	        InputStream is = null;
	        try
	        {
	            is = reader.getContentInputStream();
	            // stream the document in
	            pdf = PDDocument.load(is);
	            if (!pdf.isEncrypted())
	            {
	                // Scoop out the metadata
	                PDDocumentInformation docInfo = pdf.getDocumentInformation();
	    
	                putRawValue(KEY_AUTHOR, docInfo.getAuthor(), rawProperties);
	                putRawValue(KEY_TITLE, docInfo.getTitle(), rawProperties);
	                putRawValue(KEY_SUBJECT, docInfo.getSubject(), rawProperties);
	                putRawValue(KEY_KEYWORDS, docInfo.getKeywords(), rawProperties);
	    
	                Calendar created = docInfo.getCreationDate();
	                if (created != null)
	                {
	                    putRawValue(KEY_CREATED, created.getTime(), rawProperties);
	                }
	            }
	        }
	        finally
	        {
	            if (is != null)
	            {
	                try { is.close(); } catch (IOException e) {}
	            }
	            if (pdf != null)
	            {
	                try { pdf.close(); } catch (Throwable e) { e.printStackTrace(); }
	            }
	        }
	        // Done
	        return rawProperties;
	    }
	}
