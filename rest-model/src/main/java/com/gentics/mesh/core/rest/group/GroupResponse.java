package com.gentics.mesh.core.rest.group;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.gentics.mesh.core.rest.common.AbstractGenericRestResponse;
import com.gentics.mesh.core.rest.role.RoleReference;

/**
 * POJO that is used group response models.
 */
public class GroupResponse extends AbstractGenericRestResponse {

	@JsonProperty(required = true)
	@JsonPropertyDescription("Name of the group")
	private String name;

	@JsonProperty(required = true)
	@JsonPropertyDescription("List of role references")
	private List<RoleReference> roles = new ArrayList<>();

	public GroupResponse() {
	}

	/**
	 * Return the name of the group.
	 * 
	 * @return Name of the group
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the group.
	 * 
	 * @param name
	 *            Name of the group
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a list of role references which are assigned to the group.
	 * 
	 * @return List of role references
	 */
	public List<RoleReference> getRoles() {
		return roles;
	}

}
