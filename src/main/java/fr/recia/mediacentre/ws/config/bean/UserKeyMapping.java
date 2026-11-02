package fr.recia.mediacentre.ws.config.bean;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserKeyMapping {

    @NotBlank
    private String uidToReplace;

    @NotNull
    private Map<String, List<String>> userInfos;

}
