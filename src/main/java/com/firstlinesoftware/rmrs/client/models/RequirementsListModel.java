package com.firstlinesoftware.rmrs.client.models;

import com.firstlinesoftware.base.client.models.AbstractTreeTableModel;
import com.firstlinesoftware.base.shared.dto.TreeRow;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.google.gwt.view.client.AbstractDataProvider;

public class RequirementsListModel extends AbstractTreeTableModel<Position> {
    private Position position;

    @Override
    public boolean isLeaf(Object value) {
        return !(value == null || (value instanceof TreeRow && !((TreeRow) value).isLeaf()));
    }

    @Override
    public AbstractDataProvider<?> createDataProvider(Position p) {
        final PersonRequirementsDataProvider provider = new PersonRequirementsDataProvider(false);
        provider.setPosition(position);
        return provider;
    }

    public void setParent(Position position) {
        this.position = position;
        if (rootProvider instanceof PersonRequirementsDataProvider) {
            ((PersonRequirementsDataProvider) rootProvider).setPosition(position);
        }
    }

//    public void setOnlyEffective(boolean on) {
//        if (rootProvider instanceof RequirementsDataProvider) {
//            ((RequirementsDataProvider) rootProvider).setShowAllItems(on);
//        }
//    }
//
    @Override
    public double getRowsHeight(int count, boolean compact) {
        return compact ? super.getRowsHeight(count, true) : -1;

    }
}