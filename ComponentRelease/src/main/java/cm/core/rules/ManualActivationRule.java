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

import javax.persistence.Entity;

import cm.core.data.CaseFileItem;
import cm.core.utils.RuleExpressionFactory;
/**
 * <p>
 * Represents the ManualActivationRule/manual activation decorator in CMMN. Has a reference
 * to a {@link CaseFileItem}. The actual implementation is based on class
 * {@link RuleExpression}.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.13.2 and 8.6.2 for more information.
 * </p>
 * 
 * @author André Zensen
 * @see {@link RuleExpression}
 */
@Entity
public class ManualActivationRule extends Rule{

	public ManualActivationRule() {
		super();
	}
	/**
	 * <p>
	 * Constructs a new ManualActivationRule object serving as a link to the implementation
	 * and a CaseFileItem to evaluate.
	 * </p>
	 * 
	 * @param name
	 *            the name referenced by a {@link RuleExpressionFactory}
	 * @param contextRef
	 *            the {@link CaseFileItem} to be evaluated
	 */
	public ManualActivationRule(String name, CaseFileItem contextRef) {
		super(name, contextRef);
	}

	@Override
	public boolean evaluate() {
		RuleExpression expression = RuleExpressionFactory.getExpressionForRule(this);
		boolean repeat = expression.evaluate();
		return repeat;
	}


	
}
