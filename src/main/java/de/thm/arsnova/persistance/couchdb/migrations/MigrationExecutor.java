package de.thm.arsnova.persistance.couchdb.migrations;

import de.thm.arsnova.entities.MigrationState;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MigrationExecutor {
	private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);

	private List<Migration> migrations;

	public MigrationExecutor(List<Migration> migrations) {
		this.migrations = migrations.stream()
				.sorted(Comparator.comparing(Migration::getId)).collect(Collectors.toList());
	}

	public boolean runMigrations(@NonNull final MigrationState migrationState) {
		List<Migration> pendingMigrations = migrations.stream()
				.filter(m -> !migrationState.getCompleted().contains(m.getId())).collect(Collectors.toList());
		boolean stateChange = false;
		if (migrationState.getActive() != null) {
			throw new IllegalStateException("An migration is already active: " + migrationState.getActive());
		}
		logger.debug("Pending migrations: " + pendingMigrations.stream()
				.map(Migration::getId).collect(Collectors.joining()));
		for (Migration migration : pendingMigrations) {
			stateChange = true;
			migrationState.setActive(migration.getId(), new Date());
			migration.migrate();
			migrationState.getCompleted().add(migration.getId());
			migrationState.setActive(null);
		}

		return stateChange;
	}
}
