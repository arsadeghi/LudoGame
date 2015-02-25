package edu.gmu.ludo.dao;

import java.util.List;

import edu.gmu.ludo.entity.AuditTrail;

public interface AuditTrailDao extends AbstractDao<AuditTrail, Integer> {

	
	List<AuditTrail> getAuditTrailsForGame(Integer gameId, String username);
	
}
