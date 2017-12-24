package com.firstlinesoftware.rmrs.client.models;

import com.firstlinesoftware.base.client.models.NavigatorModel;
import com.firstlinesoftware.base.client.models.SingleSelectionModel;
import com.firstlinesoftware.base.client.models.TakesFolder;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.common.base.Joiner;
import com.google.gwt.view.client.ProvidesKey;

import java.util.Date;

public class RequirementsNavigatorModel extends NavigatorModel<Requirement> {

    private RequirementsDataProvider root;

    public RequirementsNavigatorModel() {
        super(new SingleSelectionModel<>(new ProvidesKey<Requirement>() {
            @Override
            public Object getKey(Requirement item) {
                return item != null ? item.id : null;
            }
        }));
    }

    @Override
    public TakesFolder createDataProvider() {
        root = new RequirementsDataProvider(true);
        return root;
    }

    @Override
    public boolean isLeaf(Object value) {
        return value instanceof Requirement && Boolean.TRUE.equals(((Requirement) value).leafHeader);
    }

    @Override
    protected String getFolderLabel(Requirement item) {
        final String v = item.volume != null ? item.volume + " " : "";
        final String p = item.part != null ? item.part + " " : "";
        final String n = item.number != null ? item.number + " " : "";
        final String t = item.getTitle() != null ?
                item.getTitle().length() > 100 ? item.getTitle().substring(0, 100) : item.getTitle()
                : "";

        return Joiner.on(' ').join(/*v, p,*/ n, t);
    }

    public void setOnlyEffective(Date on, Boolean onlySigned) {
        if(root != null) {
            root.setShowAllItems(on, onlySigned);
        }
    }
}
