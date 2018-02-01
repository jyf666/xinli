package models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Questiontype entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "questiontype")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Questiontype implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private String introduce;
	private Integer limitTime;
	private String scoringFormula;
	private String type;
	private String useStatus;

	// Constructors

	/** default constructor */
	public Questiontype() {
	}

	/** full constructor */
	public Questiontype(String name, Integer limitTime) {
		this.name = name;
		this.limitTime = limitTime;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false, length = 25)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "limit_time", nullable = false)
	public Integer getLimitTime() {
		return this.limitTime;
	}

	public void setLimitTime(Integer limitTime) {
		this.limitTime = limitTime;
	}

	@Column(name = "introduce", nullable = false, length = 100)
	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	@Column(name = "scoring_formula", nullable = false, length = 200)
	public String getScoringFormula() {
		return scoringFormula;
	}

	public void setScoringFormula(String scoringFormula) {
		this.scoringFormula = scoringFormula;
	}

	@Column(name = "use_status", nullable = false, length = 1)
	public String getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}

	@Column(name = "type", nullable = false, length = 1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Questiontype that = (Questiontype) o;

		return id.equals(that.id);

	}
}