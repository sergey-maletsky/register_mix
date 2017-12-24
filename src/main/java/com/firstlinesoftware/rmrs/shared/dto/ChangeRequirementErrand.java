package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.ecm.shared.dto.HasComments;
import com.firstlinesoftware.ecm.shared.dto.RelatedDocument;
import com.firstlinesoftware.exec.shared.dto.AbstractErrand;
import com.firstlinesoftware.route.shared.dto.IsRegistrable;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeRequirementErrand extends AbstractErrand implements IsRegistrable, HasChangesToRequirements, HasComments {
    public static final String KIND = RmrsTasks.ERRAND_CHANGE_REQUIREMENT;

    public List<Requirement> createNew;
    public List<Requirement> modifyExisting;

    public ChangeRequirementErrand() {
        kind = KIND;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && equals(createNew, ((ChangeRequirementErrand) obj).createNew)
                && equals(modifyExisting, ((ChangeRequirementErrand) obj).modifyExisting);
    }

    @Override
    public ChangeRequirementErrand clone() {
        final ChangeRequirementErrand r = (ChangeRequirementErrand) super.clone();
        r.createNew = cloneArray(createNew);
        r.modifyExisting = cloneArray(modifyExisting);
        return r;
    }

    @Override
    protected ChangeRequirementErrand createInstance() {
        return new ChangeRequirementErrand();
    }

    @Override
    public List<Requirement> getCreateNew() {
        return createNew;
    }

    @Override
    public void setCreateNew(List<Requirement> createNew) {
        this.createNew = createNew;
    }

    @Override
    public List<Requirement> getModifyExisting() {
        return modifyExisting;
    }

    @Override
    public void setModifyExisting(List<Requirement> modifyExisting) {
        this.modifyExisting = modifyExisting;
    }

    public boolean hasCreateNew() {
        return createNew != null && !createNew.isEmpty();
    }

    public boolean hasModifyExisting() {
        return modifyExisting != null && !modifyExisting.isEmpty();
    }

    public Set<String> collectCompleted() {
        final Set<String> completed = new HashSet<>();
        if(relatedDocuments != null) {
            for (RelatedDocument r : relatedDocuments) {
                if(RmrsDirectories.RELATION_TYPE_BASED_ON.equals(r.relationType)) {
                    completed.add(r.id);
                }
            }
        }
        return completed;
    }

    @Override
    public Date getRegistrationDate() {
        return getCreated();
    }

    @Override
    public boolean isRegistered() {
        return true;
    }
}
