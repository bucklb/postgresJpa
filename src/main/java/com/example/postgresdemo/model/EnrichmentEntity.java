package com.example.postgresdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

// Keep it simple and just have one organisation per enrichment
@Entity
@Table(name = "enrichments")
public class EnrichmentEntity {

    @Id
    @GeneratedValue(generator = "enrichment_generator")
    @SequenceGenerator(
            name = "enrichment_generator",
            sequenceName = "enrichment_sequence",
            initialValue = 1000
    )
    private Long id;

    @Column
    private String council = null;
    @Column
    private String organisation = null;
    @Column
    private String response = null;

    // Is this driven by entity or by domain ???
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "birth_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private BirthCaseEntity birth;


    @Column(name = "birth")
    private Long birthCaseId = null;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCouncil() { return council; }
    public void setCouncil(String council) { this.council = council; }

    public String getOrganisation() { return organisation; }
    public void setOrganisation(String organisation) { this.organisation = organisation; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public BirthCaseEntity getBirthCaseEntity() {
        return birth;
    }
    public void setBirthCaseEntity(BirthCaseEntity birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enrichment {\n");
        sb.append("    council: ").append(toIndentedString(council)).append("\n");
        sb.append("    organisationToInform: ").append(toIndentedString(organisation)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
