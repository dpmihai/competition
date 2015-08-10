package competition.domain.entity;

public interface Auditable {

    public AuditInfo getAuditInfo();

    public void setAuditInfo(AuditInfo auditInfo);
    
}
