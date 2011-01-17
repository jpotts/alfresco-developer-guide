package com.someco.model;

import org.alfresco.service.namespace.QName;


public interface SomeCoModel {
    
	// Types
	public static final String NAMESPACE_SOMECO_CONTENT_MODEL  = "http://www.someco.com/model/content/1.0";
	public static final String TYPE_SC_DOC_STRING = "doc";
    public static final QName TYPE_SC_DOC = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.TYPE_SC_DOC_STRING);
    public static final String TYPE_SC_WHITEPAPER_STRING = "whitepaper";
    public static final QName TYPE_SC_WHITEPAPER = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "whitepaper");
    public static final QName TYPE_SC_RATING = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "rating");
	
    // Aspects
	public static final String ASPECT_SC_WEBABLE_STRING = "webable";
    public static final QName ASPECT_SC_WEBABLE = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.ASPECT_SC_WEBABLE_STRING);
    public static final String ASPECT_SC_CLIENT_RELATED_STRING = "clientRelated";
    public static final QName ASPECT_SC_CLIENT_RELATED = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.ASPECT_SC_CLIENT_RELATED_STRING);
    public static final QName ASPECT_SC_RATEABLE = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "rateable");
    
    // Properties
    public static final String PROP_CLIENT_NAME_STRING = "clientName";
    public static final QName PROP_CLIENT_NAME = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.PROP_CLIENT_NAME_STRING);
    public static final QName PROP_CLIENT_PROJECT = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "projectName");
    public static final String PROP_PUBLISHED_STRING = "published";
    public static final QName PROP_PUBLISHED = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.PROP_PUBLISHED_STRING);
    public static final String PROP_IS_ACTIVE_STRING = "isActive";
    public static final QName PROP_IS_ACTIVE = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.PROP_IS_ACTIVE_STRING);
    public static final QName PROP_RATING = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "rating");
    public static final QName PROP_RATER = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "rater");
    public static final QName PROP_AVERAGE_RATING= QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "averageRating");
    public static final QName PROP_TOTAL_RATING= QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "totalRating");
    public static final QName PROP_RATING_COUNT= QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "ratingCount");
    
    // Associations
    public static final String ASSN_RELATED_DOCUMENTS_STRING = "relatedDocuments";
    public static final QName ASSN_RELATED_DOCUMENTS = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, SomeCoModel.ASSN_RELATED_DOCUMENTS_STRING);
    public static final QName ASSN_SC_RATINGS = QName.createQName(SomeCoModel.NAMESPACE_SOMECO_CONTENT_MODEL, "ratings");

}
