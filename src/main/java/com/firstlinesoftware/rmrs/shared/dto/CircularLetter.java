package com.firstlinesoftware.rmrs.shared.dto;

import com.firstlinesoftware.ecm.shared.dto.HasComments;
import com.firstlinesoftware.ecm.shared.dto.HasDepartmentTemplates;
import com.firstlinesoftware.ecm.shared.dto.HasHistory;
import com.firstlinesoftware.ecm.shared.dto.HasLifecycle;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.route.shared.dto.*;

import java.util.List;

public class CircularLetter extends AbstractRoute implements HasHistory, HasComments, HasSigning, HasApproval, HasLifecycle, IsRegistrable, HasDepartmentTemplates, HasRegistration {
    public static final String KIND = "routes.circular";

//    public String name; //Касательно
    public String referred_en; //Касательно на англ.
    public String observable; //Объект наблюдения
    public String observable_en; //Объект наблюдения на англ.
    public String commissioning; //Ввод в действие
    public String commissioning_en; //Ввод в действие на англ.
    public String validTo; //Срок действия: до
    public String validTo_en; //Срок действия: до на англ.
    public String validExtendedUntil; //Срок действия продлен до
    public String validExtendedUntil_en; //Срок действия продлен до на англ.
    public String content; //Содержание ЦП
    public String content_en; //Содержание ЦП на англ.
    public String action; //Действие по выполнению
    public String action_en; //Действие по выполнению на англ.
    public Position approvePosition; //Утверждающий
    public String businessCaseNumber; // Номер дела

    public String changedContent; //Вносит изменения в
    public List<Requirement> approvedRequirements; //Утверждаемые требования

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && equals(referred_en, ((CircularLetter) obj).referred_en)
                && equals(observable, ((CircularLetter) obj).observable)
                && equals(observable_en, ((CircularLetter) obj).observable_en)
                && equals(commissioning, ((CircularLetter) obj).commissioning)
                && equals(commissioning_en, ((CircularLetter) obj).commissioning_en)
                && equals(validTo, ((CircularLetter) obj).validTo)
                && equals(validTo_en, ((CircularLetter) obj).validTo_en)
                && equals(validExtendedUntil, ((CircularLetter) obj).validExtendedUntil)
                && equals(validExtendedUntil_en, ((CircularLetter) obj).validExtendedUntil_en)
                && equals(content, ((CircularLetter) obj).content)
                && equals(content_en, ((CircularLetter) obj).content_en)
                && equals(action, ((CircularLetter) obj).action)
                && equals(action_en, ((CircularLetter) obj).action_en)
                && equals(approvePosition, ((CircularLetter) obj).approvePosition)
                && equals(changedContent, ((CircularLetter) obj).changedContent)
                && equals(approvedRequirements, ((CircularLetter) obj).approvedRequirements)
                && equals(businessCaseNumber, ((CircularLetter) obj).businessCaseNumber)
                ;
    }

    @Override
    protected CircularLetter createInstance() {
        return new CircularLetter();
    }

    @Override
    public CircularLetter clone() {
        final CircularLetter r = (CircularLetter) super.clone();
        r.referred_en = referred_en;
        r.observable = observable;
        r.observable_en = observable_en;
        r.commissioning = commissioning;
        r.commissioning_en = commissioning_en;
        r.validTo = validTo;
        r.validTo_en = validTo_en;
        r.validExtendedUntil = validExtendedUntil;
        r.validExtendedUntil_en = validExtendedUntil_en;
        r.content = content;
        r.content_en = content_en;
        r.action = action;
        r.action_en = action_en;
        r.approvePosition = approvePosition;
        r.changedContent = changedContent;
        r.approvedRequirements = cloneList(approvedRequirements);
        r.businessCaseNumber = businessCaseNumber;
        return r;
    }
}
