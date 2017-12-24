package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.fields.GridField;
import com.firstlinesoftware.base.client.widgets.GridEditor;
import com.firstlinesoftware.rmrs.client.columns.RequirementColumns;
import com.firstlinesoftware.rmrs.client.widgets.RequirementsGridEditor;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

public abstract class RequirementsGridField <T>  extends GridField<Requirement, T> {
    public RequirementsGridField() {
        super(RequirementColumns.ROW_CLASS, RequirementColumns.GRID_ROW_CLASS);
    }

    @Override
    public GridEditor getEditorWidget() {
        return new RequirementsGridEditor();
    }
}
