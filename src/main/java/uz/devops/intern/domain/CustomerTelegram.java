package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A CustomerTelegram.
 */
@Entity
@Table(name = "customer_telegram")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerTelegram implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "is_bot")
    private Boolean isBot;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "username")
    private String username;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "step")
    private Integer step;

    @Column(name = "can_join_groups")
    private Boolean canJoinGroups;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "is_active")
    private Boolean isActive;

    @JsonIgnoreProperties(value = { "user", "groups" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Customers customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CustomerTelegram id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsBot() {
        return this.isBot;
    }

    public CustomerTelegram isBot(Boolean isBot) {
        this.setIsBot(isBot);
        return this;
    }

    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public CustomerTelegram firstname(String firstname) {
        this.setFirstname(firstname);
        return this;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public CustomerTelegram lastname(String lastname) {
        this.setLastname(lastname);
        return this;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return this.username;
    }

    public CustomerTelegram username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTelegramId() {
        return this.telegramId;
    }

    public CustomerTelegram telegramId(Long telegramId) {
        this.setTelegramId(telegramId);
        return this;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public CustomerTelegram phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getStep() {
        return this.step;
    }

    public CustomerTelegram step(Integer step) {
        this.setStep(step);
        return this;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Boolean getCanJoinGroups() {
        return this.canJoinGroups;
    }

    public CustomerTelegram canJoinGroups(Boolean canJoinGroups) {
        this.setCanJoinGroups(canJoinGroups);
        return this;
    }

    public void setCanJoinGroups(Boolean canJoinGroups) {
        this.canJoinGroups = canJoinGroups;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public CustomerTelegram languageCode(String languageCode) {
        this.setLanguageCode(languageCode);
        return this;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public CustomerTelegram isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Customers getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customers customers) {
        this.customer = customers;
    }

    public CustomerTelegram customer(Customers customers) {
        this.setCustomer(customers);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerTelegram)) {
            return false;
        }
        return id != null && id.equals(((CustomerTelegram) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerTelegram{" +
            "id=" + getId() +
            ", isBot='" + getIsBot() + "'" +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", username='" + getUsername() + "'" +
            ", telegramId=" + getTelegramId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", step=" + getStep() +
            ", canJoinGroups='" + getCanJoinGroups() + "'" +
            ", languageCode='" + getLanguageCode() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
