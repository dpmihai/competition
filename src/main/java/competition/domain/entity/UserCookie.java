package competition.domain.entity;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class UserCookie implements Serializable {
	
	private static final long serialVersionUID = -5132991323586777100L;
	
	private String username;
	private String password;
	private boolean rememberme;
	
	public UserCookie(String username, String password, boolean rememberme) {
		super();
		this.username = username;
		this.password = password;
		this.rememberme = rememberme;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isRememberme() {
		return rememberme;
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
