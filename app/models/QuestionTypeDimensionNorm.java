package models;

import javax.persistence.*;
import java.util.Date;

/**
 * QuestionTypeNorm entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "question_type_dimension_norm")
public class QuestionTypeDimensionNorm implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer tpnid;
	private Integer QType;
	private String dimension;
	private String avg;
	private String stdev;
	private Date dateCreated;

	// Constructors

	/** default constructor */
	public QuestionTypeDimensionNorm() {
	}

	/** minimal constructor */
	public QuestionTypeDimensionNorm(Integer tpnid, Integer QType, Date dateCreated) {
		this.tpnid = tpnid;
		this.QType = QType;
		this.dateCreated = dateCreated;
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

	@Column(name = "tpnid", nullable = false)
	public Integer getTpnid() {
		return this.tpnid;
	}

	public void setTpnid(Integer tpnid) {
		this.tpnid = tpnid;
	}

	@Column(name = "q_type", nullable = false)
	public Integer getQType() {
		return this.QType;
	}

	public void setQType(Integer QType) {
		this.QType = QType;
	}

	@Column(name = "dimension", nullable = false)
	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	@Column(name = "avg", nullable = false)
	public String getAvg() {
		return avg;
	}

	public void setAvg(String avg) {
		this.avg = avg;
	}
	@Column(name = "stdev", nullable = false)
	public String getStdev() {
		return stdev;
	}

	public void setStdev(String stdev) {
		this.stdev = stdev;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 19)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

}