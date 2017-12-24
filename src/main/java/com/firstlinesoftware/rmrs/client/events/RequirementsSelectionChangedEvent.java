package com.firstlinesoftware.rmrs.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class RequirementsSelectionChangedEvent extends GwtEvent<RequirementsSelectionChangedEvent.Handler> {
    public String folderItemId;

    /**
     * Handler interface for {@link com.firstlinesoftware.rmrs.client.events.RequirementsSelectionChangedEvent} events.
     */
    public static interface Handler extends EventHandler {

        /**
         * Called when a {@link com.firstlinesoftware.rmrs.client.events.RequirementsSelectionChangedEvent} is fired.
         *
         * @param event the {@link com.firstlinesoftware.rmrs.client.events.RequirementsSelectionChangedEvent} that was fired
         */
        void onRequirementSelected(RequirementsSelectionChangedEvent event);
    }

    /**
     * Handler type.
     */
    private static Type<RequirementsSelectionChangedEvent.Handler> TYPE;

    /**
     * Fires a selection change event on all registered handlers in the handler
     * manager. If no such handlers exist, this method will do nothing.
     *
     * @param source the source of the handlers
     * @param id - folder item folder, or null
     */
    public static void fire(HasHandlers source, String id) {
        if (TYPE != null) {
            RequirementsSelectionChangedEvent event = new RequirementsSelectionChangedEvent(id);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<RequirementsSelectionChangedEvent.Handler> getType() {
        if (TYPE == null) {
            TYPE = new Type<RequirementsSelectionChangedEvent.Handler>();
        }
        return TYPE;
    }

    /**
     * Creates a selection change event.
     * @param id - folder item folder
     */
    public RequirementsSelectionChangedEvent(String id) {
        folderItemId = id;
    }

    @Override
    public final Type<RequirementsSelectionChangedEvent.Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequirementsSelectionChangedEvent.Handler handler) {
        handler.onRequirementSelected(this);
    }
}
