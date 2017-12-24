package com.firstlinesoftware.rmrs.client.fields;

import com.firstlinesoftware.base.client.Base;
import com.firstlinesoftware.base.client.factories.FormItemFactory;
import com.firstlinesoftware.base.client.factories.Registrable;
import com.firstlinesoftware.base.client.widgets.GridEditor;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.icons.RmrsIcons;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.client.widgets.RequirementDraftPanel;
import com.firstlinesoftware.rmrs.client.widgets.RequirementsGridEditor;
import com.firstlinesoftware.rmrs.shared.dto.HasChangesToRequirements;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;

import java.util.List;

public class HasChangesToRequirementsFields implements Registrable {
    private final FormItemFactory formItemFactory = Base.getInjector().getFormItemFactory();
    private final RmrsMessages messages = Rmrs.getInjector().getMessages();
    private final RmrsIcons icons = Rmrs.getInjector().getIcons();

    @Override
    public void register() {
        formItemFactory.register(new CreateRequirementsField(), messages.createRequirements());
        formItemFactory.register(new ModifyRequirementsField(), messages.modifyRequirements());
    }

     private class CreateRequirementsField extends RequirementsGridField<HasChangesToRequirements> {
        @Override
        protected List<Requirement> getValue(HasChangesToRequirements dto) {
            return dto.getCreateNew();
        }

        @Override
        protected void setValue(HasChangesToRequirements dto, List<Requirement> value) {
            dto.setCreateNew(value);
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof HasChangesToRequirements;
        }

         @Override
         public GridEditor getEditorWidget() {
             return new RequirementsGridEditor(new RequirementDraftPanel());
         }
     }

    private class ModifyRequirementsField extends RequirementsGridField<HasChangesToRequirements> {
         @Override
        protected List<Requirement> getValue(HasChangesToRequirements dto) {
            return dto.getModifyExisting();
        }

        @Override
        protected void setValue(HasChangesToRequirements dto, List<Requirement> value) {
            dto.setModifyExisting(value);
        }

        @Override
        public boolean hasAspect(Object dto) {
            return dto instanceof HasChangesToRequirements;
        }
    }
}
