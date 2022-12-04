package uz.devops.intern.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A TelegramGroup.
 */
@Entity
@Table(name = "telegram_group")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TelegramGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "chat_id")
    private Long chatId;

    @ManyToMany(mappedBy = "telegramGroups")
    @JsonIgnoreProperties(value = { "customer", "telegramGroups" }, allowSetters = true)
    private Set<CustomerTelegram> customerTelegrams = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TelegramGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public TelegramGroup name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getChatId() {
        return this.chatId;
    }

    public TelegramGroup chatId(Long chatId) {
        this.setChatId(chatId);
        return this;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Set<CustomerTelegram> getCustomerTelegrams() {
        return this.customerTelegrams;
    }

    public void setCustomerTelegrams(Set<CustomerTelegram> customerTelegrams) {
        if (this.customerTelegrams != null) {
            this.customerTelegrams.forEach(i -> i.removeTelegramGroup(this));
        }
        if (customerTelegrams != null) {
            customerTelegrams.forEach(i -> i.addTelegramGroup(this));
        }
        this.customerTelegrams = customerTelegrams;
    }

    public TelegramGroup customerTelegrams(Set<CustomerTelegram> customerTelegrams) {
        this.setCustomerTelegrams(customerTelegrams);
        return this;
    }

    public TelegramGroup addCustomerTelegram(CustomerTelegram customerTelegram) {
        this.customerTelegrams.add(customerTelegram);
        customerTelegram.getTelegramGroups().add(this);
        return this;
    }

    public TelegramGroup removeCustomerTelegram(CustomerTelegram customerTelegram) {
        this.customerTelegrams.remove(customerTelegram);
        customerTelegram.getTelegramGroups().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TelegramGroup)) {
            return false;
        }
        return id != null && id.equals(((TelegramGroup) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TelegramGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", chatId=" + getChatId() +
            "}";
    }
}
