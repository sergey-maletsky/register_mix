package com.firstlinesoftware.rmrs.client.models;

import com.firstlinesoftware.base.client.models.SearchPersistentsModel;
import com.firstlinesoftware.base.shared.dto.TreeRow;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.view.client.AbstractDataProvider;

import java.util.Date;

public class RequirementsTreeTableModel extends SearchPersistentsModel<Requirement> {
    private String itemId;

    @Override
    public boolean isLeaf(Object value) {
        return !(value == null || (value instanceof TreeRow && !((TreeRow) value).isLeaf()));
    }

    @Override
    public AbstractDataProvider<?> createDataProvider(Requirement parent) {
        final RequirementsDataProvider provider = new RequirementsDataProvider(false);
        if (parent == null) {
            provider.setFolder(itemId);
        } else {
            provider.setFolder(parent.id);
        }
        return provider;
    }

    public void setParent(String parent) {
        this.itemId = parent;
        if (rootProvider instanceof RequirementsDataProvider) {
            ((RequirementsDataProvider) rootProvider).setFolder(parent);
        }
    }

    public void setOnlyEffective(Date on, Boolean onlySigned) {
        if (rootProvider instanceof RequirementsDataProvider) {
            ((RequirementsDataProvider) rootProvider).setShowAllItems(on, onlySigned);
        }
    }

    public void setShowRecursive(boolean on) {
        if (rootProvider instanceof RequirementsDataProvider) {
            ((RequirementsDataProvider) rootProvider).setShowRecursive(on);
        }

    }

    @Override
    public double getRowsHeight(int count, boolean compact) {
        return compact ? super.getRowsHeight(count, true) : -1;
    }
}
