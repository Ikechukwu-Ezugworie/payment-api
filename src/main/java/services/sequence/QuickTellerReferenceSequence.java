package services.sequence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;

@Singleton
public class QuickTellerReferenceSequence extends SequenceService {

    @Inject
    public QuickTellerReferenceSequence(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider, "merchant");
    }

    @Override
    public String getNext() {
        return String.format("M%07d", getNextLong());
    }

}
