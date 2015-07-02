package com.gentics.mesh.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.gentics.mesh.core.data.Group;
import com.gentics.mesh.core.data.User;
import com.gentics.mesh.core.data.root.GroupRoot;
import com.gentics.mesh.core.data.root.UserRoot;
import com.gentics.mesh.core.data.service.GroupService;
import com.gentics.mesh.test.AbstractDBTest;

public class GroupTest extends AbstractDBTest {

	@Autowired
	private GroupService groupService;

	@Before
	public void setup() throws Exception {
		setupData();
	}

	@Test
	public void testUserGroup() {
		UserRoot userRoot = data().getMeshRoot().getUserRoot();
		GroupRoot groupRoot = data().getMeshRoot().getGroupRoot();

		User user = userRoot.create("testuser");
		Group group = groupRoot.create("test group");
		group.addUser(user);

		assertEquals("The group should contain one member.", 1, group.getUsers().size());

		User userOfGroup = group.getUsers().iterator().next();
		assertEquals("Username did not match the expected one.", user.getUsername(), userOfGroup.getUsername());
	}

	@Test
	public void testRootGroupNode() {
		GroupRoot root = data().getMeshRoot().getGroupRoot();
		int nGroupsBefore = root.getGroups().size();
		GroupRoot groupRoot = data().getMeshRoot().getGroupRoot();
		assertNotNull(groupRoot.create("test group2"));

		int nGroupsAfter = root.getGroups().size();
		assertEquals(nGroupsBefore + 1, nGroupsAfter);
	}

}
