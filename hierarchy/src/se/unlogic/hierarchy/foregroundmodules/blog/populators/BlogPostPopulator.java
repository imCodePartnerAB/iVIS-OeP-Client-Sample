/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.populators;

import java.util.Arrays;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.foregroundmodules.blog.beans.BlogPost;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

public class BlogPostPopulator extends AnnotatedRequestPopulator<BlogPost> implements BeanRequestPopulator<BlogPost> {

	public BlogPostPopulator() {
		super(BlogPost.class);
	}

	@Override
	public BlogPost populate(HttpServletRequest req) throws ValidationException {
		return this.populate(new BlogPost(), req);
	}

	@Override
	public BlogPost populate(BlogPost blogPost, HttpServletRequest req) throws ValidationException {

		blogPost = super.populate(blogPost, req);

		if (blogPost.getMessage().contains(BlogPost.SPLIT_TAG)) {
			blogPost.setSplit(true);
		} else {
			blogPost.setSplit(false);
		}

		TreeSet<String> tagSet = new TreeSet<String>();

		String[] tags = req.getParameterValues("tag");

		if(tags != null){
			tagSet.addAll(Arrays.asList(tags));
		}

		String newTags = req.getParameter("newtags");

		if(newTags != null){

			String[] newTagArray = newTags.split("\n");

			for(String newTag : newTagArray){

				newTag = newTag.replace("\n", "");
				newTag = newTag.replace("\r", "");
				
				if(!StringUtils.isEmpty(newTag)){

					tagSet.add(newTag);
				}
			}
		}

		if (!tagSet.isEmpty()) {
			blogPost.setTags(tagSet);
		} else {
			blogPost.setTags(null);
		}

		return blogPost;
	}
}
