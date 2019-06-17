/*
 * Copyright © 2018-2019 André Zensen, University of Applied Sciences Bielefeld
 * and various authors (see https://www.fh-bielefeld.de/wug/forschung/ag-pm)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cm.core.rules;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import cm.core.data.CaseFileItem;
import cm.core.utils.RuleExpressionFactory;

/**
 * <p>
 * Base class for creating a named {@link Rule} with a reference to a
 * {@link CaseFileItem}. The actual implementation is based on class
 * {@link RuleExpression}.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.13 and 8.6 for more information.
 * </p>
 * 
 * @author André Zensen
 * @see {@link RuleExpression}, {@link ManualActivationRule},
 *      {@link RepetitionRule} and {@link RequiredRule}
 */
@MappedSuperclass
public abstract class Rule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	@ManyToOne
	protected CaseFileItem contextRef;

	public Rule() {

	}

	/**
	 * <p>
	 * Constructs a new Rule object serving as a link to the implementation and a
	 * CaseFileItem to evaluate.
	 * </p>
	 * 
	 * @param name
	 *            the name referenced by a {@link RuleExpressionFactory}
	 * @param contextRef
	 *            the {@link CaseFileItem} to be evaluated
	 */
	public Rule(String name, CaseFileItem contextRef) {
		this.name = name;
		this.contextRef = contextRef;
	}

	public String getName() {
		return this.name;
	}

	public CaseFileItem getContextRef() {
		return contextRef;
	}

	public void setContextRef(CaseFileItem contextRef) {
		this.contextRef = contextRef;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Implements the evaluation of the referenced CaseFileItem. First calls
	 * {@link RuleExpressionFactory} to get the implementation at runtime, then
	 * calls its evaluate method.
	 * 
	 * @return <code>true</code> if the Rule still holds, or <code>false</code> if
	 *         not
	 */
	public abstract boolean evaluate();

}
