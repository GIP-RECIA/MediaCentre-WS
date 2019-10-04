package fr.recia.mediacentre.ws.model.ressource;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OperatorDroit implements Serializable {

    OR,
    AND;

}
