package com.tayjava.cache;

/** Cached course metadata for BFF composition (titles / slugs). Not used for enroll decisions. */
public record CourseView(Long id, String slug, String title, String instructorUsername, boolean published) {}
