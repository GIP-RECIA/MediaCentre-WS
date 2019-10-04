package fr.recia.mediacentre.ws.model.ressource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "operator",
        "filtreDroitType"
})
@ToString
@EqualsAndHashCode
public class FiltreDroit  {

    @NotNull
    private OperatorDroit operator;

    @Valid
    public List<FiltreDroitOnPattern> filtreDroitType = new ArrayList<>();

    public FiltreDroit() {
        super();
    }

    public FiltreDroit(@NotNull final OperatorDroit operator, @Valid final List<FiltreDroitOnPattern> filtreDroitTypes) {
        this.operator = operator;
        this.filtreDroitType = filtreDroitTypes;
    }

    @JsonProperty("operator")
    public OperatorDroit getOperator() {
        return operator;
    }
    @JsonProperty("operator")
    public void setOperator(OperatorDroit operator) {
        this.operator = operator;
    }
    @JsonProperty("filtreDroitType")
    public List<FiltreDroitOnPattern> getFiltreDroitType() {
        return filtreDroitType;
    }
    @JsonProperty("filtreDroitType")
    public void setFiltreDroitType(List<FiltreDroitOnPattern> filtreDroitTypes) {
        this.filtreDroitType = filtreDroitTypes;
    }
}
