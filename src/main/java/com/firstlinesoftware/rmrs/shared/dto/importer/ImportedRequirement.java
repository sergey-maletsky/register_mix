package com.firstlinesoftware.rmrs.shared.dto.importer;

import com.firstlinesoftware.base.shared.dto.AttachedFile;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.UUID;

/**
 * Created by ADemkina on 28.01.2017.
 */
@XmlRootElement
@XmlType(propOrder={"id", "parent", "rule", "part", "volume", "header", "russianText", "englishText",
                    "russian", "english", "tags", "links"})
public class ImportedRequirement{

    private UUID id;

    private Boolean header;
    private UUID parent;
    private String rule;
    private String part;
    private String volume;

    private String russianText;
    private String englishText;
    private AttachedFile russian;
    private AttachedFile english;

    private List<String> tags;
    private List<UUID> links;

    public ImportedRequirement(UUID id, UUID parentId, String volume, String part, String rule, boolean isHeader) {
        this.id = id;
        this.parent = parentId;
        this.volume = volume;
        this.part = part;
        this.rule = rule;
        this.header = isHeader;
    }

    //for jaxb only
    public ImportedRequirement() {
    }

    @XmlElement(required = true)
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getHeader() {
        return header;
    }

    public void setHeader(Boolean header) {
        this.header = header;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getRussianText() {
        return russianText;
    }

    public void setRussianText(String russianText) {
        this.russianText = russianText;
    }

    public String getEnglishText() {
        return englishText;
    }

    public void setEnglishText(String englishText) {
        this.englishText = englishText;
    }

    public AttachedFile getRussian() {
        return russian;
    }

    public void setRussian(AttachedFile russian) {
        this.russian = russian;
    }

    public AttachedFile getEnglish() {
        return english;
    }

    public void setEnglish(AttachedFile english) {
        this.english = english;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<UUID> getLinks() {
        return links;
    }

    public void setLinks(List<UUID> links) {
        this.links = links;
    }
}
