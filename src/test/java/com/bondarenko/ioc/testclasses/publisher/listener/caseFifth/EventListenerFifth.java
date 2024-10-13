package com.bondarenko.ioc.testclasses.publisher.listener.caseFifth;


import com.bondarenko.ioc.annotation.EventListener;
import com.bondarenko.ioc.testclasses.publisher.event.EventFirst;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventListenerFifth {

    final List<EventFirst> events = new ArrayList<>();

    @EventListener(EventFirst.class)
    public void listen(EventFirst event) {
        events.add(event);
    }
}