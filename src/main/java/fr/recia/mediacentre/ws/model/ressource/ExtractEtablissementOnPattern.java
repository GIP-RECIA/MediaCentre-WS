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
        "matcher",
        "group"
})
@ToString
@EqualsAndHashCode
public class ExtractEtablissementOnPattern {

    @NotBlank
    public String attribut;

    @NotBlank
    public String matcher;

    public int group;

    @Getter
    private Pattern compiledPattern;

    public ExtractEtablissementOnPattern() {
        super();
    }

    public ExtractEtablissementOnPattern(@NotBlank  final String attribut, @NotBlank  final String matcher, final int group) {
        this.attribut = attribut;
        this.matcher = matcher;
        this.group = group;
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

    @JsonProperty(value = "matcher", required = true)
    public String getMatcher() {
        return matcher;
    }

    @JsonProperty(value = "matcher", required = true)
    public void setMatcher(@NotBlank String matcher) {
        this.matcher = matcher;
        compilePattern();
    }

    @JsonProperty(value = "group", required = true)
    public int getGroup() {
        return group;
    }

    @JsonProperty(value = "group", required = true)
    public void setGroup(int group) {
        this.group = group;
    }

    private void compilePattern() {
        compiledPattern = Pattern.compile(matcher);
    }
}
