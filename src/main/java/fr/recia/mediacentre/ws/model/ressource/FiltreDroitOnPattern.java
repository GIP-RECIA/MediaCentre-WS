package fr.recia.mediacentre.ws.model.ressource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "attribut",
        "pattern"
})
@ToString
@EqualsAndHashCode
public class FiltreDroitOnPattern{

    @NonNull
    public String attribut;

    @NonNull
    public String pattern;

    @Getter
    private Pattern compiledPattern;


    public FiltreDroitOnPattern() {
        super();
    }

    public FiltreDroitOnPattern(@NonNull final String attribut, @NonNull final String pattern) {
        this.attribut = attribut;
        this.pattern = pattern;
        compilePattern();
    }

    @JsonProperty(value = "attribut", required = true)
    public String getAttribut() {
        return attribut;
    }

    @JsonProperty(value = "attribut", required = true)
    public void setAttribut(String attribut) {
        this.attribut = attribut;
    }

    @JsonProperty(value = "pattern", required = true)
    public String getPattern() {
        return pattern;
    }

    @JsonProperty(value = "pattern", required = true)
    public void setPattern(String pattern) {
        this.pattern = pattern;
        compilePattern();
    }

    private void compilePattern() {
        compiledPattern = Pattern.compile(pattern);
    }
}
