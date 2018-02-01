package models;

import org.hibernate.annotations.*;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Question entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "question")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Question implements java.io.Serializable {

	// Fields

	private String id;
	private String uuid;
	private String question;
	private String choices;
	private String choicesType;
	private String answer;
	private Integer qtype;
	private String subType;
	private String difficulty;
	private String version;
	private String useStatus;
	private Date dateCreated;
	private String prompt;// 练习题答案提示
	private String ispractice;// 是否是练习题
	private String material;// 阅读材料
	private Integer dimension;// 维度

	// Constructors

	/** default constructor */
	public Question() {
	}

	/** minimal constructor */
	public Question(String question, String choices, String answer,
			Integer qtype, Date dateCreated) {
		this.question = question;
		this.choices = choices;
		this.answer = answer;
		this.qtype = qtype;
		this.dateCreated = dateCreated;
	}

	/** full constructor */
	public Question(String uuid, String question, String choices, String choicesType,
			String answer, Integer qtype, String subType, String difficulty,
			String version, Date dateCreated, String prompt, String ispractice, Integer dimension) {
		this.uuid = uuid;
		this.question = question;
		this.choices = choices;
		this.choicesType = choicesType;
		this.answer = answer;
		this.qtype = qtype;
		this.subType = subType;
		this.difficulty = difficulty;
		this.version = version;
		this.dateCreated = dateCreated;
		this.prompt = prompt;
		this.ispractice = ispractice;
		this.dimension = dimension;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="uuid")
	@GenericGenerator(name="uuid",strategy="org.hibernate.id.UUIDGenerator")
	@Column(name = "id", unique = true, nullable = false)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "uuid", unique = true, nullable = false)
	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(name = "question", nullable = false, length = 1000)
	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Column(name = "choices", nullable = false, length = 500)
	public String getChoices() {
		return this.choices;
	}

	public void setChoices(String choices) {
		this.choices = choices;
	}

	@Column(name = "choicesType", length = 1)
	public String getChoicesType() {
		return this.choicesType;
	}

	public void setChoicesType(String choicesType) {
		this.choicesType = choicesType;
	}

	@Column(name = "answer", nullable = false, length = 250)
	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Column(name = "q_type", nullable = false)
	public Integer getQtype() {
		return this.qtype;
	}

	public void setQtype(Integer qtype) {
		this.qtype = qtype;
	}

	@Column(name = "sub_type", length = 5)
	public String getSubType() {
		return this.subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	@Column(name = "difficulty", length = 50)
	public String getDifficulty() {
		return this.difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	@Column(name = "version", length = 30)
	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_created", nullable = false, length = 19)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "use_status",nullable = false, length = 1)
	public String getUseStatus() {
		return useStatus;
	}

	public void setUseStatus(String useStatus) {
		this.useStatus = useStatus;
	}

	@Column(name = "prompt", length = 500)
	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	@Column(name = "ispractice", length = 1)
	public String getIspractice() {
		return ispractice;
	}

	public void setIspractice(String ispractice) {
		this.ispractice = ispractice;
	}

	@Column(name = "material", length = 500)
	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	@Column(name = "dimension")
	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
		this.dimension = dimension;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Question question1 = (Question) o;

		return !(question != null ? !question.equals(question1.question) : question1.question != null);

	}
}