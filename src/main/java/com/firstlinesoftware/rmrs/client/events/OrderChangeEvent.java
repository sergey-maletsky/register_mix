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


import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

public class OrderChangeEvent extends GwtEvent<OrderChangeEvent.OrderChangeEventHandler> {

    private static Type<OrderChangeEventHandler> TYPE;

    public List<Requirement> value;

    public OrderChangeEvent(List<Requirement> value) {
        this.value = value;
    }

    public static Type<OrderChangeEventHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public final Type<OrderChangeEventHandler> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(OrderChangeEventHandler handler) {
        handler.onOrderChanged(this);
    }

    public static interface OrderChangeEventHandler extends EventHandler {
      void onOrderChanged(OrderChangeEvent event);
    }
}
