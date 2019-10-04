package fr.recia.mediacentre.ws.model.ressource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "attribut",
        "pattern"
})
@ToString
@EqualsAndHashCode
public class FiltreDroitOnPattern{

    @NotBlank
    public String attribut;

    @NotBlank
    public String pattern;

    @Getter
    private Pattern compiledPattern;


    public FiltreDroitOnPattern() {
        super();
    }

    public FiltreDroitOnPattern(@NotBlank final String attribut, @NotBlank final String pattern) {
        this.attribut = attribut;
        this.pattern = pattern;
        compilePattern();
    }

    @JsonProperty(value = "attribut", required = true)
    public String getAttribut() {
        return attribut;
    }

    @JsonProperty(value = "attribut", required = true)
    public void setAttribut(@NotBlank String attribut) {
        this.attribut = attribut;
    }

    @JsonProperty(value = "pattern", required = true)
    public String getPattern() {
        return pattern;
    }

    @JsonProperty(value = "pattern", required = true)
    public void setPattern(@NotBlank String pattern) {
        this.pattern = pattern;
        compilePattern();
    }

    private void compilePattern() {
        compiledPattern = Pattern.compile(pattern);
    }
}
