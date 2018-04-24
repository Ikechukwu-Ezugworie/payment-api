package services.sequence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;

@Singleton
public class PayerIdSequence extends SequenceService {

    @Inject
    public PayerIdSequence(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, "provider");
    }

    @Override
    public String getNext() {
        return String.format("%08d", getNextLong());
    }

}
