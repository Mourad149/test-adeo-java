package adeo.leroymerlin.cdp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAllBy();
    }

    public void delete(Long id) {
        eventRepository.delete(id);
    }

    public List<Event> getFilteredEvents(String query) {
        List<Event> events = Optional.ofNullable(eventRepository.findAllBy()).orElseThrow(()->new IllegalArgumentException("No Events found"));
        return events.stream()
                     .filter(event -> event.getBands()
                                           .stream()
                             // keeping only records that fullfill : if at least on element (member) has its name containing the query
                                           .anyMatch(band -> band.getMembers()
                                                                 .stream()
                                                                 .filter(m -> m.getName().toLowerCase().contains(query.toLowerCase()))
                                                                 .collect(Collectors.toList()).size() > 0 ))
                // adding count to event name and band name
                     .map(event -> countChildItemsForEvent(event))
                     .collect(Collectors.toList());

    }

    public void updateEvent(Long id, Event event){
       Event oldEvent = Optional.ofNullable(eventRepository.findOne(id)).orElseThrow(()->new IllegalArgumentException("No Event found"));
       eventRepository.save(mapOldEventToNewEvent(oldEvent,event));
    }

    private Event mapOldEventToNewEvent(Event oldEvent,Event newEvent){
        oldEvent.setNbStars(newEvent.getNbStars());
        oldEvent.setComment(newEvent.getComment());
        return oldEvent;
    }

    private Event countChildItemsForEvent(Event event){
        event.setTitle(event.getTitle() +" "+ "["+event.getBands().size()+"]");
        event.getBands().stream().forEachOrdered(band -> {
            band.setName(band.getName()+" "+"["+band.getMembers().size()+"]");
        });
        return event;
    }

}
