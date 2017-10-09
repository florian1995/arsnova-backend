package de.thm.arsnova.persistance.couchdb.migrations;

public interface Migration {
	String getId();
	void migrate();
}
