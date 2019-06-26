/**
 * Copyright (C) 2017 GIP-RECIA https://www.recia.fr/
 * @Author (C) 2017 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
