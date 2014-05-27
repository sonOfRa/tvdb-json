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
import de.slevermann.tvdb.models.Episode;
import de.slevermann.tvdb.models.Series;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Controller for episode-related actions
 *
 * @author Simon Levermann
 */
public class EpisodeController extends BaseController {

	@Transactional
	public static Result testEpisode() {
		Series s = new Series();
		s.setName("Serie 1");
		s.setLanguage("en");
		JPA.em().persist(s);
		Episode e = new Episode();
		e.setSeries(s);
		e.setEpisodeName("name");
		e.setLanguage("en");
		JPA.em().persist(e);
		return ok();
	}

	@Transactional(readOnly = true)
	public static Result bla() {
		TypedQuery<Episode> eps = JPA.em().createQuery("select e from Episode e", Episode.class);
		List<Episode> episodes = eps.getResultList();
		ObjectMapper mapper = new ObjectMapper();
		try {
			return ok(mapper.writeValueAsString(episodes));
		} catch (JsonProcessingException e) {
			return internalServerError();
		}
	}
}
