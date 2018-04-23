package services;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;

@Singleton
public class TransactionIdSequence extends SequenceService {

    @Inject
    public TransactionIdSequence(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, "provider");
    }

    @Override
    public String getNext() {
        return String.format("TR%08d", getNextLong());
    }

}
