package com.firstlinesoftware.rmrs.server.adapters;

import com.firstlinesoftware.base.server.adapters.ImportAdapter;
import com.firstlinesoftware.base.server.exceptions.ServerException;
import com.firstlinesoftware.base.server.importers.ItemImporter;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.rmrs.shared.dto.Requirements;
import com.firstlinesoftware.rmrs.shared.dto.importer.ImportedRequirement;
import com.firstlinesoftware.rmrs.shared.dto.importer.ImportedRequirements;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RequirementsXmlAdapter implements ImportAdapter<Requirements, File> {
    private final Logger logger = Logger.getLogger(this.getClass());
    public static final String PDF_MIME_TYPE = "application/pdf";
    Map<String, Integer> orderByParent = new HashMap<>();

     @Override
    public void doImport(ItemImporter<Requirements> importer, File location) {
        if (location != null) {
            try {
                final Unmarshaller unmarshaller = JAXBContext.newInstance(ImportedRequirements.class).createUnmarshaller();
                ImportedRequirements importedRequirements = (ImportedRequirements) unmarshaller.unmarshal(location);
                importRequirements(importedRequirements, importer);
            } catch (JAXBException e) {
                throw new ServerException("while importing requirements", e);
            } catch (IllegalArgumentException ignored) {
                throw new ServerException("no requirements xml at " + location.getAbsolutePath());
            }
        } else {
            importer.importItem(null);
        }
    }

    public void importRequirements(ImportedRequirements importedRequirements, ItemImporter<Requirements> importer) {
        Requirements requirements = new Requirements();
        for (ImportedRequirement importedRequirement : importedRequirements.getRequirements()) {
            requirements.getRequirements().add(getRequirement(importedRequirements, importedRequirement));
        }
        reorderRequirements(requirements.getRequirements());

        importer.importItem(requirements);
    }

    public Requirement getRequirement(ImportedRequirements importedRequirements, ImportedRequirement importedRequirement) {
        Requirement requirement = null;
        if (importedRequirement != null) {
            requirement = new Requirement();
            requirement.lifecycle = Document.DOCUMENT_LIFECYCLE_DRAFT;
            ImportedRequirement parentImportedRequirement = importedRequirements.getRequirement(importedRequirement.getParent());
            requirement.parent = getRequirement(importedRequirements, parentImportedRequirement);
            if (parentImportedRequirement != null) {
                requirement.parent.setExternalId(parentImportedRequirement.getId().toString());
            }

            requirement.setExternalId(importedRequirement.getId().toString());
            requirement.number = importedRequirement.getRule();
            requirement.part = importedRequirement.getPart();
            requirement.volume = importedRequirement.getVolume();

            final String r = importedRequirement.getRussianText();
            requirement.russianText = trim(r);
            requirement.setName(trim(r));
            requirement.englishText = trim(importedRequirement.getEnglishText());
            requirement.russian = importedRequirement.getRussian();
            requirement.english = importedRequirement.getEnglish();

            requirement.header = importedRequirement.getHeader();
            requirement.externalModifiedDate = importedRequirements.getDateTime();
            requirement.setKind(Requirement.KIND);
        }

        return requirement;
    }

    private static String trim(String r) {
        return r != null ? r.length() > 256 ? r.substring(0, 256) + "..." : r : "";
    }

    public void reorderRequirements (List<Requirement> requirements) {
        for (Requirement requirement: requirements) {
            Integer order = 1;
            if (requirement.parent != null) {
                order = orderByParent.get(requirement.parent.getExternalId());
                order = order == null ? 1 : ++order;
                orderByParent.put(requirement.parent.getExternalId(), order);
            }
            requirement.order = order;
        }
    }

}
