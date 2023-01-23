package adeo.leroymerlin.cdp.tests;


import adeo.leroymerlin.cdp.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Captor
    ArgumentCaptor<Event> captor;
    @InjectMocks
    EventService eventService;

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Test
    public void filterEventsOne(){
        when(eventRepository.findAllBy()).thenReturn(buildListEvents());
       List<Event> events =  eventService.getFilteredEvents("Jo");
       assertEquals(1,events.size());
       assertEquals("event1 [2]",events.get(0).getTitle());
        assertEquals("band1 [2]",events.get(0).getBands().stream().findFirst().get().getName());
    }

    @Test
    public void filterEvents(){
        when(eventRepository.findAllBy()).thenReturn(buildListEvents());
        List<Event> events =  eventService.getFilteredEvents("ok");
        assertEquals(2,events.size());
        assertEquals("event1 [2]",events.get(0).getTitle());
        assertEquals("band1 [2]", events.get(0).getBands().stream().findFirst().get().getName());
        assertEquals("event2 [1]",events.get(1).getTitle());
        assertEquals("band2 [1]",events.get(1).getBands().stream().findFirst().get().getName());


    }

    @Test()
    public void noEventsFound_Exception() throws Exception{
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("No Events found");
        when(eventRepository.findAllBy()).thenReturn(null);
        List<Event> events =  eventService.getFilteredEvents("ok");
    }

    @Test
    public void updateEventNotFound(){
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("No Event found");
        when(eventRepository.findOne(any())).thenReturn(null);
        eventService.updateEvent(new Long(1),new Event());
    }

    @Test
    public void updateEvent(){
        Event event = new Event();

        when(eventRepository.findOne(eq(new Long(1)))).thenReturn(buildEvent());

        event.setId(new Long(1));
        event.setTitle("event");
        event.setComment("comment");
        event.setNbStars(3);

        eventService.updateEvent(new Long(1),event);

        verify(eventRepository,times(1)).save(captor.capture());

        assertEquals("comment",captor.getValue().getComment());
        assertEquals(3,captor.getValue().getNbStars().intValue());

    }

    private Event buildEvent(){
        Event event = new Event();
        event.setTitle("event");
        event.setComment("");
        event.setNbStars(0);
        event.setId(new Long(1));

        return  event;
    }
    private List<Event> buildListEvents(){
        Event event1 = new Event();
        Band band1 = new Band();
        Band band12 = new Band();
        Member member1 = new Member();
        Member member12 = new Member();

        member1.setName("Wojook nop query");
        member12.setName("nonok");

        Set<Member> members1 = new HashSet<>();
        members1.add(member1);
        members1.add(member12);
        band1.setName("band1");
        band12.setName("band12");
        band12.setMembers(members1);
        band1.setMembers(members1);
        Set<Band> bands1 = new LinkedHashSet<>();
        bands1.add(band1);
        bands1.add(band12);
        event1.setId(new Long(1));
        event1.setTitle("event1");
        event1.setBands(bands1);

        Event event2 = new Event();
        Band band2 = new Band();
        Member member2 = new Member();
        member2.setName("Lonok nop");
        Set<Member> members2 = new HashSet<>();
        members2.add(member2);
        band2.setName("band2");
        band2.setMembers(members2);
        Set<Band> bands2 = new HashSet<>();
        bands2.add(band2);
        event2.setId(new Long(2));
        event2.setTitle("event2");
        event2.setBands(bands2);
        List<Event> events = Arrays.asList(event1,event2);

        return events;
    }
}
