package services.sequence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;

@Singleton
public class MerchantIdentifierSequence extends SequenceService {

    @Inject
    public MerchantIdentifierSequence(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, "provider");
    }

    @Override
    public String getNext() {
        return String.format("M%07d", getNextLong());
    }

}
