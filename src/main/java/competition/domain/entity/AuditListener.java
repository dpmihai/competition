package competition.domain.entity;

import java.util.Date;

import javax.persistence.PrePersist;

import competition.web.security.SecurityUtil;

/**
 * @author Decebal Suiu
 */
public class AuditListener {

    @PrePersist
    public void beforeInsert(Auditable auditable) {
        auditable.getAuditInfo().setCreatedBy(getCreatedBy());
	auditable.getAuditInfo().setCreationDate(new Date());
    }

    /*
    @PreUpdate
    public void beforeUpdate(Auditable auditable) {
        auditable.getAuditInfo().setUpdatedBy(getCurrentUserName());
	auditable.getAuditInfo().setUpdateDate(new Date());
    }
    */

    private String getCreatedBy() {
	User loggedUser = SecurityUtil.getLoggedUser();
	if (loggedUser == null) {
	    return null;
	}
	
	return loggedUser.getUsername();
    }
    
}
