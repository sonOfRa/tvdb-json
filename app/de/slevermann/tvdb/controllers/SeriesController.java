/*
 * Copyright (c) 2014, Simon Levermann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package de.slevermann.tvdb.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.slevermann.tvdb.models.JacksonViews;
import de.slevermann.tvdb.models.Series;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Series-related Actions
 *
 * @author Simon Levermann
 */
public class SeriesController extends BaseController {

	@Transactional(readOnly = true)
	public static Result series(Long id) {
		Series series = JPA.em().find(Series.class, id);

		if (series == null) {
			return notFound();
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return ok(objectMapper.writeValueAsString(series));
			} catch (JsonProcessingException e) {
				Logger.error("Failed to create JSON:", e);
				return internalServerError();
			}
		}
	}

	@Transactional(readOnly = true)
	public static Result seriesWithEpisodes(Long id) {
		Series series = JPA.em().find(Series.class, id);

		if (series == null) {
			return notFound();
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return ok(objectMapper.writerWithView(JacksonViews.SeriesWithEpisodesView.class).writeValueAsString(series));
			} catch (JsonProcessingException e) {
				Logger.error("Failed to create JSON:", e);
				return internalServerError();
			}
		}
	}

	public static Result searchSeries(String name) {
		TypedQuery<Series> q = JPA.em().createQuery("select s from Series s where s.name like :name", Series.class);
		q.setParameter("name", "%" + name + "%");

		List<Series> result;
		try {
			result = q.getResultList();
		} catch (NoResultException e) {
			result = new ArrayList<>();
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return ok(objectMapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			return internalServerError();
		}
	}
}
