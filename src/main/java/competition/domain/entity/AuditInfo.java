package competition.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Resolution;

/**
 * @author Decebal Suiu
 */
@Embeddable
public class AuditInfo implements Serializable {

    @Column(name = "created_by", length = 50)
    protected String createdBy;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.MINUTE)
    protected Date creationDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object object) {
    	return EqualsBuilder.reflectionEquals(this, object);
    }

    @Override
    public int hashCode() {
    	return HashCodeBuilder.reflectionHashCode(this);
    }

}
