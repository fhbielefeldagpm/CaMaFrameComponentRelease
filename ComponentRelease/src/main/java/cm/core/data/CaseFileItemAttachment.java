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

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.CaseWorker;
/**
 * <p>Class representing attachments of {@link CaseFileItem}s in CMMN. Can store either a reference link to a file or the file as a byte array.</p>
 * <p>Not specified in the CMMN 1.1 specification.</p>
 * 
 * @author André Zensen
 *
 */
@Entity
public class CaseFileItemAttachment {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String name;
	private String type;
	private double size;
	private String path;
	@Temporal(TemporalType.DATE)
	private Date createdOn;
	private String createdBy;
	@JohnzonIgnore
	@Basic(fetch=FetchType.LAZY)
	@Lob
    private byte[] data;
		
	public CaseFileItemAttachment() {
		super();
	}	
	/**
	 * Constructs a new CaseFileItemAttachment. Needs to be added to a {@link CaseFileItem}.
	 * @param name		the name of the file
	 * @param type		the (MIME) type of the file
	 * @param size		the size of the file
	 * @param path		the (relative or absolute) path of the file
	 * @param createdOn	the date the file was created on
	 * @param createdBy the short name of the user, e.g. a {@link CaseWorker}, the file was created by
	 */
	public CaseFileItemAttachment(String name, String type, double size, String path, Date createdOn,
			String createdBy) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.path = path;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}	
}
