package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.rmrs.server.adapters.RequirementsXmlAdapter;
import com.firstlinesoftware.rmrs.shared.dto.importer.ImportedRequirement;
import com.firstlinesoftware.rmrs.shared.dto.importer.ImportedRequirements;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by ADemkina on 31.01.2017.
 */
public class ImportedRequirmentsTest {

    RequirementsXmlAdapter adapter = new RequirementsXmlAdapter();

    @Test
    public void testUnmarshall() throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(ImportedRequirements.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        ImportedRequirements requirements = (ImportedRequirements) jaxbUnmarshaller.unmarshal(this.getClass().getClassLoader().getResourceAsStream("pdfrules/requirements.xml"));
        assertNotNull(requirements);
        assertNotNull(requirements.getRequirements());
        Requirements reqs = new Requirements();
        for (ImportedRequirement importedRequirement : requirements.getRequirements()) {
            reqs.getRequirements().add(adapter.getRequirement(requirements, importedRequirement));
        }
        adapter.reorderRequirements(reqs.getRequirements());
        for (Requirement r: reqs.getRequirements()) {
            if (r.parent != null) {
                assertNotEquals(r.externalId, r.parent.externalId);
            }
            List<Requirement> children = getChildren (r, reqs.getRequirements());
            int maxOrder = getMaxOrder (children);
            assertEquals(children.size(), maxOrder);
            int reqsWithSameId = getRequirementsWithSameIdCount(r, reqs.getRequirements());
            if (reqsWithSameId > 1) {
                System.out.println(r.externalId);
            }
            assertEquals(1, reqsWithSameId);
        }
    }

    private int getRequirementsWithSameIdCount(Requirement current, List<Requirement> all) {
        int count = 0;
        for (Requirement r: all) {
            if (r.externalId != null &&  r.externalId.equals(current.externalId)) {
                count++;
            }
        }
        return count;
    }

    private List<Requirement> getChildren(Requirement current, List<Requirement> all) {
        List<Requirement> children = new ArrayList<>();
        for (Requirement r: all) {
            if (r.parent != null && r.parent.externalId != null &&  r.parent.externalId.equals(current.externalId)) {
                children.add(r);
            }
        }
        return children;
    }

    private Integer getMaxOrder(List<Requirement> children) {
        Integer order = 0;
        for (Requirement r: children) {
            if (r.order > order) {
                order = r.order;
            }
        }
        return order;
    }
}
