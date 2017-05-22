/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.cassandra.core.query;

import static org.springframework.util.ObjectUtils.nullSafeHashCode;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.util.Assert;

/**
 * Basic class for creating queries. It follows a fluent API style so that you can easily create a
 * {@link CriteriaDefinition}. Static import of the 'Criteria.where' method will improve readability.
 *
 * @author Mark Paluch
 * @since 2.0
 */
public class Criteria implements CriteriaDefinition {

	private final ColumnName columnName;

	private Predicate predicate;

	private Criteria(ColumnName columnName, Predicate predicate) {

		this(columnName);

		Assert.notNull(predicate, "Predicate must not be null");

		this.predicate = predicate;
	}

	/**
	 * Create an empty {@link Criteria} given a {@link ColumnName}.
	 */
	protected Criteria(ColumnName columnName) {

		Assert.notNull(columnName, "ColumnName must not be null");

		this.columnName = columnName;
	}

	/**
	 * Static factory method to create a {@link Criteria} using the provided {@code columnName}.
	 *
	 * @param columnName must not be {@literal null}.
	 * @return a new {@link Criteria} for {@code columnName}.
	 */
	public static Criteria where(String columnName) {
		return new Criteria(ColumnName.from(columnName));
	}

	/**
	 * Static factory method to create a {@link Criteria} using the provided {@code columnName}.
	 *
	 * @param columnName must not be {@literal null}.
	 * @return a new {@link Criteria} for {@code columnName}.
	 */
	public static Criteria of(ColumnName columnName, Predicate predicate) {

		Assert.notNull(columnName, "ColumnName must not be null");
		Assert.notNull(predicate, "Predicate must not be null");

		return new Criteria(columnName, predicate);
	}

	/**
	 * Create a criterion using equality.
	 *
	 * @param value the value to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition is(Object value) {

		this.predicate = new Predicate(Operators.EQ, value);
		return this;
	}

	/**
	 * Create a criterion using the {@literal >} operator.
	 *
	 * @param value the value to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition lt(Object value) {

		Assert.notNull(value, "Value must not be null");

		this.predicate = new Predicate(Operators.LT, value);
		return this;
	}

	/**
	 * Create a criterion using the {@literal >=} operator.
	 *
	 * @param value the value to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition lte(Object value) {

		Assert.notNull(value, "Value must not be null");

		this.predicate = new Predicate(Operators.LTE, value);
		return this;
	}

	/**
	 * Create a criterion using the {@literal <} operator.
	 *
	 * @param value the value to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition gt(Object value) {

		Assert.notNull(value, "Value must not be null");

		this.predicate = new Predicate(Operators.GT, value);
		return this;
	}

	/**
	 * Create a criterion using the {@literal <=} operator.
	 *
	 * @param value the value to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition gte(Object value) {

		Assert.notNull(value, "Value must not be null");

		this.predicate = new Predicate(Operators.GTE, value);
		return this;
	}

	/**
	 * Create a criterion using the {@literal IN} operator.
	 *
	 * @param values the values to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition in(Object... values) {

		Assert.notNull(values, "Value must not be null");

		if (values.length > 1 && values[1] instanceof Collection) {
			throw new InvalidDataAccessApiUsageException(
					"You can only pass in one argument of type " + values[1].getClass().getName());
		}

		return in(Arrays.asList(values));
	}

	/**
	 * Create a criterion using the {@literal IN} operator.
	 *
	 * @param values the collection of values to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition in(Collection<?> values) {

		Assert.notNull(values, "Value must not be null");

		this.predicate = new Predicate(Operators.IN, values);
		return this;
	}

	/**
	 * Create a criterion using the {@literal LIKE} operator.
	 *
	 * @param value the value to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition like(Object value) {

		Assert.notNull(value, "Value must not be null");

		this.predicate = new Predicate(Operators.LIKE, value);
		return this;
	}

	/**
	 * Create a criterion using the {@literal CONTAINS} operator.
	 *
	 * @param value the value to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition contains(Object value) {

		Assert.notNull(value, "Value must not be null");

		this.predicate = new Predicate(Operators.CONTAINS, value);
		return this;
	}

	/**
	 * Create a criterion using the {@literal CONTAINS KEY} operator.
	 *
	 * @param key the key to match against.
	 * @return {@literal this} {@link Criteria} object.
	 */
	public CriteriaDefinition containsKey(Object key) {

		Assert.notNull(key, "Value must not be null");

		this.predicate = new Predicate(Operators.CONTAINS_KEY, key);
		return this;
	}

	/**
	 * @return the {@link ColumnName}.
	 */
	public ColumnName getColumnName() {
		return this.columnName;
	}

	/**
	 * @return the {@link Predicate}.
	 */
	public Predicate getPredicate() {
		return predicate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Criteria)) {
			return false;
		}

		Criteria that = (Criteria) obj;

		return simpleCriteriaEquals(this, that);
	}

	protected boolean simpleCriteriaEquals(CriteriaDefinition left, CriteriaDefinition right) {

		boolean keyEqual = left.getColumnName() == null ? right.getColumnName() == null
				: left.getColumnName().equals(right.getColumnName());
		boolean criteriaEqual = left.getPredicate().equals(right.getPredicate());

		return keyEqual && criteriaEqual;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int result = 17;

		result += nullSafeHashCode(columnName);
		result += nullSafeHashCode(predicate);

		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return SerializationUtils.serializeToCqlSafely(this);
	}
}