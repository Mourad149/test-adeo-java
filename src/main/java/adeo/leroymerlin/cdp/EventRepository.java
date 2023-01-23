package adeo.leroymerlin.cdp;


import org.springframework.data.repository.CrudRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends CrudRepository<Event, Long> {
// the repository was extending Repository, i changed it to CrudRepository that contains methods ready to use eq delete
    List<Event> findAllBy();

}
