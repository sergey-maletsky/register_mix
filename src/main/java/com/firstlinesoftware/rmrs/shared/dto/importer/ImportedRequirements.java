package com.firstlinesoftware.rmrs.shared.dto.importer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 * Created by ADemkina on 28.01.2017.
 */
@XmlRootElement
public class ImportedRequirements {

    private List<ImportedRequirement> requirements = new ArrayList<>();
    private Date dateTime;

    @XmlElementWrapper(name="requirements")
    @XmlElement(name="requirement")
    public List<ImportedRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<ImportedRequirement> requirements) {
        this.requirements = requirements;
    }

    public void add(ImportedRequirement importedRequirement) {

        requirements.add(importedRequirement);
    }

    @XmlElement
    @XmlJavaTypeAdapter(value = DateTimeAdapter.class)
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public ImportedRequirement getRequirement(UUID id) {

        for(ImportedRequirement r : this.requirements){
            if(r.getId().equals(id)){
                return r;
            }
        }

        return null;
    }
}
