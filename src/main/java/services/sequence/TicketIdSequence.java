package services.sequence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import javax.persistence.EntityManager;

@Singleton
public class TicketIdSequence extends SequenceService {

    @Inject
    public TicketIdSequence(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, "ticket");
    }

    @Override
    @Transactional
    public String getNext() {
        return String.format("%07d", getNextLong());
    }

}
