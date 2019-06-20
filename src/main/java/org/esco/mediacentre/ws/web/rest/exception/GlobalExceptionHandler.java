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
package org.esco.mediacentre.ws.web.rest.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.esco.mediacentre.ws.model.erreur.Erreur;
import org.esco.mediacentre.ws.model.erreur.ErreurWrapper;
import org.esco.mediacentre.ws.service.exception.AuthorizedResourceException;
import org.esco.mediacentre.ws.service.exception.ListRequestErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(HttpClientErrorException.class)
	@ResponseBody
	ResponseEntity<ErreurWrapper> HandleHttpClientErrorException(HttpServletRequest request, Throwable ex) {
		final HttpClientErrorException e = (HttpClientErrorException) ex;

		ObjectMapper mapper = new ObjectMapper();
        ErreurWrapper erreurMsg;
		try {
			erreurMsg = mapper.readerFor(ErreurWrapper.class).readValue(e.getResponseBodyAsString());
			log.info("Returning an error Object form the GAR : ", erreurMsg);
		} catch (IOException ioe) {
			log.info("The error wasn't returned by the GAR, so we construct the error from the HttpClientException !");
			erreurMsg = new ErreurWrapper(new Erreur(Integer.toString(e.getStatusCode().value()), e.getStatusText(), null));
		}
		return new ResponseEntity<ErreurWrapper>(erreurMsg, e.getStatusCode());
	}

	@ExceptionHandler(ListRequestErrorException.class)
	@ResponseBody
	ResponseEntity<ErreurWrapper> HandleListRequestErrorException(HttpServletRequest request, Throwable ex) {
		final ListRequestErrorException e = (ListRequestErrorException) ex;

		return new ResponseEntity<ErreurWrapper>(new ErreurWrapper(
				new Erreur(
						HttpStatus.BAD_REQUEST.getReasonPhrase(),
						((ListRequestErrorException) ex).getAllMessages(),
						null)
		),HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RestClientException.class)
	@ResponseBody
	ResponseEntity<ErreurWrapper> HandleRestClientException(HttpServletRequest request, Throwable ex) {
		final RestClientException e = (RestClientException) ex;
        log.info("The error wasn't returned by the GAR, so we construct the error from the RestClientException !");
		return new ResponseEntity<ErreurWrapper>(new ErreurWrapper(
                new Erreur(
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				        e.getLocalizedMessage(),
				        null)
        ),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(AuthorizedResourceException.class)
	@ResponseBody
	ResponseEntity<ErreurWrapper> AuthorizedResourceException(HttpServletRequest request, Throwable ex) {
		final AuthorizedResourceException e = (AuthorizedResourceException) ex;
		return new ResponseEntity<ErreurWrapper>(new ErreurWrapper(
				new Erreur(
						HttpStatus.BAD_REQUEST.getReasonPhrase(),
						ex.getLocalizedMessage(),
						null)
		),HttpStatus.BAD_REQUEST);
	}
}
