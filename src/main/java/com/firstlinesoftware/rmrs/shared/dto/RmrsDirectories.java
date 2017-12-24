package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.base.shared.directories.DirectoryType;

public class RmrsDirectories {
    public static final String RELATION_TYPE_BASED_ON = "type_13.type_14";
    public static final String RELATION_TYPE_RELATED_WITH = "relatedWith.relatedTo";
    public static final String RELATION_TYPE_CREATED_ON = "created.createdOn";
    public static final String RELATION_TYPE_CHANGES = "changes.basedOn";
    public static final String RELATION_TYPE_DUPLICATE = "duplicate_proposal.original_proposal";
    public static final String RELATION_TYPE_REPLACE_BY = "replace.by";
    public static final DirectoryType TAGS = new DirectoryType("tags");
}
