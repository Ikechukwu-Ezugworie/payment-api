package services.sequence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;

@Singleton
public class NotificationIdSequence extends SequenceService {

    @Inject
    public NotificationIdSequence(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, "notification");
    }

    @Override
    public String getNext() {
        return String.format("%08d", getNextLong());
    }

}
