package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.base.shared.dto.AttachedFile;
import com.firstlinesoftware.base.shared.dto.DateRange;
import com.firstlinesoftware.base.shared.dto.ExternalRecord;
import com.firstlinesoftware.ecm.shared.dto.HasHistory;
import com.firstlinesoftware.ecm.shared.dto.HasLifecycle;
import com.firstlinesoftware.ecm.shared.dto.HasNumber;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.route.shared.dto.AbstractRoute;
import com.firstlinesoftware.route.shared.dto.HasApproval;

import java.util.Date;
import java.util.List;

public class Requirement extends AbstractRoute implements ExternalRecord, HasLifecycle, HasMultiLanguageFile, HasHistory, HasApproval, HasNumber {
    public static final String KIND = "rmrs.requirement";
    public static final String LIFECYCLE_SIGNED = "routes_signed";

    public Integer order;
    public Requirement parent;
    public String part;
    public String volume;

    public String russianText;
    public String englishText;
    public AttachedFile russian;
    public AttachedFile english;
    public Boolean header;
    public Boolean leafHeader;
    public List<String> tags;
    public DateRange effective;

    public Position responsible;//gets approval task
    public String circularLetter;
    public String fullPath;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && equals(order, ((Requirement) obj).order)
                && equals(parent, ((Requirement) obj).parent)
                && equals(number, ((Requirement) obj).number)
                && equals(part, ((Requirement) obj).part)
                && equals(volume, ((Requirement) obj).volume)
                && equals(russianText, ((Requirement) obj).russianText)
                && equals(englishText, ((Requirement) obj).englishText)
                && equals(russian, ((Requirement) obj).russian)
                && equals(english, ((Requirement) obj).english)
                && equals(header, ((Requirement) obj).header)
                && equals(leafHeader, ((Requirement) obj).leafHeader)
                && equals(tags, ((Requirement) obj).tags)
                && equals(effective, ((Requirement) obj).effective)
                && equals(responsible, ((Requirement) obj).responsible)
                && equals(circularLetter, ((Requirement) obj).circularLetter)
                && equals(fullPath, ((Requirement) obj).fullPath)
                ;
    }

    @Override
    protected Requirement createInstance() {
        return new Requirement();
    }

    @Override
    public Requirement clone() {
        final Requirement r = (Requirement) super.clone();
        r.order = order;
        r.parent = parent;
        r.number = number;
        r.part = part;
        r.volume = volume;
        r.russianText = russianText;
        r.englishText = englishText;
        r.russian = russian;
        r.english = english;
        r.header = header;
        r.leafHeader = leafHeader;
        r.tags = tags;
        r.effective = effective;
        r.responsible = responsible;
        r.circularLetter = circularLetter;
        r.fullPath = fullPath;
        return r;
    }

    public boolean isEffective(Date date) {
        if (effective == null || date == null) {
            return true;
        }
        if (effective.min != null && effective.min.after(date)) {
            return false;
        } else if (effective.max != null && effective.max.before(date)) {
            return false;
        }
        return true;
    }

    public Date getStartDate() {
        return effective == null ? null : effective.min;
    }

    public Date getEndDate() {
        return effective == null ? null : effective.max;
    }

    @Override
    public AttachedFile getRussian() {
        return russian;
    }

    @Override
    public AttachedFile getEnglish() {
        return english;
    }

    @Override
    public void setEnglish(AttachedFile english) {
        this.english = english;
    }

    @Override
    public void setRussian(AttachedFile russian) {
        this.russian = russian;
    }
}
