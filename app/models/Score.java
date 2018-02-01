package models;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;

/**
 * Score entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "score")
public class Score implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer uid;
	private Integer tid;
	private Integer orgCode;
	private Integer qtype;
	private String zScore;
	private String originalScore;
	private String standardScore;
	private String dimension;
	private String quality;
	private Date dateCreated;
	private Date dateUpdate;
	private String rank; //排名

	// Constructors

	/** default constructor */
	public Score() {
	}

	/** minimal constructor */
	public Score(Integer uid, Integer tid, Integer orgCode,
			Date dateCreated, Date dateUpdate) {
		this.uid = uid;
		this.tid = tid;
		this.orgCode = orgCode;
		this.dateCreated = dateCreated;
		this.dateUpdate = dateUpdate;
	}

	/** full constructor */
	public Score(Integer uid, Integer tid, Integer orgCode, Integer qtype,
			String score, String quality, Date dateCreated,
			Date dateUpdate) {
		this.uid = uid;
		this.tid = tid;
		this.orgCode = orgCode;
		this.qtype = qtype;
		this.zScore = score;
		this.quality = quality;
		this.dateCreated = dateCreated;
		this.dateUpdate = dateUpdate;
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

	@Column(name = "uid", nullable = false)
	public Integer getUid() {
		return this.uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	@Column(name = "tid", nullable = false)
	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	@Column(name = "org_code", nullable = false)
	public Integer getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}


	@Column(name = "q_type")
	public Integer getQtype() {
		return qtype;
	}

	public void setQtype(Integer qtype) {
		this.qtype = qtype;
	}
	@Column(name = "z_score", length = 30)
	public String getzScore() {
		return zScore;
	}

	public void setzScore(String zScore) {
		this.zScore = zScore;
	}





	@Column(name = "original_score", length = 30)

	public String getOriginalScore() {
		return originalScore;
	}

	public void setOriginalScore(String originalScore) {
		this.originalScore = originalScore;
	}
	@Column(name = "standard_score", length = 30)
	public String getStandardScore() {
		return standardScore;
	}

	public void setStandardScore(String standardScore) {
		this.standardScore = standardScore;
	}

	@Column(name = "quality", length = 300)
	public String getQuality() {
		return this.quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 19)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_update", nullable = false, length = 19)
	public Date getDateUpdate() {
		return this.dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public String getDimension() {
		return dimension;
	}
	@Column(name = "dimension", length = 1)
	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	@Column(name = "rank", length = 10)
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
}