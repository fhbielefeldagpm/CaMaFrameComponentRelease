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
package cm.core.data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * <p>Class representing a single (primitive) property of a {@link CaseFileItem} in CMMN. Called Property in the specification. </p>
 * <p>Deviates from the specification. See CMMN 1.1 specification section 5.1.4.1 for more information.</p>
 * 
 * @author André Zensen
 *
 */
@Entity
public class SimpleProperty {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	@Enumerated(EnumType.STRING)
	private CaseFileItemPropertyType type;
	private String value;
	
	public SimpleProperty(String name, String value) {
		this.name = name;
		this.value = value;
	}
	public SimpleProperty() {
		
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CaseFileItemPropertyType getType() {
		return type;
	}
	public void setType(CaseFileItemPropertyType type) {
		this.type = type;
	}
	public String getValue() {
		return this.value;
	};
	public void setValue(String value) {
		this.value = value;
	};
	
	
}
