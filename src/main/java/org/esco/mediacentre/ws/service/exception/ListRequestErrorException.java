package org.esco.mediacentre.ws.service.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListRequestErrorException extends RuntimeException {

    List<Throwable> exceptions = new ArrayList<>();

    public ListRequestErrorException() {
        super();
    }


    public void addException(Throwable cause) {
        exceptions.add(cause);
    }

    public void addAllException(List<? extends Exception> causes) {
        exceptions.addAll(causes);
    }
    public String getAllMessages() {
        return  exceptions.stream().map( n -> n.getLocalizedMessage() ) .collect( Collectors.joining( "\n" ) );
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("List of exceptions: ");
        for (Throwable e: exceptions) {
            sb.append("\n\t" + e.toString());
        }
        return sb.toString();
    }


}
