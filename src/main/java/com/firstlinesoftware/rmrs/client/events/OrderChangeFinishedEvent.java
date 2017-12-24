/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.firstlinesoftware.rmrs.client.events;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class OrderChangeFinishedEvent extends GwtEvent<OrderChangeFinishedEvent.OrderChangeFinishedEventHandler> {

    private static Type<OrderChangeFinishedEvent.OrderChangeFinishedEventHandler> TYPE;
    public final boolean save;

    public OrderChangeFinishedEvent(boolean save) {
        this.save = save;
    }

    public static Type<OrderChangeFinishedEvent.OrderChangeFinishedEventHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public final Type<OrderChangeFinishedEvent.OrderChangeFinishedEventHandler> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(OrderChangeFinishedEvent.OrderChangeFinishedEventHandler handler) {
        handler.onOrderChangeFinished(this);
    }

    public static interface OrderChangeFinishedEventHandler extends EventHandler {
      void onOrderChangeFinished(OrderChangeFinishedEvent event);
    }
}
