package vaulsys.contact;

import vaulsys.calendar.DateTime;
import vaulsys.persistence.IEntity;
import vaulsys.user.User;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;


@Entity
@Table(name = "fine_contact_city")
public class City implements IEntity<Long> {

	@Id
//    @GeneratedValue(generator="switch-gen")
//    private Long id;
	Long code;

	String name;

	@Column(nullable = false)
	String abbreviation;
	
	@Column(length = 20)
	String comsCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "state")
	@ForeignKey(name = "city_state_fk")
	State state;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_user")
	@ForeignKey(name = "city_user_fk")
	protected User creatorUser;

	@AttributeOverrides({
	@AttributeOverride(name = "dayDate.date", column = @Column(name = "created_date")),
	@AttributeOverride(name = "dayTime.dayTime", column = @Column(name = "created_time"))
			})
	protected DateTime createdDateTime;

	public City() {
	}

	public City(String name) {
		this.name = name;
	}

	public City(String name, Long code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Long getId() {
		return getCode();
	}

	public void setId(Long id) {
		setCode(id);
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	@Override
	public String toString() {
		return String.format("%s - %s", this.name, this.code);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof City))
			return false;
		City that = (City) o;
		return code.equals(that.code) && name.equals(that.name) && abbreviation.equals(that.abbreviation);
	}

	public User getCreatorUser() {
		return creatorUser;
	}

	public void setCreatorUser(User creatorUser) {
		this.creatorUser = creatorUser;
	}

	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}


	public String getComsCode() {
		return comsCode;
	}


	public void setComsCode(String comsCode) {
		this.comsCode = comsCode;
	}
}