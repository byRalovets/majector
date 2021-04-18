package by.ralovets.majector.dataset.correct;

import by.ralovets.majector.annotation.Inject;

public class RestEventController {

    private EventService eventService;

    @Inject
    public RestEventController(EventService eventService) {
        this.eventService = eventService;
    }
}
