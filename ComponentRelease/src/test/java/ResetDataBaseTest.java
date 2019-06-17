
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.jupiter.api.Test;

//import org.junit.Test;

import cm.core.CaseRole;
import cm.core.CaseWorker;
import cm.core.utils.CaseFactory;
import cm.core.utils.PersistenceSettings;

public class ResetDataBaseTest {

	@Test
	public void rebuildDropAndCreate() {

		Map<String, String> persistenceMap = new HashMap<String, String>();
		persistenceMap.put("eclipselink.ddl-generation", "drop-and-create-tables");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PersistenceSettings.persistenceContextUnitLocalName, persistenceMap);

		EntityManager em = emf.createEntityManager();

		persistenceMap = new HashMap<String, String>();
		persistenceMap.put("eclipselink.ddl-generation", "none");
		emf = Persistence.createEntityManagerFactory(PersistenceSettings.persistenceContextUnitLocalName, persistenceMap);
		em = emf.createEntityManager();

		CaseRole dev = new CaseRole(CaseFactory.CaseRoles.developer);
		CaseRole reviewer = new CaseRole(CaseFactory.CaseRoles.reviewer);

		em.getTransaction().begin();
		em.persist(dev);
		em.persist(reviewer);
		em.getTransaction().commit();

		CaseWorker admin = new CaseWorker("admin", "admin", "John", "Admin", true);
		admin.addCaseRole(reviewer);
		CaseWorker worker = new CaseWorker("worker", "worker", "Jane", "Worker", false);
		worker.addCaseRole(dev);

		TypedQuery<CaseWorker> query = em.createQuery("SELECT cw FROM CaseWorker cw", CaseWorker.class);
		List<CaseWorker> caseWorkers = query.getResultList();

		List<String> foundNames = new ArrayList<>();

		if (caseWorkers.size() > 0) {
			for (CaseWorker cw : caseWorkers) {
				foundNames.add(cw.getUser());
			}
		}

		try {
			em.getTransaction().begin();
			if (!foundNames.contains("admin")) {
				em.persist(admin);
			}
			if (!foundNames.contains("worker")) {
				em.persist(worker);
			}
			em.getTransaction().commit();
			em.close();
			emf.close();
		} catch (Error e) {

		}
	}
}
