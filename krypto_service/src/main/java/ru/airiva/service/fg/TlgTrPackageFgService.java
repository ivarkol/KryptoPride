package ru.airiva.service.fg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.entities.TlgTrPackageEntity;
import ru.airiva.service.da.repository.TlgTrPackageRepo;

import java.util.Set;

/**
 * @author Ivan
 */
@Service
public class TlgTrPackageFgService {

    private TlgTrPackageRepo tlgTrPackageRepo;

    @Autowired
    public void setTlgTrPackageRepo(TlgTrPackageRepo tlgTrPackageRepo) {
        this.tlgTrPackageRepo = tlgTrPackageRepo;
    }

    public Set<TlgTrPackageEntity> getTranslations(Long personId) {
        return tlgTrPackageRepo.findByPersonEntityId(personId);
    }

    public void deleteTranslationById(Long id) {
        tlgTrPackageRepo.deleteById(id);
    }

    public void saveTranslation(TlgTrPackageEntity tlgTrPackageEntity) {
        tlgTrPackageRepo.saveAndFlush(tlgTrPackageEntity);
    }

}
