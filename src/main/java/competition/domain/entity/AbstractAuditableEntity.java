package competition.domain.entity;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Decebal Suiu
 */
@MappedSuperclass
@EntityListeners(AuditListener.class)
public abstract class AbstractAuditableEntity implements Auditable, Serializable {

    private static final long serialVersionUID = 1L;
    
    @JsonIgnore
    @Embedded
    private AuditInfo auditInfo = new AuditInfo();

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

    @JsonIgnore
    public String getCreatedBy() {
    	return auditInfo.getCreatedBy();
    }
    
}
