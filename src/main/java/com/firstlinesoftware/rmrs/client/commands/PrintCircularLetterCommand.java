package com.firstlinesoftware.rmrs.client.commands;

import com.firstlinesoftware.base.client.widgets.popups.MenuPopupPresenter;
import com.firstlinesoftware.crm.client.places.CreateEmployeePlace;
import com.firstlinesoftware.ecm.client.callbacks.ReportActionCallback;
import com.firstlinesoftware.ecm.client.commands.EcmCommand;
import com.firstlinesoftware.ecm.shared.dto.Document;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.firstlinesoftware.rmrs.client.proxies.RequirementsReportProxy;

public class PrintCircularLetterCommand extends EcmCommand {

    private Widget button;
    private RmrsMessages messages = Rmrs.getInjector().getMessages();
    private static final String ICON_PRINT = "images/buttons/32/print.png";
    private RequirementsReportProxy demandProxy = Rmrs.getInjector().getRequirementReportProxy();

    @Override
    public Widget getInstance(final Document dto, final String positionId) {
        button = createButton(ICON_PRINT, messages.print(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final MenuPopupPresenter presenter = new MenuPopupPresenter(button, true);

                presenter.addCommand(messages.languageRu(), new Command() {
                    @Override
                    public void execute() {
                        print("ru", (CircularLetter)dto);
                    }
                });
                presenter.addCommand(messages.languageEng(), new Command() {
                    @Override
                    public void execute() {
                        print("en", (CircularLetter)dto);
                    }
                });

                presenter.setAutoHide(true);
                presenter.showModal();
            }
        });
        return button;
    }

    private void print(String language, final CircularLetter letter){

        final String waitMessage = messages.creatingReport() + ": " + messages.circularLetter();
        final String errorMessage = messages.errorWhileCreatingReport();

        demandProxy.printCircular(letter, new ReportActionCallback(waitMessage,
                errorMessage, messages.circularLetter()), "circular", language);
    }
}