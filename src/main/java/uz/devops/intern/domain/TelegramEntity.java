package uz.devops.intern.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A TelegramEntity.
 */
@Entity
@Table(name = "telegram_entity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelegramEntity implements Serializable {

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

    @Column(name = "can_join_groups")
    private Boolean canJoinGroups;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TelegramEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsBot() {
        return this.isBot;
    }

    public TelegramEntity isBot(Boolean isBot) {
        this.setIsBot(isBot);
        return this;
    }

    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public TelegramEntity firstname(String firstname) {
        this.setFirstname(firstname);
        return this;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public TelegramEntity lastname(String lastname) {
        this.setLastname(lastname);
        return this;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return this.username;
    }

    public TelegramEntity username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTelegramId() {
        return this.telegramId;
    }

    public TelegramEntity telegramId(Long telegramId) {
        this.setTelegramId(telegramId);
        return this;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Boolean getCanJoinGroups() {
        return this.canJoinGroups;
    }

    public TelegramEntity canJoinGroups(Boolean canJoinGroups) {
        this.setCanJoinGroups(canJoinGroups);
        return this;
    }

    public void setCanJoinGroups(Boolean canJoinGroups) {
        this.canJoinGroups = canJoinGroups;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public TelegramEntity languageCode(String languageCode) {
        this.setLanguageCode(languageCode);
        return this;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public TelegramEntity isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TelegramEntity user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelegramEntity)) {
            return false;
        }
        return id != null && id.equals(((TelegramEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelegramEntity{" +
            "id=" + getId() +
            ", isBot='" + getIsBot() + "'" +
            ", firstname='" + getFirstname() + "'" +
            ", lastname='" + getLastname() + "'" +
            ", username='" + getUsername() + "'" +
            ", telegramId=" + getTelegramId() +
            ", canJoinGroups='" + getCanJoinGroups() + "'" +
            ", languageCode='" + getLanguageCode() + "'" +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
